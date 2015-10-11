package be.ugent.mmlab.rml.processor.concrete;

import be.ugent.mmlab.rml.model.dataset.RMLDataset;
import be.ugent.mmlab.rml.performer.RMLPerformer;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.processor.AbstractRMLProcessor;
import be.ugent.mmlab.rml.processor.termmap.TermMapProcessorFactory;
import be.ugent.mmlab.rml.processor.termmap.concrete.ConcreteTermMapFactory;
import be.ugent.mmlab.rml.vocabularies.QLVocabulary.QLTerm;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import jodd.csselly.selector.PseudoFunctionSelector;
import jodd.jerry.Jerry;
import jodd.lagarto.dom.Node;
import jodd.lagarto.dom.NodeSelector;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openrdf.model.Resource;


/**
 * RML Processor
 *
 * @author andimou
 */
public class CSS3Extractor extends AbstractRMLProcessor {

    // Log
    private static final Logger log = 
            LoggerFactory.getLogger(CSS3Extractor.class);
    private int enumerator;

    public CSS3Extractor() {
        TermMapProcessorFactory factory = new ConcreteTermMapFactory();
        this.termMapProcessor = factory.create(QLTerm.CSS3_CLASS);
        PseudoFunctionSelector.registerPseudoFunction(CSS3NotFunction.class);
    }

    @Override
    public void execute(RMLDataset dataset, TriplesMap map,
            RMLPerformer performer, InputStream input,
            String[] exeTriplesMap, boolean pomExecution) {
        //this should not be needed to be defined within the extractor
        String reference = getReference(map.getLogicalSource());
        // more configuration...
        Jerry doc = null;
        try {
            doc = Jerry.jerry(IOUtils.toString(input, "UTF-8"));
        } catch (IOException ex) {
            log.error("IO Exception " + ex);
        }
        NodeSelector nodeSelector;
        nodeSelector = new NodeSelector(doc.get(0));

        List<Node> selectedNodes = nodeSelector.select(reference);
        for (int i = 0; i < selectedNodes.size(); i++) {
            performer.perform(selectedNodes.get(i).getHtml(), dataset, map,
                    exeTriplesMap, pomExecution);
        }
    }

    @Override
    public void execute_node(
            RMLDataset dataset, String expression, 
            TriplesMap parentTriplesMap, RMLPerformer performer, Object node, 
            Resource subject, String[] exeTriplesMap, boolean pomExecution) {
        if (expression.startsWith("+")) {
            expression = expression.substring(1);
        }

        Jerry doc = Jerry.jerry(node.toString());
        NodeSelector nodeSelector = new NodeSelector(doc.get(0));

        List<Node> selectedNodes = nodeSelector.select(expression.trim());
        for (Node selectNode : selectedNodes) {
            performer.perform(selectNode.getHtml(), dataset, parentTriplesMap, 
                    exeTriplesMap, false);
        }
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
