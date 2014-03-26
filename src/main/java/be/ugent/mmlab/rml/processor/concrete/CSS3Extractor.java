package be.ugent.mmlab.rml.processor.concrete;

import be.ugent.mmlab.rml.core.RMLMappingFactory;
import be.ugent.mmlab.rml.core.RMLPerformer;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.processor.AbstractRMLProcessor;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
        //this should not be needed to be defined within the extractor
        String reference = getReference(map.getLogicalSource());
        // more configuration...
        Jerry doc = null;
        File file = new File(fileName);
        try {
            doc = Jerry.jerry(FileUtil.readString(file));
        } catch (IOException ex) {
            Logger.getLogger(CSS3Extractor.class.getName()).log(Level.SEVERE, null, ex);
        }
        NodeSelector nodeSelector = null;        
        nodeSelector = new NodeSelector(doc.get(0));
        
        List<Node> selectedNodes = nodeSelector.select(reference);
        for (int i = 0; i < selectedNodes.size(); i++) {
            performer.perform(selectedNodes.get(i).getHtml(), dataset, map);
        }
               
    }

    @Override
    public void execute_node(SesameDataSet dataset, TriplesMap map, TriplesMap parentTriplesMap, RMLPerformer performer, Object node) {
        int end = map.getLogicalSource().getReference().length();
        String expression = parentTriplesMap.getLogicalSource().getReference().toString().substring(end);
        if(expression.startsWith("+"))
            expression = expression.substring(1);
        
        Jerry doc = Jerry.jerry(node.toString());
        NodeSelector nodeSelector = new NodeSelector(doc.get(0));
        
        List<Node> selectedNodes = nodeSelector.select(expression.trim());
        for(Node selectNode : selectedNodes)
            performer.perform(selectNode.getHtml(), dataset, parentTriplesMap);
    }

    @Override
    
    public List<String> extractValueFromNode(Object node, String expression) {
        Jerry doc = Jerry.jerry(node.toString());
        List<String> list = new ArrayList();
        String replacement = null;
       
        if(expression.equals("#")){
            list.add(Integer.toString(enumerator++));
            return list;
        }
        
        if(expression.contains("&")){  
            String[] valueList = null;
            String regex = expression.split("&")[1];
            expression = expression.split("&")[0];
            
            if(regex.startsWith("deduct:")){
                regex = regex.split("deduct:")[1];
                expression = expression.split("\\{")[0];
                String value = StringEscapeUtils.unescapeHtml(doc.$(expression).text().trim().replaceAll("[\\t\\n\\r\\s]{2,}", " "));
                if(!value.isEmpty()){
                    if(value.contains("--"))
                        valueList = value.split("--");
                    else 
                        valueList = value.split("-");
                
                if(valueList.length>1){
                int val = Integer.parseInt(valueList[1]) - Integer.parseInt(valueList[0]);
                
                list.add(StringEscapeUtils.unescapeHtml(String.valueOf(val)));
                }
                else 
                {
                    valueList[0] = String.valueOf(1);
                    list.add(StringEscapeUtils.unescapeHtml(String.valueOf(valueList[0])));
                }
                
                }
                return list;
            }
            
            if(regex.contains("#")){
                replacement = regex.split("#")[1];
                regex = regex.split("#")[0];
            }
            
            if(regex.contains("href")){
                Node doc2 = doc.get(0);
                NodeSelector nodeSelector = new NodeSelector(doc2);
                List<Node> selectedNodes = nodeSelector.select(expression);
                for(Node snode:selectedNodes)
                    list.add(StringEscapeUtils.unescapeHtml(snode.getAttribute("href").toString().replaceAll(regex, replacement).trim().replaceAll("[\\t\\n\\r\\s]{2,}", " ")));
            }
            else
            {
                String value = StringEscapeUtils.unescapeHtml(doc.$(expression).text().trim().replaceAll("[\\t\\n\\r\\s]{2,}", " "));
                if(replacement == null) { 
                    
                    Node doc2 = doc.get(0);
                    NodeSelector nodeSelector = new NodeSelector(doc2);
                    List<Node> selectedNodes = nodeSelector.select(expression);
                    for(Node snode:selectedNodes){
                        if(snode.getInnerHtml().toString() != null && !snode.getInnerHtml().toString().equals("")){
                            if(!snode.getInnerHtml().toString().trim().replaceAll("[\\t\\n\\r\\s]{2,}", " ").equals(""))
                                list.add(StringEscapeUtils.unescapeHtml(snode.getInnerHtml().toString().trim().replaceAll("[\\t\\n\\r\\s]{2,}", " ")));
                        }
                    }
                    Pattern pattern = Pattern.compile(expression);
                    Matcher matcher = pattern.matcher(value);
                    if(matcher.find()){
                        if(!matcher.replaceAll(replacement).equals("") && !matcher.replaceAll(replacement).equals(" "))
                            list.add(StringEscapeUtils.unescapeHtml(matcher.group()));
                    }
                    else
                        list.add(StringEscapeUtils.unescapeHtml(value));
                }
            else{   
                DecimalFormat formatter = new DecimalFormat("00");
                Pattern replace = Pattern.compile(regex);
                Matcher matcher = replace.matcher(value);
                if(matcher.find()){
                    if(!matcher.replaceAll(replacement).equals("") && !matcher.replaceAll(replacement).equals(" ")){
                        String[] values = null;
                        if(matcher.replaceAll(replacement).matches("\\d{1}")){
                            int newInt = Integer.parseInt(matcher.replaceAll(replacement));                           
                            list.add(formatter.format(newInt).toString().trim());
                        }
                        
                        else if (matcher.replaceAll(replacement).toString().contains("January"))
                            list.add(String.valueOf(matcher.replaceAll(replacement).replace("January", formatter.format(Calendar.JANUARY+1))));
                        else if (matcher.replaceAll(replacement).toString().contains("Jan"))
                            list.add(String.valueOf(matcher.replaceAll(replacement).replace("Jan", formatter.format(Calendar.JANUARY+1))));
                        else if (matcher.replaceAll(replacement).toString().contains("February"))
                            list.add(String.valueOf(matcher.replaceAll(replacement).replace("February", formatter.format(Calendar.FEBRUARY+1))));
                        else if (matcher.replaceAll(replacement).toString().contains("Feb"))
                            list.add(String.valueOf(matcher.replaceAll(replacement).replace("Feb", formatter.format(Calendar.FEBRUARY+1))));
                        else if (matcher.replaceAll(replacement).toString().contains("March"))
                            list.add(String.valueOf(matcher.replaceAll(replacement).replace("March", formatter.format(Calendar.MARCH+1))));
                        else if (matcher.replaceAll(replacement).toString().contains("Mar"))
                            list.add(String.valueOf(matcher.replaceAll(replacement).replace("Mar", formatter.format(Calendar.MARCH+1))));
                        else if (matcher.replaceAll(replacement).toString().contains("April"))
                            list.add(String.valueOf(matcher.replaceAll(replacement).replace("April", formatter.format(Calendar.APRIL+1))));
                        else if (matcher.replaceAll(replacement).toString().contains("Apr"))
                            list.add(String.valueOf(matcher.replaceAll(replacement).replace("Apr", formatter.format(Calendar.APRIL+1))));
                        else if (matcher.replaceAll(replacement).toString().contains("May"))
                            list.add(String.valueOf(matcher.replaceAll(replacement).replace("May", formatter.format(Calendar.MAY+1))));
                        else if (matcher.replaceAll(replacement).toString().contains("June"))
                            list.add(String.valueOf(matcher.replaceAll(replacement).replace("June", formatter.format(Calendar.JUNE+1))));
                        else if (matcher.replaceAll(replacement).toString().contains("Jun"))
                            list.add(String.valueOf(matcher.replaceAll(replacement).replace("Jun", formatter.format(Calendar.JUNE+1))));
                        else if (matcher.replaceAll(replacement).toString().contains("July"))
                            list.add(String.valueOf(matcher.replaceAll(replacement).replace("July", formatter.format(Calendar.JULY+1))));
                        else if (matcher.replaceAll(replacement).toString().contains("Jul"))
                            list.add(String.valueOf(matcher.replaceAll(replacement).replace("Jul", formatter.format(Calendar.JULY+1))));
                        else if (matcher.replaceAll(replacement).toString().contains("August"))
                            list.add(String.valueOf(matcher.replaceAll(replacement).replace("August", formatter.format(Calendar.AUGUST+1))));
                        else if (matcher.replaceAll(replacement).toString().contains("Aug"))
                            list.add(String.valueOf(matcher.replaceAll(replacement).replace("Aug", formatter.format(Calendar.AUGUST+1))));
                        else if (matcher.replaceAll(replacement).toString().contains("September"))
                            list.add(String.valueOf(matcher.replaceAll(replacement).replace("September", formatter.format(Calendar.SEPTEMBER+1))));
                        else if (matcher.replaceAll(replacement).toString().contains("Sept"))
                            list.add(String.valueOf(matcher.replaceAll(replacement).replace("Sept", formatter.format(Calendar.SEPTEMBER+1))));
                        else if (matcher.replaceAll(replacement).toString().contains("Sep"))
                            list.add(String.valueOf(matcher.replaceAll(replacement).replace("Sep", formatter.format(Calendar.SEPTEMBER+1))));
                        else if (matcher.replaceAll(replacement).toString().contains("October"))
                            list.add(String.valueOf(matcher.replaceAll(replacement).replace("October", formatter.format(Calendar.OCTOBER+1))));
                        else if (matcher.replaceAll(replacement).toString().contains("Oct"))
                            list.add(String.valueOf(matcher.replaceAll(replacement).replace("Oct", formatter.format(Calendar.OCTOBER+1))));
                        else if (matcher.replaceAll(replacement).toString().contains("November"))
                            list.add(String.valueOf(matcher.replaceAll(replacement).replace("November", formatter.format(Calendar.NOVEMBER+1))));
                        else if (matcher.replaceAll(replacement).toString().contains("Nov"))
                            list.add(String.valueOf(matcher.replaceAll(replacement).replace("Nov", formatter.format(Calendar.NOVEMBER+1))));
                        else if (matcher.replaceAll(replacement).toString().contains("December"))
                            list.add(String.valueOf(matcher.replaceAll(replacement).replace("December", formatter.format(Calendar.DECEMBER+1))));
                        else if (matcher.replaceAll(replacement).toString().contains("Dec"))
                            list.add(String.valueOf(matcher.replaceAll(replacement).replace("Dec", formatter.format(Calendar.DECEMBER+1))));
                        else if (replace.toString().contains("Edited by:") || replace.toString().contains("CEURAUTHORS")){
                            values = matcher.replaceAll(replacement).split(",");
                            for(String val:values)
                                list.add(StringEscapeUtils.unescapeHtml(val));
                        }
                        else
                            if(!matcher.replaceAll(replacement).equals(""))
                            list.add(StringEscapeUtils.unescapeHtml(matcher.replaceAll(replacement)));
                    }        
                }
            }     
            }
        }
        else{
            Node doc2 = doc.get(0);
            NodeSelector nodeSelector = new NodeSelector(doc2);
            List<Node> selectedNodes = nodeSelector.select(expression);
            for(Node snode:selectedNodes){
                if(snode.getInnerHtml().toString() != null && !snode.getInnerHtml().toString().trim().replaceAll("[\\t\\n\\r\\s]{2,}", " ").equals("")){
                    String finVal;
                        if (expression.contains("CEURAUTHORS")){
                                String[] values = snode.getInnerHtml().toString().trim().replaceAll("[\\t\\n\\r\\s]{2,}", " ").split(",");
                                for(String val:values)
                                    list.add(StringEscapeUtils.unescapeHtml(val));
                            }
                        else{
                            finVal = checkforDate(StringEscapeUtils.unescapeHtml(snode.getInnerHtml().toString().trim().replaceAll("[\\t\\n\\r\\s]{2,}", " ")));
                            if(!finVal.equals(""))
                                list.add(finVal);
                        }
                }
            }
        }
        return list;
    }
    
    private String checkforDate(String value){
        DecimalFormat formatter = new DecimalFormat("00");
        
        if (value.equals("January") || value.equals("Jan"))
            value = String.valueOf(formatter.format(Calendar.JANUARY+1));
        else if (value.equals("February") || value.equals("Feb"))
            value = String.valueOf(formatter.format(Calendar.FEBRUARY+1));
        else if (value.equals("March") || value.equals("Mar"))
            value = String.valueOf(formatter.format(Calendar.MARCH+1));
        else if (value.equals("April") || value.equals("Apr"))
            value = String.valueOf(formatter.format(Calendar.APRIL+1));
        else if (value.contains("May") || value.equals("May"))
            value = String.valueOf(formatter.format(Calendar.MAY+1));
        else if (value.equals("June") || value.equals("Jun"))
            value = String.valueOf(formatter.format(Calendar.JUNE+1));
        else if (value.equals("July") || value.equals("July"))
            value = String.valueOf(formatter.format(Calendar.JULY+1));
        else if (value.equals("August") || value.equals("Aug"))
            value = String.valueOf(formatter.format(Calendar.AUGUST+1));
        else if (value.equals("September") || value.equals("Sep") || value.equals("Sept"))
            value = String.valueOf(formatter.format(Calendar.SEPTEMBER+1));
        else if (value.equals("October") || value.equals("Oct"))
            value = String.valueOf(formatter.format(Calendar.OCTOBER+1));
        else if (value.equals("November") || value.equals("Nov"))
            value = String.valueOf(formatter.format(Calendar.NOVEMBER+1));
        else if (value.equals("December") || value.equals("Dec"))
            value = String.valueOf(formatter.format(Calendar.DECEMBER+1));
       return value;
    }   
}
