package be.ugent.mmlab.rml.processor.concrete;

import be.ugent.mmlab.rml.processor.RMLProcessor;
import be.ugent.mmlab.rml.processor.RMLProcessorFactory;
import be.ugent.mmlab.rml.vocabulary.QLVocabulary.QLTerm;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * RML Processor
 * 
 * This factory class creates language-dependent processors processors
 *
 * @author mielvandersande
 * modified by andimou
 */
public class ConcreteRMLProcessorFactory implements RMLProcessorFactory{
    
    // Log
    private static final Logger log = LogManager.getLogger(ConcreteRMLProcessorFactory.class);

    /**
     * Create the language-dependent processor based on the given language
     * @param term Expression language
     * @return processor able to process the specified language
     */
    @Override
    public RMLProcessor create(QLTerm term) {
        switch (term){
            case XPATH_CLASS:
                return new XPathProcessor();
            case CSV_CLASS:
                return new CSVProcessor();
            case JSONPATH_CLASS:
                return new JSONPathProcessor();
            case CSS3_CLASS:
                return new CSS3Extractor();
            default:
                log.error(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                        + "The term " + term + "was not defined.");
                return null;
        }
    }
    
}
