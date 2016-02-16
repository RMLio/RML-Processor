package be.ugent.mmlab.rml.processor.concrete;

import be.ugent.mmlab.rml.model.dataset.RMLDataset;
import be.ugent.mmlab.rml.performer.RMLPerformer;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.processor.AbstractRMLProcessor;
import be.ugent.mmlab.rml.vocabularies.QLVocabulary.QLTerm;
import com.jayway.jsonpath.JsonPath;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openrdf.model.Resource;

/**
 * RML Processor
 *
 * @author mielvandersande, andimou
 */
public class JSONPathProcessor extends AbstractRMLProcessor {

    // Log
    static final Logger log = LoggerFactory.getLogger(JSONPathProcessor.class);
    
    JSONPathProcessor(){
        TermMapProcessorFactory factory = new ConcreteTermMapFactory();
        this.termMapProcessor = factory.create(QLTerm.JSONPATH_CLASS);
    }

    @Override
    public void execute(
            RMLDataset dataset, TriplesMap map, 
            RMLPerformer performer, InputStream input, 
            String[] exeTriplesMap, boolean pomExecution) {

        try {
            String reference = getReference(map.getLogicalSource());
            //This is a none streaming solution. A streaming parser requires own implementation, possibly based on https://code.google.com/p/json-simple/wiki/DecodingExamples
            JsonPath path = JsonPath.compile(reference);
            //log.debug("path " + path.getPath());
            //log.debug("input stream " + input);
            Object val = path.read(input);
            execute(dataset, map, performer, val, exeTriplesMap, pomExecution);

        } catch (FileNotFoundException ex) {
            log.error("FileNotFoundException " + ex);
        } catch (IOException ex) {
            log.error("IOException " + ex);
        } 
    }

    @Override
    public void execute_node(
            RMLDataset dataset, String expression, 
            TriplesMap parentTriplesMap, RMLPerformer performer, Object node, 
            Resource subject, String[] exeTriplesMap, boolean pomExecution) {
       
        Object val = JsonPath.read(node, expression);
        
        execute(dataset, parentTriplesMap, performer, val, exeTriplesMap, pomExecution);
        
        //TODO: check if it's complete for sub-mappings
    }
    
    private void execute (RMLDataset dataset, TriplesMap parentTriplesMap, 
            RMLPerformer performer, Object node, 
            String[] exeTriplesMap, boolean pomExecution){
        if (node instanceof JSONObject) 
            performer.perform(node, dataset, parentTriplesMap, 
                    exeTriplesMap, pomExecution);
        else {
            List<Object> nodes;

            if (node instanceof JSONArray) {
                JSONArray arr = (JSONArray) node;
                nodes = arr.subList(0, arr.size());
            } else {
                try {
                    nodes = (List<Object>) node;
                } catch (ClassCastException cce) {
                    nodes = new ArrayList<Object>();
                }
            }
                
            //iterate over all the objects
            for (Object object : nodes) 
                performer.perform(object, dataset, parentTriplesMap, 
                        exeTriplesMap, pomExecution);
        }
    }

}
