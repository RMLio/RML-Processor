package be.ugent.mmlab.rml.processor;

import be.ugent.mmlab.rml.core.ConditionalJoinRMLPerformer;
import be.ugent.mmlab.rml.core.JoinRMLPerformer;
import be.ugent.mmlab.rml.core.RMLEngine;
import be.ugent.mmlab.rml.core.RMLPerformer;
import be.ugent.mmlab.rml.model.JoinCondition;
import be.ugent.mmlab.rml.model.LogicalSource;
import be.ugent.mmlab.rml.model.ObjectMap;
import be.ugent.mmlab.rml.model.PredicateMap;
import be.ugent.mmlab.rml.model.PredicateObjectMap;
import be.ugent.mmlab.rml.model.ReferencingObjectMap;
import be.ugent.mmlab.rml.model.SubjectMap;
import be.ugent.mmlab.rml.model.TermMap;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.model.reference.ReferenceIdentifierImpl;
import be.ugent.mmlab.rml.vocabulary.Vocab.QLTerm;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.antidot.semantic.rdf.model.impl.sesame.SesameDataSet;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.core.R2RMLEngine;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.tools.R2RMLToolkit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.BNodeImpl;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.RDF;

/**
 * This class contains all generic functionality for executing an iteration and
 * processing the mapping
 *
 * @author mielvandersande, andimou
 */
public abstract class AbstractRMLProcessor implements RMLProcessor {

    /**
     * Gets the globally defined identifier-to-path map
     *
     * @param ls the current LogicalSource
     * @return the location of the file or table
     */
    // Log
    private static Log log = LogFactory.getLog(R2RMLEngine.class);

    protected String getIdentifier(LogicalSource ls) {
        //TODO Change this to a more general, configurable resource management
        return RMLEngine.fileMap.get(ls.getIdentifier());
    }

