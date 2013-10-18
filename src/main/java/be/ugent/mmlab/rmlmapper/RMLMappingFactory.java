/* 
 * Copyright 2011 Antidot opensource@antidot.net
 * https://github.com/antidot/db2triples
 * 
 * DB2Triples is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU General Public License as 
 * published by the Free Software Foundation; either version 2 of 
 * the License, or (at your option) any later version.
 * 
 * DB2Triples is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * *************************************************************************
 *
 * R2RML : R2RML Mapping Factory absctract class
 *
 * Factory responsible of R2RML Mapping generation.
 *
 *
 ***************************************************************************
 */
package be.ugent.mmlab.rmlmapper;

import be.ugent.mmlab.rmlmapper.RMLVocabulary.RMLTerm;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.antidot.semantic.rdf.model.impl.sesame.SesameDataSet;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.core.R2RMLVocabulary;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.core.R2RMLVocabulary.R2RMLTerm;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.InvalidR2RMLStructureException;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.InvalidR2RMLSyntaxException;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.R2RMLDataError;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.model.GraphMap;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.model.JoinCondition;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.model.LogicalTable;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.model.ObjectMap;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.model.PredicateMap;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.model.PredicateObjectMap;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.model.R2RMLMapping;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.model.R2RMLView;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.model.ReferencingObjectMap;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.model.StdGraphMap;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.model.StdJoinCondition;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.model.StdObjectMap;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.model.StdPredicateMap;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.model.StdPredicateObjectMap;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.model.StdR2RMLView;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.model.StdReferencingObjectMap;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.model.StdSQLBaseTableOrView;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.model.StdSubjectMap;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.model.StdTriplesMap;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.model.SubjectMap;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.model.TriplesMap;
import net.antidot.sql.model.db.ColumnIdentifier;
import net.antidot.sql.model.db.ColumnIdentifierImpl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openrdf.model.BNode;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;

public abstract class RMLMappingFactory {

    // Log
    private static Log log = LogFactory.getLog(RMLMappingFactory.class);
    // Value factory
    private static ValueFactory vf = new ValueFactoryImpl();

    /**
     * Extract R2RML Mapping object from a R2RML file written with Turtle
     * syntax. This syntax is recommanded in R2RML : RDB to RDF Mapping Language
     * (W3C Working Draft 20 September 2011)"An R2RML mapping document is any
     * document written in the Turtle [TURTLE] RDF syntax that encodes an R2RML
     * mapping graph."
     *
     * Important : The R2RML vocabulary also includes the following R2RML
     * classes, which represent various R2RML mapping constructs. Using these
     * classes is optional in a mapping graph. The applicable class of a
     * resource can always be inferred from its properties. Consequently, in
     * order to identify each triple type, a rule will be used to extract the
     * applicable class of a resource.
     *
     * @param fileToR2RMLFile
     * @return
     * @throws InvalidR2RMLSyntaxException
     * @throws InvalidR2RMLStructureException
     * @throws R2RMLDataError
     * @throws IOException
     * @throws RDFParseException
     * @throws RepositoryException
     */
    public static R2RMLMapping extractR2RMLMapping(String fileToR2RMLFile)
            throws InvalidR2RMLStructureException, InvalidR2RMLSyntaxException,
            R2RMLDataError, RepositoryException, RDFParseException, IOException {
        // Load RDF data from R2RML Mapping document
        SesameDataSet r2rmlMappingGraph = new SesameDataSet();
        r2rmlMappingGraph.loadDataFromFile(fileToR2RMLFile, RDFFormat.TURTLE);
        log.debug("[R2RMLMappingFactory:extractR2RMLMapping] Number of R2RML triples in file "
                + fileToR2RMLFile + " : " + r2rmlMappingGraph.getSize());
        // Transform RDF with replacement shortcuts
        replaceShortcuts(r2rmlMappingGraph);
        // Run few tests to help user in its RDF syntax
        launchPreChecks(r2rmlMappingGraph);
        // Construct R2RML Mapping object
        Map<Resource, TriplesMap> triplesMapResources = extractTripleMapResources(r2rmlMappingGraph);

        log.debug("[R2RMLMappingFactory:extractR2RMLMapping] Number of R2RML triples with "
                + " type "
                + R2RMLTerm.TRIPLES_MAP_CLASS
                + " in file "
                + fileToR2RMLFile + " : " + triplesMapResources.size());
        // Fill each triplesMap object
        for (Resource triplesMapResource : triplesMapResources.keySet()) // Extract each triplesMap
        {
            extractTriplesMap(r2rmlMappingGraph, triplesMapResource,
                    triplesMapResources);
        }
        // Generate R2RMLMapping object
        R2RMLMapping result = new R2RMLMapping(triplesMapResources.values());
        return result;
    }

