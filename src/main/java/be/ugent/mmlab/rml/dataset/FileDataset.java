package be.ugent.mmlab.rml.dataset;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;

/**
 * RML Processor
 * 
 * @author mielvandersande
 */
public class FileDataset extends StdRMLDataset {

    // Log
    private static Log log = LogFactory.getLog(FileDataset.class);
    
    private File target;
    private BufferedWriter fw;
    private RDFWriter writer;
    private RDFFormat format = RDFFormat.NTRIPLES;
    //private int bnodeid = 0;
    
    private int size = 0;

    public FileDataset(String target) {
        try {
            this.target = new File(target);
            fw = new BufferedWriter(new FileWriter(target));
            writer = Rio.createWriter(this.format, fw);
            writer.startRDF();
        } catch (IOException ex) {
            log.error("IOException " + ex);
        } catch (RDFHandlerException ex) {
            log.error("RDFHandlerException " + ex);
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
            log.error("IOException ", ex);
        } catch ( RDFHandlerException ex) {
            log.error("RDFHandlerException ", ex);
        }

    }

    @Override
    public void add(Resource s, IRI p, Value o, Resource... contexts) {
        SimpleValueFactory vf = SimpleValueFactory.getInstance();

        if (log.isDebugEnabled()) {
            log.debug("Add triple (" + s.stringValue()
                    + ", " + p.stringValue() + ", " + o.stringValue() + ").");
        }

        Statement st = vf.createStatement(s, p, o);
        try {
            writer.handleStatement(st);
            size++;
        } catch (RDFHandlerException ex) {
            log.error("RDFHandlerException " + ex);
        }

    }
   
    /**
     * Close current repository.
     *
     */
    @Override
    public void closeRepository() {
        log.debug("Closing file...");
        try {
            fw.flush();
            writer.endRDF();
            fw.close();
        } catch (RDFHandlerException ex) {
            log.error(ex);
        } catch (IOException ex) {
            log.error("IOException " + ex);
        } 
    }

    @Override
    public int getSize() {
        return size;
    }
}