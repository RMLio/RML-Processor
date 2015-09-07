package be.ugent.mmlab.rml.processor.concrete;

import be.ugent.mmlab.rml.processor.RMLProcessor;
import be.ugent.mmlab.rml.processor.RMLProcessorFactory;
import be.ugent.mmlab.rml.vocabularies.QLVocabulary.QLTerm;
import static be.ugent.mmlab.rml.vocabularies.QLVocabulary.QLTerm.CSV_CLASS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger log = LoggerFactory.getLogger(ConcreteRMLProcessorFactory.class);

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
            case SQL_CLASS:
                //TODO: optimize the following 
                //TODO: no reference formulations implies 
                //SQL reference formulation
                //but check first if custom defined one
                return new CSVProcessor();
            case JSONPATH_CLASS:
                return new JSONPathProcessor();
            case CSS3_CLASS:
                return new CSS3Extractor();
            case XLS_CLASS:
                return new XLSProcessor();
            case XLSX_CLASS:
                return new XLSXProcessor();
            default:
                log.error(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                        + "The term " + term + "was not defined.");
                return null;
        }
    }
    
}
