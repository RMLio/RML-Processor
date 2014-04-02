package be.ugent.mmlab.rml.processor.concrete;

import be.ugent.mmlab.rml.core.RMLPerformer;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.processor.AbstractRMLProcessor;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.antidot.semantic.rdf.model.impl.sesame.SesameDataSet;
import jodd.jerry.Jerry;
import jodd.lagarto.dom.Node;
import jodd.lagarto.dom.NodeSelector;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;


/**
 *
 * @author andimou
 */
public class CSS3Extractor extends AbstractRMLProcessor{
    
    private static Log log = LogFactory.getLog(AbstractRMLProcessor.class);
    private int enumerator;
    
    @Override
    public void execute(SesameDataSet dataset, TriplesMap map, RMLPerformer performer, String fileName) {       
        //this should not be needed to be defined within the extractor
        String reference = getReference(map.getLogicalSource());
        try {
            log.info("[CSS3Extractor] filename " + fileName);
            String content = get(fileName);
            
            Jerry doc = Jerry.jerry(content.trim());
            NodeSelector nodeSelector = new NodeSelector(doc.get(0));
            List<Node> selectedNodes = nodeSelector.select(reference);
            log.info("[CSS3Extractor] selectedNodes " +selectedNodes);
            for (int i = 0; i < selectedNodes.size(); i++) 
                performer.perform(selectedNodes.get(i).getHtml(), dataset, map);
        } catch (IOException ex) {
            Logger.getLogger(CSS3Extractor.class.getName()).log(Level.SEVERE, null, ex);
        }          
    }
    
    public static String get(String url) throws IOException {
		DefaultHttpClient httpclient = new DefaultHttpClient();

		HttpProtocolParams.setUserAgent(httpclient.getParams(), "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:9.0.1) Gecko/20100101 Firefox/9.0.1");

		HttpGet httpget = new HttpGet(url);
		HttpResponse response = httpclient.execute(httpget);

		HttpEntity entity = response.getEntity();
		if (entity == null) {
			return null;
		}

		String content = EntityUtils.toString(entity);
		httpclient.getConnectionManager().shutdown();
		return content;
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
        
        /*String months[] = {"January", "February", "March", "April",
                     "May", "June", "July", "August", "September",
                     "October", "November", "December"};
        String shortMonths[] = {"Jan", "Feb", "Mar", "Apr",
                     "May", "June", "July", "Aug", "Sep",
                     "Oct", "Nov", "Dec"};*/
       
        if(expression.equals("#")){
            list.add(Integer.toString(enumerator++));
            return list;
        }
        
        if(expression.contains("&")){  
            String[] valueList = null;
            String regex = expression.split("&")[1];
            expression = expression.split("&")[0];
            
            /*if(regex.startsWith("deduct:")){
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
            }*/
            
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
                else
                {   
                    //DecimalFormat formatter = new DecimalFormat("00");
                    Pattern replace = Pattern.compile(regex);
                    Matcher matcher = replace.matcher(value);
                    if(matcher.find()){
                        if(!matcher.replaceAll(replacement).equals("") && !matcher.replaceAll(replacement).equals(" ")){
                            String[] values = null;
                            /*if(matcher.replaceAll(replacement).matches("\\d{1}")){
                                int newInt = Integer.parseInt(matcher.replaceAll(replacement));                           
                                list.add(formatter.format(newInt).toString().trim());
                            }

                            for(int i=0; i<11; i++)
                                if( matcher.replaceAll(replacement).toString().contains(months[i]))
                                    list.add(String.valueOf(matcher.replaceAll(replacement).replace(months[i], formatter.format(i+1))));

                            for(int i=0; i<11; i++)
                                if( matcher.replaceAll(replacement).toString().contains(shortMonths[i]))
                                    list.add(String.valueOf(matcher.replaceAll(replacement).replace(shortMonths[i], formatter.format(i+1))));

                            if (replace.toString().contains("Edited by:") || replace.toString().contains("CEURAUTHORS")){
                                values = matcher.replaceAll(replacement).split(",");
                                for(String val:values)
                                    list.add(StringEscapeUtils.unescapeHtml(val));
                            }
                            else*/
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
                            //finVal = checkforDate(StringEscapeUtils.unescapeHtml(snode.getInnerHtml().toString().trim().replaceAll("[\\t\\n\\r\\s]{2,}", " ")));
                            finVal = StringEscapeUtils.unescapeHtml(snode.getInnerHtml().toString().trim().replaceAll("[\\t\\n\\r\\s]{2,}", " "));
                            if(!finVal.equals(""))
                                list.add(finVal);
                        }
                }
            }
        }
        return list;
    }
    
    private String checkforDate(String value){
         String months[] = {"January", "February", "March", "April",
                     "May", "June", "July", "August", "September",
                     "October", "November", "December"};
        String shortMonths[] = {"Jan", "Feb", "Mar", "Apr",
                     "May", "June", "July", "Aug", "Sep",
                     "Oct", "Nov", "Dec"};
       
        DecimalFormat formatter = new DecimalFormat("00");
        
        for(int i=0; i<11; i++)
            if( value.contains(months[i]))
                value = String.valueOf(formatter.format(i+1));
                         
        for(int i=0; i<11; i++)
            if( value.contains(shortMonths[i]) )
                value = String.valueOf(formatter.format(i+1));
                        
        
       return value;
    }   
}