    /**
     * Constant-valued term maps can be expressed more concisely using the
     * constant shortcut properties rr:subject, rr:predicate, rr:object and
     * rr:graph. Occurrances of these properties must be treated exactly as if
     * the following triples were present in the mapping graph instead.
     *
     * @param r2rmlMappingGraph
     */
    private static void replaceShortcuts(SesameDataSet r2rmlMappingGraph) {
        Map<URI, URI> shortcutPredicates = new HashMap<URI, URI>();
        shortcutPredicates.put(
                vf.createURI(R2RMLVocabulary.R2RML_NAMESPACE
                + R2RMLTerm.SUBJECT),
                vf.createURI(R2RMLVocabulary.R2RML_NAMESPACE
                + R2RMLTerm.SUBJECT_MAP));
        shortcutPredicates.put(
                vf.createURI(R2RMLVocabulary.R2RML_NAMESPACE
                + R2RMLTerm.PREDICATE),
                vf.createURI(R2RMLVocabulary.R2RML_NAMESPACE
                + R2RMLTerm.PREDICATE_MAP));
        shortcutPredicates.put(vf.createURI(R2RMLVocabulary.R2RML_NAMESPACE
                + R2RMLTerm.OBJECT), vf
                .createURI(R2RMLVocabulary.R2RML_NAMESPACE
                + R2RMLTerm.OBJECT_MAP));
        shortcutPredicates
                .put(vf.createURI(R2RMLVocabulary.R2RML_NAMESPACE
                + R2RMLTerm.GRAPH),
                vf.createURI(R2RMLVocabulary.R2RML_NAMESPACE
                + R2RMLTerm.GRAPH_MAP));
        for (URI u : shortcutPredicates.keySet()) {
            List<Statement> shortcutTriples = r2rmlMappingGraph.tuplePattern(
                    null, u, null);
            log.debug("[R2RMLMappingFactory:replaceShortcuts] Number of R2RML shortcuts found "
                    + "for "
                    + u.getLocalName()
                    + " : "
                    + shortcutTriples.size());
            for (Statement shortcutTriple : shortcutTriples) {
                r2rmlMappingGraph.remove(shortcutTriple.getSubject(),
                        shortcutTriple.getPredicate(),
                        shortcutTriple.getObject());
                BNode blankMap = vf.createBNode();

                URI pMap = vf.createURI(shortcutPredicates.get(u).toString());
                URI pConstant = vf.createURI(R2RMLVocabulary.R2RML_NAMESPACE
                        + R2RMLTerm.CONSTANT);
                r2rmlMappingGraph.add(shortcutTriple.getSubject(), pMap,
                        blankMap);
                r2rmlMappingGraph.add(blankMap, pConstant,
                        shortcutTriple.getObject());
            }
        }
    }

    /**
     * Construct TriplesMap objects rule. A triples map is represented by a
     * resource that references the following other resources : - It must have
     * exactly one subject map * using the rr:subjectMap property.
     *
     * @param r2rmlMappingGraph
     * @return
     * @throws InvalidR2RMLStructureException
     */
    private static Map<Resource, TriplesMap> extractTripleMapResources(
            SesameDataSet r2rmlMappingGraph)
            throws InvalidR2RMLStructureException {
        // A triples map is represented by a resource that references the
        // following other resources :
        // - It must have exactly one subject map
        Map<Resource, TriplesMap> triplesMapResources = new HashMap<Resource, TriplesMap>();
        URI p = r2rmlMappingGraph.URIref(R2RMLVocabulary.R2RML_NAMESPACE
                + R2RMLVocabulary.R2RMLTerm.SUBJECT_MAP);
        List<Statement> statements = r2rmlMappingGraph.tuplePattern(null, p,
                null);
        if (statements.isEmpty()) {
            log.warn("[R2RMLMappingFactory:extractR2RMLMapping] No subject statement found. Exit...");
        } /*
         * throw new InvalidR2RMLStructureException(
         * "[R2RMLMappingFactory:extractR2RMLMapping]" +
         * " One subject statement is required.");
         */ else // No subject map, Many shortcuts subjects
        {
            for (Statement s : statements) {
                List<Statement> otherStatements = r2rmlMappingGraph
                        .tuplePattern(s.getSubject(), p, null);
                if (otherStatements.size() > 1) {
                    throw new InvalidR2RMLStructureException(
                            "[R2RMLMappingFactory:extractR2RMLMapping] "
                            + s.getSubject() + " has many subjectMap "
                            + "(or subject) but only one is required.");
                } else // First initialization of triples map : stored to link them
                // with referencing objects
                {
                    triplesMapResources.put(s.getSubject(), new StdTriplesMap(
                            null, null, null, s.getSubject().stringValue()));
                }
            }
        }
        return triplesMapResources;
    }

    private static void launchPreChecks(SesameDataSet r2rmlMappingGraph)
            throws InvalidR2RMLStructureException {
        // Pre-check 1 : test if a triplesMap with predicateObject map exists
        // without subject map
        URI p = r2rmlMappingGraph.URIref(R2RMLVocabulary.R2RML_NAMESPACE
                + R2RMLTerm.PREDICATE_OBJECT_MAP);
        List<Statement> statements = r2rmlMappingGraph.tuplePattern(null, p,
                null);
        for (Statement s : statements) {
            p = r2rmlMappingGraph.URIref(R2RMLVocabulary.R2RML_NAMESPACE
                    + R2RMLTerm.SUBJECT_MAP);
            List<Statement> otherStatements = r2rmlMappingGraph.tuplePattern(
                    s.getSubject(), p, null);
            if (otherStatements.isEmpty()) {
                throw new InvalidR2RMLStructureException(
                        "[R2RMLMappingFactory:launchPreChecks] You have a triples map without subject map : "
                        + s.getSubject().stringValue() + ".");
            }
        }
    }

