package be.ugent.mmlab.rml.processor.concrete;

import be.ugent.mmlab.rml.core.RMLMappingFactory;
import be.ugent.mmlab.rml.core.RMLPerformer;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.processor.AbstractRMLProcessor;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jodd.csselly.CSSelly;
import jodd.csselly.CssSelector;
import jodd.io.FileUtil;
import net.antidot.semantic.rdf.model.impl.sesame.SesameDataSet;
//import jodd.jerry.Jerry.jerry;
import jodd.jerry.Jerry;
import jodd.jerry.Jerry.JerryParser;
import jodd.lagarto.dom.LagartoDOMBuilder;
import jodd.lagarto.dom.Node;
import jodd.lagarto.dom.NodeSelector;
import jodd.util.SystemUtil;
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
        JerryParser jerryParser = Jerry.jerry();
        //LagartoDOMBuilder domBuilder = (LagartoDOMBuilder) jerryParser.getDOMBuilder();
        //domBuilder.enableHtmlMode();
        // more configuration...
        Jerry doc = null;
        
        File file = new File(fileName);
        try {
            doc = Jerry.jerry(FileUtil.readString(file));
            log.info("[CSS3 Extractor] doc " + doc.html());
        } catch (IOException ex) {
            Logger.getLogger(CSS3Extractor.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        log.info("[CSS3 Extractor] year " + doc.$("span.CEURPUBYEAR").html());
        log.info("[CSS3 Extractor] title " + doc.$("span.CEURFULLTITLE").html());
        Jerry jerry = jerryParser.parse(fileName);
        
        log.info("[CSS3 Extractor] jerry " + jerry.get(0));
        
        CSSelly csselly = new CSSelly("head");
        List<CssSelector> selectors = csselly.parse();
        log.info("[CSS3 Extractor] cssselly " + csselly.toString());
        
        NodeSelector nodeSelector;
        //nodeSelector = new NodeSelector(*);
        //LinkedList<Node> selectedNodes = nodeSelector.select("div#jodd li");
        
        log.info("[CSS3 Extractor] jerry selects " + jerry.$("title").html());
        
    }

    @Override
    public void execute_node(SesameDataSet dataset, TriplesMap map, TriplesMap parentTriplesMap, RMLPerformer performer, Object node) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<String> extractValueFromNode(Object node, String expression) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
