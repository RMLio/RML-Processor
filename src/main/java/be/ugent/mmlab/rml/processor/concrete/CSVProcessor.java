package be.ugent.mmlab.rml.processor.concrete;

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
import java.util.logging.Level;
import java.util.logging.Logger;
import net.antidot.semantic.rdf.model.impl.sesame.SesameDataSet;

/**
 *
 * @author mielvandersande
 */
public class CSVProcessor extends AbstractRMLProcessor {

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
                performer.perform(row, dataset, map);
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(CSVProcessor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CSVProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String extractValueFromNode(Object node, String expression) {
        HashMap<String, String> row = (HashMap<String, String>) node;
        //call the right header in the row
        return row.get(expression);
    }
}
