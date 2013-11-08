/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.mmlab.rml.processor;

import be.ugent.mmlab.model.selector.SelectorIdentifier;
import be.ugent.mmlab.model.selector.SelectorIdentifierImpl;
import be.ugent.mmlab.rml.core.RMLEngine;
import be.ugent.mmlab.rml.model.LogicalSource;
import be.ugent.mmlab.rml.model.PredicateObjectMap;
import be.ugent.mmlab.rml.model.SubjectMap;
import be.ugent.mmlab.rml.model.TriplesMap;
import com.sun.org.apache.xpath.internal.domapi.XPathEvaluatorImpl;
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
import org.jaxen.saxpath.SAXPathException;
import org.w3c.dom.Node;
import org.w3c.dom.xpath.XPathEvaluator;
import org.xml.sax.InputSource;

/**
 *
 * @author mielvandersande
 */
public class XPathProcessor implements RMLProcessor {

    public void execute(SesameDataSet dataset, TriplesMap map) {
        try {
            LogicalSource ls = map.getLogicalSource();
            String selector = ls.getSelector();
            
            String fileName = RMLEngine.fileMap.get(ls.getIdentifier());

            //Inititalize the XMLDog for processing XPath
            DefaultNamespaceContext nsContext = new DefaultNamespaceContext(); // an implementation of javax.xml.namespace.NamespaceContext
            nsContext.declarePrefix("xsd", Namespaces.URI_XSD);

            XMLDog dog = new XMLDog(nsContext);

            //adding expression to the xpathprocessor
            dog.addXPath(selector);

            jlibs.xml.sax.dog.sniff.Event event = dog.createEvent();
            event.setXMLBuilder(new DOMBuilder());
            event.setListener(new InstantEvaluationListener() {
                @Override
                public void onNodeHit(Expression expression, NodeItem nodeItem) {
                    Node node = (Node) nodeItem.xml;
                    
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
        } catch (SAXPathException ex) {
            Logger.getLogger(XPathProcessor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XPathException ex) {
            Logger.getLogger(XPathProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    private void processNode(SesameDataSet dataset, TriplesMap tm, Node node){
        
        
        processSubjectMap(dataset, tm.getSubjectMap(), node);
        
        for (PredicateObjectMap pom : tm.getPredicateObjectMaps()){
            processPredicateObjectMap(dataset, pom, node);
        }

    }

    private void processSubjectMap(SesameDataSet dataset, SubjectMap subjectMap, Node node) {
        XPathEvaluator eval = new XPathEvaluatorImpl();
        String value = eval.evaluate(subjectMap., node, null, type, eval);
    }

    private void processPredicateObjectMap(SesameDataSet dataset, PredicateObjectMap pom, Node node) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
