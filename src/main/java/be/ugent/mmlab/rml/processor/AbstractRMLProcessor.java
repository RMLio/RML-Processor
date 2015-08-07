package be.ugent.mmlab.rml.processor;

import be.ugent.mmlab.rml.core.JoinRMLPerformer;
import be.ugent.mmlab.rml.core.RMLPerformer;
import be.ugent.mmlab.rml.core.SimpleReferencePerformer;
import be.ugent.mmlab.rml.model.RDFTerm.GraphMap;
import be.ugent.mmlab.rml.model.JoinCondition;
import be.ugent.mmlab.rml.model.LogicalSource;
import be.ugent.mmlab.rml.model.RDFTerm.ObjectMap;
import be.ugent.mmlab.rml.model.RDFTerm.PredicateMap;
import be.ugent.mmlab.rml.model.PredicateObjectMap;
import be.ugent.mmlab.rml.model.RDFTerm.ReferencingObjectMap;
import be.ugent.mmlab.rml.model.RDFTerm.SubjectMap;
import be.ugent.mmlab.rml.model.RDFTerm.TermMap;
import be.ugent.mmlab.rml.model.RDFTerm.TermType;
import static be.ugent.mmlab.rml.model.RDFTerm.TermType.BLANK_NODE;
import static be.ugent.mmlab.rml.model.RDFTerm.TermType.IRI;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.processor.concrete.ConcreteRMLProcessorFactory;
import be.ugent.mmlab.rml.input.processor.AbstractInputProcessor;
import be.ugent.mmlab.rml.input.processor.InputProcessor;
import be.ugent.mmlab.rml.model.std.StdTemplateMap;
import be.ugent.mmlab.rml.processor.termmap.TermMapProcessor;
import be.ugent.mmlab.rml.processor.termmap.TermMapProcessorFactory;
import be.ugent.mmlab.rml.processor.termmap.concrete.ConcreteTermMapFactory;
import be.ugent.mmlab.rml.sesame.RMLSesameDataSet;
import be.ugent.mmlab.rml.vocabulary.QLVocabulary.QLTerm;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.BNodeImpl;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.RDF;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RML Processor
 * 
 * This class contains all generic functionality for executing an iteration and
 * processing the mapping
 *
 * @author mielvandersande, andimou
 */
public abstract class AbstractRMLProcessor implements RMLProcessor {
    
    protected TermMapProcessor termMapProcessor ;

    /**
     * Gets the globally defined identifier-to-path map
     *
     * @param ls the current LogicalSource
     * @return the location of the file or table
     */
    
    // Log
    private static final Logger log = LoggerFactory.getLogger(AbstractRMLProcessor.class);

