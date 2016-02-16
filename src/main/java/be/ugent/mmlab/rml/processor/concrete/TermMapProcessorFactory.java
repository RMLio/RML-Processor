package be.ugent.mmlab.rml.processor.concrete;

import be.ugent.mmlab.rml.logicalsourcehandler.termmap.TermMapProcessor;
import be.ugent.mmlab.rml.processor.RMLProcessor;
import be.ugent.mmlab.rml.vocabularies.QLVocabulary.QLTerm;

/**
 * RML Processor
 *
 * @author andimou
 */
public interface TermMapProcessorFactory {
    /**
     *
     * @param term
     * @return
     */
    public  TermMapProcessor create(QLTerm term);
    
    public  TermMapProcessor create(QLTerm term, RMLProcessor processor);
}
