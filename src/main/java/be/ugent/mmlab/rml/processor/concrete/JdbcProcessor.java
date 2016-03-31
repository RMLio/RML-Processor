
package be.ugent.mmlab.rml.processor.concrete;

import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.model.dataset.RMLDataset;
import be.ugent.mmlab.rml.performer.RMLPerformer;
import be.ugent.mmlab.rml.processor.AbstractRMLProcessor;
import be.ugent.mmlab.rml.vocabularies.QLVocabulary;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.openrdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RML Processor
 *
 * @author andimou
 */
public class JdbcProcessor extends AbstractRMLProcessor  {
    private static final Logger log = 
            LoggerFactory.getLogger(JdbcProcessor.class.getSimpleName());
    
    JdbcProcessor(Map<String, String> parameters){
        TermMapProcessorFactory factory = new ConcreteTermMapFactory();
        this.termMapProcessor = factory.create(QLVocabulary.QLTerm.SQL_CLASS);
        this.parameters = parameters;
    }

    @Override
    public void execute(RMLDataset dataset, TriplesMap map, RMLPerformer performer, 
    InputStream input, String[] exeTriplesMap, boolean pomExecution) {
    ++enumerator;
        try {
            byte[] bytes = IOUtils.toByteArray(input);
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bis);
            Object object = ois.readObject();
            performer.perform(object, dataset, map, 
                        exeTriplesMap, parameters, pomExecution);
        } catch (IOException ex) {
            log.error("IOException " + ex);
        } catch (ClassNotFoundException ex) {
            log.error("ClassNotFoundException " + ex);
        }
        
    }

    @Override
    public void execute_node(RMLDataset dataset, String expression, 
    TriplesMap parentTriplesMap, RMLPerformer performer, Object node, 
    Resource subject, String[] exeTriplesMap, boolean pomExecution) {
        log.error("Not supported yet."); 
    }

}
