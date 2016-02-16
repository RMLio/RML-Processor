package be.ugent.mmlab.rml.processor.concrete;

import be.ugent.mmlab.rml.model.dataset.RMLDataset;
import be.ugent.mmlab.rml.performer.RMLPerformer;
import be.ugent.mmlab.rml.model.LogicalSource;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.model.std.CsvwReferenceFormulation;
import be.ugent.mmlab.rml.processor.AbstractRMLProcessor;
import be.ugent.mmlab.rml.vocabularies.QLVocabulary.QLTerm;
import com.csvreader.CsvReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openrdf.model.Resource;

/**
 * RML Processor
 *
 * @author mielvandersande, andimou
 */
public class CSVProcessor extends AbstractRMLProcessor {

    // Log
    private static final Logger log = 
            LoggerFactory.getLogger(CSVProcessor.class);
    
    CSVProcessor(){
        TermMapProcessorFactory factory = new ConcreteTermMapFactory();
        this.termMapProcessor = factory.create(QLTerm.CSV_CLASS);
    }
    
    private char getDelimiter(LogicalSource ls) {
        String d = null;
        CsvwReferenceFormulation refForm =
                (CsvwReferenceFormulation) ls.getCustomReferenceFormulation();
        if (refForm == null) {
            return ',';
        } else {
            d = refForm.getDelimiter();
            if (d == null) {
                return ',';
            }
        }
        return d.charAt(0);
    }

    @Override
    public void execute(RMLDataset dataset, TriplesMap map, 
    RMLPerformer performer, InputStream input, 
    String[] exeTriplesMap, boolean pomExecution) {

        try {
            char delimiter = getDelimiter(map.getLogicalSource());

            //TODO: add charset guessing
            CsvReader reader = new CsvReader(input, Charset.defaultCharset());
            reader.setDelimiter(delimiter);
            
            reader.readHeaders();
            //Iterate the rows
            while (reader.readRecord()) {
                HashMap<String, String> row = new HashMap<>();
               for (String header : reader.getHeaders()) {
                   row.put(new String(header.getBytes("iso8859-1"), UTF_8), reader.get(header));
                }
                //let the performer handle the rows
                performer.perform(row, dataset, map, exeTriplesMap, pomExecution);
            }

        } catch (FileNotFoundException ex) {
            log.error("FileNotFoundException " + ex);
        } catch (IOException ex) {
            log.error("IOException " + ex);
        } 
    }

    @Override
    public void execute_node(
            RMLDataset dataset, String expression, 
            TriplesMap parentTriplesMap, RMLPerformer performer, Object node, 
            Resource subject, String[] exeTriplesMap, boolean pomExecution) {
        log.error("Not applicable for CSV sources."); 
        //TODO: implement this
    }

    
}
