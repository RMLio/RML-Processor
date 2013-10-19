package be.ugent.mmlab.rml.main;

import be.ugent.mmlab.rml.vocabulary.RMLVocabulary;
import be.ugent.mmlab.rml.vocabulary.RMLVocabulary.RMLTerm;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.xpath.XPathException;
import jlibs.xml.DefaultNamespaceContext;
import jlibs.xml.Namespaces;
import jlibs.xml.sax.dog.NodeItem;
import jlibs.xml.sax.dog.XMLDog;
import jlibs.xml.sax.dog.expr.Expression;
import jlibs.xml.sax.dog.expr.InstantEvaluationListener;
import jlibs.xml.sax.dog.sniff.DOMBuilder;
import net.antidot.semantic.rdf.model.impl.sesame.SesameDataSet;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.core.R2RMLMappingFactory;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.core.R2RMLVocabulary;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.core.R2RMLVocabulary.R2RMLTerm;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.tools.R2RMLToolkit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jaxen.saxpath.SAXPathException;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.xml.sax.InputSource;

/**
 * Hello world!
 *
 */
public class App {
    
    private static HashMap<String, String> sources = new HashMap<String, String>();
    // Log
    private static Log log = LogFactory.getLog(R2RMLMappingFactory.class);
    // Value factory
    private static ValueFactory vf = new ValueFactoryImpl();
    
