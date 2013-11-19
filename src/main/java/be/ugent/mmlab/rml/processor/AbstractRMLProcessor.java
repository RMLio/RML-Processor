/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.mmlab.rml.processor;

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
 *
 * @author mielvandersande
 */
public abstract class AbstractRMLProcessor implements RMLProcessor {

    protected String getIdentifier(LogicalSource ls) {
        //TODO Change this to a more general, configurable resource management
        return RMLEngine.fileMap.get(ls.getIdentifier());
    }

    protected String getSelector(LogicalSource ls) {
        return ls.getSelector();
    }

    protected void processNode(SesameDataSet dataset, TriplesMap tm, Object node) {
        Resource subject = processSubjectMap(dataset, tm.getSubjectMap(), node);

        for (PredicateObjectMap pom : tm.getPredicateObjectMaps()) {
            processPredicateObjectMap(dataset, subject, pom, node);
        }

    }

    protected void processJoin(SesameDataSet dataset, TriplesMap tm, Object node, HashMap<String, String> joinMap, Resource subject, URI predicate) {
        Value object = processSubjectMap(dataset, tm.getSubjectMap(), node);

        for (String expr : joinMap.keySet()) {
            if (!joinMap.get(expr).equals(extractValueFromNode(node, expr))) {
                return;
            }
        }
        dataset.add(subject, predicate, object);

    }

    protected Resource processSubjectMap(SesameDataSet dataset, SubjectMap subjectMap, Object node) {

        String value = processTermMap(subjectMap, node);

        Resource subject = new URIImpl(value);

        Set<org.openrdf.model.URI> classIRIs = subjectMap.getClassIRIs();
        for (org.openrdf.model.URI classIRI : classIRIs) {
            dataset.add(subject, RDF.TYPE, classIRI);
        }

        return subject;
    }

    private String processTermMap(TermMap map, Object node) {
        String value = null;

        switch (map.getTermMapType()) {
            case SELECTOR_VALUED:
                SelectorIdentifierImpl identifier = (SelectorIdentifierImpl) map.getSelectorValue();
                value = extractValueFromNode(node, identifier.toString());

                break;
            case CONSTANT_VALUED:
                value = map.getConstantValue().stringValue();

                break;
            case TEMPLATE_VALUED:
                value = map.getStringTemplate();
                Set<String> tokens = R2RMLToolkit.extractColumnNamesFromStringTemplate(value);
                for (String expression : tokens) {
                    String replacement = extractValueFromNode(node, expression);
                    value = value.replaceAll("\\{" + expression + "\\}", replacement);
                }

                break;
        }
        if (value == null) {
            //do something to catch error
        }

        return value;
    }

    protected void processPredicateObjectMap(SesameDataSet dataset, Resource subject, PredicateObjectMap pom, Object node) {

        Set<PredicateMap> predicateMaps = pom.getPredicateMaps();
        for (PredicateMap predicateMap : predicateMaps) {
            URI predicate = processPredicateMap(predicateMap, node);

            Set<ReferencingObjectMap> referencingObjectMaps = pom.getReferencingObjectMaps();
            for (ReferencingObjectMap referencingObjectMap : referencingObjectMaps) {
                Set<JoinCondition> joinConditions = referencingObjectMap.getJoinConditions();

                HashMap<String, String> joinMap = new HashMap<String, String>();

                for (JoinCondition joinCondition : joinConditions) {
                    String childValue = extractValueFromNode(node, joinCondition.getChild());

                    joinMap.put(joinCondition.getParent(), childValue);
                }
                TriplesMap parentTriplesMap = referencingObjectMap.getParentTriplesMap();

                //Create the processor to perform the join
                RMLProcessorFactory factory = new ConcreteRMLProcessorFactory();
                QLTerm queryLanguage = parentTriplesMap.getLogicalSource().getQueryLanguage();
                RMLProcessor processor = factory.create(queryLanguage);

                //Execute the join with candidate s, p
                processor.execute(dataset, parentTriplesMap, joinMap, subject, predicate);
            }


            Set<ObjectMap> objectMaps = pom.getObjectMaps();
            for (ObjectMap objectMap : objectMaps) {
                Value object = processObjectMap(objectMap, node);

                dataset.add(subject, predicate, object);
            }

        }
    }

    private URI processPredicateMap(PredicateMap predicateMap, Object node) {
        String value = processTermMap(predicateMap, node);

        return new URIImpl(value);
    }

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

    protected abstract String extractValueFromNode(Object node, String expression);
}
