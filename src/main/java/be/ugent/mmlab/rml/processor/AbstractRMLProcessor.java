package be.ugent.mmlab.rml.processor;

import be.ugent.mmlab.rml.core.JoinRMLPerformer;
import be.ugent.mmlab.rml.core.RMLEngine;
import be.ugent.mmlab.rml.model.JoinCondition;
import be.ugent.mmlab.rml.model.LogicalSource;
import be.ugent.mmlab.rml.model.ObjectMap;
import be.ugent.mmlab.rml.model.PredicateMap;
import be.ugent.mmlab.rml.model.PredicateObjectMap;
import be.ugent.mmlab.rml.model.ReferencingObjectMap;
import be.ugent.mmlab.rml.model.SubjectMap;
import be.ugent.mmlab.rml.model.TermMap;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.model.selector.SelectorIdentifierImpl;
import be.ugent.mmlab.rml.vocabulary.Vocab.QLTerm;
import java.util.HashMap;
import java.util.Set;
import net.antidot.semantic.rdf.model.impl.sesame.SesameDataSet;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.tools.R2RMLToolkit;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.BNodeImpl;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.RDF;

/**
 * This class contains all generic functionality for executing an iteration and processing the mapping
 * 
 * @author mielvandersande
 */
public abstract class AbstractRMLProcessor implements RMLProcessor {

    /**
     * Gets the globally defined identifier-to-path map
     * @param ls the current LogicalSource
     * @return the location of the file or table
     */
    protected String getIdentifier(LogicalSource ls) {
        //TODO Change this to a more general, configurable resource management
        return RMLEngine.fileMap.get(ls.getIdentifier());
    }
    /**
     * gets the expression specified in the logical source
     * @param ls
     * @return 
     */
    protected String getSelector(LogicalSource ls) {
        return ls.getSelector();
    }

    /**
     * 
     * Process the subject map
     * 
     * @param dataset
     * @param subjectMap
     * @param node
     * @return the created subject
     */
    public Resource processSubjectMap(SesameDataSet dataset, SubjectMap subjectMap, Object node) {
        //Get the uri
        String value = processTermMap(subjectMap, node);

        if (value == null){
            return null;
        }
        
        Resource subject = new URIImpl(value);
        //Add the type triples
        Set<org.openrdf.model.URI> classIRIs = subjectMap.getClassIRIs();
        for (org.openrdf.model.URI classIRI : classIRIs) {
            dataset.add(subject, RDF.TYPE, classIRI);
        }

        return subject;
    }

    /**
     * Process any Term Map
     * 
     * @param map current term map
     * @param node current node in iteration
     * @return the resulting value
     */
    private String processTermMap(TermMap map, Object node) {
        String value = null;

        switch (map.getTermMapType()) {
            case SELECTOR_VALUED:
                //Get the expression and extract the value
                SelectorIdentifierImpl identifier = (SelectorIdentifierImpl) map.getSelectorValue();
                value = extractValueFromNode(node, identifier.toString());

                break;
            case CONSTANT_VALUED:
                //Extract the value directly from the mapping
                value = map.getConstantValue().stringValue();

                break;
            case TEMPLATE_VALUED:
                //Resolve the template
                value = map.getStringTemplate();
                Set<String> tokens = R2RMLToolkit.extractColumnNamesFromStringTemplate(value);
                for (String expression : tokens) {
                    String replacement = extractValueFromNode(node, expression);
                    if (replacement == null){
                        //if the replacement value is null, the resulting uri would be the template. Return null instead.
                        return null;
                    }
                    value = value.replaceAll("\\{" + expression + "\\}", replacement);
                }

                break;
        }
        if (value == null) {
            //Catch error? or are null values propagated to result in a triple not created
        }

        return value;
    }
    /**
     * Process a predicate object map
     * 
     * @param dataset
     * @param subject the subject from the triple
     * @param pom the predicate object map
     * @param node the current node
     */
    public void processPredicateObjectMap(SesameDataSet dataset, Resource subject, PredicateObjectMap pom, Object node) {

        Set<PredicateMap> predicateMaps = pom.getPredicateMaps();
        //Go over each predicate map
        for (PredicateMap predicateMap : predicateMaps) {
            //Get the predicate
            URI predicate = processPredicateMap(predicateMap, node);

            //Process the joins first
            Set<ReferencingObjectMap> referencingObjectMaps = pom.getReferencingObjectMaps();
            for (ReferencingObjectMap referencingObjectMap : referencingObjectMaps) {
                Set<JoinCondition> joinConditions = referencingObjectMap.getJoinConditions();

                //Build a join map where 
                //  key: the parent expression 
                //  value: the value extracted from the child
                HashMap<String, String> joinMap = new HashMap<String, String>();

                for (JoinCondition joinCondition : joinConditions) {
                    String childValue = extractValueFromNode(node, joinCondition.getChild());

                    joinMap.put(joinCondition.getParent(), childValue);
                }
                TriplesMap parentTriplesMap = referencingObjectMap.getParentTriplesMap();

                //Create the processor based on the parent triples map to perform the join
                RMLProcessorFactory factory = new ConcreteRMLProcessorFactory();
                QLTerm queryLanguage = parentTriplesMap.getLogicalSource().getQueryLanguage();
                RMLProcessor processor = factory.create(queryLanguage);

                //Execute the join with candidate s, p
                //Create a join performer to make the processor execute joins (Strategy pattern & composition)
                processor.execute(dataset, parentTriplesMap, new JoinRMLPerformer(processor, joinMap, subject, predicate));
            }

            //process the objectmaps without joins
            Set<ObjectMap> objectMaps = pom.getObjectMaps();
            for (ObjectMap objectMap : objectMaps) {
                Value object = processObjectMap(objectMap, node);

                dataset.add(subject, predicate, object);
            }

        }
    }
    /**
     * process a predicate map
     * 
     * @param predicateMap
     * @param node
     * @return the uri of the extracted predicate
     */
    private URI processPredicateMap(PredicateMap predicateMap, Object node) {
        // Get the value
        String value = processTermMap(predicateMap, node);
        //return the uri
        return new URIImpl(value);
    }
    /**
     * process an object map
     * 
     * @param objectMap
     * @param node
     * @return 
     */
    private Value processObjectMap(ObjectMap objectMap, Object node) {
        String value = processTermMap(objectMap, node);
        switch (objectMap.getTermType()) {
            case BLANK_NODE:
                return new BNodeImpl(value);

            case LITERAL:
                if (objectMap.getLanguageTag() != null) {
                    return new LiteralImpl(value, objectMap.getLanguageTag());
                } else if (objectMap.getDataType() != null) {
                    URI datatype = new URIImpl(objectMap.getDataType().getAbsoluteStringURI());
                    return new LiteralImpl(value, datatype);
                } else {
                    return new LiteralImpl(value);
                }
        }

        return new URIImpl(value);
    }
}