    /**
     * Extract triplesMap contents.
     *
     * @param triplesMap
     * @param r2rmlMappingGraph
     * @param triplesMapSubject
     * @param triplesMapResources
     * @param storedTriplesMaps
     * @throws InvalidR2RMLStructureException
     * @throws InvalidR2RMLSyntaxException
     * @throws R2RMLDataError
     */
    private static void extractTriplesMap(SesameDataSet r2rmlMappingGraph,
            Resource triplesMapSubject,
            Map<Resource, TriplesMap> triplesMapResources)
            throws InvalidR2RMLStructureException, InvalidR2RMLSyntaxException,
            R2RMLDataError {

        if (log.isDebugEnabled()) {
            log.debug("[R2RMLMappingFactory:extractTriplesMap] Extract TriplesMap subject : "
                    + triplesMapSubject.stringValue());
        }

        TriplesMap result = triplesMapResources.get(triplesMapSubject);

        // Extract TriplesMap properties
        LogicalTable logicalTable = extractLogicalTable(r2rmlMappingGraph,
                triplesMapSubject);

        // Extract subject
        // Create a graph maps storage to save all met graph uri during parsing.
        Set<GraphMap> graphMaps = new HashSet<GraphMap>();
        log.debug("[R2RMLMappingFactory:extractTriplesMap] Current number of created graphMaps : "
                + graphMaps.size());
        SubjectMap subjectMap = extractSubjectMap(r2rmlMappingGraph,
                triplesMapSubject, graphMaps, result);
        log.debug("[R2RMLMappingFactory:extractTriplesMap] Current number of created graphMaps : "
                + graphMaps.size());
        // Extract predicate-object maps
        Set<PredicateObjectMap> predicateObjectMaps = extractPredicateObjectMaps(
                r2rmlMappingGraph, triplesMapSubject, graphMaps, result,
                triplesMapResources);
        log.debug("[R2RMLMappingFactory:extractTriplesMap] Current number of created graphMaps : "
                + graphMaps.size());
        // Fill triplesMap
        for (PredicateObjectMap predicateObjectMap : predicateObjectMaps) {
            result.addPredicateObjectMap(predicateObjectMap);
        }
        result.setLogicalTable(logicalTable);
        result.setSubjectMap(subjectMap);
        log.debug("[R2RMLMappingFactory:extractTriplesMap] Extract of TriplesMap subject : "
                + triplesMapSubject.stringValue() + " done.");
    }

    /*
     * Still needs changing!!!!
     */
    private static Set<PredicateObjectMap> extractPredicateObjectMaps(
            SesameDataSet r2rmlMappingGraph, Resource triplesMapSubject,
            Set<GraphMap> graphMaps, TriplesMap result,
            Map<Resource, TriplesMap> triplesMapResources)
            throws InvalidR2RMLStructureException, R2RMLDataError,
            InvalidR2RMLSyntaxException {
        log.debug("[R2RMLMappingFactory:extractPredicateObjectMaps] Extract predicate-object maps...");
        // Extract predicate-object maps
        URI p = r2rmlMappingGraph.URIref(R2RMLVocabulary.R2RML_NAMESPACE
                + R2RMLTerm.PREDICATE_OBJECT_MAP);
        List<Statement> statements = r2rmlMappingGraph.tuplePattern(
                triplesMapSubject, p, null);
        Set<PredicateObjectMap> predicateObjectMaps = new HashSet<PredicateObjectMap>();
        try {
            for (Statement statement : statements) {
                PredicateObjectMap predicateObjectMap = extractPredicateObjectMap(
                        r2rmlMappingGraph, (Resource) statement.getObject(),
                        graphMaps, triplesMapResources);
                // Add own tripleMap to predicateObjectMap
                predicateObjectMap.setOwnTriplesMap(result);
                predicateObjectMaps.add(predicateObjectMap);
            }
        } catch (ClassCastException e) {
            throw new InvalidR2RMLStructureException(
                    "[R2RMLMappingFactory:extractPredicateObjectMaps] "
                    + "A resource was expected in object of predicateObjectMap of "
                    + triplesMapSubject.stringValue());
        }
        log.debug("[R2RMLMappingFactory:extractPredicateObjectMaps] Number of extracted predicate-object maps : "
                + predicateObjectMaps.size());
        return predicateObjectMaps;
    }
    /*
     * Still needs changing
     */

