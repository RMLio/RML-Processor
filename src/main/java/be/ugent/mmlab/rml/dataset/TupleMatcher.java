package be.ugent.mmlab.rml.dataset;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.rio.helpers.AbstractRDFHandler;


/**
 * RML Processor
 *
 * @author mielvandersande
 */
class TupleMatcher extends AbstractRDFHandler {

    private List<Statement> statements = new ArrayList<>();
    private Resource s;
    private IRI p;
    private Value o;

    public TupleMatcher(Resource s, IRI p, Value o, Resource... contexts) {
        this.s = s;
        this.p = p;
        this.o = o;
    }

    public List<Statement> getStatements() {
        return statements;
    }

    @Override
    public void handleStatement(Statement st) {
        if ((this.s == null || this.s.equals(st.getSubject())) && (this.p == null || this.p.equals(st.getPredicate())) && (this.o == null || this.o.equals(st.getObject()))) {
            statements.add(st);
        }
    }
}
