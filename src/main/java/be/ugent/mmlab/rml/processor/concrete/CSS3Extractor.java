package be.ugent.mmlab.rml.processor.concrete;

import be.ugent.mmlab.rml.core.RMLMappingFactory;
import be.ugent.mmlab.rml.core.RMLPerformer;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.processor.AbstractRMLProcessor;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jodd.io.FileUtil;
import net.antidot.semantic.rdf.model.impl.sesame.SesameDataSet;
import jodd.jerry.Jerry;
import jodd.lagarto.dom.Node;
import jodd.lagarto.dom.NodeSelector;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 *
 * @author andimou
 */
public class CSS3Extractor extends AbstractRMLProcessor{
    
    private static Log log = LogFactory.getLog(RMLMappingFactory.class);
    private int enumerator;

    @Override
    public void execute(SesameDataSet dataset, TriplesMap map, RMLPerformer performer, String fileName) {
        log.info("[CSS3 Extractor] CSS3 Extractor");
        
        //this should not be needed to be defined within the extractor
        String reference = getReference(map.getLogicalSource());
        log.info("[CSS3 Extractor] reference " + reference);
        // more configuration...
        Jerry doc = null;
        File file = new File(fileName);
        try {
            doc = Jerry.jerry(FileUtil.readString(file));
        } catch (IOException ex) {
            Logger.getLogger(CSS3Extractor.class.getName()).log(Level.SEVERE, null, ex);
        }
        NodeSelector nodeSelector = null;
        //working examples
        //log.info("[CSS3 Extractor] year " + doc.$("span.CEURPUBYEAR").html());
        //log.info("[CSS3 Extractor] title " + doc.$("span.CEURFULLTITLE").html());
        //log.info("[CSS3Extractor] href " + doc.$("[href]").html()); 
         
        nodeSelector = new NodeSelector(doc.get(0));
        
        List<Node> selectedNodes = nodeSelector.select(reference);
        for (int i = 0; i < selectedNodes.size(); i++) {
            performer.perform(selectedNodes.get(i).getHtml(), dataset, map);
        }
               
    }

    @Override
    public void execute_node(SesameDataSet dataset, TriplesMap map, TriplesMap parentTriplesMap, RMLPerformer performer, Object node) {
        int end = map.getLogicalSource().getReference().length();
        log.debug("[AbstractRMLProcessorProcessor] initial expression " + map.getLogicalSource().getReference());
        log.debug("[AbstractRMLProcessorProcessor] next expression " + parentTriplesMap.getLogicalSource().getReference());
        String expression = parentTriplesMap.getLogicalSource().getReference().toString().substring(end);
        if(expression.startsWith("+"))
            expression = expression.substring(1);
        log.debug("[AbstractRMLProcessorProcessor] expression " + expression);
        
        Jerry doc = Jerry.jerry(node.toString());
        NodeSelector nodeSelector = new NodeSelector(doc.get(0));
        
        //List<String> list = new ArrayList();
        List<Node> selectedNodes = nodeSelector.select(expression.trim());
        log.info("[CSS3Extractor:execute_node] selectedNodes " + selectedNodes);
        for(Node selectNode : selectedNodes){
            //list.add(StringEscapeUtils.unescapeHtml(selectNode.getTextContent()));
            performer.perform(selectNode.getHtml(), dataset, parentTriplesMap);
        }
    }

    @Override
    /*public List<String> extractValueFromNode(Object node, String expression) {
        Jerry doc = Jerry.jerry(node.toString());
        NodeSelector nodeSelector = new NodeSelector(doc.get(0));
        
        log.info("[CSS3Extractor:extractValueFromNode] expression " + expression);
        List<String> list = new ArrayList();
        List<Node> selectedNodes = nodeSelector.select(expression);
        for(Node selectNode : selectedNodes)
            list.add(StringEscapeUtils.unescapeHtml(selectNode.getTextContent()));
        return list;
    }*/
    
    public List<String> extractValueFromNode(Object node, String expression) {
        Jerry doc = Jerry.jerry(node.toString());
        log.info("[CSS3Extractor:extractValueFromNode] expression " + expression);
        List<String> list = new ArrayList();
               
        if(expression.equals("#")){
            log.info("[CSS3Extractor:extractValueFromNode] enumeration.");
            list.add(Integer.toString(enumerator++));
            return list;
        }
        //use the same syntax as expression in templates to define the regex to the rml:reference
        //normally it provides a pattern and return as many matches as found
        //if it contains # the part after the # is the replacement regex
        if(expression.contains("{") && expression.contains("}")){
            String replacement = null, valueNew;
            String[] valueList ;
            String regex = expression.split("\\{")[1];
            regex = regex.split("\\}")[0];
            log.info("[CSS3Extractor:extractValueFromNode] regex " + regex.toString());
            if(regex.contains("#")){
                replacement = regex.split("#")[1];
                log.info("[CSS3Extractor:extractValueFromNode] replacement " + replacement.toString());
            }
            regex = regex.split("#")[0];
            expression = expression.split("\\{")[0];
            log.info("[CSS3Extractor:extractValueFromNode] expression " + expression.toString());
            String value = StringEscapeUtils.unescapeHtml(doc.$(expression).text().trim().replaceAll("[\\t\\n\\r\\s]{2,}", " "));
            log.info("[CSS3Extractor:extractValueFromNode] value " + value.toString());
            if(replacement == null){
                log.info("[CSS3Extractor:extractValueFromNode] rml:reference without replacement.");
                valueList = value.split(regex);
                for(String val : valueList){
                    log.info("[CSS3Extractor:extractValueFromNode] val " + val.toString());
                    if(val.isEmpty() || !val.equals(""))
                        list.add(val);
                }
            }
            else
            {
                log.info("[CSS3Extractor:extractValueFromNode] rml:reference with replacement.");
                Pattern replace = Pattern.compile(regex);
                log.info("[CSS3Extractor:extractValueFromNode] replace " + replace.toString());
                Matcher matcher = replace.matcher(value);
                log.info("[CSS3Extractor:extractValueFromNode] matcher " + matcher.toString());
                if(!matcher.replaceAll(replacement).equals(""))
                    list.add(matcher.replaceAll(replacement));
            }
                //valueNew = value.replaceAll(expression, replacement);
            //String[] valueList = value.split("(\\w\\s)*,");
            
        }
        else 
            if(expression.contains("&")){
            log.info("[CSS3Extractor:extractValueFromNode] rr:template with regex.");    
            String[] valueList ;
            String regex = expression.split("&")[1];
            log.info("[CSS3Extractor:extractValueFromNode] regex " + regex.toString());
            expression = expression.split("&")[0];
            log.info("[CSS3Extractor:extractValueFromNode] expression " + expression.toString());
            String value = StringEscapeUtils.unescapeHtml(doc.$(expression).text().trim().replaceAll("[\\t\\n\\r\\s]{2,}", " "));
            log.info("[CSS3Extractor:extractValueFromNode] value " + value.toString());
                valueList = value.split(regex);
                for(String val : valueList){
                    log.info("[CSS3Extractor:extractValueFromNode] val " + val.toString());
                    list.add(val);
                }
            return list;
            }
        else{
            String value = StringEscapeUtils.unescapeHtml(doc.$(expression).text().trim().replaceAll("[\\t\\n\\r]", " "));
            if(!value.equals(""))
            list.add(value);
        }
        return list;
    }
    
}
