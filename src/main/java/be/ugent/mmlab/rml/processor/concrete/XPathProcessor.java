package be.ugent.mmlab.rml.processor.concrete;

import be.ugent.mmlab.rml.core.ConditionalJoinRMLPerformer;
import be.ugent.mmlab.rml.core.RMLMappingFactory;
import be.ugent.mmlab.rml.core.RMLPerformer;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.processor.AbstractRMLProcessor;
import com.sun.org.apache.xpath.internal.domapi.XPathEvaluatorImpl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

    private static Log log = LogFactory.getLog(RMLMappingFactory.class);

    public void execute(final SesameDataSet dataset, final TriplesMap map, final RMLPerformer performer) {
        try {

            String reference = getReference(map.getLogicalSource());
            String fileName = getIdentifier(map.getLogicalSource());

            //Inititalize the XMLDog for processing XPath
            DefaultNamespaceContext nsContext = new DefaultNamespaceContext(); // an implementation of javax.xml.namespace.NamespaceContext
            nsContext.declarePrefix("xsd", Namespaces.URI_XSD);
            
            //Get the namespaces from xml file?
            nsContext.declarePrefix("gml","http://www.opengis.net/gml");

            XMLDog dog = new XMLDog(nsContext);
            

            //adding expression to the xpathprocessor
            dog.addXPath(reference);

            jlibs.xml.sax.dog.sniff.Event event = dog.createEvent();

            event.setXMLBuilder(new DOMBuilder());

            event.setListener(new InstantEvaluationListener() {
                //When an XPath expression matches
                @Override
                public void onNodeHit(Expression expression, NodeItem nodeItem) {
                    Node node = (Node) nodeItem.xml;
                    //Let the performer do its thing
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
            //Execute the streaming
            dog.sniff(event, new InputSource(fileName));
        } catch (SAXPathException ex) {
            Logger.getLogger(XPathProcessor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XPathException ex) {
            Logger.getLogger(XPathProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Process a XPath expression against an XML node
     *
     * @param node
     * @param expression
     * @return value that matches expression
     */
    private String[] extractValueFromNode(Node node, String expression) {
        XPathEvaluator eval = new XPathEvaluatorImpl();
        Logger.getLogger(XPathProcessor.class.getName()).log(Level.INFO, null, "About to run: " + expression + " over " + node);
        XPathResult result = (XPathResult) eval.evaluate(expression, node, null, XPathResult.STRING_TYPE, null);

        switch (result.getResultType()) {
            case XPathResult.STRING_TYPE:
                String value = result.getStringValue();
                if (value.isEmpty()){
                    return new String[0];
                }
                return new String[]{value};

            case XPathResult.ORDERED_NODE_ITERATOR_TYPE:
            case XPathResult.UNORDERED_NODE_ITERATOR_TYPE:
                Node n;
                List<String> list = new ArrayList<String>();
                while ((n = result.iterateNext()) != null) {
                    list.add(n.getNodeValue());
                }
                return list.toArray(new String[0]);
            default:
                return new String[0];
        }
    }

    public String[] extractValueFromNode(Object node, String expression) {
        return extractValueFromNode((Node) node, expression);
    }

    /*public void executeRefObjMap(final SesameDataSet dataset, final TriplesMap map, final ConditionalJoinRMLPerformer performer, final HashMap<String, String> joinMap) {
        try {
            for (final Map.Entry<String, String> entry : joinMap.entrySet()) {

                String reference = getReference(map.getLogicalSource());
                String fileName = getIdentifier(map.getLogicalSource());

                //Inititalize the XMLDog for processing XPath
                DefaultNamespaceContext nsContext = new DefaultNamespaceContext(); // an implementation of javax.xml.namespace.NamespaceContext
                nsContext.declarePrefix("xsd", Namespaces.URI_XSD);

                final XMLDog dog = new XMLDog(nsContext);

                dog.addXPath(reference);

                final jlibs.xml.sax.dog.sniff.Event event = dog.createEvent();

                event.setXMLBuilder(new DOMBuilder());

                event.setListener(new InstantEvaluationListener() {
                    //When an XPath expression matches
                    @Override
                    public void onNodeHit(Expression expression, NodeItem nodeItem) {
                        Node node = (Node) nodeItem.xml;
                        String subReference = entry.getKey();

                        String value = extractValueFromNode(node, subReference);
                        log.debug("[XPathProcessor:executeRefObjMap]. value: " + value);
                        if (value.equals(entry.getValue())) {
                            log.debug("[XPathProcessor:perform] " + "node " + node.toString());
                            performer.perform(node, dataset, map);
                        }
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
                //Execute the streaming
                dog.sniff(event, new InputSource(fileName));
            }
        } catch (SAXPathException ex) {
            Logger.getLogger(XPathProcessor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XPathException ex) {
            Logger.getLogger(XPathProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }*/
}
