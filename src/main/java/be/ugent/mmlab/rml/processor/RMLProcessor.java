/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.mmlab.rml.processor;

import net.antidot.semantic.rdf.model.impl.sesame.SesameDataSet;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.model.TriplesMap;

/**
 *
 * @author mielvandersande
 */
public abstract class RMLProcessor {
    
    public abstract void execute(SesameDataSet dataset, TriplesMap map);

}
