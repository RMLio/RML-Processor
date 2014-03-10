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
        
        // more configuration...
        Jerry doc = null;
        
        File file = new File(fileName);
        try {
            doc = Jerry.jerry(FileUtil.readString(file));
        } catch (IOException ex) {
            Logger.getLogger(CSS3Extractor.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //working examples
        //log.info("[CSS3 Extractor] year " + doc.$("span.CEURPUBYEAR").html());
        //log.info("[CSS3 Extractor] title " + doc.$("span.CEURFULLTITLE").html());
        
        performer.perform(doc, dataset, map);
        
    }

    @Override
    public void execute_node(SesameDataSet dataset, TriplesMap map, TriplesMap parentTriplesMap, RMLPerformer performer, Object node) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<String> extractValueFromNode(Object node, String expression) {
        Jerry doc = (Jerry) node;
        
        log.info("[CSS3 Extractor] year " + doc.$(expression).html());
        
        List<String> list = new ArrayList();
        list.add(doc.$(expression).html());
        return list;

    }
    
}
