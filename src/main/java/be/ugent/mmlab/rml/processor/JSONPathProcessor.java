/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.mmlab.rml.processor;

import be.ugent.mmlab.rml.model.TriplesMap;
import com.jayway.jsonpath.JsonPath;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.antidot.semantic.rdf.model.impl.sesame.SesameDataSet;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;

/**
 *
 * @author mielvandersande
 */
public class JSONPathProcessor extends AbstractRMLProcessor {

    public void execute(SesameDataSet dataset, TriplesMap map, HashMap<String, String> conditions, Resource subject, URI predicate) {
        InputStream fis = null;
        try {
            String identifier = getIdentifier(map.getLogicalSource());
            String selector = getSelector(map.getLogicalSource());
            //This is a none streaming solution. A streaming parser requires own implementation, possibly based on https://code.google.com/p/json-simple/wiki/DecodingExamples
            JsonPath path = JsonPath.compile(selector);

            final boolean isJoin = !(conditions == null || subject == null || predicate == null);

            fis = new FileInputStream(identifier);
            List<Object> nodes = path.read(fis);
            for (Object object : nodes) {
                if (isJoin) {
                    processJoin(dataset, map, object, conditions, subject, predicate);
                } else {
                    processNode(dataset, map, object);
                }

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

    @Override
    protected String extractValueFromNode(Object node, String expression) {
        return JsonPath.read(node, expression);
    }

    public void execute(SesameDataSet dataset, TriplesMap map) {
        execute(dataset, map, null, null, null);
    }
}
