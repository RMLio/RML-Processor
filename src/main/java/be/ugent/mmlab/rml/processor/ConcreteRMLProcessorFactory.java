package be.ugent.mmlab.rml.processor;

import be.ugent.mmlab.rml.processor.concrete.CSVProcessor;
import be.ugent.mmlab.rml.processor.concrete.JSONPathProcessor;
import be.ugent.mmlab.rml.processor.concrete.XPathProcessor;
import be.ugent.mmlab.rml.vocabulary.Vocab.QLTerm;

/**
 * This factory class creates language-dependent processors processors
 *
 * @author mielvandersande
 */
public class ConcreteRMLProcessorFactory implements RMLProcessorFactory{

    /**
     * Create the language-dependent processor based on the given language
     * @param term Expression language
     * @return processor able to process the specified language
     */
    public RMLProcessor create(QLTerm term) {
        switch (term){
            case XPATH_CLASS:
                return new XPathProcessor();
            case CSV_CLASS:
                return new CSVProcessor();
            case JSONPATH_CLASS:
                return new JSONPathProcessor();
        }
        return null;
    }
    
}
