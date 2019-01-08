/*
 * Copyright © 2018-2019 CERN European Organization for Nuclear Research
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


import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;

/**
 * Goal that merges Java files.
 */
@Mojo(name = "merge", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class JavaMergerMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}")
    private MavenProject project;


    /**
     * Location of the file.
     */
    @Parameter(defaultValue = "${project.build.directory}/generated-sources/java-merger/", property = "outputDir", required = true)
    private File outputDirectory;

    /**
     * Source files that need to be merged
     */
    @Parameter(property = "sourceFiles", required = true )
    private String[] sourceFiles;

    /**
     * Package in which the class will be generated
     */
    @Parameter(property = "destinationPackage", required = true )
    private String destinationPackage;

    /**
     * Type of file to generate. Possible values are:
     *  - class
     *  - interface
     */
    @Parameter(defaultValue = "class", property = "type", required = true )
    private String type;

    /**
     * Name of generated class
     */
    @Parameter(property = "className", required = true )
    private String className;

    /**
     * Annotations to add to the class
     */
    @Parameter(property = "classAnnotations", required = false )
    private String[] classAnnotations;

    /**
     * Imports to be added to the class
     */
    @Parameter(property = "additionalImports", required = false )
    private String[] additionalImports;


    /*
     Log an info message
     */
    private void logInfo(String message) {
        getLog().info("[java-merger] " + message);
    }

    public void execute() throws MojoExecutionException {

        logInfo("Merge of following files: ");
        for (String sourceFile : sourceFiles) {
            logInfo(" - " + sourceFile);
        }
        logInfo("Merge destination: " + destinationPackage);
        logInfo("Class name: " + className);
        logInfo("Type: " + type);

        JavaMerger merger = new JavaMerger
                .Builder()
                .withOutputDirectory(this.outputDirectory)
                .withSourceFiles(this.sourceFiles)
                .withDestinationPackage(this.destinationPackage)
                .withType(this.type)
                .withClassName(this.className)
                .withClassAnnotations(this.classAnnotations)
                .withAdditionalImports(this.additionalImports)
                .build();

        project.addCompileSourceRoot(merger.getGenerationFolderPath());
        try {
            merger.merge();
        } catch (Exception e) {
            getLog().error(e);
            throw new MojoExecutionException("Error while merging Java files", e);
        }
    }
}