    private static PredicateObjectMap extractPredicateObjectMap(
            SesameDataSet r2rmlMappingGraph,
            Resource predicateObject,
            Set<GraphMap> savedGraphMaps,
            Map<Resource, TriplesMap> triplesMapResources)
            throws InvalidR2RMLStructureException, R2RMLDataError,
            InvalidR2RMLSyntaxException {
        log.debug("[R2RMLMappingFactory:extractPredicateObjectMap] Extract predicate-object map..");
        // Extract predicate maps
        URI p = r2rmlMappingGraph.URIref(R2RMLVocabulary.R2RML_NAMESPACE
                + R2RMLTerm.PREDICATE_MAP);
        List<Statement> statements = r2rmlMappingGraph.tuplePattern(
                predicateObject, p, null);
        if (statements.size() < 1) {
            throw new InvalidR2RMLStructureException(
                    "[R2RMLMappingFactory:extractSubjectMap] "
                    + predicateObject.stringValue()
                    + " has no predicate map defined : one or more is required.");
        }
        Set<PredicateMap> predicateMaps = new HashSet<PredicateMap>();
        try {
            for (Statement statement : statements) {
                PredicateMap predicateMap = extractPredicateMap(
                        r2rmlMappingGraph, (Resource) statement.getObject(),
                        savedGraphMaps);
                predicateMaps.add(predicateMap);
            }
        } catch (ClassCastException e) {
            throw new InvalidR2RMLStructureException(
                    "[R2RMLMappingFactory:extractPredicateObjectMaps] "
                    + "A resource was expected in object of predicateMap of "
                    + predicateObject.stringValue());
        }
        // Extract object maps
        URI o = r2rmlMappingGraph.URIref(R2RMLVocabulary.R2RML_NAMESPACE
                + R2RMLTerm.OBJECT_MAP);
        statements = r2rmlMappingGraph.tuplePattern(predicateObject, o, null);
        if (statements.size() < 1) {
            throw new InvalidR2RMLStructureException(
                    "[R2RMLMappingFactory:extractPredicateObjectMap] "
                    + predicateObject.stringValue()
                    + " has no object map defined : one or more is required.");
        }
        Set<ObjectMap> objectMaps = new HashSet<ObjectMap>();
        Set<ReferencingObjectMap> refObjectMaps = new HashSet<ReferencingObjectMap>();
        try {
            for (Statement statement : statements) {
                log.debug("[R2RMLMappingFactory:extractPredicateObjectMap] Try to extract object map..");
                ReferencingObjectMap refObjectMap = extractReferencingObjectMap(
                        r2rmlMappingGraph, (Resource) statement.getObject(),
                        savedGraphMaps, triplesMapResources);
                if (refObjectMap != null) {
                    refObjectMaps.add(refObjectMap);
                    // Not a simple object map, skip to next.
                    continue;
                }
                ObjectMap objectMap = extractObjectMap(r2rmlMappingGraph,
                        (Resource) statement.getObject(), savedGraphMaps);
                if (objectMap != null) {
                    objectMaps.add(objectMap);
                }
            }
        } catch (ClassCastException e) {
            throw new InvalidR2RMLStructureException(
                    "[R2RMLMappingFactory:extractPredicateObjectMaps] "
                    + "A resource was expected in object of objectMap of "
                    + predicateObject.stringValue());
        }
        PredicateObjectMap predicateObjectMap = new StdPredicateObjectMap(
                predicateMaps, objectMaps, refObjectMaps);
        // Add graphMaps
        Set<Value> graphMapValues = extractValuesFromResource(
                r2rmlMappingGraph, predicateObject, R2RMLTerm.GRAPH_MAP);
        Set<GraphMap> graphMaps = new HashSet<GraphMap>();
        if (graphMapValues != null) {
            for (Value graphMap : graphMapValues) {
                // Create associated graphMap if it has not already created
                boolean found = false;
                GraphMap graphMapFound = null;
                /*
                 * for (GraphMap savedGraphMap : savedGraphMaps) if
                 * (savedGraphMap.getGraph().equals(graphMap)) { found = true;
                 * graphMapFound = savedGraphMap; }
                 */
                if (found) {
                    graphMaps.add(graphMapFound);
                } else {
                    GraphMap newGraphMap = extractGraphMap(r2rmlMappingGraph,
                            (Resource) graphMap);
                    savedGraphMaps.add(newGraphMap);
                    graphMaps.add(newGraphMap);
                }
            }
        }
        predicateObjectMap.setGraphMaps(graphMaps);
        log.debug("[R2RMLMappingFactory:extractPredicateObjectMap] Extract predicate-object map done.");
        return predicateObjectMap;
    }
    /*
     * Still needs changing
     */

    private static ReferencingObjectMap extractReferencingObjectMap(
            SesameDataSet r2rmlMappingGraph, Resource object,
            Set<GraphMap> graphMaps,
            Map<Resource, TriplesMap> triplesMapResources)
            throws InvalidR2RMLStructureException, InvalidR2RMLSyntaxException {
        log.debug("[R2RMLMappingFactory:extractReferencingObjectMap] Extract referencing object map..");
        URI parentTriplesMap = (URI) extractValueFromTermMap(r2rmlMappingGraph,
                object, R2RMLTerm.PARENT_TRIPLES_MAP);
        Set<JoinCondition> joinConditions = extractJoinConditions(
                r2rmlMappingGraph, object);
        if (parentTriplesMap == null && !joinConditions.isEmpty()) {
            throw new InvalidR2RMLStructureException(
                    "[R2RMLMappingFactory:extractReferencingObjectMap] "
                    + object.stringValue()
                    + " has no parentTriplesMap map defined whereas one or more joinConditions exist"
                    + " : exactly one parentTripleMap is required.");
        }
        if (parentTriplesMap == null && joinConditions.isEmpty()) {
            log.debug("[R2RMLMappingFactory:extractReferencingObjectMap] This object map is not a referencing object map.");
            return null;
        }
        // Extract parent
        boolean contains = false;
        TriplesMap parent = null;
        for (Resource triplesMapResource : triplesMapResources.keySet()) {
            if (triplesMapResource.stringValue().equals(
                    parentTriplesMap.stringValue())) {
                contains = true;
                parent = triplesMapResources.get(triplesMapResource);
                log.debug("[R2RMLMappingFactory:extractReferencingObjectMap] Parent triples map found : "
                        + triplesMapResource.stringValue());
                break;
            }
        }
        if (!contains) {
            throw new InvalidR2RMLStructureException(
                    "[R2RMLMappingFactory:extractReferencingObjectMap] "
                    + object.stringValue()
                    + " reference to parent triples maps is broken : "
                    + parentTriplesMap.stringValue() + " not found.");
        }
        // Link between this reerencing object and its triplesMap parent will be
        // performed
        // at the end f treatment.
        ReferencingObjectMap refObjectMap = new StdReferencingObjectMap(null,
                parent, joinConditions);
        log.debug("[R2RMLMappingFactory:extractReferencingObjectMap] Extract referencing object map done.");
        return refObjectMap;
    }
    /*
     * Still needs changing
     */

