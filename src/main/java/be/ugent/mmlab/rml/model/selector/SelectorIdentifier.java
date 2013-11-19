/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.mmlab.rml.model.selector;

/**
 *
 * @author mielvandersande
 */
public interface SelectorIdentifier {
    
    /** Made a replaceAll on the input String to replace all occurrence of
     * the "{parameter}" in.
     * @param input The input String
     * @return
     */
    public String replaceAll(String input, String replaceValue);
    
}
