/*
 * 
 * @author andimou
 * 
 */
package be.ugent.mmlab.rml.dataset;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;

/**
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
    public void add(Resource s, URI p, Value o, Resource... contexts) {
        if (log.isDebugEnabled()) {
            log.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + ": " 
                    + "[FileSesameDataSet:add] Add triple (" + s.stringValue()
                    + ", " + p.stringValue() + ", " + o.stringValue() + ").");
        }

        Statement st = new StatementImpl(s, p, o);
        try {
            writer.handleStatement(st);
            size++;
        } catch (RDFHandlerException ex) {
            log.fatal(o);
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
}