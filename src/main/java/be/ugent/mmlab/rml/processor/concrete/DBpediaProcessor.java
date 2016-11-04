package be.ugent.mmlab.rml.processor.concrete;

import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.model.dataset.RMLDataset;
import be.ugent.mmlab.rml.performer.RMLPerformer;
import be.ugent.mmlab.rml.processor.AbstractRMLProcessor;
import be.ugent.mmlab.rml.vocabularies.QLVocabulary;
import org.eclipse.rdf4j.model.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 */
public class DBpediaProcessor extends AbstractRMLProcessor {

    DBpediaProcessor(Map<String, String> parameters){
        TermMapProcessorFactory factory = new ConcreteTermMapFactory();
        this.termMapProcessor = factory.create(QLVocabulary.QLTerm.DBPEDIA_CLASS);
        this.parameters = parameters;
    }

    @Override
    public void execute(RMLDataset dataset, TriplesMap map, RMLPerformer performer, InputStream input, String[] exeTriplesMap, boolean pomExecution) {
        try {
            ObjectInputStream ois = new ObjectInputStream(input);
            HashMap<String, String> templateNode = (HashMap<String, String>) ois.readObject();

            performer.perform(
                    templateNode, dataset, map, exeTriplesMap, parameters, pomExecution);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void execute_node(RMLDataset dataset, String expression, TriplesMap parentTriplesMap, RMLPerformer performer, Object node, Resource subject, String[] exeTriplesMap, boolean pomExecution) {

    }
}
