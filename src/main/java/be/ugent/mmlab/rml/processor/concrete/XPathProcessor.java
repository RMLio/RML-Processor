package be.ugent.mmlab.rml.processor.concrete;

import be.ugent.mmlab.rml.input.processor.AbstractInputProcessor;
import be.ugent.mmlab.rml.input.processor.SourceProcessor;
import be.ugent.mmlab.rml.model.dataset.RMLDataset;
import be.ugent.mmlab.rml.performer.NodeRMLPerformer;
import be.ugent.mmlab.rml.performer.RMLPerformer;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.processor.AbstractRMLProcessor;
import be.ugent.mmlab.rml.processor.RMLProcessor;
import be.ugent.mmlab.rml.processor.RMLProcessorFactory;
import be.ugent.mmlab.rml.xml.XOMBuilder;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import javax.xml.xpath.XPathException;
import jlibs.xml.DefaultNamespaceContext;
import jlibs.xml.Namespaces;
import jlibs.xml.sax.dog.NodeItem;
import jlibs.xml.sax.dog.XMLDog;
import jlibs.xml.sax.dog.XPathResults;
import jlibs.xml.sax.dog.expr.Expression;
import jlibs.xml.sax.dog.expr.InstantEvaluationListener;
import jlibs.xml.sax.dog.sniff.Event;
import net.sf.saxon.s9api.DocumentBuilder;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.XPathContext;
import org.jaxen.saxpath.SAXPathException;
import org.xml.sax.InputSource;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XPathSelector;
import net.sf.saxon.s9api.XdmNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.rdf4j.model.Resource;

/**
 * RML Processor
 *
 * @author mielvandersande, andimou
 */
public class XPathProcessor extends AbstractRMLProcessor {
    private TriplesMap map;
    private DefaultNamespaceContext dnc;
    public XPathContext nsContext ;
    
    // Log
    static final Logger log = LoggerFactory.getLogger(
            XPathProcessor.class.getSimpleName());
    
    public XPathProcessor(Map<String, String> parameters){
        dnc = new DefaultNamespaceContext();
        nsContext = new XPathContext();
        
        //Add at least xsd namespace
        this.nsContext.addNamespace("xsd", Namespaces.URI_XSD);
        dnc.declarePrefix("xsd", Namespaces.URI_XSD);
        dnc.declarePrefix("sparql", "http://www.w3.org/2005/sparql-results#");
        nsContext = new XPathContext("xsd", Namespaces.URI_XSD);
        this.parameters = parameters;
    }
    
    public XPathProcessor(Map<String, String> parameters, TriplesMap map){
        dnc = new DefaultNamespaceContext();
        nsContext = new XPathContext();
        
        //Add at least xsd namespace
        this.nsContext.addNamespace("xsd", Namespaces.URI_XSD);
        dnc.declarePrefix("xsd", Namespaces.URI_XSD);
        dnc.declarePrefix("sparql", "http://www.w3.org/2005/sparql-results#");
        dnc = get_namespaces(map);
        this.parameters = parameters;
    }
    
    
    private DefaultNamespaceContext get_namespaces(
            TriplesMap map) {
        if(dnc == null){
            dnc = new DefaultNamespaceContext();
        }
        XMLDog dog = new XMLDog(dnc);
        try {
            Expression xpath = dog.addXPath("//*/namespace::*[name()]");
            //log.debug("XPath to be evaluated " + xpath);
            SourceProcessor inputProcessor = new AbstractInputProcessor();
                       
            InputStream input = inputProcessor.getInputStream(
                    map.getLogicalSource(), parameters);
            XPathResults results = dog.sniff(
                    new InputSource(input));
            
            List<NodeItem> namespacesPaths = 
                    (List<NodeItem>) results.getResult(xpath);
            //log.debug("namespaces paths " + namespacesPaths);
            for (NodeItem namespacePath : namespacesPaths) {
                this.nsContext.addNamespace(namespacePath.qualifiedName, namespacePath.value);
                dnc.declarePrefix(namespacePath.qualifiedName, namespacePath.value);
            }
            input.close();
        } catch (SAXPathException ex) {
            log.error("SAX Path Exception: " + ex);
        } catch (XPathException ex) {
            log.error("XPath Exception " + ex);
        } catch (IOException ex) {
            log.error("IO Exception " + ex);
        }
        return dnc;
    }
    