    /**
     * gets the expression specified in the logical source
     *
     * @param ls
     * @return
     */
    protected String getReference(LogicalSource ls) {
        return ls.getReference();
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
        List<String> values = processTermMap(subjectMap, node);

        if (values.isEmpty()) {
            return null;
        }

        //Since it is the subject, more than one value is not allowed. Only return the first one. Throw exception if not?
        String value = values.get(0);

        if (value == null) {
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
    private List<String> processTermMap(TermMap map, Object node) {
        List<String> value = new ArrayList<String>();

        switch (map.getTermMapType()) {
            case REFERENCE_VALUED:
                //Get the expression and extract the value
                ReferenceIdentifierImpl identifier = (ReferenceIdentifierImpl) map.getReferenceValue();
                return Arrays.asList(extractValueFromNode(node, identifier.toString()));
            case CONSTANT_VALUED:
                //Extract the value directly from the mapping
                value.add(map.getConstantValue().stringValue());
                return value;

            case TEMPLATE_VALUED:
                //Resolve the template
                String template = map.getStringTemplate();
                Set<String> tokens = R2RMLToolkit.extractColumnNamesFromStringTemplate(template);


                for (String expression : tokens) {
                    String[] replacements = extractValueFromNode(node, expression);

                    for (int i = 0; i < replacements.length; i++) {
                        if (value.size() < (i + 1)) {
                            value.add(template);
                        }

                        String replacement = replacements[i];

                        if (replacement == null) {
                            //if the replacement value is null, the resulting uri would be the template. Return null instead.
                            return null;
                        }

                        String temp = value.get(i);

                        if (expression.contains("[")) {
                            expression = expression.replaceAll("\\[", "").replaceAll("\\]", "");
                            temp = temp.replaceAll("\\[", "").replaceAll("\\]", "");
                        }
                        //JSONPath expression cause problems when replacing, remove the $ first
                        if (expression.contains("$")) {
                            expression = expression.replaceAll("\\$", "");
                            temp = temp.replaceAll("\\$", "");
                        }
                        
                        try {
                            //Use encoding UTF-8 explicit URL encode; other one is deprecated
                            temp = temp.replaceAll("\\{" + expression + "\\}", URLEncoder.encode(replacement,"UTF-8"));
                        } catch (UnsupportedEncodingException ex) {
                            log.error(ex);
                        }
                        
                        value.set(i, temp);
                    }
                }
                
                //Check if there are any placeholders left in the templates
                List<String> validValues = new ArrayList<String>();
                for (String uri : value){
                    if (R2RMLToolkit.extractColumnNamesFromStringTemplate(uri).isEmpty()){
                        validValues.add(uri);
                    }
                }

                return validValues;
        }
        return value;
    }

    /**
     * Process a predicate object map
     *
     * @param dataset
     * @param subject   the subject from the triple
     * @param pom       the predicate object map
     * @param node      the current node
     */
    public void processPredicateObjectMap(SesameDataSet dataset, Resource subject, PredicateObjectMap pom, Object node) {

        Set<PredicateMap> predicateMaps = pom.getPredicateMaps();
        //Go over each predicate map
        for (PredicateMap predicateMap : predicateMaps) {
            //Get the predicate
            List<URI> predicates = processPredicateMap(predicateMap, node);

            for (URI predicate : predicates) {
                //Process the joins first
                Set<ReferencingObjectMap> referencingObjectMaps = pom.getReferencingObjectMaps();
                for (ReferencingObjectMap referencingObjectMap : referencingObjectMaps) {
                    Set<JoinCondition> joinConditions = referencingObjectMap.getJoinConditions();

                    TriplesMap parentTriplesMap = referencingObjectMap.getParentTriplesMap();

                    //Create the processor based on the parent triples map to perform the join
                    RMLProcessorFactory factory = new ConcreteRMLProcessorFactory();
                    QLTerm queryLanguage = parentTriplesMap.getLogicalSource().getQueryLanguage();
                    RMLProcessor processor = factory.create(queryLanguage);

                    RMLPerformer performer;

                    if (joinConditions.isEmpty()) {
                        performer = new JoinRMLPerformer(processor, subject, predicate);
                    } else {
                        //Build a join map where
                        //  key: the parent expression
                        //  value: the value extracted from the child
                        HashMap<String, String> joinMap = new HashMap<String, String>();

                        for (JoinCondition joinCondition : joinConditions) {
                            String[] childValues = extractValueFromNode(node, joinCondition.getChild());

                            //MVS: Allow multiple values?
                            String childValue = childValues[0];

                            log.debug("[AbstractRMLProcessorProcessor:processPredicateObjectMap]. joinCondition child: " + joinCondition.getChild());
                            log.debug("[AbstractRMLProcessorProcessor:processPredicateObjectMap]. joinCondition parent: " + joinCondition.getParent());
                            log.debug("[AbstractRMLProcessorProcessor:processPredicateObjectMap]. childValue: " + childValue);

                            joinMap.put(joinCondition.getParent(), childValue);
                        }

                        //Execute the join with candidate s, p
                        //Create a join performer to make the processor execute joins (Strategy pattern & composition)
                        performer = new ConditionalJoinRMLPerformer(processor, joinMap, subject, predicate);
                    }
                    processor.execute(dataset, parentTriplesMap, performer);
                }

                //process the objectmaps without joins
                Set<ObjectMap> objectMaps = pom.getObjectMaps();
                for (ObjectMap objectMap : objectMaps) {
                    List<Value> objects = processObjectMap(objectMap, node);
                    for (Value object : objects) {
                        if (object != null && !object.toString().isEmpty()) {
                            dataset.add(subject, predicate, object);
                        }
                    }
                }
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
    private List<URI> processPredicateMap(PredicateMap predicateMap, Object node) {
        // Get the value
        List<String> values = processTermMap(predicateMap, node);

        List<URI> uris = new ArrayList<URI>();
        for (String value : values) {
            uris.add(new URIImpl(value));
        }
        //return the uri
        return uris;
    }

    /**
     * process an object map
     *
     * @param objectMap
     * @param node
     * @return
     */
    private List<Value> processObjectMap(ObjectMap objectMap, Object node) {
        List<String> values = processTermMap(objectMap, node);

        List<Value> valueList = new ArrayList<Value>();
        for (String value : values) {
            log.debug("[AbstractRMLProcessor:literal] value " + value);
            switch (objectMap.getTermType()) {
                case BLANK_NODE:
                    valueList.add(new BNodeImpl(value));
                    break;
                case LITERAL:
                    if (objectMap.getLanguageTag() != null) {
                        valueList.add(new LiteralImpl(value, objectMap.getLanguageTag()));
                    } else if (objectMap.getDataType() != null) {
                        URI datatype = new URIImpl(objectMap.getDataType().getAbsoluteStringURI());
                        valueList.add(new LiteralImpl(value, datatype));
                    } else if (value != null) {
                        log.debug("[AbstractRMLProcessor:literal] Literal value " + value);
                        valueList.add(new LiteralImpl(value));
                    }
            }

        }
        return valueList;
    }
}
