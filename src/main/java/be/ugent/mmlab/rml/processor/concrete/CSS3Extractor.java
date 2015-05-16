package be.ugent.mmlab.rml.processor.concrete;

import be.ugent.mmlab.rml.core.RMLPerformer;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.processor.AbstractRMLProcessor;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import jodd.csselly.selector.PseudoFunctionSelector;
import net.antidot.semantic.rdf.model.impl.sesame.SesameDataSet;
import jodd.jerry.Jerry;
import jodd.lagarto.dom.Node;
import jodd.lagarto.dom.NodeSelector;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openrdf.model.Resource;


/**
 *
 * @author andimou
 */
public class CSS3Extractor extends AbstractRMLProcessor{
    
    private static Log log = LogFactory.getLog(CSS3Extractor.class);
    private int enumerator;
    
    public CSS3Extractor() {
        PseudoFunctionSelector.registerPseudoFunction(CSS3NotFunction.class);
    }
    
    @Override
    public void execute(SesameDataSet dataset, TriplesMap map, RMLPerformer performer, InputStream input) {
        //this should not be needed to be defined within the extractor
        String reference = getReference(map.getLogicalSource());
        // more configuration...
        Jerry doc = null;
        try {
            doc = Jerry.jerry(IOUtils.toString(input, "UTF-8"));
        } catch (IOException ex) {
            log.error(ex);
        }
        NodeSelector nodeSelector = null;
        nodeSelector = new NodeSelector(doc.get(0));

        List<Node> selectedNodes = nodeSelector.select(reference);
        for (int i = 0; i < selectedNodes.size(); i++) {
            performer.perform(selectedNodes.get(i).getHtml(), dataset, map);
        }

    }

    @Override
    public void execute_node(
            SesameDataSet dataset, String expression, TriplesMap parentTriplesMap,
            RMLPerformer performer, Object node, Resource subject) {
        if (expression.startsWith("+")) 
            expression = expression.substring(1);

        Jerry doc = Jerry.jerry(node.toString());
        NodeSelector nodeSelector = new NodeSelector(doc.get(0));

        List<Node> selectedNodes = nodeSelector.select(expression.trim());
        for (Node selectNode : selectedNodes) {
            performer.perform(selectNode.getHtml(), dataset, parentTriplesMap);
        }
    }

    @Override
    public List<String> extractValueFromNode(Object node, String expression) {
        Jerry doc = Jerry.jerry(node.toString());
        List<String> list = new ArrayList();
        String replacement = null;
        
        if(expression.equals("*")){
            list.add(node.toString());
            return list;
        }
        
        if (expression.equals("#")) {
            list.add(Integer.toString(enumerator++));
            return list;
        }
        
        Node doc2 = doc.get(0);
        NodeSelector nodeSelector = new NodeSelector(doc2);
        List<Node> selectedNodes ; //= nodeSelector.select(expression);
        if (expression.contains("href"))
            selectedNodes = nodeSelector.select("a");
        else
            selectedNodes = nodeSelector.select(expression);

        for (Node snode : selectedNodes) {
            if (expression.contains("href")) {
                list.add(StringEscapeUtils.unescapeHtml(
                        snode.getAttribute("href").toString().replaceAll(expression, replacement).trim().replaceAll("[\\t\\n\\r\\s]{2,}", " ")));
            } else {
                String value = StringEscapeUtils.unescapeHtml(snode.getTextContent().replaceAll("[\\t\\n\\r\\s]{2,}", " ").trim());
                if(value != null & !value.equals(""))
                    list.add(StringEscapeUtils.unescapeHtml(value));
            }
        }
        /*for (Node snode : selectedNodes) {
            if (snode.getInnerHtml().toString() != null && !snode.getInnerHtml().toString().trim().replaceAll("[\\t\\n\\r\\s]{2,}", " ").equals("")) {
                list.add(snode.getInnerHtml().toString().trim().replaceAll("[\\t\\n\\r\\s]{2,}", " "));
            }
        }*/
        return list;
    }

    @Override
    public String cleansing(String value) {
        try {
            Jerry doc = Jerry.jerry(value);
            Node node = doc.get(0);
            value = node.getTextContent().trim().replaceAll("[\\t\\n\\r\\s]", " ");
        } finally {
            return value;
        }
    }
    }
