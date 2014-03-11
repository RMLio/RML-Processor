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
        
        log.info("[CSS3 Extractor] href " + doc.$("[href]").html()); 
         
        nodeSelector = new NodeSelector(doc.get(0));

        List<Node> selectedNodes = nodeSelector.select(reference);
        for (int i = 0; i < selectedNodes.size(); i++) {
            Node n = selectedNodes.get(i);
            performer.perform(n.getHtml(), dataset, map);
        }
               
    }

    @Override
    public void execute_node(SesameDataSet dataset, TriplesMap map, TriplesMap parentTriplesMap, RMLPerformer performer, Object node) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<String> extractValueFromNode(Object node, String expression) {
        Jerry doc = Jerry.jerry(node.toString());
        log.info("[CSS3Extractor:extractValueFromNode] expression " + expression);
        List<String> list = new ArrayList();
        String value = StringEscapeUtils.unescapeHtml(doc.$(expression).html());
        list.add(value);
        return list;
    }
    
}
