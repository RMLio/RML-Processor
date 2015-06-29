
package be.ugent.mmlab.rml.processor;

import be.ugent.mmlab.rml.vocabulary.QLVocabulary;

/**
 * RML Processor
 * 
 * Interface for creating processors
 * 
 * @author mielvandersande
 */
public interface RMLProcessorFactory {
    
    /**
     *
     * @param term
     * @return
     */
    public  RMLProcessor create(QLVocabulary.QLTerm term);
    
}
