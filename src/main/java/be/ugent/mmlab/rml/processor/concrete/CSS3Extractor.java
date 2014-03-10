package be.ugent.mmlab.rml.processor.concrete;

import be.ugent.mmlab.rml.core.RMLPerformer;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.processor.AbstractRMLProcessor;
import java.util.List;
import net.antidot.semantic.rdf.model.impl.sesame.SesameDataSet;
//import jodd.jerry.Jerry.jerry;
import jodd.jerry.Jerry;
import jodd.jerry.Jerry.JerryParser;
import jodd.lagarto.dom.LagartoDOMBuilder;


/**
 *
 * @author andimou
 */
public class CSS3Extractor extends AbstractRMLProcessor{

    @Override
    public void execute(SesameDataSet dataset, TriplesMap map, RMLPerformer performer, String fileName) {
        JerryParser jerryParser = Jerry.jerry();
        LagartoDOMBuilder domBuilder = (LagartoDOMBuilder) jerryParser.getDOMBuilder();
        domBuilder.enableHtmlMode();
        // more configuration...
        Jerry jerry = jerryParser.parse(fileName);
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
