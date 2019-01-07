# Java Merger
This project is a Maven Plugin that allows you to merge Java classes/interfaces
during your project build.

This project was initially created to merge several JAX-WS generated classes into a single one, for performance reasons.
Yet, it is generic and could be reused for other purposes. 

## Usage
```
<plugin>
    <groupId>ch.cern.eam</groupId>
    <artifactId>javamerger-maven-plugin</artifactId>
    <version>1.0.0</version>
    <executions>
        <execution>
            <phase>generate-sources</phase>
            <goals>
                <goal>merge</goal>
            </goals>
            <configuration>
                <sourceFiles>
                    <sourceFile>${project.build.directory}/generated-sources/wsimport/ch/cern/eam/example/Class1.java</sourceFile>
                    <sourceFile>${project.build.directory}/generated-sources/wsimport/ch/cern/eam/example/Class2.java</sourceFile>
                </sourceFiles>
                <destinationPackage>ch.cern.eam.example</destinationPackage>
                <classAnnotations>
                    <classAnnotation>@WebService(name = "InforWS", targetNamespace = "http://wsdls.datastream.net/WS")</classAnnotation>
                    <classAnnotation>@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)</classAnnotation>
                </classAnnotations>
                <additionalImports>
                    <additionalImport>import javax.jws.WebService;</additionalImport>
                    <additionalImport>import javax.jws.soap.SOAPBinding;</additionalImport>
                </additionalImports>
                <className>MergedClass</className>
                <type>class</type>
            </configuration>
        </execution>
    </executions>
</plugin>
```

## Options
| Option                    | Description |
| -------------             |:-------------:|
| **sourceFiles**           | List of source files to be merged |
| **destinationPackage**    | Package of the generated class/interface |
| **className**             | Name of the generated class/interface |
| **classAnnotations**      | Annotations to add to the generated class/interface |
| **additionalImports**     | Imports to add to the generated class/interface |
| **type**                  | Type of the generated file. Can be "class" or "interface" |

## License
This software is published under the GNU General Public License v3.0 or later.