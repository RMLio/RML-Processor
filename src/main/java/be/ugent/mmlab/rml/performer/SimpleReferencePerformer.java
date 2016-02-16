package be.ugent.mmlab.rml.performer;

import be.ugent.mmlab.rml.logicalsourcehandler.termmap.TermMapProcessor;
import be.ugent.mmlab.rml.model.dataset.RMLDataset;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.processor.RMLProcessor;
import be.ugent.mmlab.rml.processor.RMLProcessorFactory;
import be.ugent.mmlab.rml.processor.concrete.ConcreteRMLProcessorFactory;
import be.ugent.mmlab.rml.processor.concrete.ConcreteTermMapFactory;
import be.ugent.mmlab.rml.processor.concrete.TermMapProcessorFactory;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

/**
 * RML Processor
 *
 * @author andimou
 */
public class SimpleReferencePerformer extends NodeRMLPerformer {
    
    // Log
    private static final Logger log = 
            LoggerFactory.getLogger(SimpleReferencePerformer.class);
    
    private Resource subject;
    private URI predicate;
    
    public SimpleReferencePerformer(
            RMLProcessor processor, Resource subject, URI predicate) {
        super(processor);
        this.subject = subject;
        this.predicate = predicate;
    }
    
    @Override
    public void perform(Object node, RMLDataset dataset, 
    TriplesMap map, String[] exeTriplesMap, boolean pomExecution) {

        if(map.getSubjectMap().getTermType() == 
                be.ugent.mmlab.rml.model.RDFTerm.TermType.BLANK_NODE 
          || map.getSubjectMap().getTermType() == 
                be.ugent.mmlab.rml.model.RDFTerm.TermType.IRI){
            RMLProcessorFactory factory = new ConcreteRMLProcessorFactory();
            RMLProcessor subprocessor = factory.create(
                    map.getLogicalSource().getReferenceFormulation());
            RMLPerformer performer = new NodeRMLPerformer(subprocessor); 
            Resource object = processor.processSubjectMap(this.processor,
                    dataset, map, map.getSubjectMap(), node, exeTriplesMap); 
            if (object != null) {
                List<Statement> triples =
                        dataset.tuplePattern(subject, predicate, object);
                if(triples.size() == 0) {
                    dataset.add(subject, predicate, object);
                    log.debug("Subject " + subject
                            + " Predicate " + predicate
                            + " Object " + object.toString());
                }
                
                if ((map.getLogicalSource().getReferenceFormulation().toString().
                        equals("CSV"))
                        || (map.getLogicalSource().getReferenceFormulation().toString().
                            equals("XLSX"))
                        || (map.getLogicalSource().getIterator().
                            equals(map.getLogicalSource().getIterator()))) {
                    performer.perform(node, dataset, map, object, exeTriplesMap);
                } else {
                    int end = map.getLogicalSource().getIterator().length();
                    log.info("reference " 
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
                    processor.execute_node(
                            dataset, expression, map, performer, 
                            node, object, exeTriplesMap, pomExecution);
                }
            }
            else
                log.debug("Object of " + map.getName() + " was null. ");
        }
        else{
            TermMapProcessorFactory factory = new ConcreteTermMapFactory();
            TermMapProcessor termMapProcessor = 
                    factory.create(map.getLogicalSource().getReferenceFormulation());

            List<String> values = 
                    termMapProcessor.processTermMap(map.getSubjectMap(), node);        
            for(String value : values){
                Resource object = new URIImpl(value);
                List<Statement> triples =
                        dataset.tuplePattern(subject, predicate, object);
                if (triples.size() == 0) {
                    dataset.add(subject, predicate, object);
                    log.debug("Subject " + subject
                            + " Predicate " + predicate
                            + " Object " + object.toString());
                }
            }   
        }    
    }
}
