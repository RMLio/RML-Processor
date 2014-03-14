package be.ugent.mmlab.rml.processor.concrete;

import be.ugent.mmlab.rml.core.RMLMappingFactory;
import be.ugent.mmlab.rml.core.RMLPerformer;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.processor.AbstractRMLProcessor;
import be.ugent.mmlab.rml.xml.XOMBuilder;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.XPathContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jaxen.saxpath.SAXPathException;
import org.xml.sax.InputSource;

/**
 *
 * @author mielvandersande, andimou
 */
public class XPathProcessor extends AbstractRMLProcessor {

    private static Log log = LogFactory.getLog(RMLMappingFactory.class);

    private XPathContext nsContext = new XPathContext();
    
    private DefaultNamespaceContext get_namespaces (){
        //Get the namespaces from xml file?
        DefaultNamespaceContext dnc = new DefaultNamespaceContext();
        
        this.nsContext.addNamespace("xsd", Namespaces.URI_XSD);
        dnc.declarePrefix("xsd", Namespaces.URI_XSD);
        this.nsContext.addNamespace("gml", "http://www.opengis.net/gml");
        dnc.declarePrefix("gml", "http://www.opengis.net/gml");
        this.nsContext.addNamespace("agiv", "http://www.agiv.be/agiv");
        dnc.declarePrefix("agiv", "http://www.agiv.be/agiv");
            
        this.nsContext.addNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        dnc.declarePrefix("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        this.nsContext.addNamespace("simcore", "http://www.lbl.gov/namespaces/Sim/SimModelCore");
        dnc.declarePrefix("simcore", "http://www.lbl.gov/namespaces/Sim/SimModelCore");
        this.nsContext.addNamespace("simres", "http://www.lbl.gov/namespaces/Sim/ResourcesGeneral");
        dnc.declarePrefix("simres", "http://www.lbl.gov/namespaces/Sim/ResourcesGeneral");
        this.nsContext.addNamespace("simgeom", "http://www.lbl.gov/namespaces/Sim/ResourcesGeometry");
        dnc.declarePrefix("simgeom", "http://www.lbl.gov/namespaces/Sim/ResourcesGeometry");
        this.nsContext.addNamespace("simbldg", "http://www.lbl.gov/namespaces/Sim/BuildingModel");
        dnc.declarePrefix("simbldg", "http://www.lbl.gov/namespaces/Sim/BuildingModel");
        this.nsContext.addNamespace("simmep", "http://www.lbl.gov/namespaces/Sim/MepModel");
        dnc.declarePrefix("simmep", "http://www.lbl.gov/namespaces/Sim/MepModel");
        this.nsContext.addNamespace("simmodel", "http://www.lbl.gov/namespaces/Sim/Model");
        dnc.declarePrefix("simmodel", "http://www.lbl.gov/namespaces/Sim/Model");

       //spc
       this.nsContext.addNamespace("mml","http://www.w3.org/1998/Math/MathML");
       dnc.declarePrefix("mml", "http://www.w3.org/1998/Math/MathML");
       this.nsContext.addNamespace("xlink", "http://www.w3.org/1999/xlink");
       dnc.declarePrefix("xlink", "http://www.w3.org/1999/xlink");
       this.nsContext.addNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
       dnc.declarePrefix("xsi", "http://www.w3.org/2001/XMLSchema-instance");
       this.nsContext.addNamespace("tp", "http://www.plazi.org/taxpub");
       dnc.declarePrefix("tp", "http://www.plazi.org/taxpub");
    
       return dnc;
    }

    @Override
    public void execute(final SesameDataSet dataset, final TriplesMap map, final RMLPerformer performer, String fileName) {
        try {
            String reference = getReference(map.getLogicalSource());
            //Inititalize the XMLDog for processing XPath
            // an implementation of javax.xml.namespace.NamespaceContext
            //DefaultNamespaceContext dnc = new DefaultNamespaceContext();
            DefaultNamespaceContext dnc = get_namespaces();
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
                    //if(!nodeItem.namespaceURI.isEmpty())
                        //log.info("namespace? " + nodeItem.namespaceURI);
                    //else
                        //log.info("no namespace.");
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
    
    @Override
    public void execute_node(SesameDataSet dataset, String expression, TriplesMap parentTriplesMap, RMLPerformer performer, Object node) {
        //still need to make it work with more nore-results 
        //currently it handles only one
        
        DefaultNamespaceContext dnc = get_namespaces();     
        if(expression.startsWith("/"))
            expression = expression.substring(1);
        log.debug("[AbstractRMLProcessorProcessor] expression " + expression);
        
        Node node2 = (Node) node;
        log.info("[AbstractRMLProcessorProcessor:node] " + "\n \n node2 " + node2.toXML() + "\n \n");
        Nodes nodes = node2.query(expression, nsContext);
        log.debug("[AbstractRMLProcessorProcessor:node] " + "nodes' size " + nodes.size());
        log.debug("[AbstractRMLProcessorProcessor:node] " + "nodes " + nodes);
        
        for (int i = 0; i < nodes.size(); i++) {
            Node n = nodes.get(i);
            log.debug("[AbstractRMLProcessorProcessor:node] " + "new node " + n.toXML().toString());
            performer.perform(n, dataset, parentTriplesMap);
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

            //MVS's for geo
            //checks if the node has a value or children
            if(!n.getValue().isEmpty() || (n.getChildCount()!=0))
                /* if (!(n instanceof Attribute) && n.getChild(0) instanceof Element) {
                    list.add(n.toXML());
                } 
                else {
                    list.add(n.getValue());
                }
            */
                //checks if the node has children, then cleans up new lines and extra spaces
                if (!(n instanceof Attribute) && n.getChildCount()>1)
                    list.add(n.getValue().trim().replaceAll("[\\t\\n\\r]", " ").replaceAll(" +", " ").replaceAll("\\( ", "\\(").replaceAll(" \\)", "\\)").replaceAll(" :", ":").replaceAll(" ,", ","));
                else
                    list.add(n.getValue());
        }
        
        return list;
        
    }


    @Override
    public List<String> extractValueFromNode(Object node, String expression) {
        return extractValueFromNode((Node) node, expression);
    }
}
