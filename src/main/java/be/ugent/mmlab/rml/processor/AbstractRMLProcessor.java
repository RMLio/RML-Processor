package be.ugent.mmlab.rml.processor;

import be.ugent.mmlab.rml.core.ConditionalJoinRMLPerformer;
import be.ugent.mmlab.rml.core.JoinRMLPerformer;
import be.ugent.mmlab.rml.core.RMLEngine;
import be.ugent.mmlab.rml.core.RMLPerformer;
import be.ugent.mmlab.rml.core.SimpleReferencePerformer;
import be.ugent.mmlab.rml.model.GraphMap;
import be.ugent.mmlab.rml.model.JoinCondition;
import be.ugent.mmlab.rml.model.LogicalSource;
import be.ugent.mmlab.rml.model.ObjectMap;
import be.ugent.mmlab.rml.model.PredicateMap;
import be.ugent.mmlab.rml.model.PredicateObjectMap;
import be.ugent.mmlab.rml.model.ReferencingObjectMap;
import be.ugent.mmlab.rml.model.SubjectMap;
import be.ugent.mmlab.rml.model.TermMap;
import be.ugent.mmlab.rml.model.TermType;
import static be.ugent.mmlab.rml.model.TermType.BLANK_NODE;
import static be.ugent.mmlab.rml.model.TermType.IRI;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.model.reference.ReferenceIdentifierImpl;
import be.ugent.mmlab.rml.processor.concrete.ConcreteRMLProcessorFactory;
import be.ugent.mmlab.rml.vocabulary.RMLVocabulary.QLTerm;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import net.antidot.semantic.rdf.model.impl.sesame.SesameDataSet;
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
import java.util.regex.Pattern;

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
    private static Log log = LogFactory.getLog(AbstractRMLProcessor.class);

    /*protected String getIdentifier(LogicalSource ls) {
        return RMLEngine.getFileMap().getProperty(ls.getIdentifier());
    }*/

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
    @Override
    public Resource processSubjectMap(SesameDataSet dataset, SubjectMap subjectMap, Object node) {  
        //Get the uri
        List<String> values = processTermMap(subjectMap, node);
        //log.info("Abstract RML Processor Graph Map" + subjectMap.getGraphMaps().toString());
        if (values.isEmpty()) 
            if(subjectMap.getTermType() != BLANK_NODE)
                return null;
            
        String value = null;
        if(subjectMap.getTermType() != BLANK_NODE){
            //Since it is the subject, more than one value is not allowed. 
            //Only return the first one. Throw exception if not?
            value = values.get(0);

            if ((value == null) || (value.equals(""))) 
                return null;
        }
        
        Resource subject = null;
                
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
    public void processSubjectTypeMap(SesameDataSet dataset, Resource subject, SubjectMap subjectMap, Object node) {

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

    /**
     * Process any Term Map
     *
     * @param map current term map
     * @param node current node in iteration
     * @return the resulting value
     */

    @Override
    public List<String> processTermMap(TermMap map, Object node) {
        List<String> values = new ArrayList<>(), valueList = new ArrayList<>();
        List<String> validValues = new ArrayList<>();

        switch (map.getTermMapType()) {
            case REFERENCE_VALUED:
                //Get the expression and extract the value
                ReferenceIdentifierImpl identifier = (ReferenceIdentifierImpl) map.getReferenceValue();
                values = extractValueFromNode(node, identifier.toString().trim());
                for (String value : values) {
                    if (map.getSplit() != null
                            || map.getProcess() != null
                            || map.getReplace() != null) {
                        List<String> tempValueList = postProcessTermMap(map, node, value, null);
                        if (tempValueList != null) {
                            for (String tempVal : tempValueList) {
                                //valueList = applyTermType(tempVal.stringValue(), valueList, map);
                                valueList.add(tempVal);
                            }
                        }
                    } else {
                            //valueList = applyTermType(tempVal.stringValue(), valueList, map);
                        valueList.add(value.trim().replace("\n", " "));
                    }
                        //valueList = applyTermType(value, valueList, map);
                    }
                return valueList;
                
            case CONSTANT_VALUED:
                //Extract the value directly from the mapping
                values.add(map.getConstantValue().stringValue().trim());
                return values;

            case TEMPLATE_VALUED:
                //Resolve the template
                String template = map.getStringTemplate();
                Set<String> tokens = R2RMLToolkit.extractColumnNamesFromStringTemplate(template);
                for (String expression : tokens) {
                    List<String> replacements = extractValueFromNode(node, expression);
                    if (replacements != null || replacements.isEmpty() || replacements.size() == 0) {
                        for (int i = 0; i < replacements.size(); i++) {
                            if (values.size() < (i + 1)) {
                                values.add(template);
                            }
                            String replacement = replacements.get(i);
                            if (replacement != null || !replacement.equals("")) {
                                if (map.getSplit() != null || map.getProcess() != null || map.getReplace() != null) {
                                    List<String> list = postProcessTermMap(map, node, replacement, null);
                                    for (String val : list) {
                                        String temp = processTemplate(map, expression, template, val);
                                        if (R2RMLToolkit.extractColumnNamesFromStringTemplate(temp).isEmpty()) {
                                            validValues.add(temp);
                                        }
                                    }
                                } else {
                                    if (replacement != null & !replacement.isEmpty()) {
                                        String temp = processTemplate(map, expression, template, replacement);
                                        template = temp;
                                        if (R2RMLToolkit.extractColumnNamesFromStringTemplate(temp).isEmpty()) {
                                            validValues.add(temp);
                                        }
                                    }

                                }
                            } else {
                                log.debug("No suitable replacement for template " + template + ".");
                                return null;
                            }
                        }
                    } else {
                        log.debug("No replacements found for template " + template + ".");
                        return null;
                    }
                }

                //Check if there are any placeholders left in the templates and remove uris that are not
//                List<String> validValues = new ArrayList<>();
                for (String uri : values) {
                    if (R2RMLToolkit.extractColumnNamesFromStringTemplate(uri).isEmpty()) {
                        validValues.add(uri);
                    }
                }
                return validValues;

            default:
                return values;
        }

        //return value;
    }
    
    public String processTemplate(TermMap map, String expression, String template, String replacement) {
        if (expression.contains("[")) {
            expression = expression.replaceAll("\\[", "").replaceAll("\\]", "");
            template = template.replaceAll("\\[", "").replaceAll("\\]", "");
        }
        //JSONPath expression cause problems when replacing, remove the $ first
        if ((map.getOwnTriplesMap().getLogicalSource().getReferenceFormulation() == QLTerm.JSONPATH_CLASS)
                && expression.contains("$")) {
            expression = expression.replaceAll("\\$", "");
            template = template.replaceAll("\\$", "");
        }
        try {
            if (map.getTermType().toString().equals(TermType.IRI.toString())) {
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
            //Use encoding UTF-8 explicit URL encode; other one is deprecated 
            //temp.replaceAll(expression, "body div.CEURTOC h2+ul li a&<a href=\\\"([\\\\w\\\\d]*.\\\\w*)\\\">([\\\\w\\\\d]*)</a>#$1");
            //temp = temp.replaceAll("\\{" + Pattern.quote(expression) + "\\}", ((replacement.startsWith("http")||replacement.startsWith("ftp")) ? replacement.toString() : URLEncoder.encode(replacement,"UTF-8")));

        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(AbstractRMLProcessor.class.getName()).log(Level.SEVERE, null, ex);
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
    public void processPredicateObjectMap(SesameDataSet dataset, Resource subject, PredicateObjectMap pom, Object node, TriplesMap map) {

        Set<PredicateMap> predicateMaps = pom.getPredicateMaps();
        //Go over each predicate map
        for (PredicateMap predicateMap : predicateMaps) {
            //Get the predicate
            List<URI> predicates = processPredicateMap(predicateMap, node);
            //TODO:verify that it retrieves all predicates

            for (URI predicate : predicates) {
                //Process the joins first
                Set<ReferencingObjectMap> referencingObjectMaps = pom.getReferencingObjectMaps();
                for (ReferencingObjectMap referencingObjectMap : referencingObjectMaps) {
                    Set<JoinCondition> joinConditions = referencingObjectMap.getJoinConditions();
                    
                    TriplesMap parentTriplesMap = referencingObjectMap.getParentTriplesMap();
                    
                    //Create the processor based on the parent triples map to perform the join
                    RMLProcessorFactory factory = new ConcreteRMLProcessorFactory();
                    QLTerm referenceFormulation = parentTriplesMap.getLogicalSource().getReferenceFormulation();
                    String source = parentTriplesMap.getLogicalSource().getIdentifier();

                    InputStream input = null;
                    try {
                        input = RMLEngine.getInputStream(source, parentTriplesMap);
                    } catch (MalformedURLException ex) {
                        Logger.getLogger(AbstractRMLProcessor.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(AbstractRMLProcessor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    RMLProcessor processor = factory.create(referenceFormulation);

                    RMLPerformer performer ;
                    //different Logical Source and no Conditions
                    if (joinConditions.isEmpty() 
                            & !parentTriplesMap.getLogicalSource().getIdentifier().equals(map.getLogicalSource().getIdentifier())) {
                        performer = new JoinRMLPerformer(processor, subject, predicate);
                        processor.execute(dataset, parentTriplesMap, performer, input);
                    } 
                    //same Logical Source and no Conditions
                    else if (joinConditions.isEmpty() 
                            & parentTriplesMap.getLogicalSource().getIdentifier().equals(map.getLogicalSource().getIdentifier())){
                        performer = new SimpleReferencePerformer(processor, subject, predicate);
                        if((parentTriplesMap.getLogicalSource().getReferenceFormulation().toString().equals("CSV")) 
                                || (parentTriplesMap.getLogicalSource().getReference().equals(map.getLogicalSource().getReference()))){
                            performer.perform(node, dataset, parentTriplesMap);
                        }
                        else{
                            int end = map.getLogicalSource().getReference().length();
                            //log.info("RML:AbstractRMLProcessor " + parentTriplesMap.getLogicalSource().getReference().toString());
                            String expression = "";
                            switch (parentTriplesMap.getLogicalSource().getReferenceFormulation().toString()) {
                                case "XPath":
                                    expression = parentTriplesMap.getLogicalSource().getReference().toString().substring(end);
                                    break;
                                case "JSONPath":
                                    expression = parentTriplesMap.getLogicalSource().getReference().toString().substring(end+1);
                                    break;
                                case "CSS3":
                                    expression = parentTriplesMap.getLogicalSource().getReference().toString().substring(end);
                                    break;
                            }
                            processor.execute_node(dataset, expression, parentTriplesMap, performer, node, null);
                        }
                    }
                    //Conditions
                    else {
                        //Build a join map where
                        //  key: the parent expression
                        //  value: the value extracted from the child
                        HashMap<String, String> joinMap = new HashMap<>();
                        
                        for (JoinCondition joinCondition : joinConditions) {
                            List<String> childValues = extractValueFromNode(node, joinCondition.getChild());
                            
                            //Allow multiple values as child - fits with RML's definition of multiple Object Maps
                            for(String childValue : childValues){
                                joinMap.put(joinCondition.getParent(), childValue);  
                                if(joinMap.size() == joinConditions.size()){
                                    performer = new ConditionalJoinRMLPerformer(processor, joinMap, subject, predicate);
                                    processor.execute(dataset, parentTriplesMap, performer, input);
                                }
                            }
                        }
                    }

                }

                //process the objectmaps
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
                    }
                    else
                        log.debug("No object created. No triple will be generated.");
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
        List<String> values = processTermMap(objectMap, node);
        List<Value> valueList = new ArrayList<>();
        for (String value : values) {
            valueList = applyTermType(value, valueList, objectMap);
            /*if (objectMap.getSplit() != null
                    || objectMap.getProcess() != null
                    || objectMap.getReplace() != null) {
                List<Value> tempValueList = postProcessTermMap(objectMap, node, value, null);
                if(tempValueList != null)
                for (Value tempVal : tempValueList) {
                    valueList = applyTermType(tempVal.stringValue(), valueList, objectMap);
                }
            } else {
                valueList = applyTermType(value, valueList, objectMap);
            }*/
        }
        
        return valueList;
    }
    
    private List<Value> applyTermType(String value, List<Value> valueList, TermMap termMap){
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
    
    public List<String> postProcessTermMap(
            TermMap termMap, Object node, String value, List<Value> valueList) {
        String[] list ;
        String split = termMap.getSplit();
        String process = termMap.getProcess();
        String replace = termMap.getReplace();
        List<String> stringList = null;

        if (split != null) {
            list = value.split(split);
            if (replace != null && list != null) {
                Integer replaceOrder = Integer.parseInt(replace.substring(1));

                if ((replaceOrder - 1) > 0 && (replaceOrder - 1) < list.length) {
                    value = list[replaceOrder - 1];
                } else {
                    value = null;
                }
                if (stringList == null) {
                    //valueList = new ArrayList<Value>();
                    stringList = new ArrayList<String>();
                }
                if (value != null && !value.equals("")) {
                    //valueList.add(new LiteralImpl(cleansing(value)));
                    stringList.add(cleansing(value));
                }
            }
            else {
                for (String item : list) {
                    if (stringList == null) {
                        //valueList = new ArrayList<Value>();
                        stringList = new ArrayList<String>();
                    }
                    //valueList.add(new LiteralImpl(cleansing(item)));
                    stringList.add(cleansing(item));
                }
            }
        }

        if (process != null && replace != null) {
            Pattern replacement = Pattern.compile(process);
            Matcher matcher = replacement.matcher(value);
            if (matcher.find()) {
                if (stringList == null) {
                    //valueList = new ArrayList<Value>();
                    stringList = new ArrayList<String>();
                }
                value = matcher.replaceAll(replace);
                if (value != null && !value.equals("")) {
                    //valueList.add(new LiteralImpl(cleansing(value)));
                    stringList.add(cleansing(value));
                }
            }
            else{
                //valueList.add(new LiteralImpl(cleansing(value)));
                //stringList.add(cleansing(value));
                log.debug("no match found for " + process);
            }
        }
        return stringList;
    }
    
    @Override
    public List<String> postProcessLogicalSource(String split, Object node) {
        String[] list, value ;
        ArrayList<String> valueList = null;

        if (split != null) {
            list = node.toString().split(split);

            for (String item : list) {
                if (valueList == null) {
                    valueList = new ArrayList<String>();
                }
                valueList.add(cleansing(item));
            }
            
        }

        return valueList;
    }
}