    private static Set<JoinCondition> extractJoinConditions(
            SesameDataSet r2rmlMappingGraph, Resource object)
            throws InvalidR2RMLStructureException, InvalidR2RMLSyntaxException {
        log.debug("[R2RMLMappingFactory:extractJoinConditions] Extract join conditions..");
        Set<JoinCondition> result = new HashSet<JoinCondition>();
        // Extract predicate-object maps
        URI p = r2rmlMappingGraph.URIref(R2RMLVocabulary.R2RML_NAMESPACE
                + R2RMLTerm.JOIN_CONDITION);
        List<Statement> statements = r2rmlMappingGraph.tuplePattern(object, p,
                null);
        try {
            for (Statement statement : statements) {
                Resource jc = (Resource) statement.getObject();
                String child = extractLiteralFromTermMap(r2rmlMappingGraph, jc,
                        R2RMLTerm.CHILD);
                String parent = extractLiteralFromTermMap(r2rmlMappingGraph,
                        jc, R2RMLTerm.PARENT);
                if (parent == null || child == null) {
                    throw new InvalidR2RMLStructureException(
                            "[R2RMLMappingFactory:extractReferencingObjectMap] "
                            + object.stringValue()
                            + " must have exactly two properties child and parent. ");
                }
                result.add(new StdJoinCondition(child, parent));
            }
        } catch (ClassCastException e) {
            throw new InvalidR2RMLStructureException(
                    "[R2RMLMappingFactory:extractJoinConditions] "
                    + "A resource was expected in object of predicateMap of "
                    + object.stringValue());
        }
        log.debug("[R2RMLMappingFactory:extractJoinConditions] Extract join conditions done.");
        return result;
    }
    /*
     * Still needs changing
     */

    private static ObjectMap extractObjectMap(SesameDataSet r2rmlMappingGraph,
            Resource object, Set<GraphMap> graphMaps)
            throws InvalidR2RMLStructureException, R2RMLDataError,
            InvalidR2RMLSyntaxException {
        log.debug("[R2RMLMappingFactory:extractObjectMap] Extract object map..");
        // Extract object maps properties
        Value constantValue = extractValueFromTermMap(r2rmlMappingGraph,
                object, R2RMLTerm.CONSTANT);
        String stringTemplate = extractLiteralFromTermMap(r2rmlMappingGraph,
                object, R2RMLTerm.TEMPLATE);
        String languageTag = extractLiteralFromTermMap(r2rmlMappingGraph,
                object, R2RMLTerm.LANGUAGE);
        URI termType = (URI) extractValueFromTermMap(r2rmlMappingGraph, object,
                R2RMLTerm.TERM_TYPE);
        URI dataType = (URI) extractValueFromTermMap(r2rmlMappingGraph, object,
                R2RMLTerm.DATATYPE);
        String inverseExpression = extractLiteralFromTermMap(r2rmlMappingGraph,
                object, R2RMLTerm.INVERSE_EXPRESSION);
        String columnValueStr = extractLiteralFromTermMap(r2rmlMappingGraph,
                object, R2RMLTerm.COLUMN);
        
        String selectorValueStr = extractLiteralFromTermMap(r2rmlMappingGraph,
                object, RMLTerm.SELECTOR);
        
        ColumnIdentifier columnValue = null;
        if (selectorValueStr != null){
            columnValue = SelectorIdentifierImpl.buildFromR2RMLConfigFile(selectorValueStr);
        } else {
            columnValue = ColumnIdentifierImpl.buildFromR2RMLConfigFile(columnValueStr);
        }

        
        StdObjectMap result = new StdObjectMap(null, constantValue, dataType,
                languageTag, stringTemplate, termType, inverseExpression,
                columnValue);
        log.debug("[R2RMLMappingFactory:extractObjectMap] Extract object map done.");
        return result;
    }

    private static PredicateMap extractPredicateMap(
            SesameDataSet r2rmlMappingGraph, Resource object,
            Set<GraphMap> graphMaps) throws InvalidR2RMLStructureException,
            R2RMLDataError, InvalidR2RMLSyntaxException {
        log.debug("[R2RMLMappingFactory:extractPredicateMap] Extract predicate map..");
        // Extract object maps properties
        Value constantValue = extractValueFromTermMap(r2rmlMappingGraph,
                object, R2RMLTerm.CONSTANT);
        String stringTemplate = extractLiteralFromTermMap(r2rmlMappingGraph,
                object, R2RMLTerm.TEMPLATE);
        URI termType = (URI) extractValueFromTermMap(r2rmlMappingGraph, object,
                R2RMLTerm.TERM_TYPE);

        String inverseExpression = extractLiteralFromTermMap(r2rmlMappingGraph,
                object, R2RMLTerm.INVERSE_EXPRESSION);
        String columnValueStr = extractLiteralFromTermMap(r2rmlMappingGraph,
                object, R2RMLTerm.COLUMN);
        ColumnIdentifier columnValue = ColumnIdentifierImpl.buildFromR2RMLConfigFile(columnValueStr);
        PredicateMap result = new StdPredicateMap(null, constantValue,
                stringTemplate, inverseExpression, columnValue, termType);
        log.debug("[R2RMLMappingFactory:extractPredicateMap] Extract predicate map done.");
        return result;
    }

