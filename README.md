RMLProcessor
============

Processor for RML (R2RML extension) in Java based on [DB2Triples](https://github.com/antidot/db2triples/)

Class diagram
-------------
![alt text](https://raw.github.com/mmlab/RMLProcessor/master/docs/class-diagram.jpg)

Usage
-----
The processor can be run using Maven, so make sure you have installed it first: http://maven.apache.org/download.cgi
You can run a mapping process by executing the following command.
    
    mvn exec:java -Dexec.args="<mapping_file> <output_file> [-sp source.properties] [-g <graph>]"

With 
    
    <mapping_file> = The RML mapping file conform with the [RML specification](http://semweb.mmlab.be/ns/rml)
    <output_file> = The file where the output RDF triples are stored; default in [N-Triples](http://www.w3.org/TR/n-triples/) syntax.
    <sources_properties> = Java properties file containing key-value pairs which configure the data sources used in the mapping file.
    <graph> (optional) = The named graph in which the output RDF triples are stored.
        
An example `<sources_properties>` file `sources.properties` could contain:
    
    #File: sources.properties
    file1=/path/to/file1.csv
    file2=/path/to/file2.json
    file3=/path/to/file3.xml

For instance, to run example1, execute the following command by replacing the paths to the files with the local paths:

    mvn exec:java -Dexec.args="/path/to/the/mapping/document/example.rml.ttl /path/to/the/output/file/example1_test.output.nt -sp /path/to/the/properties/file/source.properties"

and the source.properties file should contain
    /example1/example1=/path/to/the/source/file/example1.xml

More Information
----------------

More information about the solution can be found at http://rml.io

This application is developed by Multimedia Lab http://www.mmlab.be

Copyright 2014, Multimedia Lab - Ghent University - iMinds