    /**
     * gets the expression specified in the logical source
     *
     * @param ls
     * @return
     */
    protected String getReference(LogicalSource ls) {
        return ls.getIterator();
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
    @Override
    public Resource processSubjectMap(RMLSesameDataSet dataset, SubjectMap subjectMap, Object node) {  

        //Get the uri
        TermMapProcessorFactory factory = new ConcreteTermMapFactory();
        this.termMapProcessor = 
                factory.create(subjectMap.getOwnTriplesMap().getLogicalSource().getReferenceFormulation());

        List<String> values = this.termMapProcessor.processTermMap(subjectMap, node);
        //log.info("Abstract RML Processor Graph Map" + subjectMap.getGraphMaps().toString());
        if (values == null || values.isEmpty()) 
            if(subjectMap.getTermType() != BLANK_NODE){
                log.error(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                        + "No subject was generated for " + subjectMap.toString());
                return null;
            }
            
        String value = null;
        if(subjectMap.getTermType() != BLANK_NODE){
            //Since it is the subject, more than one value is not allowed. 
            //Only return the first one. Throw exception if not?
            value = values.get(0);

            if ((value == null) || (value.equals(""))) {
                log.error(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                        + "No subject was generated for " + subjectMap.toString());
                return null;
            }
        }
        
        Resource subject ;
                
        //TODO: doublicate code from ObjectMap - they should be handled together
        switch (subjectMap.getTermType()) {
            case IRI:
                if (value != null && !value.equals("")) 
                    if (value.startsWith("www.")) {
                        value = "http://" + value;
                    }
                    subject = new URIImpl(value);
                break;
            case BLANK_NODE:
                subject = new BNodeImpl(org.apache.commons.lang.RandomStringUtils.randomAlphanumeric(10));
                break;
            default:
                subject = new URIImpl(value);
        }
        return subject;
    }
    
    @Override
    public void processSubjectTypeMap(
            RMLSesameDataSet dataset, Resource subject, SubjectMap subjectMap, Object node) {

        //Add the type triples
        Set<org.openrdf.model.URI> classIRIs = subjectMap.getClassIRIs();
        if(subject != null)
            for (org.openrdf.model.URI classIRI : classIRIs) 
                if(subjectMap.getGraphMaps().isEmpty())
                    dataset.add(subject, RDF.TYPE, classIRI);
                else
                    for (GraphMap map : subjectMap.getGraphMaps()) 
                        if (map.getConstantValue() != null) 
                            dataset.add(subject, RDF.TYPE, classIRI, new URIImpl(map.getConstantValue().toString()));
    }

    //TODO:move this to Term Map processor
    @Override
    public List<String> processTemplate(
            TermMap map, List<String> replacements, String expression) {
        List<String> values = new ArrayList<>(), validValues = new ArrayList<>();
        String template = map.getStringTemplate();

        for (int i = 0; i < replacements.size(); i++) {
            if (values.size() < (i + 1)) {
                values.add(template);
            }
            String replacement = replacements.get(i);
            if (replacement != null || !replacement.equals("")) {
                if (!replacement.isEmpty()) {
                    String temp = this.termMapProcessor.processTemplate(map, expression, template, replacement);
                    template = temp;
                    if (StdTemplateMap.extractVariablesFromStringTemplate(temp).isEmpty()) {
                        validValues.add(temp);
                    }
                }

            } else {
                log.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                        + "No suitable replacement for template " + template + ".");
                return null;
            }
        }

        return validValues;
    }
    
    //TODO:move this to Term Map processor
    @Override
    public String processTemplate(String expression, String template, String termType,
            QLTerm referenceFormulation, String replacement) {
        if (expression.contains("[")) {
            expression = expression.replaceAll("\\[", "").replaceAll("\\]", "");
            template = template.replaceAll("\\[", "").replaceAll("\\]", "");
        }
        //JSONPath expression cause problems when replacing, remove the $ first
        if ((referenceFormulation == QLTerm.JSONPATH_CLASS)
                && expression.contains("$")) {
            expression = expression.replaceAll("\\$", "");
            template = template.replaceAll("\\$", "");
        }
        try {
            if (termType.equals(TermType.IRI.toString())) {
                //TODO: replace the following with URIbuilder
                template = template.replaceAll("\\{" + Pattern.quote(expression) + "\\}",
                        URLEncoder.encode(replacement, "UTF-8")
                        .replaceAll("\\+", "%20")
                        .replaceAll("\\%21", "!")
                        .replaceAll("\\%27", "'")
                        .replaceAll("\\%28", "(")
                        .replaceAll("\\%29", ")")
                        .replaceAll("\\%7E", "~"));
            } else {
                template = template.replaceAll("\\{" + expression + "\\}", replacement);
            }
        } catch (UnsupportedEncodingException ex) {
            log.error("UnsupportedEncodingException " + ex);
        }
        return template.toString();
    }
    
