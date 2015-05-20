package be.ugent.mmlab.rml.core;

import be.ugent.mmlab.rml.dataset.FileSesameDataset;
import be.ugent.mmlab.rml.input.processor.AbstractInputProcessor;
import be.ugent.mmlab.rml.input.processor.InputProcessor;
import be.ugent.mmlab.rml.model.LogicalSource;
import be.ugent.mmlab.rml.model.PredicateObjectMap;
import be.ugent.mmlab.rml.model.RMLMapping;
import be.ugent.mmlab.rml.model.ReferencingObjectMap;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.processor.RMLProcessor;
import be.ugent.mmlab.rml.processor.RMLProcessorFactory;
import be.ugent.mmlab.rml.processor.concrete.ConcreteRMLProcessorFactory;
import be.ugent.mmlab.rml.sesame.RMLSesameDataSet;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.ProtocolException;
import java.sql.SQLException;
import java.util.Properties;
import net.antidot.semantic.rdf.model.impl.sesame.SesameDataSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openrdf.repository.RepositoryException;

/**
 * Engine that will perform the mapping starting from the TermMaps
 * 
 * @author mielvandersande, andimou
 */
public class RMLEngine {

    // Log
    private static Log log = LogFactory.getLog(RMLEngine.class);
    // A base IRI used in resolving relative IRIs produced by the R2RML mapping.
    private String baseIRI;
    //private static boolean source_properties;
    //Properties containing the identifiers for files
    //There are probably better ways to do this than a static variable
    private static Properties fileMap = new Properties();

    public static Properties getFileMap() {
        return fileMap;
    }
    
    
    protected String getIdentifier(LogicalSource ls) {
        return RMLEngine.getFileMap().getProperty(ls.getSource());
    }

    /**
     * Generate RDF based on a RML mapping
     *
     * @param rmlMapping Parsed RML mapping
     * @param baseIRI base URI of the resulting RDF
     * @return dataset containing the triples
     * @throws SQLException
     * @throws R2RMLDataError
     * @throws UnsupportedEncodingException
     */
    public SesameDataSet runRMLMapping(RMLMapping rmlMapping, String baseIRI, String[] triplesMap) 
            throws SQLException, UnsupportedEncodingException, IOException {
        return runRMLMapping(rmlMapping, baseIRI, null, "ntriples", null, triplesMap);
    }

    /**
     *
     * @param rmlMapping Parsed RML mapping
     * @param baseIRI base URI of the resulting RDF
     * @param pathToNativeStore path if triples have to be stored in sesame
     * triple store instead of memory
     * @return
     * @throws SQLException
     * @throws R2RMLDataError
     * @throws UnsupportedEncodingException
     */
    public SesameDataSet runRMLMapping(RMLMapping rmlMapping,
            String baseIRI, String pathToNativeStore, String outputFormat, 
            String parameter, String[] exeTriplesMap) 
            throws SQLException, UnsupportedEncodingException {
        long startTime = System.nanoTime();

        log.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                + "[RMLEngine:runRMLMapping] Run RML mapping... ");
        if (rmlMapping == null) 
            log.info(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + "No RML Mapping object found.");
        if (baseIRI == null) 
            log.info(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + "[RMLEngine:runRMLMapping] No base IRI found.");

        SesameDataSet sesameDataSet = chooseSesameDataSet(pathToNativeStore, outputFormat);
        // Update baseIRI
        this.baseIRI = baseIRI;
        try {
            // Explore RML Mapping TriplesMap objects  
            if(pathToNativeStore != null)
                generateRDFTriples(sesameDataSet, rmlMapping, exeTriplesMap, true);
            else
                generateRDFTriples(sesameDataSet, rmlMapping, exeTriplesMap, false);
        } catch (ProtocolException ex) {
            log.info(Thread.currentThread().getStackTrace()[1].getMethodName() + ": " + ex);
        }
               
        //TODO:add metadata this Triples Map started then, finished then and lasted that much
	long endTime = System.nanoTime();
        long duration = endTime - startTime;
        log.info(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                + "RML mapping done! Generated " 
                + sesameDataSet.getSize() + " in " 
                + ((double) duration) / 1000000000 + "s . ");

