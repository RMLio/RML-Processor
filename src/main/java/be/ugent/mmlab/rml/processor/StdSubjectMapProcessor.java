package be.ugent.mmlab.rml.processor;

import be.ugent.mmlab.rml.condition.model.Condition;
import be.ugent.mmlab.rml.logicalsourcehandler.termmap.TermMapProcessor;
import static be.ugent.mmlab.rml.model.RDFTerm.TermType.BLANK_NODE;
import static be.ugent.mmlab.rml.model.RDFTerm.TermType.IRI;
import be.ugent.mmlab.rml.model.RDFTerm.GraphMap;
import be.ugent.mmlab.rml.model.RDFTerm.SubjectMap;
import be.ugent.mmlab.rml.model.dataset.RMLDataset;
import be.ugent.mmlab.rml.model.std.StdConditionSubjectMap;
import be.ugent.mmlab.rml.processor.concrete.ConcreteTermMapFactory;
import be.ugent.mmlab.rml.processor.concrete.TermMapProcessorFactory;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang.RandomStringUtils;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.BNodeImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RML Processor
 *
 * @author andimou
 */
public class StdSubjectMapProcessor implements SubjectMapProcessor {
    private TermMapProcessor termMapProcessor ;
    
    // Log
    private static final Logger log = 
            LoggerFactory.getLogger(StdSubjectMapProcessor.class);
    
    @Override
    public Resource processSubjectMap(RMLDataset dataset, SubjectMap subjectMap, 
        Object node, RMLProcessor processor) {  
        Resource subject = null;
        boolean result ;
        
        //Get the uri
        TermMapProcessorFactory factory = new ConcreteTermMapFactory();
        this.termMapProcessor = factory.create(
                subjectMap.getOwnTriplesMap().getLogicalSource().getReferenceFormulation(),
                processor);
        
        if (subjectMap.getClass().getSimpleName().equals("StdConditionSubjectMap")) {
            log.debug("Conditional Subject Map");
            StdConditionSubjectMap condSubMap =
                    (StdConditionSubjectMap) subjectMap;
            //TODO: Move this to conditionSubjectMapProcessor
            Set<Condition> conditions = condSubMap.getConditions();
            ConditionProcessor condProcessor = new StdConditionProcessor();
            result = condProcessor.processConditions(node, termMapProcessor, conditions);
        }
        else{
            log.debug("Simple Subject Map");
            result = true;
        }
        
        if (result == true) {
            List<String> values = this.termMapProcessor.processTermMap(subjectMap, node);
            //log.info("Abstract RML Processor Graph Map" + subjectMap.getGraphMaps().toString());
            if (values == null || values.isEmpty()) {
                if (subjectMap.getTermType() != BLANK_NODE) {
                    log.debug("No subject was generated for "
                            + subjectMap.getOwnTriplesMap().getName().toString());
                    return null;
                }
            }

            String value = null;
            if (subjectMap.getTermType() != BLANK_NODE) {
                //Since it is the subject, more than one value is not allowed. 
                //Only return the first one. Throw exception if not?
                value = values.get(0);

                if ((value == null) || (value.equals(""))) {
                    log.error("No subject was generated for " + subjectMap.toString());
                    return null;
                }
            }


            //TODO: doublicate code from ObjectMap - they should be handled together
            //TODO:Spring it!
            switch (subjectMap.getTermType()) {
                case IRI:
                    if (value != null && !value.equals("")) {
                        if (value.startsWith("www.")) {
                            value = "http://" + value;
                        }
                    }
                    try {
                        subject = new URIImpl(value);
                    } catch (Exception e) {
                        return null;
                    }
                    break;
                case BLANK_NODE:
                    subject = new BNodeImpl(
                            RandomStringUtils.randomAlphanumeric(10));
                    break;
                default:
                    subject = new URIImpl(value);
            }
        }
        return subject;
    }

    @Override
    public void processSubjectTypeMap(
            RMLDataset dataset, Resource subject, SubjectMap subjectMap, Object node) {

        boolean flag = false;
        Set<org.openrdf.model.URI> classIRIs = subjectMap.getClassIRIs();
        /*String[] vocabs = dataset.getMetadataVocab();
        //TODO: Decide if I keep that here or if I move it to separate class
        if (vocabs != null) {
            if (vocabs.length == 0) {
                flag = true;
            } else {
                for (String vocab : vocabs) {
                    if (vocab.equals("prov")) {
                        flag = true;
                    }
                }
            }
        }*/
        if (subject != null) {
            for (org.openrdf.model.URI classIRI : classIRIs) {
                if (subjectMap.getGraphMaps().isEmpty()) {
                    List<Statement> triples =
                            dataset.tuplePattern(subject, RDF.TYPE, classIRI);
                    if (triples.size() == 0) {
                        dataset.add(subject, RDF.TYPE, classIRI);

                        /*if (dataset.getMetadataLevel().equals("triple")) {
                            if (flag == true) {
                                dataset.getMetadataDataset().addReification(
                                        subject, classIRI, subject, subjectMap.getOwnTriplesMap());
                            }

                        }*/

                    }
                } else {
                    for (GraphMap map : subjectMap.getGraphMaps()) {
                        if (map.getConstantValue() != null) {
                            dataset.add(
                                    subject, RDF.TYPE, classIRI,
                                    new URIImpl(map.getConstantValue().toString()));
                        }
                    }
                }
            }
        }
    }
}
