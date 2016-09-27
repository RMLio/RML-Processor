package be.ugent.mmlab.rml.dataset;

import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.rio.helpers.AbstractRDFHandler;

/**
 * RML Processor
 *
 * @author mielvandersande
 */
class StatementCounter extends AbstractRDFHandler {

    private int countedStatements = 0;

    @Override
    public void handleStatement(Statement st) {
        countedStatements++;
    }

    public int getCountedStatements() {
        return countedStatements;
    }
}
