package be.ugent.mmlab.rml.input.processor;

import be.ugent.mmlab.rml.model.TriplesMap;
import java.io.InputStream;
import org.apache.log4j.LogManager;


/**
 *
 * @author andimou
 */
public class AbstractInputProcessor implements InputProcessor {
    
    // Log
    private static final org.apache.log4j.Logger log = LogManager.getLogger(AbstractInputProcessor.class);
    
    @Override
    public InputStream getInputStream(TriplesMap triplesMap, String source) {
        InputStream inputStream = null;
        InputProcessor inputProcessor;
        
        switch (triplesMap.getLogicalSource().getInputSource().getClass().getSimpleName()) {
            case ("LocalFileSource"):
                inputProcessor = new LocalFileProcessor();
                inputStream = inputProcessor.getInputStream(triplesMap, source);
                break;
            case ("ApiInputSource"):
                inputProcessor = new ApiProcessor();
                inputStream = inputProcessor.getInputStream(triplesMap, source);
                break;
            case ("SparqlSdInputSource"):
                inputProcessor = new SparqlProcessor();
                inputStream = inputProcessor.getInputStream(triplesMap, source);
                break;
            /*case ("DbInputSource"):
                inputProcessor = new DbProcessor();
                inputStream = inputProcessor.getInputStream(triplesMap, source);
                break;*/
            default:
                log.error("Not identified input");
        }
        return inputStream;
    }
    
}
