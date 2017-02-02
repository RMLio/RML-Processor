package be.ugent.mmlab.rml.processor;

import be.ugent.mmlab.rml.condition.model.Condition;
import be.ugent.mmlab.rml.metadata.MetadataGenerator;
import be.ugent.mmlab.rml.model.PredicateObjectMap;
import be.ugent.mmlab.rml.model.RDFTerm.GraphMap;
import be.ugent.mmlab.rml.model.RDFTerm.ObjectMap;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.model.dataset.MetadataRMLDataset;
import be.ugent.mmlab.rml.model.dataset.RMLDataset;
import be.ugent.mmlab.rml.model.std.StdConditionObjectMap;
import java.util.List;
import java.util.Set;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RML Processor
 *
 * @author andimou
 */
public class MetadataObjectMapProcessor extends StdObjectMapProcessor implements ObjectMapProcessor {
    // Log
    private static final Logger log = 
            LoggerFactory.getLogger(
            MetadataObjectMapProcessor.class.getSimpleName());
    
    MetadataGenerator metadataGenerator = null;
    
    public MetadataObjectMapProcessor(TriplesMap map, RMLProcessor processor,
            MetadataGenerator metadataGenerator){
        super(map, processor);
        this.metadataGenerator = metadataGenerator;
    }
    
    @Override
    public void processPredicateObjectMap_ObjMap(
            RMLDataset originalDataset, Resource subject, IRI predicate,
            PredicateObjectMap pom, Object node, GraphMap graphMap) {
        MetadataRMLDataset dataset = (MetadataRMLDataset) originalDataset ;
        SimpleValueFactory vf = SimpleValueFactory.getInstance();
        //MetadataGenerator metadataGenerator = new MetadataGenerator();
        
        Set<ObjectMap> objectMaps = pom.getObjectMaps();
        if(objectMaps.size() > 0)
            log.debug("Processing Simple Metadata Object Map...");
        
        for (ObjectMap objectMap : objectMaps) {
            boolean flag = true;
            //Get the one or more objects returned by the object map
            List<Value> objects = processObjectMap(objectMap, node);
            
            if(objectMap.getClass().getSimpleName().equals("StdConditionObjectMap")){
                log.debug("Conditional Object Map");
                StdConditionObjectMap tmp = (StdConditionObjectMap) objectMap;
                Set<Condition> conditions = tmp.getConditions();
                
                //process conditions
                ConditionProcessor condProcessor = new StdConditionProcessor();
                flag = condProcessor.processConditions(
                        node, termMapProcessor, conditions);
            }
            
            if (objects != null && objects.size() > 0) { //flag && 
                
                for (Value object : objects) {
                    if (object.stringValue() != null) {
                        Set<GraphMap> graphs = pom.getGraphMaps();
                        if (graphs.isEmpty() && subject != null) {
                            List<Statement> triples = 
                                    dataset.tuplePattern(subject, predicate, object);
                            if(triples.isEmpty()){
                                dataset.add(subject, predicate, object); 
                                log.debug("Should log triple level metadata...");
                                     
                                    metadataGenerator.generateTripleMetaData(
                                        dataset, pom.getOwnTriplesMap(), 
                                        subject, predicate, object, null);
                            }
                        } else {
                            for (GraphMap graph : graphs) {
                                Resource graphResource = vf.createIRI(
                                        graph.getConstantValue().toString());
                                dataset.add(subject, predicate, object, graphResource);
                                log.debug("Should log triple level metadata...");
                                metadataGenerator.generateTripleMetaData(
                                        dataset, pom.getOwnTriplesMap(), 
                                        subject, predicate, object, null);
                            }
                        }

                    }
                }
            } else {
                log.debug("No object created. No triple will be generated.");
            }
        }
    }

}
