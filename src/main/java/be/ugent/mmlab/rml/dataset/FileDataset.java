package be.ugent.mmlab.rml.dataset;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;

/**
 * RML Processor
 *
 * @author mielvandersande, andimou
 */
public class FileDataset extends RMLDataset {

    // Log
    private static final Logger log = 
            LoggerFactory.getLogger(FileDataset.class);
    
    private File target;
    private BufferedWriter fw;
    private RDFWriter writer;
    private RDFFormat format = RDFFormat.NTRIPLES;

    public FileDataset(String target) {

        this.target = new File(target);

        try {
            fw = new BufferedWriter(new FileWriter(target));
            writer = Rio.createWriter(this.format, fw);
            writer.startRDF();

        } catch (IOException ex) {
            log.error("IO Exception ", ex);
        } catch (RDFHandlerException ex) {
            log.error("RDF Handler Exception ", ex);
        }

    }
    
    public FileDataset(String target, String outputFormat) {

        this.target = new File(target);

        try {
            fw = new BufferedWriter(new FileWriter(target));
            switch (outputFormat) {
                case "ntriples": 
                    this.format = RDFFormat.NTRIPLES; 
                    break;
                case "n3": 
                    this.format = RDFFormat.N3;
                    break;
                case "turtle": 
                    this.format = RDFFormat.TURTLE;
                    break;
                case "nquads": 
                    this.format = RDFFormat.NQUADS;
                    break;
                case "rdfxml": 
                    this.format = RDFFormat.RDFXML;
                    break;
                case "rdfjson": 
                    this.format = RDFFormat.RDFJSON;
                    break;
                case "jsonld": 
                    this.format = RDFFormat.JSONLD;
                    break;
            }
            writer = Rio.createWriter(this.format, fw);
            writer.startRDF();

        } catch (IOException ex) {
            log.error("IO Exception ", ex);
        } catch (RDFHandlerException ex) {
            log.error("RDF Handler Exception ", ex);
        }

    }

    public void loadDataFromURL(String stringURL) 
            throws RepositoryException, RDFParseException, IOException {

        URL url = new URL(stringURL);
        //TODO:Replace with getWriterFormatForFileName
        format = RDFFormat.forFileName(stringURL);
    }
}