    /**
     * Extract subjectMap contents
     *
     * @param r2rmlMappingGraph
     * @param triplesMapSubject
     * @return
     * @throws InvalidR2RMLStructureException
     * @throws InvalidR2RMLSyntaxException
     * @throws R2RMLDataError
     */
    private static SubjectMap extractSubjectMap(
            SesameDataSet r2rmlMappingGraph, Resource triplesMapSubject,
            Set<GraphMap> savedGraphMaps, TriplesMap ownTriplesMap)
            throws InvalidR2RMLStructureException, R2RMLDataError,
            InvalidR2RMLSyntaxException {
        log.debug("[R2RMLMappingFactory:extractPredicateObjectMaps] Extract subject map...");
        // Extract subject map
        URI p = r2rmlMappingGraph.URIref(R2RMLVocabulary.R2RML_NAMESPACE
                + R2RMLTerm.SUBJECT_MAP);
        List<Statement> statements = r2rmlMappingGraph.tuplePattern(
                triplesMapSubject, p, null);

        if (statements.isEmpty()) {
            throw new InvalidR2RMLStructureException(
                    "[R2RMLMappingFactory:extractSubjectMap] "
                    + triplesMapSubject
                    + " has no subject map defined.");
        }
        if (statements.size() > 1) {
            throw new InvalidR2RMLStructureException(
                    "[R2RMLMappingFactory:extractSubjectMap] "
                    + triplesMapSubject
                    + " has too many subject map defined.");
        }

        Resource subjectMap = (Resource) statements.get(0).getObject();
        log.debug("[R2RMLMappingFactory:extractTriplesMap] Found subject map : "
                + subjectMap.stringValue());

        Value constantValue = extractValueFromTermMap(r2rmlMappingGraph,
                subjectMap, R2RMLTerm.CONSTANT);
        String stringTemplate = extractLiteralFromTermMap(r2rmlMappingGraph,
                subjectMap, R2RMLTerm.TEMPLATE);
        URI termType = (URI) extractValueFromTermMap(r2rmlMappingGraph,
                subjectMap, R2RMLTerm.TERM_TYPE);
        String inverseExpression = extractLiteralFromTermMap(r2rmlMappingGraph,
                subjectMap, R2RMLTerm.INVERSE_EXPRESSION);
        String columnValueStr = extractLiteralFromTermMap(r2rmlMappingGraph,
                subjectMap, R2RMLTerm.COLUMN);

        String selectorValueStr = extractLiteralFromTermMap(r2rmlMappingGraph,
                subjectMap, RMLVocabulary.RMLTerm.SELECTOR);
        
        if (columnValueStr != null && selectorValueStr != null){
            throw new InvalidR2RMLStructureException(
                    "[R2RMLMappingFactory:extractSubjectMap] "
                    + triplesMapSubject
                    + " has a selector and column defined.");
        }

        ColumnIdentifier columnValue = ColumnIdentifierImpl.buildFromR2RMLConfigFile(columnValueStr);
        Set<URI> classIRIs = extractURIsFromTermMap(r2rmlMappingGraph,
                subjectMap, R2RMLTerm.CLASS);
        Set<Value> graphMapValues = extractValuesFromResource(
                r2rmlMappingGraph, subjectMap, R2RMLTerm.GRAPH_MAP);
        Set<GraphMap> graphMaps = new HashSet<GraphMap>();
        if (graphMapValues != null) {
            for (Value graphMap : graphMapValues) {
                // Create associated graphMap if it has not already created
                boolean found = false;
                GraphMap graphMapFound = null;
                /*
                 * for (GraphMap savedGraphMap : savedGraphMaps) if
                 * (savedGraphMap.getGraph().equals(graphMap)) { found = true;
                 * graphMapFound = savedGraphMap; }
                 */
                if (found) {
                    graphMaps.add(graphMapFound);
                } else {
                    GraphMap newGraphMap = extractGraphMap(r2rmlMappingGraph,
                            (Resource) graphMap);
                    savedGraphMaps.add(newGraphMap);
                    graphMaps.add(newGraphMap);
                }
            }
        }
        SubjectMap result = new StdSubjectMap(ownTriplesMap, constantValue,
                stringTemplate, termType, inverseExpression, columnValue,
                classIRIs, graphMaps);
        log.debug("[R2RMLMappingFactory:extractSubjectMap] Subject map extracted.");
        return result;
    }

    /*
     * Still needs to be modified!!
     */
    private static GraphMap extractGraphMap(SesameDataSet r2rmlMappingGraph,
            Resource graphMap) throws InvalidR2RMLStructureException,
            R2RMLDataError, InvalidR2RMLSyntaxException {
        log.debug("[R2RMLMappingFactory:extractPredicateObjectMaps] Extract graph map...");

        Value constantValue = extractValueFromTermMap(r2rmlMappingGraph,
                graphMap, R2RMLTerm.CONSTANT);
        String stringTemplate = extractLiteralFromTermMap(r2rmlMappingGraph,
                graphMap, R2RMLTerm.TEMPLATE);
        String inverseExpression = extractLiteralFromTermMap(r2rmlMappingGraph,
                graphMap, R2RMLTerm.INVERSE_EXPRESSION);
        String columnValueStr = extractLiteralFromTermMap(r2rmlMappingGraph,
                graphMap, R2RMLTerm.COLUMN);
        ColumnIdentifier columnValue = ColumnIdentifierImpl.buildFromR2RMLConfigFile(columnValueStr);
        URI termType = (URI) extractValueFromTermMap(r2rmlMappingGraph,
                graphMap, R2RMLTerm.TERM_TYPE);

        GraphMap result = new StdGraphMap(constantValue, stringTemplate,
                inverseExpression, columnValue, termType);
        log.debug("[R2RMLMappingFactory:extractPredicateObjectMaps] Graph map extracted.");
        return result;
    }

