## How to run

### 1. Setup the working directory (where you'll store the excel files)

On a mac, this might be a directory created in /Users/<your_user_name>/KeywordsApi.

This is where you will put your input file and tell the program to look for the input file.


### 2. Install the library dependencies

The following libraries are used:
* com.google.code.gson
* org.apache.poi
    * poi 
    * poi-ooxml
    
My suggestion is to just load the required libraries manually as external jar files. I have included them in this repo under the directory /lib.
    
If you know how to use maven, then instead of loading the libraries manually, you can reference the below file to update the build file in eclipse. I didn't do this becaue I'm using intellij instead of eclipse and I'm personally not very familiar with Maven.

#### Maven pom.xml
```
<project>
  <modelVersion>4.0.0</modelVersion>
 
  <groupId>com.mycompany.app</groupId>
  <artifactId>my-app</artifactId>
  <version>1</version>
  
  <dependencies>
    <dependency>
      <groupId>com.google.code.gson</groupId>
      <artifactId>gson</artifactId>
      <version>2.8.9</version>
    </dependency>
    <dependency>
      <groupId>org.apache.poi</groupId>
      <artifactId>poi</artifactId>
      <version>5.2.0</version>
    </dependency>
    <dependency>
        <groupId>org.apache.poi</groupId>
        <artifactId>poi-ooxml</artifactId>
        <version>5.2.0</version>
    </dependency>
  </dependencies>
</project>
```

### 3. Edit the public static void main(String[] args) method in the Main class

This is the method that runs when you execute the code. All you should need to do is update the CSV_FILE_NAME variable to point to the excel file in your working directory (the directory you setup in the first step).

### 4. Execute the main class.