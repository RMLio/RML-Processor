package be.ugent.mmlab.rml.input.extractor.concrete;

import be.ugent.mmlab.rml.input.model.InputSource;
import be.ugent.mmlab.rml.input.extractor.AbstractInputExtractor;
import be.ugent.mmlab.rml.input.model.std.ApiInputSource;
import be.ugent.mmlab.rml.sesame.RMLSesameDataSet;
import be.ugent.mmlab.rml.vocabulary.HydraVocabulary;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.log4j.LogManager;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

/**
 *
 * @author andimou
 */
public class ApiExtractor extends AbstractInputExtractor {
    
    // Log
    private static final org.apache.log4j.Logger log = LogManager.getLogger(ApiExtractor.class);
    // Value factory
    private static ValueFactory vf = new ValueFactoryImpl();


    @Override
    public Set<InputSource> extractInput(RMLSesameDataSet rmlMappingGraph, Resource resource) {
        Set<InputSource> inputSources = new HashSet<InputSource>();
        URI predicate = rmlMappingGraph.URIref(
                HydraVocabulary.HYDRA_NAMESPACE + HydraVocabulary.HydraTerm.TEMPLATE);
        
         List<Statement> statements = rmlMappingGraph.tuplePattern(
                        (Resource) resource, predicate, null);
         
         for(Statement statement : statements)
              inputSources.add(
                      new ApiInputSource(resource.stringValue(), statement.getObject().stringValue()));
         
         return inputSources;
         
    }
    
}