    private String replace (Node node, String expression){
        return this.termMapProcessor.extractValueFromNode(
                node,expression.split("\\{")[1].split("\\}")[0]).get(0);
    }
    
    public String execute(Node node, String expression) throws SaxonApiException {
            //log.debug("node is " + node.toString());
            Processor proc = new Processor(false);
            XPathCompiler xpath = proc.newXPathCompiler();
            DocumentBuilder builder = proc.newDocumentBuilder();
            
            String source = 
                    getClass().getResource(map.getLogicalSource().getSource().getTemplate()).getFile();

            XdmNode doc = builder.build(new File(source));
            String expre = replace(node, expression);
            expression = expression.replaceAll(
                    "\\{" + expression.split("\\{")[1].split("\\}")[0] + "\\}", "'" + expre + "'");

            XPathSelector selector = xpath.compile(expression).load();
            selector.setContextItem(doc);
            
            // Evaluate the expression.
            Object result = selector.evaluate();

            return result.toString();
 
    }
    
    @Override
    public void execute(final RMLDataset dataset, final TriplesMap map, 
        final RMLPerformer performer, final InputStream input, 
        final String[] exeTriplesMap, final boolean pomExecution) { 
        
        try {
            this.map = map;
            String reference = getReference(map.getLogicalSource());
            
            dnc = get_namespaces(map);

            XMLDog dog = new XMLDog(dnc);

            //adding expression to the xpathprocessor
            dog.addXPath(reference);

            Event event = dog.createEvent();

            //event.setXMLBuilder(new DOMBuilder());
            //use XOM now
            event.setXMLBuilder(new XOMBuilder());
            InputSource source = new InputSource(input);
                
            event.setListener(new InstantEvaluationListener() {
                boolean finalResult = false;
                //When an XPath expression matches
                @Override
                public void onNodeHit(
                        Expression expression, NodeItem nodeItem) {
                    
                    //log.debug("Expression " + expression);
                    Node node = (Node) nodeItem.xml;
                    //log.debug("node is " + node.toXML().toString());
                    //Let the performer do its thing
                    boolean result = performer.perform(node, dataset, map, exeTriplesMap, 
                            parameters, pomExecution);
                    if(result == true){
                        finalResult = result;
                    }
                }

                @Override
                public void finishedNodeSet(Expression expression) {
                    log.debug("All iterations are finished and the final result is " + finalResult);
                    setStatus(finalResult);
                    //System.out.println("Finished Nodeset: " + expression.getXPath());
                }

                @Override
                public void onResult(Expression expression, Object result) {
                    log.debug("Node is resulted");
                    // this method is called only for xpaths which returns primitive result
                    // i.e result will be one of String, Boolean, Double
                    //System.out.println("XPath: " + expression.getXPath() + " result: " + result);
                }
            });
            
            //Execute the streaming
            dog.sniff(event, source);
        } catch (SAXPathException ex) {
            log.error("SAXPathException " + ex);
        } catch (XPathException ex) {
            log.error("XPathException " + ex 
                    + " for " + map.getName() + " Triples Map");
        } 
    }
    
    public void setStatus(boolean status){
        this.setIterationStatus(status);
    }
    
    @Override
    public void execute_node(
            RMLDataset dataset, String expression, 
            TriplesMap parentTriplesMap, RMLPerformer performer, Object node, 
            Resource subject, String[] exeTriplesMap, boolean pomExecution) {
        boolean result = false;
        //still need to make it work with more nore-results 
        //currently it handles only one
        log.debug("Execute node..");
        //log.debug("node is " + node.toString());
        if(expression.startsWith("/"))
            expression = expression.substring(1);
        
        Node node2 = (Node) node; 
        Nodes nodes = node2.query(expression, nsContext);
        
        for (int i = 0; i < nodes.size(); i++) {
            Node n = nodes.get(i);
            if(subject == null)
                performer.perform(n, dataset, parentTriplesMap, 
                        exeTriplesMap, parameters, pomExecution);
            else{
                RMLProcessorFactory factory = new ConcreteRMLProcessorFactory();
                RMLProcessor subprocessor = 
                        factory.create(
                        map.getLogicalSource().getReferenceFormulation(), 
                        parameters, parentTriplesMap);
                RMLPerformer subperformer = new NodeRMLPerformer(subprocessor);
                subperformer.perform(
                        n, dataset, parentTriplesMap, subject, exeTriplesMap);
            }
        }

    }
    
    public DefaultNamespaceContext getNamespaces(){
        return dnc;
    }
}
