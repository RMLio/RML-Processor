/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.mmlab.rml.processor;

import be.ugent.mmlab.rml.model.TriplesMap;
import net.antidot.semantic.rdf.model.impl.sesame.SesameDataSet;



/**
 *
 * @author mielvandersande
 */
public interface RMLProcessor {
    
    public void execute(SesameDataSet dataset, TriplesMap map);

}
