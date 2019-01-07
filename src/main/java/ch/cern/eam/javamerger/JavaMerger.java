/*
 * Copyright © 2018 CERN European Organization for Nuclear Research
 * Email: eam-service@cern.ch
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package ch.cern.eam.javamerger;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Merge several Java files into one
 */
public class JavaMerger {

    private File outputDirectory;

    private String[] sourceFiles;

    private String destinationPackage;

    private String type;

    private String className;

    private String[] classAnnotations;

    private String[] additionalImports;

    private JavaMerger(File outputDirectory, String[] sourceFiles, String destinationPackage, String type, String className, String[] classAnnotations, String[] additionalImports) {
        this.outputDirectory = outputDirectory;
        this.sourceFiles = sourceFiles != null ? sourceFiles : new String[]{};
        this.destinationPackage = destinationPackage;
        this.type = type;
        this.className = className;
        this.classAnnotations = classAnnotations != null ? classAnnotations : new String[]{};
        this.additionalImports = additionalImports != null ? additionalImports : new String[]{};
    }

    public static class Builder {
        private File outputDirectory;
        private String[] sourceFiles;
        private String destinationPackage;
        private String type;
        private String className;
        private String[] classAnnotations;
        private String[] additionalImports;

        public Builder() {
        }

        public Builder withOutputDirectory(File outputDirectory) {
            this.outputDirectory = outputDirectory;
            return this;
        }

        public Builder withSourceFiles(String[] sourceFiles) {
            this.sourceFiles = sourceFiles;
            return this;
        }

        public Builder withDestinationPackage(String destinationPackage) {
            this.destinationPackage = destinationPackage;
            return this;
        }

        public Builder withType(String type) {
            this.type = type;
            return this;
        }

        public Builder withClassName(String className) {
            this.className = className;
            return this;
        }

        public Builder withClassAnnotations(String[] classAnnotations) {
            this.classAnnotations = classAnnotations;
            return this;
        }

        public Builder withAdditionalImports(String[] additionalImports) {
            this.additionalImports = additionalImports;
            return this;
        }

        public JavaMerger build() {
            return new JavaMerger(this.outputDirectory, this.sourceFiles, this.destinationPackage, this.type, this.className, this.classAnnotations, this.additionalImports);
        }
    }



    /**
     * Generation of the class/interface that merges all input source files
     */
    public void merge() {

        // Creation of folder if it does not exist
        File folder = new File(getGenerationFolderPath());
        if (!folder.exists()) {
            folder.mkdirs();
        }

        try (FileWriter fw = new FileWriter(new File(getGeneratedFilePath()))) {

            // Package declaration
            this.writeLine(fw, "package " + destinationPackage + ";");

            // Merge all imports
            List<String> imports = this.getImports(this.sourceFiles, this.additionalImports);
            for (String importLine: imports) {
                this.writeLine(fw, importLine);
            }

            this.writeLine(fw, "");

            // class/interface annotations
            for (String classAnnotation: classAnnotations) {
                this.writeLine(fw, classAnnotation);
            }

            // class/interface declaration
            switch(FILE_TYPE.getByCode(this.type)) {
                case CLASS:
                    this.writeLine(fw, "public class " + this.className + " {");
                    break;
                case INTERFACE:
                    this.writeLine(fw, "public interface " + this.className + " {");
                    break;
                default:
                    throw new NotImplementedException();
            }

            // Merge class bodies
            List<String> classBodies = this.getClassBodies(this.sourceFiles);
            for (String classBodyLine: classBodies) {
                this.writeLine(fw, classBodyLine);
            }

            // End of class/interface
            this.writeLine(fw, "}");
        } catch(IOException e) {
            throw new JavaMergerException(e);
        }
    }

    /**
     * Get path of generated file based on the JavaMerger configuration
     * @return
     */
    private String getGeneratedFilePath() {
        return this.getGenerationFolderPath()
            + "/"
            +  this.className
            + ".java";
    }

    /**
     * Get path of folder that will contain the generated file
     * @return
     */
    public String getGenerationFolderPath() {
        return this.outputDirectory
            + "/"
            + this.destinationPackage.replace(".", "/");
    }

    private void writeLine(FileWriter fw, String line) {
        try {
            String lineBr = line + "\r\n";
            fw.write(lineBr);
        } catch(IOException e) {
            throw new JavaMergerException(e);
        }
    }


    /**
     * Get the concatenation of imports in Java files
     * Each import should be unique in the returned list
     * @param filePaths
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    private List<String> getImports(String[] filePaths, String[] additionalImports) {
        return Stream.concat(
                // Imports coming from the files
                Stream.of(filePaths)
                    .map(this::getImports)
                    .flatMap(List::stream),
                // Additional imports
                Stream.of(additionalImports))
            .sorted()
            .distinct()
            .collect(Collectors.toList());
    }


    /**
     * Get imports from a java file
     * @param filePath
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    private List<String> getImports(String filePath) {
        File file = new File(filePath);
        try (FileReader fileReader = new FileReader(file);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            List<String> imports = new ArrayList<>();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("import")) {
                    imports.add(line);
                }
            }
            return imports;
        } catch (IOException e) {
            throw new JavaMergerException(e);
        }
    }


    /**
     * Get all class bodies
     * @param filePaths
     * @return
     */
    private List<String> getClassBodies(String... filePaths) {
        return Stream.of(filePaths)
                .map(this::getClassBody)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }


    /**
     * Get the lines of code containing the body of a class
     * @param filePath
     * @return
     */
    private List<String> getClassBody(String filePath) {
        File file = new File(filePath);
        try (FileReader fileReader = new FileReader(file);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            List<String> methodsData = new ArrayList<>();
            String line;
            boolean include = false;
            while ((line = bufferedReader.readLine()) != null) {
                if (include) {
                    // Detection of end of body
                    if (line.startsWith("}")) {
                        include = false;
                    } else {
                        methodsData.add(line);
                    }
                }
                // Detection of start of body
                else if (line.startsWith("public interface") || line.startsWith("public class")) {
                    include = true;
                }
            }
            return methodsData;
        } catch (IOException e) {
            throw new JavaMergerException(e);
        }
    }

}
