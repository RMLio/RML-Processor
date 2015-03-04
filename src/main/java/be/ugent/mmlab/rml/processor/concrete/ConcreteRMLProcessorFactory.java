package be.ugent.mmlab.rml.processor.concrete;

import be.ugent.mmlab.rml.processor.RMLProcessor;
import be.ugent.mmlab.rml.processor.RMLProcessorFactory;
import be.ugent.mmlab.rml.vocabulary.RMLVocabulary.QLTerm;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This factory class creates language-dependent processors processors
 *
 * @author mielvandersande
 */
public class ConcreteRMLProcessorFactory implements RMLProcessorFactory{
    
    private static Log log = LogFactory.getLog(ConcreteRMLProcessorFactory.class);

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
        }
        return null;
    }
    
}
