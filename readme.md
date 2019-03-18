This gradle plugin can be used to perform a pre-build tasks related to google flatbuffers. Currently, you can use it to

* Generate source files from flatbuffers schema files (*.fbs) to be included in your build.
* Generate binary files from json files matching a schema for use in the project.

### Supported OS
* Windows

### Supported Language Plugins
* cpp-application

### Usage
___
**build.gradle**
```groovy
plugins {
    id 'github.fatalcatharsis.flatc' version '1.0'
}
```
        
**build.gradle.kts**
```kotlin
plugins {
    id("github.fatalcatharsis.flatc") version "1.0"
}
```
    
### Tasks
___

#### FlatcCompile
##### Description
This task takes an input directory containing files of filetype .fbs, converts them to source files, and adds the output directory to the source set of the build.
##### Task Attributes
|Name|Type|Default|Required|Description|
|----|----|-------|--------|-----------|
|input|File| |Yes| A directory containing framebuffer schema files. Must be an existing directory and must containg at least one file with the extension .fbs|
|output|File| |Yes| A directory to output the framebuffer source files to. If the directory does not exist it will be created. If this option is not provided, the outputted source files will be placed in "\<*projectRoot*\>/\<*buildDir*\>/generated/headers".|
|recursive|Boolean|false|No| Determines whether to search the input directory recursively or not. Defaults to false.|

##### Usage
**build.gradle**
  
    task compileSchema(type: FlatcCompile) {
        input = file('./src/main/resources/schema')
        output = file('./generated')
        recursive = true
    }

**build.gradle.kts**

    tasks.create<FlatcCompile>("compileSchema") {
        input = file("./src/main/resources/schema")
        output = file("./generated")
        recursive = true
    }

The result of this will be source files output in "\<projectRoot\>/generated".

#### FlatcConvert
##### Description
This task takes a single framebuffer template and any number of json files and converts them to flatbuffer binary files.
##### Task Attributes
|Name|Type|Default|Required|Description|
|----|----|-------|--------|-----------|
|input|File| |Yes| A directory containing json files. Should match the schema from the file mentioned in the **template** parameter. Must be an existing file or directory. Must contain at least one .json file.|
|template|File| |Yes| Path to a single .fbs file. Must be a path to a valid existing framebuffer schema file with the extension .fbs.|
|output|File|Output directory of your artifacts|No| Path to an output directory for the converted binary files. Directory will be created if it does not exist.|
|recursive|Boolean|false|No| Determines whether to search the input directory recursively or not. Defaults to false.|
##### Usage
**build.gradle**
  
    task convertJson(type: FlatcConvert) {
        input = file('./src/main/resource/data')
        template = file('./src/main/schema/example.fbs')
        output = file('./generated')
        recursive = true
    }

**build.gradle.kts**

    tasks.create<FlatcConvert>("convertJson") {
        input = file('./src/main/resource/data')
        template = file('./src/main/schema/example.fbs')
        output = file('./generated')
        recursive = true
    }