    /**
     * Extract content literal from a term type resource.
     *
     * @param r2rmlMappingGraph
     * @param termType
     * @param term
     * @return
     * @throws InvalidR2RMLStructureException
     */
    private static String extractLiteralFromTermMap(
            SesameDataSet r2rmlMappingGraph, Resource termType, Enum term)
            throws InvalidR2RMLStructureException {

        URI p = getTermURI(r2rmlMappingGraph, term);

        List<Statement> statements = r2rmlMappingGraph.tuplePattern(termType,
                p, null);
        if (statements.isEmpty()) {
            return null;
        }
        if (statements.size() > 1) {
            throw new InvalidR2RMLStructureException(
                    "[R2RMLMappingFactory:extractValueFromTermMap] " + termType
                    + " has too many " + term + " predicate defined.");
        }
        String result = statements.get(0).getObject().stringValue();
        if (log.isDebugEnabled()) {
            log.debug("[R2RMLMappingFactory:extractLiteralFromTermMap] Extracted "
                    + term + " : " + result);
        }
        return result;
    }

    /**
     * Extract content value from a term type resource.
     *
     * @return
     * @throws InvalidR2RMLStructureException
     */
    private static Value extractValueFromTermMap(
            SesameDataSet r2rmlMappingGraph, Resource termType,
            Enum term)
            throws InvalidR2RMLStructureException {



        URI p = getTermURI(r2rmlMappingGraph, term);

        List<Statement> statements = r2rmlMappingGraph.tuplePattern(termType,
                p, null);
        if (statements.isEmpty()) {
            return null;
        }
        if (statements.size() > 1) {
            throw new InvalidR2RMLStructureException(
                    "[R2RMLMappingFactory:extractValueFromTermMap] " + termType
                    + " has too many " + term + " predicate defined.");
        }
        Value result = statements.get(0).getObject();
        log.debug("[R2RMLMappingFactory:extractValueFromTermMap] Extracted "
                + term + " : " + result.stringValue());
        return result;
    }

    /**
     * Extract content values from a term type resource.
     *
     * @return
     * @throws InvalidR2RMLStructureException
     */
    private static Set<Value> extractValuesFromResource(
            SesameDataSet r2rmlMappingGraph,
            Resource termType,
            Enum term)
            throws InvalidR2RMLStructureException {
        URI p = getTermURI(r2rmlMappingGraph, term);

        List<Statement> statements = r2rmlMappingGraph.tuplePattern(termType,
                p, null);
        if (statements.isEmpty()) {
            return null;
        }
        Set<Value> values = new HashSet<Value>();
        for (Statement statement : statements) {
            Value value = statement.getObject();
            log.debug("[R2RMLMappingFactory:extractURIsFromTermMap] Extracted "
                    + term + " : " + value.stringValue());
            values.add(value);
        }
        return values;
    }

    /**
     * Extract content URIs from a term type resource.
     *
     * @return
     * @throws InvalidR2RMLStructureException
     */
    private static Set<URI> extractURIsFromTermMap(
            SesameDataSet r2rmlMappingGraph, Resource termType,
            R2RMLTerm term)
            throws InvalidR2RMLStructureException {
        URI p = getTermURI(r2rmlMappingGraph, term);

        List<Statement> statements = r2rmlMappingGraph.tuplePattern(termType,
                p, null);
        if (statements.isEmpty()) {
            return null;
        }
        Set<URI> uris = new HashSet<URI>();
        for (Statement statement : statements) {
            URI uri = (URI) statement.getObject();
            log.debug("[R2RMLMappingFactory:extractURIsFromTermMap] Extracted "
                    + term + " : " + uri.stringValue());
            uris.add(uri);
        }
        return uris;
    }

    private static URI getTermURI(SesameDataSet r2rmlMappingGraph, Enum term) throws InvalidR2RMLStructureException {
        String namespace = R2RMLVocabulary.R2RML_NAMESPACE;

        if (term instanceof RMLVocabulary.RMLTerm) {
            namespace = RMLVocabulary.RML_NAMESPACE;
        } else if (!(term instanceof R2RMLTerm)) {
            throw new InvalidR2RMLStructureException(
                    "[R2RMLMappingFactory:extractValueFromTermMap] " + term + " is not valid.");
        }

        return r2rmlMappingGraph
                .URIref(namespace + term);
    }

