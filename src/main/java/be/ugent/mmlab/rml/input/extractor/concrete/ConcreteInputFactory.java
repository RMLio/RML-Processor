package be.ugent.mmlab.rml.input.extractor.concrete;

import be.ugent.mmlab.rml.input.InputFactory;
import be.ugent.mmlab.rml.input.model.InputSource;
import be.ugent.mmlab.rml.input.extractor.InputExtractor;
import be.ugent.mmlab.rml.sesame.RMLSesameDataSet;
import java.util.List;
import java.util.Set;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.vocabulary.RDF;

/**
 *
 * @author andimou
 */
public class ConcreteInputFactory implements InputFactory {
    
    // Log
    private static final Logger log = LogManager.getLogger(ConcreteInputFactory.class);
    
    public Set<InputSource> chooseInput(RMLSesameDataSet rmlMappingGraph, Resource resource){
        InputExtractor input ;
        Set<InputSource> inputSources = null;
        
        List<Statement> inputStatement = rmlMappingGraph.tuplePattern(
                        (Resource) resource, RDF.TYPE, null);
        
        switch(inputStatement.get(0).getObject().stringValue().toString()){
            case ("http://www.w3.org/ns/hydra/core#APIDocumentation"):
                input = new ApiExtractor();
                inputSources = input.extractInput(rmlMappingGraph, resource);
                break;
            case ("http://www.w3.org/ns/sparql-service-description#Service"):
                input = new SparqlExtractor();
                inputSources = input.extractInput(rmlMappingGraph, resource);
                break;
            /*case("http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#Database"):
                input = new DbExtractor();
                inputSources = input.extractInput(rmlMappingGraph, resource);
                break;*/
            default:
                log.error("Not identified input");
        }
        
        return inputSources;
        
    }
    
}
