/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.mmlab.rml.processor.concrete;

import be.ugent.mmlab.rml.core.RMLMappingFactory;
import be.ugent.mmlab.rml.core.RMLPerformer;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.processor.AbstractRMLProcessor;
import com.jayway.jsonpath.JsonPath;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.antidot.semantic.rdf.model.impl.sesame.SesameDataSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author mielvandersande
 */
public class JSONPathProcessor extends AbstractRMLProcessor {
    
    private static Log log = LogFactory.getLog(RMLMappingFactory.class);

    public void execute(SesameDataSet dataset, TriplesMap map, RMLPerformer performer) {
        InputStream fis = null;
        try {
            String identifier = getIdentifier(map.getLogicalSource());
            String reference = getReference(map.getLogicalSource());
            //This is a none streaming solution. A streaming parser requires own implementation, possibly based on https://code.google.com/p/json-simple/wiki/DecodingExamples
            JsonPath path = JsonPath.compile(reference);

            fis = new FileInputStream(identifier);
            List<Object> nodes = path.read(fis);
            //iterate over all the objects
            for (Object object : nodes) {
                performer.perform(object, dataset, map);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(JSONPathProcessor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(JSONPathProcessor.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                Logger.getLogger(JSONPathProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public String extractValueFromNode(Object node, String expression) {
        try {
            return JsonPath.read(node, expression);
        } catch (com.jayway.jsonpath.InvalidPathException ex){
            return null;
        } catch (Exception ex){
            System.out.println("");
            log.debug("[JSONPathProcessor:extractValueFromNode]. Error: " + ex);
            return null;
        }
    }
}
