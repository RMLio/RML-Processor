/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.mmlab.rml.processor;

import be.ugent.mmlab.rml.vocabulary.RMLVocabulary.QLTerm;

/**
 *
 * @author mielvandersande
 */
public class ConcreteRMLProcessorFactory extends RMLProcessorFactory{

    @Override
    public RMLProcessor create(QLTerm term) {
        switch (term){
            case XPATH_CLASS:
                return new XPathProcessor();
        }
        return null;
    }
    
}
