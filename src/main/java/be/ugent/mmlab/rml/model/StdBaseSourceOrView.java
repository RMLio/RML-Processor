/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.mmlab.rml.model;

import be.ugent.mmlab.rml.model.RMLBaseSourceOrView;

/**
 *
 * @author mielvandersande
 */
public class StdBaseSourceOrView implements RMLBaseSourceOrView{
    
    private String sourceName;

    public StdBaseSourceOrView(String sourceName) {
        if (sourceName == null)
			throw new IllegalArgumentException(
					"[StdBaseSourceOrView:construct] Source name must not have to be NULL.");
        
        //Add some validation step
//        if (!SourceValidator.isValidSourceIdentifier(sourceName))
//			throw new InvalidR2RMLSyntaxException(
//					"[StdBaseSourceOrView:construct] Source name must be a valid schema-qualified"
//							+ " name.");
        
        this.sourceName = sourceName;
    }
    
    

    public String getSourceName() {
        return sourceName;
    }

    public String getEffectiveSQLQuery() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
