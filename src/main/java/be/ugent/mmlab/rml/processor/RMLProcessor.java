/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.mmlab.rml.processor;

import be.ugent.mmlab.rml.model.TriplesMap;
import java.util.HashMap;
import net.antidot.semantic.rdf.model.impl.sesame.SesameDataSet;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;



/**
 *
 * @author mielvandersande
 */
public interface RMLProcessor {
    
    public void execute(SesameDataSet dataset, TriplesMap map);
    public void execute(SesameDataSet dataset, TriplesMap map, HashMap<String, String> conditions, Resource subject, URI predicate);

}
