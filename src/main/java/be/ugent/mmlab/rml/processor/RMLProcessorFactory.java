/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.mmlab.rml.processor;

import be.ugent.mmlab.rml.vocabulary.Vocab.QLTerm;

/**
 *
 * @author mielvandersande
 */
public interface RMLProcessorFactory {
    
    public RMLProcessor create(QLTerm term);
    
}
