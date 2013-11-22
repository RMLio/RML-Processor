/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.mmlab.rml.core;

import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.processor.RMLProcessor;
import net.antidot.semantic.rdf.model.impl.sesame.SesameDataSet;

/**
 *
 * @author mielvandersande
 */
public interface RMLPerformer {
    public void perform(Object node, SesameDataSet dataset, TriplesMap map);
}
