/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.mmlab.model.selector;

import net.antidot.sql.model.db.ColumnIdentifier;
import net.antidot.sql.model.type.SQLType;

/**
 *
 * @author mielvandersande
 */
public class SelectorIdentifierImpl implements SelectorIdentifier {
    
    private String selector = null;
    
    private SelectorIdentifierImpl(String selector) {
	this.selector = selector;
    }
    
    
    
    /**
     * Build a Selector Identifier from a RML config file.
     * 
     * @param selectorName
     *            The selector.
     * @return
     */
    public static SelectorIdentifierImpl buildFromR2RMLConfigFile(String selector) {
	if (selector == null) {
            return null;
        }
        
	// Be optimist...
	return new SelectorIdentifierImpl(selector);
    }

    public String replaceAll(String input, String replaceValue) {
        // Try simple replace...
	String localResult = input.replaceAll("\\{" + selector + "\\}",
		replaceValue);
        // Must have replaced something
	assert !localResult.equals(input) : ("Impossible to replace "
		+ selector + " in " + input);
	return localResult;
    }
    
}