    public static void main(String[] args) {
        try {
            String fileToR2RMLFile = "/Users/mielvandersande/Desktop/Projects/USC-ISI/Karma/R2RML/Example/documents-export-2013-10-14/example.rml.ttl";
            
            sources.put("example.xml", "/Users/mielvandersande/Desktop/Projects/USC-ISI/Karma/R2RML/Example/documents-export-2013-10-14/example.xml");
            
            SesameDataSet r2rmlMappingGraph = new SesameDataSet();
            r2rmlMappingGraph.loadDataFromFile(fileToR2RMLFile, RDFFormat.TURTLE);


            /*
             * Extract all expressions
             */
            HashMap<Statement, Set<String>> xpathMap = new HashMap<Statement, Set<String>>();
            for (Enum e : RMLVocabulary.expressionProperties) {
                
                String namespace = RMLVocabulary.RML_NAMESPACE;
                if (e instanceof R2RMLTerm) {
                    namespace = R2RMLVocabulary.R2RML_NAMESPACE;
                }
                URI p = r2rmlMappingGraph.URIref(namespace
                        + e);
                
                List<Statement> selectors = r2rmlMappingGraph.tuplePattern(null, p,
                        null);
                
                for (Statement stmnt : selectors) {
                    
                    Set<String> expressions = new HashSet<String>();
                    if (p.toString().equals(R2RMLVocabulary.R2RML_NAMESPACE + R2RMLTerm.TEMPLATE)) {
                        expressions = R2RMLToolkit.extractColumnNamesFromStringTemplate(stmnt.getObject().stringValue());
                    } else {
                        String exp = stmnt.getObject().stringValue();
                        expressions.add(exp);
                    }
                    
                    xpathMap.put(stmnt, expressions);
                    
                    
                }
            }
            
            
            parseExpressions(sources.get("example.xml"), xpathMap);






            /*

             //Find all triple maps
             ArrayList<Resource> tripleMapResources = new ArrayList<Resource>();
             URI p = r2rmlMappingGraph.URIref(R2RMLVocabulary.R2RML_NAMESPACE
             + R2RMLTerm.SUBJECT_MAP);
             List<Statement> statements = r2rmlMappingGraph.tuplePattern(null, p,
             null);

             if (statements.isEmpty()) {
             log.warn("[R2RMLMappingFactory:extractR2RMLMapping] No subject statement found. Exit...");
             } else // No subject map, Many shortcuts subjects
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
             Resource r = s.getSubject();
             tripleMapResources.add(r);
             }
             }
             }

             // Get the XPath expressions

             HashMap<Resource, String> expressions = new HashMap<Resource, String>();

             for (Resource r : tripleMapResources) {

             URI pLogicalSource = r2rmlMappingGraph.URIref(RMLVocabulary.RML_NAMESPACE
             + RMLTerm.LOGICAL_SOURCE);

             List<Statement> logicalSources = r2rmlMappingGraph.tuplePattern(r, pLogicalSource,
             null);

             Resource logicalSource = (Resource) logicalSources.get(0).getObject();

             URI pSourceName = r2rmlMappingGraph.URIref(RMLVocabulary.RML_NAMESPACE
             + RMLTerm.SOURCE_NAME);

             URI pQueryLanguage = r2rmlMappingGraph.URIref(RMLVocabulary.RML_NAMESPACE
             + RMLTerm.QUERY_LANGUAGE);

             List<Statement> sourceNames = r2rmlMappingGraph.tuplePattern(logicalSource, pSourceName,
             null);

             String sourceName = sourceNames.get(0).getObject().toString();

             List<Statement> queryLanguages = r2rmlMappingGraph.tuplePattern(logicalSource, pQueryLanguage,
             null);

             Resource queryLanguage = (Resource) queryLanguages.get(0).getObject();

             switch (RMLVocabulary.getQLTerms(queryLanguage.stringValue())) {
             case XPATH_CLASS:
             processXPath(r2rmlMappingGraph, r);
             }

             System.out.println(sourceName);
             System.out.println(queryLanguage);


             }
             */
            
        } catch (RepositoryException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RDFParseException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
        /* catch (InvalidR2RMLStructureException ex) {
         Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
         }*/
        
        
        
        
        
    }
    
    private static void processXPath(SesameDataSet r2rmlMappingGraph, Resource r) {
        HashMap<Resource, String> xpathMap = new HashMap<Resource, String>();
        
        
        URI pSelector = r2rmlMappingGraph.URIref(RMLVocabulary.RML_NAMESPACE
                + RMLTerm.SELECTOR);
        
        List<Statement> selectors = r2rmlMappingGraph.tuplePattern(r, pSelector,
                null);
        
        for (Statement selector : selectors) {
            System.out.println(selector.getSubject());
        }
        
        
    }
    
    private static void parseExpressions(String fileName, HashMap<Statement, Set<String>> xpathMap) {
        try {
            
            
            DefaultNamespaceContext nsContext = new DefaultNamespaceContext(); // an implementation of javax.xml.namespace.NamespaceContext
            nsContext.declarePrefix("xsd", Namespaces.URI_XSD);
            
            XMLDog dog = new XMLDog(nsContext);
            
            for (Set<String> stringSet : xpathMap.values()) {
                Iterator<String> it = stringSet.iterator();
                
                while (it.hasNext()) {
                    String path = it.next();
                    dog.addXPath(path);
                    
                }
            }
            
            
            jlibs.xml.sax.dog.sniff.Event event = dog.createEvent();
            event.setXMLBuilder(new DOMBuilder());
            event.setListener(new InstantEvaluationListener() {
                @Override
                public void onNodeHit(Expression expression, NodeItem nodeItem) {
                    org.w3c.dom.Node node = (org.w3c.dom.Node) nodeItem.xml;
                    System.out.println("XPath: " + expression.getXPath() + " has hit: " + node.getTextContent());
                }
                
                @Override
                public void finishedNodeSet(Expression expression) {
                    System.out.println("Finished Nodeset: " + expression.getXPath());
                }
                
                @Override
                public void onResult(Expression expression, Object result) {
                    // this method is called only for xpaths which returns primitive result
                    // i.e result will be one of String, Boolean, Double
                    System.out.println("XPath: " + expression.getXPath() + " result: " + result);
                }
            });
            
            
            
            
            
            
            dog.sniff(event, new InputSource(fileName));

            
        } catch (XPathException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXPathException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
