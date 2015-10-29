package be.ugent.mmlab.rml.processor.concrete;

import be.ugent.mmlab.rml.model.dataset.RMLDataset;
import be.ugent.mmlab.rml.performer.NodeRMLPerformer;
import be.ugent.mmlab.rml.performer.RMLPerformer;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.processor.AbstractRMLProcessor;
import be.ugent.mmlab.rml.processor.RMLProcessor;
import be.ugent.mmlab.rml.processor.RMLProcessorFactory;
import be.ugent.mmlab.rml.processor.termmap.TermMapProcessorFactory;
import be.ugent.mmlab.rml.processor.termmap.concrete.ConcreteTermMapFactory;
import be.ugent.mmlab.rml.vocabularies.QLVocabulary.QLTerm;
import be.ugent.mmlab.rml.xml.XOMBuilder;
import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import javax.xml.xpath.XPathException;
import jlibs.xml.DefaultNamespaceContext;
import jlibs.xml.Namespaces;
import jlibs.xml.sax.dog.NodeItem;
import jlibs.xml.sax.dog.XMLDog;
import jlibs.xml.sax.dog.XPathResults;
import jlibs.xml.sax.dog.expr.Expression;
import jlibs.xml.sax.dog.expr.InstantEvaluationListener;
import jlibs.xml.sax.dog.sniff.Event;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.XPathContext;
import org.jaxen.saxpath.SAXPathException;
import org.xml.sax.InputSource;
import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XPathSelector;
import net.sf.saxon.s9api.XdmNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openrdf.model.Resource;

/**
 * RML Processor
 *
 * @author mielvandersande, andimou
 */
public class XPathProcessor extends AbstractRMLProcessor {
    private int enumerator =0;
    private TriplesMap map;
    
    // Log
    static final Logger log = LoggerFactory.getLogger(XPathProcessor.class);
    
    public XPathProcessor(){
        TermMapProcessorFactory factory = new ConcreteTermMapFactory();
        this.termMapProcessor = factory.create(QLTerm.XPATH_CLASS);
    }

    public XPathContext nsContext = new XPathContext();
    
    private DefaultNamespaceContext get_namespaces(TriplesMap map) {
        
        DefaultNamespaceContext dnc = new DefaultNamespaceContext();
        this.nsContext.addNamespace("xsd", Namespaces.URI_XSD);
        dnc.declarePrefix("xsd", Namespaces.URI_XSD);
        this.nsContext.addNamespace("simmodel", "http://www.lbl.gov/namespaces/Sim/Model");
        dnc.declarePrefix("simmodel", "http://www.lbl.gov/namespaces/Sim/Model");

        XMLDog dog = new XMLDog(dnc);
        try {
            Expression xpath = dog.addXPath("/*/namespace::*[name()]");
            InputSource source = new InputSource(
                    map.getLogicalSource().getSource().getTemplate());
            XPathResults results = dog.sniff(source);

            if (results != null) {
                Collection<NodeItem> result =
                        (Collection<NodeItem>) results.getResult(xpath);
                for (NodeItem res : result) {
                    this.nsContext.addNamespace(res.qualifiedName, res.value);
                    dnc.declarePrefix(res.qualifiedName, res.value);
                }
            }

        } catch (SAXPathException ex) {
            log.error("SAX Path Exception: " + ex );
        } catch (XPathException ex) {
            log.error("XPath Exception: " + ex );
        } 
        return dnc;
    }
    
    private String replace (Node node, String expression){
        return this.termMapProcessor.extractValueFromNode(
                node,expression.split("\\{")[1].split("\\}")[0]).get(0);
    }
    
    public String execute(Node node, String expression) throws SaxonApiException {

            Processor proc = new Processor(false);
            XPathCompiler xpath = proc.newXPathCompiler();
            DocumentBuilder builder = proc.newDocumentBuilder();

            String source = 
                    getClass().getResource(map.getLogicalSource().getSource().getTemplate()).getFile();

            XdmNode doc = builder.build(new File(source));
            String expre = replace(node, expression);
            expression = expression.
                    replaceAll("\\{" + expression.split("\\{")[1].split("\\}")[0] + "\\}", "'" + expre + "'");

            XPathSelector selector = xpath.compile(expression).load();
            selector.setContextItem(doc);
            
            // Evaluate the expression.
            Object result = selector.evaluate();

            return result.toString();
 
    }
    
    @Override
    public void execute(final RMLDataset dataset, final TriplesMap map, 
        final RMLPerformer performer, InputStream input, 
        final String[] exeTriplesMap, final boolean pomExecution) {
        try {
            this.map = map;
            String reference = getReference(map.getLogicalSource());
            //Inititalize the XMLDog for processing XPath
            // an implementation of javax.xml.namespace.NamespaceContext
            //DefaultNamespaceContext dnc = new DefaultNamespaceContext();
            
            DefaultNamespaceContext dnc = get_namespaces(map);

            XMLDog dog = new XMLDog(dnc);
            //XMLDog dog = get_namespaces(input);
            //adding expression to the xpathprocessor
            dog.addXPath(reference);

            Event event = dog.createEvent();

            //event.setXMLBuilder(new DOMBuilder());
            //use XOM now
            event.setXMLBuilder(new XOMBuilder());
            
            event.setListener(new InstantEvaluationListener() {
                //When an XPath expression matches
                @Override
                public void onNodeHit(
                        Expression expression, NodeItem nodeItem) {
                    //log.debug("Expression " + expression);
                    Node node = (Node) nodeItem.xml;
                    //Let the performer do its thing
                    performer.perform(
                            node, dataset, map, exeTriplesMap, pomExecution);
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
             dog.sniff(event, new InputSource(input));
        } catch (SAXPathException ex) {
            log.error("SAXPathException " + ex);
        } catch (XPathException ex) {
            log.error("XPathException " + ex 
                    + " for the expression " 
                    + getReference(map.getLogicalSource()));
        } 
    }
    
    @Override
    public void execute_node(
            RMLDataset dataset, String expression, 
            TriplesMap parentTriplesMap, RMLPerformer performer, Object node, 
            Resource subject, String[] exeTriplesMap, boolean pomExecution) {
        //still need to make it work with more nore-results 
        //currently it handles only one
        log.debug("Execute node..");
    
        if(expression.startsWith("/"))
            expression = expression.substring(1);
        
        Node node2 = (Node) node; 
        Nodes nodes = node2.query(expression, nsContext);
        
        for (int i = 0; i < nodes.size(); i++) {
            Node n = nodes.get(i);
            if(subject == null)
                performer.perform(n, dataset, parentTriplesMap, 
                        exeTriplesMap, pomExecution);
            else{
                RMLProcessorFactory factory = new ConcreteRMLProcessorFactory();
                RMLProcessor subprocessor = 
                        factory.create(map.getLogicalSource().getReferenceFormulation());
                RMLPerformer subperformer = new NodeRMLPerformer(subprocessor);
                subperformer.perform(
                        n, dataset, parentTriplesMap, subject, exeTriplesMap);
            }
        }

    }

    @Override
    public String cleansing(String value) {
        return value;
    }
}
