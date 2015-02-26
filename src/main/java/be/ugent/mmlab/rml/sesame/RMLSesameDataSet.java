/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.mmlab.rml.sesame;

import info.aduna.iteration.Iterations;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import net.antidot.semantic.rdf.model.impl.sesame.SesameDataSet;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.UnsupportedQueryLanguageException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.http.HTTPRepository;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;
import org.openrdf.sail.SailException;
import org.openrdf.sail.inferencer.fc.CustomGraphQueryInferencer;
import org.openrdf.sail.inferencer.fc.ForwardChainingRDFSInferencer;
import org.openrdf.sail.memory.MemoryStore;
import org.openrdf.sail.nativerdf.NativeStore;

/**
 *
 * @author andimou
 */
public class RMLSesameDataSet extends SesameDataSet {
    
    private Repository currentRepository = null;

    // useful -local- constants
    static RDFFormat NTRIPLES = RDFFormat.NTRIPLES;
    static RDFFormat N3 = RDFFormat.N3;
    static RDFFormat RDFXML = RDFFormat.RDFXML;

    static RDFFormat Turtle = RDFFormat.TURTLE;
    static RDFFormat TURTLE = RDFFormat.TURTLE;
    static String RDFTYPE = RDF.TYPE.toString();

    // Log
    private static final Logger log = LogManager.getLogger(RMLSesameDataSet.class);
       
    public RMLSesameDataSet() {
		this(true);
	}
    
    public RMLSesameDataSet(boolean inferencing) {
        try {
            if (inferencing) {
                log.debug("inference enabled");

                String pre =
                          "PREFIX rml: <http://semweb.mmlab.be/ns/rml#>\n"
                        + "PREFIX rr:  <http://www.w3.org/ns/r2rml#>";
                
                String rule =
                        pre
                        + " CONSTRUCT { "
                        + "?tm <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/r2rml#TriplesMap> .  "
                        + "?ls <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/r2rml#LogicalSource> .  "
                        + "?sm <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/r2rml#SubjectMap> . "
                        + "?pom <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/r2rml#PredicateObjectMap> ."
                        + "?pm <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/r2rml#PredicateMap> ."
                        + "?om <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/r2rml#ObjectMap> ."
                        + "?jc <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/r2rml#JoinCondition> . "
                        + "?gm <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/r2rml#GraphMap> . } "
                        + " WHERE {"
                        + " ?tm <http://semweb.mmlab.be/ns/rml#logicalSource> ?ls ."
                        + " OPTIONAL {"
                        + "?tm <http://www.w3.org/ns/r2rml#predicateObjectMap> ?pom ."
                        + "?pom <http://www.w3.org/ns/r2rml#predicateMap> ?pm ."
                        + "?pom <http://www.w3.org/ns/r2rml#objectMap> ?om . }"
                        + " OPTIONAL {"
                        + "?om <http://www.w3.org/ns/r2rml#joinCondition> ?jc. }"
                        + " OPTIONAL { "
                        + "?x <http://www.w3.org/ns/r2rml#graphMap> ?gm . } "
                        + "}";
                String match =
                        pre
                        + " CONSTRUCT { "
                        + "?tm <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/r2rml#TriplesMap> . "
                        + "?ls <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/r2rml#LogicalSource> ."
                        + "?sm <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/r2rml#SubjectMap> . "
                        + "?pom <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/r2rml#PredicateObjectMap> ."
                        + "?pm <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/r2rml#PredicateMap> ."
                        + "?om <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/r2rml#ObjectMap> ."
                        + "?jc <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/r2rml#JoinCondition> ."
                        + "?gm <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/r2rml#GraphMap> . }"
                        + " WHERE { "
                        + "?tm <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/r2rml#TriplesMap> . "
                        + "?ls <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/r2rml#LogicalSource> ."
                        + "?sm <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/r2rml#SubjectMap> ."
                        + "OPTIONAL { "
                        + "?pom <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/r2rml#PredicateObjectMap> . "
                        + "?pm <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/r2rml#PredicateMap> ."
                        + "?om <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/r2rml#ObjectMap> . }"
                        + " OPTIONAL {"
                        + "?jc <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/r2rml#JoinCondition> . }"
                        + "OPTIONAL { "
                        + "?gm <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/ns/r2rml#GraphMap> . } "
                        + "}";
                currentRepository = new SailRepository(new CustomGraphQueryInferencer(
                        new MemoryStore(), QueryLanguage.SPARQL, rule, match));   
            } else {
                log.error("inference disabled");
                currentRepository = new SailRepository(new MemoryStore());
            }
                currentRepository.initialize();
        } catch (RepositoryException e) {
            log.error(Thread.currentThread().getStackTrace()[1].getMethodName() + ": " + e);
        } catch (MalformedQueryException ex) {
            log.error(Thread.currentThread().getStackTrace()[1].getMethodName() + ": " + ex);
        } catch (UnsupportedQueryLanguageException ex) {
            log.error(Thread.currentThread().getStackTrace()[1].getMethodName() + ": " + ex);
        } catch (SailException ex) {
            log.error(Thread.currentThread().getStackTrace()[1].getMethodName() + ": " + ex);
        } finally {
        }
    }
  

