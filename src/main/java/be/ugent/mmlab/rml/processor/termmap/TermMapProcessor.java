package be.ugent.mmlab.rml.processor.termmap;

import be.ugent.mmlab.rml.model.RDFTerm.TermMap;
import java.util.List;

/**
 * RML Processor
 *
 * @author andimou
 */
public interface TermMapProcessor {
    
    /**
     * Resolve an expression and extract a single string value from a node
     * @param node current object
     * @param expression reference to value
     * @return extracted value
     */
    public List<String> extractValueFromNode(Object node, String expression);
    
    /**
     *
     * @param map
     * @param expression
     * @param template
     * @param replacement
     * @return
     */
    public String processTemplate(TermMap map, String expression, String template, String replacement);
    
    
    /**
     *
     * @param map
     * @param node
     * @return
     */
    public List<String> processTermMap(TermMap map, Object node);
    
}
