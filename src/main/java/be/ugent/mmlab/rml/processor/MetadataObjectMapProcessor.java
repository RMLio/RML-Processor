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
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
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
            LoggerFactory.getLogger(MetadataObjectMapProcessor.class);
    
    public MetadataObjectMapProcessor(TriplesMap map, RMLProcessor processor){
        super(map, processor);
    }
    
    @Override
    public void processPredicateObjectMap_ObjMap(
            RMLDataset originalDataset, Resource subject, URI predicate,
            PredicateObjectMap pom, Object node) {
        MetadataRMLDataset dataset = (MetadataRMLDataset) originalDataset ;
        MetadataGenerator metadataGenerator = new MetadataGenerator();
        
        Set<ObjectMap> objectMaps = pom.getObjectMaps();
        if(objectMaps.size() > 0)
            log.debug("Processing Simple Object Map...");
        
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
            
            if (flag && objects != null) {
                
                for (Value object : objects) {
                    if (object.stringValue() != null) {
                        Set<GraphMap> graphs = pom.getGraphMaps();
                        if (graphs.isEmpty() && subject != null) {
                            List<Statement> triples = 
                                    dataset.tuplePattern(subject, predicate, object);
                            if(triples.size() == 0){
                                dataset.add(subject, predicate, object); 
                                log.debug("Should log triple level metadata...");
                                metadataGenerator.generateTripleMetaData(dataset,
                                        subject, predicate, object);
                            }
                        } else {
                            for (GraphMap graph : graphs) {
                                Resource graphResource = new URIImpl(
                                        graph.getConstantValue().toString());
                                dataset.add(subject, predicate, object, graphResource);
                                log.debug("Should log triple level metadata...");
                                metadataGenerator.generateTripleMetaData(dataset,
                                        subject, predicate, object);
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
