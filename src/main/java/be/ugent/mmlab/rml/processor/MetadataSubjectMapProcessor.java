package be.ugent.mmlab.rml.processor;

import be.ugent.mmlab.rml.condition.model.Condition;
import be.ugent.mmlab.rml.logicalsourcehandler.termmap.TermMapProcessor;
import be.ugent.mmlab.rml.logicalsourcehandler.termmap.TermMapProcessorFactory;
import be.ugent.mmlab.rml.logicalsourcehandler.termmap.concrete.ConcreteTermMapFactory;
import static be.ugent.mmlab.rml.model.RDFTerm.TermType.BLANK_NODE;
import static be.ugent.mmlab.rml.model.RDFTerm.TermType.IRI;
import be.ugent.mmlab.rml.model.RDFTerm.GraphMap;
import be.ugent.mmlab.rml.model.RDFTerm.SubjectMap;
import be.ugent.mmlab.rml.model.dataset.MetadataRMLDataset;
import be.ugent.mmlab.rml.model.dataset.RMLDataset;
import be.ugent.mmlab.rml.model.std.StdConditionSubjectMap;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang.RandomStringUtils;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.BNodeImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RML Processor
 *
 * @author andimou
 */
public class MetadataSubjectMapProcessor extends StdSubjectMapProcessor implements SubjectMapProcessor {
    private TermMapProcessor termMapProcessor ;
    
    // Log
    private static final Logger log = 
            LoggerFactory.getLogger(MetadataSubjectMapProcessor.class);
    
    
    @Override
    public void processSubjectTypeMap(RMLDataset originalDataset, 
    Resource subject, SubjectMap subjectMap, Object node) {

        boolean flag = false;
        Set<org.openrdf.model.URI> classIRIs = subjectMap.getClassIRIs();
        MetadataRMLDataset dataset = (MetadataRMLDataset) originalDataset ;
        
        List vocabs = dataset.getMetadataVocab();
        //TODO: Decide if I keep that here or if I move it to separate class
        if (vocabs != null) {
            if (vocabs.isEmpty()) {
                flag = true;
            } else {
                for (Object vocab : vocabs) {
                    if (vocab.toString().equals("prov")) {
                        flag = true;
                    }
                }
            }
        }
        if (subject != null) {
            for (org.openrdf.model.URI classIRI : classIRIs) {
                if (subjectMap.getGraphMaps().isEmpty()) {
                    List<Statement> triples =
                            dataset.tuplePattern(subject, RDF.TYPE, classIRI);
                    if (triples.size() == 0) {
                        dataset.add(subject, RDF.TYPE, classIRI);

                        /*if (dataset.getMetadataLevel().equals("triple")) {
                            if (flag == true) {
                                dataset.getMetadataDataset().addReification(
                                        subject, classIRI, subject, subjectMap.getOwnTriplesMap());
                            }

                        }*/

                    }
                } else {
                    for (GraphMap map : subjectMap.getGraphMaps()) {
                        if (map.getConstantValue() != null) {
                            dataset.add(
                                    subject, RDF.TYPE, classIRI,
                                    new URIImpl(map.getConstantValue().toString()));
                        }
                    }
                }
            }
        }
    }

    /*@Override
    public void processSubjectTypeMap(RMLDataset originalDataset, 
            Resource subject, SubjectMap subjectMap, Object node) {
        MetadataRMLDataset dataset = (MetadataRMLDataset) originalDataset;
        
        boolean flag = false;
        Set<org.openrdf.model.URI> classIRIs = subjectMap.getClassIRIs();
        String[] vocabs = dataset.getMetadataVocab();
        //TODO: Decide if I keep that here or if I move it to separate class
        if (vocabs != null) {
            if (vocabs.length == 0) {
                flag = true;
            } else {
                for (String vocab : vocabs) {
                    if (vocab.equals("prov")) {
                        flag = true;
                    }
                }
            }
        }
        if (subject != null) {
            for (org.openrdf.model.URI classIRI : classIRIs) {
                if (subjectMap.getGraphMaps().isEmpty()) {
                    List<Statement> triples =
                            dataset.tuplePattern(subject, RDF.TYPE, classIRI);
                    if (triples.size() == 0) {
                        dataset.add(subject, RDF.TYPE, classIRI);

                        if (dataset.getMetadataLevel().equals("triple")) {
                            /*if (flag == true) {
                                dataset.addReification(dataset.getMetadataDataset(),
                                        subject, classIRI, subject, subjectMap.getOwnTriplesMap());
                            }*/

   /*                     }

                    }
                } else {
                    for (GraphMap map : subjectMap.getGraphMaps()) {
                        if (map.getConstantValue() != null) {
                            dataset.add(
                                    subject, RDF.TYPE, classIRI,
                                    new URIImpl(map.getConstantValue().toString()));
                        }
                    }
                }
            }
        }
    }*/
}
