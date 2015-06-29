package be.ugent.mmlab.rml.processor.concrete;

import be.ugent.mmlab.rml.core.RMLPerformer;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.processor.AbstractRMLProcessor;
import be.ugent.mmlab.rml.processor.termmap.TermMapProcessorFactory;
import be.ugent.mmlab.rml.processor.termmap.concrete.ConcreteTermMapFactory;
import be.ugent.mmlab.rml.sesame.RMLSesameDataSet;
import be.ugent.mmlab.rml.vocabulary.QLVocabulary;
import com.jayway.jsonpath.JsonPath;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.log4j.LogManager;
import org.openrdf.model.Resource;

/**
 * RML Processor
 *
 * @author mielvandersande, andimou
 */
public class JSONPathProcessor extends AbstractRMLProcessor {

    // Log
    private static final org.apache.log4j.Logger log = LogManager.getLogger(JSONPathProcessor.class);
    
    JSONPathProcessor(){
        TermMapProcessorFactory factory = new ConcreteTermMapFactory();
        this.termMapProcessor = factory.create(QLVocabulary.QLTerm.JSONPATH_CLASS);
    }

    @Override
    public void execute(RMLSesameDataSet dataset, TriplesMap map, RMLPerformer performer, InputStream input) {

        try {
            String reference = getReference(map.getLogicalSource());
            //This is a none streaming solution. A streaming parser requires own implementation, possibly based on https://code.google.com/p/json-simple/wiki/DecodingExamples
            JsonPath path = JsonPath.compile(reference);
            
            Object val = path.read(input);
            execute(dataset, map, performer, val);

        } catch (FileNotFoundException ex) {
            log.error(ex);
        } catch (IOException ex) {
            log.error(ex);
        } 
    }

    @Override
    public void execute_node(
            RMLSesameDataSet dataset, String expression, TriplesMap parentTriplesMap, 
            RMLPerformer performer, Object node, Resource subject) {
       
        Object val = JsonPath.read(node, expression);
        
        execute(dataset, parentTriplesMap, performer, val);
        
        //TODO: check if it's complete for sub-mappings
    }
    
    private void execute (RMLSesameDataSet dataset, TriplesMap parentTriplesMap, 
            RMLPerformer performer, Object node){
        if (node instanceof JSONObject) 
            performer.perform(node, dataset, parentTriplesMap);
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
                performer.perform(object, dataset, parentTriplesMap);
        }
    }

    @Override
    public String cleansing(String value) {
        return value;
    }
}
