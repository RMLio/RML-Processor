RMLProcessor
============

Streaming processor for RML (R2RML extension) in Java

Class diagram
-------------
![alt text](https://raw.github.com/mielvds/RMLProcessor/master/docs/class-diagram.jpg)

Usage
-----
The processor can be run using Maven, so make sure you have installed it first: http://maven.apache.org/download.cgi
You can run a mapping process by executing the following command.
    
    mvn exec:exec <sources_properties> <mapping_file> <output_file> [<graph>]

With
    
    <sources_properties> = Java properties file containing key-value pairs which configure the data sources used in the mapping file. 
    <mapping_file> = The RML mapping file conform with the [RML specification](http://semweb.mmlab.be/ns/rml)
    <output_file> = The file where the output RDF triples are stored; default in [Turtle](http://www.w3.org/TR/turtle/) syntax.
    <graph> (optional) = The named graph in which the output RDF triples are stored.
        
An example `<sources_properties>` file `sources.properties` could contain:
    
    #File: sources.properties
    file1=/path/to/file1.csv
    file2=/path/to/file2.json
    file3=/path/to/file3.xml