    public RMLSesameDataSet(String pathToDir, boolean inferencing) {
        File f = new File(pathToDir);
        try {
            if (inferencing) {
                currentRepository = new SailRepository(
                        new ForwardChainingRDFSInferencer(new NativeStore(f)));
            } else {
                currentRepository = new SailRepository(new NativeStore(f));
            }
            currentRepository.initialize();
        } catch (RepositoryException e) {
            e.printStackTrace();
        }

    }

	public RMLSesameDataSet(String sesameServer, String repositoryID) {
		currentRepository = new HTTPRepository(sesameServer, repositoryID);
		try {
			currentRepository.initialize();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}

    /**
     * Load data in specified graph (use default graph if contexts is null)
     *
     * @param filePath
     * @param format
     * @param contexts
     * @throws RepositoryException
     * @throws IOException
     * @throws RDFParseException
     */
    @Override
    
    public void loadDataFromFile(String filePath, RDFFormat format,
            Resource... contexts) throws RepositoryException, IOException, RDFParseException {

        RepositoryConnection con = currentRepository.getConnection();
        try {
            // upload a file
            File f = new File(filePath);
            try{
            con.add(f, null, RDFFormat.TURTLE);
            con.commit();
            }
            catch(Exception e){
                log.error( 
                    Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                    + "Syntax error: " 
                    + e );
            }
        } finally {
            try {
                con.close();
            } catch (RepositoryException e) {
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public void addFile(String filepath, RDFFormat format) {
        RepositoryConnection con = null;
        try {
            con = currentRepository.getConnection();
            File file = new File(filepath);
            con.add(file, "", format);
            con.commit();
        } catch (RepositoryException ex) {
            log.error("ex " + ex);
            java.util.logging.Logger.getLogger(RMLSesameDataSet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(RMLSesameDataSet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RDFParseException ex) {
            java.util.logging.Logger.getLogger(RMLSesameDataSet.class.getName()).log(Level.SEVERE, null, ex);
        } 
        finally {
            try {
                con.close();
            } catch (RepositoryException ex) {
                java.util.logging.Logger.getLogger(RMLSesameDataSet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @Override
    public void add(Resource s, URI p, Value o, Resource... contexts) {

        try {
            RepositoryConnection con = currentRepository.getConnection();
            try {
                ValueFactory myFactory = con.getValueFactory();
                Statement st = myFactory.createStatement((Resource) s, p,
                        (Value) o);
                if(s == null || o == null)
                    log.debug(
                        Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                        + "Added triple without checking if it's BNode (" + s.stringValue()
                        + ", " + p.stringValue() + ", " + o.stringValue() + ").");
                con.add(st, contexts);
                con.commit();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                con.close();
            }
        } catch (Exception e) {
            // handle exception
        }
    }
    
    /**
     *
     * @param s
     * @param p
     * @param o
     * @param contexts
     */
    @Override
    public void remove(Resource s, URI p, Value o, Resource... contexts) {

        try {
            RepositoryConnection con = currentRepository.getConnection();
            

            ValueFactory myFactory = con.getValueFactory();
            if(p.stringValue().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")){
                Statement st = myFactory.createStatement((Resource) s, p,
                        (Value) o);
                log.error("stupid rdf:type");
                con.remove(st);
                con.remove(s, null, o);
            }
            
            Statement st = myFactory.createStatement((Resource) s, p,
                    (Value) o);
            RepositoryResult<Statement> statements = con.getStatements(s, p, o, false);
            Model aboutToDelete = Iterations.addAll(statements, new LinkedHashModel());
            con.remove(s, null, o, contexts);
            con.remove(aboutToDelete);
            con.remove( s, p, o, contexts);
            con.remove((Resource) s, (URI) p, (Value) o, contexts);
            con.remove(st, contexts);
            con.commit();
            con.close();
        } catch (Exception e) {
            log.error(e);
        } finally {
            //con.close();
        }
    }
    
    public void remove(Statement st, Resource... contexts) {

        try {
            RepositoryConnection con = currentRepository.getConnection();
            try {
                log.error("statement " + st);
                //con.remove((Resource) s, (URI) p, (Value) o, contexts);
                con.remove(st, contexts);
                con.commit();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                con.close();
            }
        } catch (Exception e) {
            // handle exception
        }
    }
    
    @Override
    public List<Statement> tuplePattern(Resource s, URI p, Value o,
            Resource... contexts) {
        try {
            RepositoryConnection con = currentRepository.getConnection();
            try {
                RepositoryResult<Statement> repres = con.getStatements(s, p, o, true, contexts);
                ArrayList<Statement> reslist = new ArrayList<Statement>();
                while (repres.hasNext()) {
                    reslist.add(repres.next());
                }
                return reslist;
            } finally {
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    
    @Override
    public String printRDF(RDFFormat outform) {
        try {
            RepositoryConnection con = currentRepository.getConnection();
            try {
                //ByteArrayOutputStream out = new ByteArrayOutputStream();
                //RDFWriter w = Rio.createWriter(outform, out);
                RepositoryResult<Statement> statements = con.getStatements(null, null, null, true);
                Model model = Iterations.addAll(statements, new LinkedHashModel());
                Rio.write(model, System.out, RDFFormat.TURTLE);
                //con.export(w);
                //String result = new String(out.toByteArray(), "UTF-8");
                //log.info("write result " + result);
                //return result;
                return null;
            } finally {
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void printRDFtoFile(String outputFile, RDFFormat outform) {
        
        Model model; // a collection of several RDF statements
        FileOutputStream out;
        if (outputFile != null) {
            try {
                out = new FileOutputStream(outputFile);
                RDFWriter writer = Rio.createWriter(RDFFormat.TURTLE, out);
                writer.startRDF();
                RepositoryConnection con = currentRepository.getConnection();
                RepositoryResult<Statement> statements = con.getStatements(null, null, null, true);
                con.close();
                model = Iterations.addAll(statements, new LinkedHashModel());
                Rio.write(model, out, RDFFormat.TURTLE);
                //writer.endRDF();
            } catch (RDFHandlerException e) {
                log.error(e);
            } catch (RepositoryException ex) {
                log.error(ex);
            } catch (FileNotFoundException ex) {
                log.error(ex);
            }
        }
    }
        
    public void skolemization(RMLSesameDataSet rmlMappingGraph) {
        TupleQueryResult result = null;
        try {
            RepositoryConnection con = currentRepository.getConnection();
            String queryString = "SELECT ?p WHERE { ?x ?p ?y } ";
            TupleQuery tupleQuery;
            tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
            result = tupleQuery.evaluate();

        } catch (RepositoryException ex) {
            java.util.logging.Logger.getLogger(RMLSesameDataSet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedQueryException ex) {
            java.util.logging.Logger.getLogger(RMLSesameDataSet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (QueryEvaluationException ex) {
            java.util.logging.Logger.getLogger(RMLSesameDataSet.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            while (result.hasNext()) {  // iterate over the result
                BindingSet bindingSet = result.next();
                Value valueOfX = bindingSet.getValue("x");
                Value valueOfY = bindingSet.getValue("y");
                log.debug(//Thread.currentThread().getStackTrace()[1].getMethodName() + ": "
                        "\n valueOfX "
                        + valueOfX.stringValue()
                        + "\n valueOfY"
                        + valueOfY.stringValue());
            }
        } catch (QueryEvaluationException ex) {
            java.util.logging.Logger.getLogger(RMLSesameDataSet.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
}
