
package be.ugent.mmlab.rml.processor;

import be.ugent.mmlab.rml.model.RDFTerm.TermMap;
import be.ugent.mmlab.rml.model.RDFTerm.TermType;
import be.ugent.mmlab.rml.model.std.StdTemplateMap;
import be.ugent.mmlab.rml.processor.termmap.TermMapProcessor;
import be.ugent.mmlab.rml.processor.termmap.TermMapProcessorFactory;
import be.ugent.mmlab.rml.processor.termmap.concrete.ConcreteTermMapFactory;
import be.ugent.mmlab.rml.vocabularies.QLVocabulary;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RML Processor
 *
 * @author andimou
 */
public class TemplateProcessor {
    // Log
    private static final Logger log = 
            LoggerFactory.getLogger(TemplateProcessor.class);
    
    private TermMapProcessor termMapProcessor ;
    
    public TemplateProcessor(TermMap map){
        TermMapProcessorFactory factory = new ConcreteTermMapFactory();
        this.termMapProcessor = factory.create(
                map.getOwnTriplesMap().getLogicalSource().getReferenceFormulation());
    }
    
    //TODO:move this to Term Map processor
    public List<String> processTemplate(
            TermMap map, List<String> replacements, String expression) {
        List<String> values = new ArrayList<>(), validValues = new ArrayList<>();
        String template = map.getStringTemplate();

        for (int i = 0; i < replacements.size(); i++) {
            if (values.size() < (i + 1)) {
                values.add(template);
            }
            String replacement = replacements.get(i);
            if (replacement != null || !replacement.equals("")) {
                if (!replacement.isEmpty()) {
                    String temp = this.termMapProcessor.processTemplate(
                            map, expression, template, replacement);
                    template = temp;
                    if (StdTemplateMap.extractVariablesFromStringTemplate(temp).isEmpty()) {
                        validValues.add(temp);
                    }
                }

            } else {
                log.debug("No suitable replacement for template " + template + ".");
                return null;
            }
        }

        return validValues;
    }
    
    //TODO:move this to Term Map processor?
    public String processTemplate(String expression, String template, String termType,
            QLVocabulary.QLTerm referenceFormulation, String replacement) {
        if (expression.contains("[")) {
            expression = expression.replaceAll("\\[", "").replaceAll("\\]", "");
            template = template.replaceAll("\\[", "").replaceAll("\\]", "");
        }
        //JSONPath expression cause problems when replacing, remove the $ first
        if ((referenceFormulation == QLVocabulary.QLTerm.JSONPATH_CLASS)
                && expression.contains("$")) {
            expression = expression.replaceAll("\\$", "");
            template = template.replaceAll("\\$", "");
        }
        try {
            if (termType.equals(TermType.IRI.toString())) {
                //TODO: replace the following with URIbuilder
                template = template.replaceAll("\\{" + Pattern.quote(expression) + "\\}",
                        URLEncoder.encode(replacement, "UTF-8")
                        .replaceAll("\\+", "%20")
                        .replaceAll("\\%21", "!")
                        .replaceAll("\\%27", "'")
                        .replaceAll("\\%28", "(")
                        .replaceAll("\\%29", ")")
                        .replaceAll("\\%7E", "~"));
            } else {
                template = template.replaceAll("\\{" + expression + "\\}", replacement);
            }
        } catch (UnsupportedEncodingException ex) {
            log.error("Unsupported Encoding Exception " + ex);
        }
        return template.toString();
    }

}
