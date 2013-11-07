/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.mmlab.rml.processor;

import be.ugent.mmlab.rml.model.LogicalSource;
import be.ugent.mmlab.rml.model.TriplesMap;
import net.antidot.semantic.rdf.model.impl.sesame.SesameDataSet;


/**
 *
 * @author mielvandersande
 */
public class XPathProcessor implements RMLProcessor{

    public void execute(SesameDataSet dataset, TriplesMap map) {
        LogicalSource ls = map.getLogicalSource();
        
        String selector = ls.getSelector();
        
    }
    
    
}
