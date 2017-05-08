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
import java.util.Map;

import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.IRI;

/**
 * RML Processor
 *
 * @author andimou
 */
public class SimpleReferencePerformer extends NodeRMLPerformer {
    
    // Log
    private static final Logger log = LoggerFactory.getLogger(
            SimpleReferencePerformer.class.getSimpleName());
    
    private Resource subject;
    private IRI predicate;
    private Resource graph;
    
    public SimpleReferencePerformer(
            RMLProcessor processor, Resource subject, IRI predicate, Resource graphMapValue) {
        super(processor);
        this.subject = subject;
        this.predicate = predicate;
        this.graph = graphMapValue;
    }
    
    @Override
    public boolean perform(Object node, RMLDataset dataset, TriplesMap map, 
        String[] exeTriplesMap, Map<String, String> parameters, boolean pomExecution) {
        boolean result = true;
        SimpleValueFactory vf = SimpleValueFactory.getInstance();

        if(map.getSubjectMap().getTermType() == 
                be.ugent.mmlab.rml.model.RDFTerm.TermType.BLANK_NODE 
          || map.getSubjectMap().getTermType() == 
                be.ugent.mmlab.rml.model.RDFTerm.TermType.IRI){
            RMLProcessorFactory factory = new ConcreteRMLProcessorFactory();
            RMLProcessor subprocessor = factory.create(
                    map.getLogicalSource().getReferenceFormulation(), parameters, map);
            RMLPerformer performer = new NodeRMLPerformer(subprocessor); 
            Resource object = processor.processSubjectMap(this.processor,
                    dataset, map, map.getSubjectMap(), node, exeTriplesMap); 
            if (object != null) {
                List<Statement> triples =
                        dataset.tuplePattern(subject, predicate, object);
                if(triples.size() == 0) {
                    dataset.add(subject, predicate, object, graph);
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
            else{
                log.debug("Check for fallback POMs - 3");
                log.debug("Object of " + map.getName() + " was null. ");
            }
        }
        else{
            TermMapProcessorFactory factory = new ConcreteTermMapFactory();
            TermMapProcessor termMapProcessor = 
                    factory.create(map.getLogicalSource().getReferenceFormulation());

            List<String> values = 
                    termMapProcessor.processTermMap(map.getSubjectMap(), node);        
            for(String value : values){
                Resource object = vf.createIRI(value);
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
        return result;
    }
}
