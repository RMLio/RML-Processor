/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.mmlab.rml.extractor.input;

import be.ugent.mmlab.rml.Input.API;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.sesame.RMLSesameDataSet;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import org.apache.log4j.LogManager;
import org.openrdf.model.Resource;

/**
 *
 * @author andimou
 */
public class AbstractInputExtractor implements InputExtractor{
    
    // Log
    private static final org.apache.log4j.Logger log = LogManager.getLogger(AbstractInputExtractor.class);

    @Override
    public InputStream getInputStream(String source, TriplesMap triplesMap) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String extractInput(RMLSesameDataSet rmlMappingGraph, Resource resource) {
        return null;
    }

    @Override
    public String getInputSource(String reference, TriplesMap map) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Set<String> extractStringTemplate(
			String stringTemplate) {
		Set<String> result = new HashSet<String>();
		// Curly braces that do not enclose column names MUST be
		// escaped by a backslash character (“\”).
		stringTemplate = stringTemplate.replaceAll("\\\\\\{", "");
		stringTemplate = stringTemplate.replaceAll("\\\\\\}", "");
		if (stringTemplate != null) {
			StringTokenizer st = new StringTokenizer(stringTemplate, "{}", true);
			boolean keepNext = false;
			String next = null;
			while (st.hasMoreElements()) {
				String element = st.nextElement().toString();
				if (keepNext)
					next = element;
				keepNext = element.equals("{");
				if (element.equals("}") && element != null) {
					log.debug("Extracted variable name "
							+ next + " from string template " + stringTemplate);
					result.add(next);
					next = null;
				}
			}
		}
		return result;
	}
    
}
