RMLProcessor
============

Processor for RML (R2RML extension) in Java.

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
    
    java -jar target/RML-Procssor-0.2.jar -m <mapping_file> -o <output_file> -f <output_format> [-g <graph> -tm <triples_map>]

With 
    
    <mapping_file>  = The RML mapping file conform with the [RML specification](http://rml.io/spec.html)
    <output_file>   = The file where the output RDF triples are stored; default in [N-Triples](http://www.w3.org/TR/n-triples/) syntax.
    <output_format> = The prefered output format, use one of the followings: turtle, ntriples, nquads, rdfxml, rdfjson, jsonld.
    <graph> (optional) = The named graph in which the output RDF triples are stored.
    <triples_map> (optional)  = A specific Triples Map of the mapping document to be executed. Default: all Triples Maps are executed.
        
For instance, to run example1, execute the following command by replacing the paths to the files with the local paths:

    java -jar target/RML-Processor-0.2.jar -m src/test/resources/example1/example.rml.ttl -o src/test/resources/example1/example1_test.output.nt

Note: If not output file is provided, it writes to System.out.

Remark
-----

On OSX, it might be needed to export JAVA_HOME=$(/usr/libexec/java_home)

Related Publication
-------------------
Anastasia Dimou, Miel Vander Sande, Pieter Colpaert, Ruben Verborgh, Erik Mannens, and Rik Van de Walle.

[RML: A Generic Language for Integrated RDF Mappings of Heterogeneous Data](http://ruben.verborgh.org/publications/dimou_ldow_2014/).

Proceedings of the 7th Workshop on Linked Data on the Web (2014)


More Information
----------------

More information about the solution can be found at http://rml.io

This application is developed by Multimedia Lab http://www.mmlab.be

Copyright 2013-2015, Multimedia Lab - Ghent University - iMinds

License
-------

The RMLProcessor is released under the terms of the [MIT license](http://opensource.org/licenses/mit-license.html).
