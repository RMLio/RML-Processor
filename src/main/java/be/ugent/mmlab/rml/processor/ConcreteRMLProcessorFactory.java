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
public class ConcreteRMLProcessorFactory implements RMLProcessorFactory{

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
