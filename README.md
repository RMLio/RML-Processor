RMLProcessor
============

Processor for RML (R2RML extension) in Java based on [DB2Triples](https://github.com/antidot/db2triples/)

Class diagram
-------------
![alt text](https://raw.github.com/mmlab/RMLProcessor/master/docs/class-diagram.jpg)

Installation
------------

The processor can be installed using Maven, so make sure you have installed it first: http://maven.apache.org/download.cgi and java 1.7

    mvn clean install

Usage
-----
You can run a mapping process by executing the following command.

Master branch:    
    
    java -jar target/RMLMapper-0.1.jar <mapping_file> <output_file> [-g <graph>]

With 
    
    <mapping_file> = The RML mapping file conform with the [RML specification](http://semweb.mmlab.be/ns/rml)
    <output_file> = The file where the output RDF triples are stored; default in [N-Triples](http://www.w3.org/TR/n-triples/) syntax.
    <graph> (optional) = The named graph in which the output RDF triples are stored.
        
For instance, to run example1, execute the following command by replacing the paths to the files with the local paths:

    java -jar target/RMLMapper-0.1.jar /path/to/the/mapping/document/example.rml.ttl /path/to/the/output/file/example1_test.output.nt

Remark
-----

On OSX, it might be needed to export JAVA_HOME=$(/usr/libexec/java_home)

More Information
----------------

More information about the solution can be found at http://rml.io

This application is developed by Multimedia Lab http://www.mmlab.be

Copyright 2014, Multimedia Lab - Ghent University - iMinds

License
-------

The RMLProcessor is released under the terms of the [MIT license](http://opensource.org/licenses/mit-license.html).
