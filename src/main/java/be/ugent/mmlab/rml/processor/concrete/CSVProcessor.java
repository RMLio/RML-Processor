package be.ugent.mmlab.rml.processor.concrete;

import be.ugent.mmlab.rml.core.ConditionalJoinRMLPerformer;
import be.ugent.mmlab.rml.core.RMLMappingFactory;
import be.ugent.mmlab.rml.core.RMLPerformer;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.processor.AbstractRMLProcessor;
import com.csvreader.CsvReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.antidot.semantic.rdf.model.impl.sesame.SesameDataSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author mielvandersande, andimou
 */
public class CSVProcessor extends AbstractRMLProcessor {
    
    private static Log log = LogFactory.getLog(RMLMappingFactory.class);

    public void execute(SesameDataSet dataset, TriplesMap map, RMLPerformer performer) {
        InputStream fis = null;
        try {
            String identifier = getIdentifier(map.getLogicalSource());

            fis = new FileInputStream(identifier);

            //TODO: add character guessing
            CsvReader reader = new CsvReader(fis, Charset.defaultCharset());

            reader.readHeaders();
            //Iterate the rows
            while (reader.readRecord()) {
                HashMap<String, String> row = new HashMap<String, String>();

                for (String header : reader.getHeaders()) {
                    row.put(header, reader.get(header));
                }
                //let the performer handle the rows
                log.debug("[CSVProcessor:row] " + "row " + row.toString());
                performer.perform(row, dataset, map);         
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(CSVProcessor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CSVProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /*public void executeRefObjMap(SesameDataSet dataset, TriplesMap map, ConditionalJoinRMLPerformer performer, HashMap<String, String> joinMap) {
        InputStream fis = null;
        try {
            String identifier = getIdentifier(map.getLogicalSource());

            fis = new FileInputStream(identifier);

            //TODO: add character guessing
            CsvReader reader = new CsvReader(fis, Charset.defaultCharset());

            reader.readHeaders();
            //Iterate the rows
            while (reader.readRecord()) {
                HashMap<String, String> row = new HashMap<String, String>();
                
                for (String header : reader.getHeaders()) {
                        row.put(header, reader.get(header));
                }
                //let the performer handle the rows              
                for (Entry<String, String> entry : joinMap.entrySet()) {
                    if(row.get(entry.getKey().toString()).equals(entry.getValue())){
                        log.debug("[CSVProcessor:perform] " + "row " + row.toString());
                        performer.perform(row, dataset, map);
                    }
                }
                
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(CSVProcessor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CSVProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }*/
    
    public String[] extractValueFromNode(Object node, String expression) {
        HashMap<String, String> row = (HashMap<String, String>) node;
        //call the right header in the row
        return new String[]{row.get(expression)};
    }
}
