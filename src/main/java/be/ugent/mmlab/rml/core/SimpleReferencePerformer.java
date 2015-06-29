package be.ugent.mmlab.rml.core;

import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.processor.RMLProcessor;
import be.ugent.mmlab.rml.processor.RMLProcessorFactory;
import be.ugent.mmlab.rml.processor.concrete.ConcreteRMLProcessorFactory;
import be.ugent.mmlab.rml.processor.termmap.TermMapProcessor;
import be.ugent.mmlab.rml.processor.termmap.TermMapProcessorFactory;
import be.ugent.mmlab.rml.processor.termmap.concrete.ConcreteTermMapFactory;
import be.ugent.mmlab.rml.sesame.RMLSesameDataSet;
import java.util.List;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

/**
 * RML Processor
 *
 * @author andimou
 */
public class SimpleReferencePerformer extends NodeRMLPerformer {
    
    // Log
    private static final Logger log = LogManager.getLogger(SimpleReferencePerformer.class);
    
    private Resource subject;
    private URI predicate;
    
    public SimpleReferencePerformer(RMLProcessor processor, Resource subject, URI predicate) {
        super(processor);
        this.subject = subject;
        this.predicate = predicate;
    }
    
    @Override
    public void perform(Object node, RMLSesameDataSet dataset, TriplesMap map) {
        if(map.getSubjectMap().getTermType() == be.ugent.mmlab.rml.model.RDFTerm.TermType.BLANK_NODE 
                || map.getSubjectMap().getTermType() == be.ugent.mmlab.rml.model.RDFTerm.TermType.IRI){
            RMLProcessorFactory factory = new ConcreteRMLProcessorFactory();
            RMLProcessor subprocessor = factory.create(map.getLogicalSource().getReferenceFormulation());
            RMLPerformer performer = new NodeRMLPerformer(subprocessor);            
            Resource object = processor.processSubjectMap(dataset, map.getSubjectMap(), node); 
            if (object != null) {
                dataset.add(subject, predicate, object);
                log.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                        + "[SimpleReferencePerformer:addTriples] Subject "
                        + subject + " Predicate " + predicate + "Object " + object.toString());

                if ((map.getLogicalSource().getReferenceFormulation().toString().equals("CSV"))
                        || (map.getLogicalSource().getIterator().equals(map.getLogicalSource().getIterator()))) {
                    performer.perform(node, dataset, map, object);
                } else {
                    int end = map.getLogicalSource().getIterator().length();
                    log.info(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                            + "[SimpleReferencePerformer:perform] reference " 
                            + map.getLogicalSource().getIterator().toString());
                    String expression = "";
                    switch (map.getLogicalSource().getReferenceFormulation().toString()) {
                        case "XPath":
                            expression = map.getLogicalSource().getIterator().toString().substring(end);
                            break;
                        case "JSONPath":
                            expression = map.getLogicalSource().getIterator().toString().substring(end + 1);
                            break;
                    }
                    processor.execute_node(dataset, expression, map, performer, node, object);
                }
            }
            else
                log.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                        + "[SimpleReferencePerformer] object of " 
                        + map.getName() + " was null. ");
        }
        else{
            TermMapProcessorFactory factory = new ConcreteTermMapFactory();
            TermMapProcessor termMapProcessor = 
                    factory.create(map.getLogicalSource().getReferenceFormulation());

            List<String> values = termMapProcessor.processTermMap(map.getSubjectMap(), node);    
            //List<String> values = processor.processTermMap(map.getSubjectMap(), node);    
            for(String value : values){
                Resource object = new URIImpl(value);

                dataset.add(subject, predicate, object);
                log.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                        + "[SimpleReferencePerformer:addTriples] Subject "
                        + subject + " Predicate " + predicate + "Object " + object.toString());
            }   
        }    
    }
}