        return sesameDataSet;
    }
    
    private SesameDataSet chooseSesameDataSet(
            String pathToNativeStore, String outputFormat){
        SesameDataSet sesameDataSet;
        if (pathToNativeStore != null) {
            log.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + "Use direct file "
                    + pathToNativeStore);
            sesameDataSet = new FileSesameDataset(pathToNativeStore, outputFormat);
        } else {
            log.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + "Use default store (memory) ");
            sesameDataSet = new RMLSesameDataSet();
        }
        return sesameDataSet;
        
    }
    
    private boolean check_ReferencingObjectMap(RMLMapping mapping, TriplesMap map, String[] exeTriplesMap) {
        for (TriplesMap triplesMap : mapping.getTriplesMaps()) 
            for (PredicateObjectMap predicateObjectMap : triplesMap.getPredicateObjectMaps()) 
                if (predicateObjectMap.hasReferencingObjectMaps()) 
                    for (ReferencingObjectMap referencingObjectMap : predicateObjectMap.getReferencingObjectMaps()) 
                        if (!referencingObjectMap.getJoinConditions().isEmpty() 
                                && referencingObjectMap.getParentTriplesMap().getName().equals(map.getName())
                                && exeTriplesMap != null)
                                //&& referencingObjectMap.getParentTriplesMap().getLogicalSource().getIdentifier().equals(triplesMap.getLogicalSource().getIdentifier())) 
                            return true;
        return false;
    }

    /**
     * This process adds RDF triples to the output dataset. Each generated
     * triple is placed into one or more graphs of the output dataset. The
     * generated RDF triples are determined by the following algorithm.
     *
     * @param sesameDataSet
     * @param rmlMapping
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    private void generateRDFTriples(SesameDataSet sesameDataSet, 
            RMLMapping rmlMapping, String[] exeTriplesMap, boolean filebased) 
            throws SQLException, UnsupportedEncodingException, ProtocolException {

        log.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                + "Generate RDF triples... ");
        int delta = 0;
        
        RMLProcessorFactory factory = new ConcreteRMLProcessorFactory();
        
        //TODO:put it in a separate function
        outerloop:
        for (TriplesMap triplesMap : rmlMapping.getTriplesMaps()) {
            if(exeTriplesMap != null){
                boolean flag = false;
                for(String exeTM : exeTriplesMap){
                    if(triplesMap.getName().toString().equals(exeTM.toString())){
                        flag = true;
                    }
                }
                if(!flag)
                    continue;
            }
            
            System.out.println("Generating RDF triples for " + triplesMap.getName());
            //TODO: Add metadata that this Map Doc has that many Triples Maps
            
            RMLProcessor processor = null;
            try {
                processor = factory.create(triplesMap.getLogicalSource().getReferenceFormulation());
            } catch (Exception ex) {
                log.error(Thread.currentThread().getStackTrace()[1].getMethodName() + ": " + ex 
                        + "There is no suitable processor for this reference formulation");
            }
            
            String source = triplesMap.getLogicalSource().getInputSource().getSource();
            InputProcessor inputProcessor = new AbstractInputProcessor();
            InputStream input = inputProcessor.getInputStream(triplesMap, source);
            
            try {
                processor.execute(sesameDataSet, triplesMap, new NodeRMLPerformer(processor), input);
            } catch (Exception ex) {
                log.error(ex);
                log.error(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                        + "The execution of the mapping failed.");
            }

            log.info(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + (sesameDataSet.getSize() - delta)
                    + " triples were generated for " + triplesMap.getName());
            //TODO: Add metadata that this Triples Map generatedthat many triples
            delta = sesameDataSet.getSize();
                        
            try {
                input.close();
            } catch (IOException ex) {
                log.error(ex);
                log.error(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                        + "Input file could not be closed.");
            }
        }
        if(filebased)
            try {
                sesameDataSet.closeRepository();
            } catch (RepositoryException ex) {
                log.error(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                        + "Cannot close output repository", ex);
            }
    }
}
