package be.ugent.mmlab.rml.processor.concrete;

import be.ugent.mmlab.rml.core.RMLMappingFactory;
import be.ugent.mmlab.rml.core.RMLPerformer;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.processor.AbstractRMLProcessor;
import be.ugent.mmlab.rml.xml.XOMBuilder;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.xpath.XPathException;
import jlibs.xml.DefaultNamespaceContext;
import jlibs.xml.Namespaces;
import jlibs.xml.sax.dog.NodeItem;
import jlibs.xml.sax.dog.XMLDog;
import jlibs.xml.sax.dog.expr.Expression;
import jlibs.xml.sax.dog.expr.InstantEvaluationListener;
import net.antidot.semantic.rdf.model.impl.sesame.SesameDataSet;
import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.XPathContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jaxen.saxpath.SAXPathException;
import org.xml.sax.InputSource;

/**
 *
 * @author mielvandersande
 */
public class XPathProcessor extends AbstractRMLProcessor {

    private static Log log = LogFactory.getLog(RMLMappingFactory.class);

    private XPathContext nsContext = new XPathContext();

    @Override
    public void execute(final SesameDataSet dataset, final TriplesMap map, final RMLPerformer performer) {
        try {
            System.out.println("XPath Processor execute " );
            String reference = getReference(map.getLogicalSource());
            //String fileName = getIdentifier(map.getLogicalSource());
            //String fileName = map.getLogicalSource().getIdentifier().toString();
            String fileName = getClass().getResource(map.getLogicalSource().getIdentifier()).getFile();
            System.out.println("XPath Processor filename " + fileName);
            //Inititalize the XMLDog for processing XPath
            // an implementation of javax.xml.namespace.NamespaceContext
            DefaultNamespaceContext dnc = new DefaultNamespaceContext();
            this.nsContext.addNamespace("xsd", Namespaces.URI_XSD);
            dnc.declarePrefix("xsd", Namespaces.URI_XSD);

            //Get the namespaces from xml file?
            this.nsContext.addNamespace("gml", "http://www.opengis.net/gml");
            dnc.declarePrefix("gml", "http://www.opengis.net/gml");
            this.nsContext.addNamespace("agiv", "http://www.agiv.be/agiv");
            dnc.declarePrefix("agiv", "http://www.agiv.be/agiv");

           
            XMLDog dog = new XMLDog(dnc);

            //adding expression to the xpathprocessor
            dog.addXPath(reference);

            jlibs.xml.sax.dog.sniff.Event event = dog.createEvent();

            //event.setXMLBuilder(new DOMBuilder());
            //use XOM now
            event.setXMLBuilder(new XOMBuilder());
            
            
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
            dog.sniff(event, new InputSource(new FileInputStream(fileName)));
        } catch (SAXPathException ex) {
            Logger.getLogger(XPathProcessor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XPathException ex) {
            Logger.getLogger(XPathProcessor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
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
    private List<String> extractValueFromNode(Node node, String expression) {
        Nodes nodes = node.query(expression, nsContext);
        List<String> list = new ArrayList<String>();
        
        for (int i = 0; i < nodes.size(); i++) {
            Node n = nodes.get(i);
            
             if (!(n instanceof Attribute) && n.getChild(0) instanceof Element) {
                list.add(n.toXML());
            }
            else {
                list.add(n.getValue());
            }
            //list.add(n.toXML());
        }
        
        return list;
        
    }


    @Override
    public List<String> extractValueFromNode(Object node, String expression) {
        return extractValueFromNode((Node) node, expression);
    }
}
