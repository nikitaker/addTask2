## Maven plugin for watermarks

### Installation

    mvn install
    
### Usage

  - add into  project.build.plugins of your project pom.xml:
   
   ```
     <plugin>
        <groupId>nikitaker</groupId>
        <artifactId>watermark-plugin</artifactId>
        <version>2</version>
        <configuration>
            <pathToWatermark>src/main/watermark/E1r9X.png</pathToWatermark>
            <pathToFiles>src/main/resources</pathToFiles>
        </configuration>
     </plugin>
   ```  
     
  - run `mvn replace:run`
