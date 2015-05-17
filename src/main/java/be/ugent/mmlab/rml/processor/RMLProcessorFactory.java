
package be.ugent.mmlab.rml.processor;

import be.ugent.mmlab.rml.vocabulary.QLVocabulary;

/**
 * Interface for creating processors
 * @author mielvandersande
 */
public interface RMLProcessorFactory {
    
    public  RMLProcessor create(QLVocabulary.QLTerm term);
    
}