    /**
     * Process a predicate object map
     *
     * @param dataset
     * @param subject   the subject from the triple
     * @param pom       the predicate object map
     * @param node      the current node
     */
    @Override
    public void processPredicateObjectMap(
            RMLSesameDataSet dataset, Resource subject, PredicateObjectMap pom, Object node, TriplesMap map) {

        Set<PredicateMap> predicateMaps = pom.getPredicateMaps();
        //Go over each predicate map
        for (PredicateMap predicateMap : predicateMaps) {
            //Get the predicate
            List<URI> predicates = processPredicateMap(predicateMap, node);
            
            URI predicate = predicates.get(0);
            
            //Process the joins first
            processPredicateObjectMap_RefObjMap(dataset, subject, predicate, pom, node, map);
            
            //process the objectmaps
            processPredicateObjectMap_ObjMap(dataset, subject, predicate, pom, node);
            
        }
    }
    
    private void processPredicateObjectMap_RefObjMap(
            RMLSesameDataSet dataset, Resource subject, URI predicate,
            PredicateObjectMap pom, Object node, TriplesMap map) {
        String template ;
        Set<ReferencingObjectMap> referencingObjectMaps = pom.getReferencingObjectMaps();
        for (ReferencingObjectMap referencingObjectMap : referencingObjectMaps) {
            Set<JoinCondition> joinConditions = referencingObjectMap.getJoinConditions();
            
            TriplesMap parentTriplesMap = referencingObjectMap.getParentTriplesMap();
            
            template = parentTriplesMap.getLogicalSource().getInputSource().getSource();

            //Create the processor based on the parent triples map to perform the join
            RMLProcessorFactory factory = new ConcreteRMLProcessorFactory();
            QLTerm referenceFormulation = parentTriplesMap.getLogicalSource().getReferenceFormulation();

            InputProcessor inputProcessor = new AbstractInputProcessor();
            InputStream input = inputProcessor.getInputStream(parentTriplesMap, template);
            
            RMLProcessor processor = factory.create(referenceFormulation);
            RMLPerformer performer = null;
            
            //different Logical Source and no Conditions
            if (joinConditions.isEmpty()
                    & !parentTriplesMap.getLogicalSource().getInputSource().getSource().equals(
                    map.getLogicalSource().getInputSource().getSource())) {
                performer = new JoinRMLPerformer(processor, subject, predicate);
                processor.execute(dataset, parentTriplesMap, performer, input);
            } 
            //same Logical Source and no Conditions
            else if (joinConditions.isEmpty()
                    & parentTriplesMap.getLogicalSource().getInputSource().getSource().equals(
                    map.getLogicalSource().getInputSource().getSource())) {
                performer = new SimpleReferencePerformer(processor, subject, predicate);
                if ((parentTriplesMap.getLogicalSource().getReferenceFormulation().toString().equals("CSV"))
                        || (parentTriplesMap.getLogicalSource().getIterator().equals(map.getLogicalSource().getIterator()))) {
                    performer.perform(node, dataset, parentTriplesMap);
                } else {
                    int end = map.getLogicalSource().getIterator().length();
                    
                    String expression = "";
                    //TODO:merge it with the performer's switch-case
                    switch (parentTriplesMap.getLogicalSource().getReferenceFormulation().toString()) {
                        case "XPath":
                            expression = parentTriplesMap.getLogicalSource().getIterator().toString().substring(end);
                            break;
                        case "JSONPath":
                            expression = parentTriplesMap.getLogicalSource().getIterator().toString().substring(end + 1);
                            break;
                        case "CSS3":
                            expression = parentTriplesMap.getLogicalSource().getIterator().toString().substring(end);
                            break;
                    }
                    processor.execute_node(dataset, expression, parentTriplesMap, performer, node, null);
                }
            } //Conditions
            else {
                //Build a join map where
                //  key: the parent expression
                //  value: the value extracted from the child
                processJoinConditions(node, performer, processor, subject, predicate, 
                        dataset, input, parentTriplesMap, joinConditions);
            }
        }
    }
    
