/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.mmlab.rml.processor;

import be.ugent.mmlab.rml.processor.RMLProcessor;
import be.ugent.mmlab.rml.vocabulary.RMLVocabulary;
import be.ugent.mmlab.rml.vocabulary.RMLVocabulary.QLTerm;

/**
 *
 * @author mielvandersande
 */
public abstract class RMLProcessorFactory {
    
    public abstract RMLProcessor create(QLTerm term);
    
}
