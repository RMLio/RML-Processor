package be.ugent.mmlab.rml.processor.concrete;

import be.ugent.mmlab.rml.core.RMLPerformer;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.processor.AbstractRMLProcessor;
import be.ugent.mmlab.rml.processor.termmap.TermMapProcessorFactory;
import be.ugent.mmlab.rml.processor.termmap.concrete.ConcreteTermMapFactory;
import be.ugent.mmlab.rml.sesame.RMLSesameDataSet;
import be.ugent.mmlab.rml.vocabularies.QLVocabulary;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openrdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RML Processor
 *
 * @author andimou
 */
public class XLSXProcessor extends AbstractRMLProcessor {
    
    // Log
    private static final Logger log = 
            LoggerFactory.getLogger(XLSXProcessor.class);
    
    XLSXProcessor(){
        TermMapProcessorFactory factory = new ConcreteTermMapFactory();
        this.termMapProcessor = factory.create(QLVocabulary.QLTerm.XLSX_CLASS);
    }

    @Override
    public void execute(RMLSesameDataSet dataset, TriplesMap map, 
    RMLPerformer performer, InputStream input, boolean pomExecution) {
        XSSFWorkbook wb;
        try {
            OPCPackage pkg = OPCPackage.open(input);
            wb = new XSSFWorkbook(pkg);
            Sheet sheet = wb.getSheetAt(0);
            Row header = sheet.getRow(0);
            
            for (Row row : sheet) {
                HashMap<String, String> rowMap = new HashMap<>();
                Iterator<Cell> cellIterator = row.cellIterator();
                int enumerator = 0;
                while(cellIterator.hasNext() & row.getRowNum() != 0 ){
                    Cell currentCell = cellIterator.next();
                    rowMap.put(
                            header.getCell(enumerator).getStringCellValue(), 
                            currentCell.getStringCellValue());
                    enumerator++;
                }
                //let the performer handle the rows
                if(row.getRowNum() != 0)
                    performer.perform(rowMap, dataset, map, pomExecution);
            }
            
        } catch (FileNotFoundException ex) {
            log.error("File Not Found Exception " + ex);
        } catch (IOException ex) {
            log.error("IO Exception" + ex);
        } catch (InvalidFormatException ex) {
            log.error("Invalid Format Exception " + ex);
        } catch (EncryptedDocumentException ex) {
            log.error("Encrypted Document Exception " + ex);
        }
    }

    @Override
    public void execute_node(RMLSesameDataSet dataset, String expression, 
        TriplesMap parentTriplesMap, RMLPerformer performer, Object node, 
        Resource subject, boolean pomExecution) {
        throw new UnsupportedOperationException("Not supported yet."); 
        //TODO:implement this
    }

    @Override
    public String cleansing(String value) {
        return value;
    }

}
