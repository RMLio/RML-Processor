/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.mmlab.rml.processor;

import be.ugent.mmlab.rml.core.RMLPerformer;
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
import org.w3c.dom.xpath.XPathResult;
import org.xml.sax.InputSource;

/**
 *
 * @author mielvandersande
 */
public class XPathProcessor extends AbstractRMLProcessor {

    public void execute(final SesameDataSet dataset, final TriplesMap map, final RMLPerformer performer) {
        try {

            String selector = getSelector(map.getLogicalSource());
            String fileName = getIdentifier(map.getLogicalSource());

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

                    performer.perform(node, dataset, map);
                    //System.out.println("XPath: " + expression.getXPath() + " has hit: " + node.getTextContent());
                }

                @Override
                public void finishedNodeSet(Expression expression) {
                    //System.out.println("Finished Nodeset: " + expression.getXPath());
                }

                @Override
                public void onResult(Expression expression, Object result) {
                    // this method is called only for xpaths which returns primitive result
                    // i.e result will be one of String, Boolean, Double
                    //System.out.println("XPath: " + expression.getXPath() + " result: " + result);
                }
            });

            dog.sniff(event, new InputSource(fileName));
        } catch (SAXPathException ex) {
            Logger.getLogger(XPathProcessor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XPathException ex) {
            Logger.getLogger(XPathProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     *
     * @param node
     * @param expression
     * @return value that matches expression
     */
    private String extractValueFromNode(Node node, String expression) {
        XPathEvaluator eval = new XPathEvaluatorImpl();
        Logger.getLogger(XPathProcessor.class.getName()).log(Level.INFO, null, "About to run: "+expression+ " over " + node);
        XPathResult result = (XPathResult) eval.evaluate(expression, node, null, XPathResult.STRING_TYPE, null);

        return result.getStringValue();
    }

    public String extractValueFromNode(Object node, String expression) {
        return extractValueFromNode((Node) node, expression);
    }

}