    /**
     * Extract logicalTable contents.
     *
     * @param r2rmlMappingGraph
     * @param triplesMapSubject
     * @return
     * @throws InvalidR2RMLStructureException
     * @throws InvalidR2RMLSyntaxException
     * @throws R2RMLDataError
     */
    private static LogicalTable extractLogicalTable(
            SesameDataSet rmlMappingGraph, Resource triplesMapSubject)
            throws InvalidR2RMLStructureException, InvalidR2RMLSyntaxException,
            R2RMLDataError {

        // Extract logical table blank node
        
        URI p = rmlMappingGraph.URIref(RMLVocabulary.RML_NAMESPACE
                + RMLVocabulary.RMLTerm.LOGICAL_SOURCE);

        List<Statement> statements = rmlMappingGraph.tuplePattern(
                triplesMapSubject, p, null);
        if (statements.isEmpty()) {
            throw new InvalidR2RMLStructureException(
                    "[R2RMLMappingFactory:extractLogicalTable] "
                    + triplesMapSubject
                    + " has no logical table defined.");
        }
        if (statements.size() > 1) {
            throw new InvalidR2RMLStructureException(
                    "[R2RMLMappingFactory:extractLogicalTable] "
                    + triplesMapSubject
                    + " has too many logical table defined.");
        }


        Resource blankLogicalTable = (Resource) statements.get(0).getObject();

        RMLVocabulary.QLTerm queryLanguage = getQueryLanguage(rmlMappingGraph, blankLogicalTable);

        if (queryLanguage == null) {
            throw new InvalidR2RMLStructureException(
                    "[R2RMLMappingFactory:extractLogicalTable] "
                    + triplesMapSubject
                    + " has an unknown query language.");
        }

        // Check SQL base table or view
        URI pName = rmlMappingGraph.URIref(RMLVocabulary.RML_NAMESPACE
                + RMLVocabulary.RMLTerm.SOURCE_NAME);
        List<Statement> statementsName = rmlMappingGraph.tuplePattern(
                blankLogicalTable, pName, null);
        URI pView = rmlMappingGraph.URIref(RMLVocabulary.RML_NAMESPACE
                + RMLVocabulary.RMLTerm.QUERY);
        List<Statement> statementsView = rmlMappingGraph.tuplePattern(
                blankLogicalTable, pView, null);
        LogicalTable logicalTable = null;
        if (!statementsName.isEmpty()) {
            if (statementsName.size() > 1) {
                throw new InvalidR2RMLStructureException(
                        "[R2RMLMappingFactory:extractLogicalTable] "
                        + triplesMapSubject
                        + " has too many logical table name defined.");
            }
            if (!statementsView.isEmpty()) {
                throw new InvalidR2RMLStructureException(
                        "[R2RMLMappingFactory:extractLogicalTable] "
                        + triplesMapSubject
                        + " can't have a logical table and sql query defined"
                        + " at the ame time.");
            }
            // Table name defined

            //MVS: DESIGN NEW STRUCTURE FOR THIS
            //String file = statementsName.get(0).getObject().stringValue();
            logicalTable = new StdSQLBaseTableOrView(statementsName.get(0)
                    .getObject().stringValue());
            
            
        } else {
            // Logical table defined by R2RML View
            if (statementsView.size() > 1) {
                throw new InvalidR2RMLStructureException(
                        "[R2RMLMappingFactory:extractLogicalTable] "
                        + triplesMapSubject
                        + " has too many logical table defined.");
            }
            if (statementsView.isEmpty()) {
                throw new InvalidR2RMLStructureException(
                        "[R2RMLMappingFactory:extractLogicalTable] "
                        + triplesMapSubject
                        + " has no logical table defined.");
            }
            // Check SQL versions
            URI pVersion = rmlMappingGraph
                    .URIref(RMLVocabulary.RML_NAMESPACE
                    + RMLVocabulary.RMLTerm.VERSION);

            List<Statement> statementsVersion = rmlMappingGraph.tuplePattern(
                    statementsView.get(0).getSubject(), pVersion, null);
            String sqlQuery = statementsView.get(0).getObject().stringValue();
            if (statementsVersion.isEmpty()) {
                logicalTable = new StdR2RMLView(sqlQuery);
            }
            Set<R2RMLView.SQLVersion> versions = new HashSet<R2RMLView.SQLVersion>();
            for (Statement statementVersion : statementsVersion) {

                R2RMLView.SQLVersion sqlVersion = R2RMLView.SQLVersion
                        .getSQLVersion(statementVersion.getObject()
                        .stringValue());
                versions.add(sqlVersion);
            }
            if (versions.isEmpty()) {
                // SQL 2008 by default
                if (log.isDebugEnabled()) {
                    log.debug("[R2RMLMappingFactory:extractLogicalTable] "
                            + triplesMapSubject
                            + " has no SQL version defined : SQL 2008 by default");
                }
            }
            logicalTable = new StdR2RMLView(sqlQuery, versions);
        }
        log.debug("[R2RMLMappingFactory:extractLogicalTable] Logical table extracted : "
                + logicalTable);
        return logicalTable;
    }

    private static RMLVocabulary.QLTerm getQueryLanguage(SesameDataSet rmlMappingGraph, Resource subject) throws InvalidR2RMLStructureException {
        URI pQueryLanguage = rmlMappingGraph.URIref(RMLVocabulary.RML_NAMESPACE
                + RMLVocabulary.RMLTerm.QUERY_LANGUAGE);
        List<Statement> statements = rmlMappingGraph.tuplePattern(
                subject, pQueryLanguage, null);
        if (statements.size() > 1) {
            throw new InvalidR2RMLStructureException(
                    "[R2RMLMappingFactory:extractLogicalTable] "
                    + subject
                    + " has too many query language defined.");
        }
        if (statements.isEmpty()) {
            return RMLVocabulary.QLTerm.SQL_CLASS;
        }
        Resource object = (Resource) statements.get(0).getObject();

        return RMLVocabulary.getQLTerms(object.stringValue());
    }
}