    public void processJoinConditions(Object node, RMLPerformer performer, RMLProcessor processor, 
            Resource subject, URI predicate, RMLSesameDataSet dataset, InputStream input, 
            TriplesMap parentTriplesMap, Set<JoinCondition> joinConditions) {
        HashMap<String, String> joinMap = new HashMap<>();

        for (JoinCondition joinCondition : joinConditions) {
            List<String> childValues = termMapProcessor.extractValueFromNode(node, joinCondition.getChild());
            //Allow multiple values as child - fits with RML's definition of multiple Object Maps
            for (String childValue : childValues) {    
                joinMap.put(joinCondition.getParent(), childValue);
                if (joinMap.size() == joinConditions.size()) {
                    performer = new JoinRMLPerformer(processor, subject, predicate);
                    processor.execute(dataset, parentTriplesMap, performer, input);
                }
            }
        }
    }
    
    @Override
    public void processPredicateObjectMap_ObjMap(
            RMLSesameDataSet dataset, Resource subject, URI predicate,
            PredicateObjectMap pom, Object node) {
        Set<ObjectMap> objectMaps = pom.getObjectMaps();
        for (ObjectMap objectMap : objectMaps) {
            //Get the one or more objects returned by the object map
            List<Value> objects = processObjectMap(objectMap, node);
            if (objects != null) {
                for (Value object : objects) {
                    if (object.stringValue() != null) {
                        Set<GraphMap> graphs = pom.getGraphMaps();
                        if (graphs.isEmpty() && subject != null) {
                            dataset.add(subject, predicate, object);
                        } else {
                            for (GraphMap graph : graphs) {
                                Resource graphResource = new URIImpl(graph.getConstantValue().toString());
                                dataset.add(subject, predicate, object, graphResource);
                            }
                        }

                    }
                }
            } else {
                log.debug(Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                        + "No object created. No triple will be generated.");
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
    @Override
    public List<URI> processPredicateMap(PredicateMap predicateMap, Object node) {
        // Get the value
        
        List<String> values = this.termMapProcessor.processTermMap(predicateMap, node);
        List<URI> uris = new ArrayList<>();
        for (String value : values) {
            //TODO: add better control
            if(value.startsWith("www."))
                value = "http://" + value;
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
    public List<Value> processObjectMap(ObjectMap objectMap, Object node) {
        //A Term map returns one or more values (in case expression matches more)
        
        List<String> values = this.termMapProcessor.processTermMap(objectMap, node);
        List<Value> valueList = new ArrayList<>();
        for (String value : values) {
            valueList = applyTermType(value, valueList, objectMap);
        }
        
        return valueList;
    }
    
    //TODO: Move the following to the Term Map processor
    @Override
    public List<Value> applyTermType(String value, List<Value> valueList, TermMap termMap){
        TermType termType = termMap.getTermType();
        String languageTag = termMap.getLanguageTag();
        URI datatype = termMap.getDataType();
        
        switch (termType) {
            case IRI:
                if (value != null && !value.equals("")) {
                    if (value.startsWith("www.")) {
                        value = "http://" + value;
                    }
                    if (valueList == null) {
                        valueList = new ArrayList<Value>();
                    }
                    try {
                        new URIImpl(cleansing(value));
                    } catch (Exception e) {
                        return valueList;
                    }
                    valueList.add(new URIImpl(cleansing(value)));
                } 
                break;
            case BLANK_NODE:
                valueList.add(new BNodeImpl(cleansing(value)));
                break;
            case LITERAL:
                if (languageTag != null && !value.equals("")) {
                    if (valueList == null) {
                        valueList = new ArrayList<Value>();
                    }
                    value = cleansing(value);
                    valueList.add(new LiteralImpl(value, languageTag));
                } else if (value != null && !value.equals("") && datatype != null) {
                    valueList.add(new LiteralImpl(value, datatype));
                } else if (value != null && !value.equals("")) {
                    valueList.add(new LiteralImpl(value.trim()));
                }
        }
        return valueList;
    }
}
