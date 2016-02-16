package be.ugent.mmlab.rml.processor.concrete;

import be.ugent.mmlab.rml.logicalsourcehandler.termmap.TermMapProcessor;
import be.ugent.mmlab.rml.logicalsourcehandler.termmap.concrete.CSS3TermMapProcessor;
import be.ugent.mmlab.rml.logicalsourcehandler.termmap.concrete.CSVTermMapProcessor;
import be.ugent.mmlab.rml.logicalsourcehandler.termmap.concrete.JSONPathTermMapProcessor;
import be.ugent.mmlab.rml.logicalsourcehandler.termmap.concrete.XPathTermMapProcessor;
import be.ugent.mmlab.rml.processor.RMLProcessor;
import be.ugent.mmlab.rml.vocabularies.QLVocabulary.QLTerm;
import static be.ugent.mmlab.rml.vocabularies.QLVocabulary.QLTerm.CSS3_CLASS;
import static be.ugent.mmlab.rml.vocabularies.QLVocabulary.QLTerm.CSV_CLASS;
import static be.ugent.mmlab.rml.vocabularies.QLVocabulary.QLTerm.JSONPATH_CLASS;
import static be.ugent.mmlab.rml.vocabularies.QLVocabulary.QLTerm.XLSX_CLASS;
import static be.ugent.mmlab.rml.vocabularies.QLVocabulary.QLTerm.XLS_CLASS;
import static be.ugent.mmlab.rml.vocabularies.QLVocabulary.QLTerm.XPATH_CLASS;
import nu.xom.XPathContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RML Processor
 *
 * @author andimou
 */
public class ConcreteTermMapFactory implements TermMapProcessorFactory {
    
    // Log
    private static final Logger log = LoggerFactory.getLogger(ConcreteTermMapFactory.class);
    
    /**
     *
     * @param term
     * @param processor
     * @return
     */
    @Override
    public TermMapProcessor create(QLTerm term, RMLProcessor processor) {
        //TODO: Make CSVTermMap more generic
        switch (term){
            case XPATH_CLASS:
                if (processor != null) {
                    XPathProcessor process = (XPathProcessor) processor;
                    XPathContext nsContext = process.getNamespaces();
                    return new XPathTermMapProcessor(nsContext);
                }
            case CSV_CLASS:
                return new CSVTermMapProcessor();
            case JSONPATH_CLASS:
                return new JSONPathTermMapProcessor();
            case CSS3_CLASS:
                return new CSS3TermMapProcessor();
            case XLS_CLASS:
                return new CSVTermMapProcessor();
            case XLSX_CLASS:
                return new CSVTermMapProcessor();
            default:
                log.error("The term " + term + "was not defined.");
                return null;
        }
    }
    
    @Override
    public TermMapProcessor create(QLTerm term) {
        //TODO: Make CSVTermMap more generic
        switch (term){
            case XPATH_CLASS:
                return new XPathTermMapProcessor();
            case CSV_CLASS:
                return new CSVTermMapProcessor();
            case JSONPATH_CLASS:
                return new JSONPathTermMapProcessor();
            case CSS3_CLASS:
                return new CSS3TermMapProcessor();
            case XLS_CLASS:
                return new CSVTermMapProcessor();
            case XLSX_CLASS:
                return new CSVTermMapProcessor();
            default:
                log.error("The term " + term + "was not defined.");
                return null;
        }
    }

}
