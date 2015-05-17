package be.ugent.mmlab.rml.core;

import be.ugent.mmlab.rml.model.GraphMap;
import be.ugent.mmlab.rml.model.ObjectMap;
import be.ugent.mmlab.rml.model.PredicateMap;
import be.ugent.mmlab.rml.model.PredicateObjectMap;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.condition.model.Condition;
import be.ugent.mmlab.rml.processor.RMLProcessor;
import be.ugent.mmlab.rml.condition.processor.ConditionProcessor;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.antidot.semantic.rdf.model.impl.sesame.SesameDataSet;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.tools.R2RMLToolkit;
import org.openrdf.model.Resource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.RDF;

/**
 * Performs the normal handling of an object in the iteration.
 * 
 * @author mielvandersande, andimou
 * 
 */
public class NodeRMLPerformer implements RMLPerformer{
    
    private static Log log = LogFactory.getLog(NodeRMLPerformer.class);
    
    protected RMLProcessor processor;

    /**
     * 
     * @param processor the instance processing these nodes
     */
    public NodeRMLPerformer(RMLProcessor processor) {
        this.processor = processor;
    }

    /**
     * Process the subject map and predicate-object maps
     * 
     * @param node current object in the iteration
     * @param dataset dataset for endresult
     * @param map current triple map that is being processed
     */
    @Override
    public void perform(Object node, SesameDataSet dataset, TriplesMap map) {
        
        if (map.getLogicalSource().getSplitCondition() == null) {
            //Performing without cpnditions
            //TODO: see what's wrong and needs double checking
            if ((map.getLogicalSource().getEqualConditions() == null
                    && map.getLogicalSource().getProcessConditions()  == null
                    && map.getLogicalSource().getSplitConditions()  == null
                    && map.getLogicalSource().getBindConditions()  == null)
                    || (map.getLogicalSource().getEqualConditions().isEmpty()
                    && map.getLogicalSource().getProcessConditions().isEmpty()
                    && map.getLogicalSource().getSplitConditions().isEmpty()
                    && map.getLogicalSource().getBindConditions().isEmpty())
                    ) {
                
                Resource subject = processor.processSubjectMap(dataset, map.getSubjectMap(), node);
                processor.processSubjectTypeMap(dataset, subject, map.getSubjectMap(), node);

                if (subject == null) {
                    log.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                            + "[NodeRMLPerformer:perform] No subject was generated for "
                            + map.getName() + "triple Map and row " + node.toString());
                } else {
                    Set<GraphMap> graph = map.getSubjectMap().getGraphMaps();
                    for (PredicateObjectMap pom : map.getPredicateObjectMaps()) {
                        processor.processPredicateObjectMap(dataset, subject, pom, node, map);
                    }
                }
            //Performing with conditions    
            } else {
                Set<Condition> conditions = map.getLogicalSource().getConditions();
                if(map.getLogicalSource().getProcessConditions() != null){
                    conditions.addAll(map.getLogicalSource().getProcessConditions());
                    perform(node, dataset, map, conditions);
                }
            }
        } else {
            //Default performing
            perform(node, dataset, map, map.getLogicalSource().getSplitCondition());
        }
    }
    
    @Override
    public void perform(Object node, SesameDataSet dataset, TriplesMap map, String splitCondition) {
        List<String> list = processor.postProcessLogicalSource(splitCondition, node);
        for (String item : list) {
            node = item;
            Resource subject = processor.processSubjectMap(dataset, map.getSubjectMap(), node);
            processor.processSubjectTypeMap(dataset, subject, map.getSubjectMap(), node);
            if (subject == null) {
                log.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                        + "[NodeRMLPerformer:perform] No subject was generated for " 
                        + map.getName() + "triple Map and row " + node.toString());
            } else {
                Set<GraphMap> graph = map.getSubjectMap().getGraphMaps();
                for (PredicateObjectMap pom : map.getPredicateObjectMaps()) {
                    processor.processPredicateObjectMap(dataset, subject, pom, node, map);
                }
            }
        }
    }
    
    private void perform(Object node, SesameDataSet dataset, TriplesMap map, Set<Condition> conditions) {
        List<String> values = null, list, finalList = new ArrayList<String>();
        switch(processor.getClass().getSimpleName()){
            case "XPathProcessor":
                values = processor.extractValueFromNode(node, ".");
                break;
            case "CSS3Extractor" :
                values = processor.extractValueFromNode(node, "*");
                break;
        }
        list = ConditionProcessor.processAllConditions(
                map.getLogicalSource(), values.get(0));

        for (String item : list) {
            List<String> newList = new ArrayList<String>();
            newList.add(item.replaceAll("<br>", ""));
            Set<String> tokens = R2RMLToolkit.extractColumnNamesFromStringTemplate(
                    map.getSubjectMap().getStringTemplate());
            for (String expression : tokens) {
                finalList = processor.processTemplate( map.getSubjectMap(), newList, expression);
            }

            URIImpl subject = new URIImpl(finalList.get(0).replaceAll("<br>", ""));
            Set<org.openrdf.model.URI> classIRIs = map.getSubjectMap().getClassIRIs();
            if(classIRIs != null & classIRIs.size() > 0)
            for (org.openrdf.model.URI classIRI : classIRIs)
                dataset.add(subject, RDF.TYPE, classIRI);
            //TODO:find better solution for cleaning up
            //item = item.replace(" ", "").replaceAll("<br>", "");
            //Element stringNode = new Element(item);
            
            for (PredicateObjectMap pom : map.getPredicateObjectMaps()) {    
                Set<PredicateMap> predicateMaps = pom.getPredicateMaps();
                //Go over each predicate map
                for (PredicateMap predicateMap : predicateMaps) {
                    //Get the predicate
                    List<URI> predicates = processor.processPredicateMap(predicateMap, node);
                    List<Value> valueList = new ArrayList<Value>();
                    URI predicate = predicates.get(0);
                    List<Value> object;
                    for (ObjectMap om : pom.getObjectMaps()) {
                        object = processor.applyTermType(item.replaceAll("<br>", ""), valueList, om);
                        dataset.add(subject, predicate, object.get(0));
                    }              
                }
            }
        }
    }
    
    /**
     *
     * @param node
     * @param dataset
     * @param map
     * @param subject
     */
    @Override
    public void perform(Object node, SesameDataSet dataset, TriplesMap map, Resource subject) {
        processor.processSubjectTypeMap(dataset, subject, map.getSubjectMap(), node);
        for (PredicateObjectMap pom : map.getPredicateObjectMaps()) 
            processor.processPredicateObjectMap(dataset, subject, pom, node, map);
    }
    }
