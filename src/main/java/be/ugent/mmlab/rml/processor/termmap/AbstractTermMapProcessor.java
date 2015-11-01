package be.ugent.mmlab.rml.processor.termmap;

import be.ugent.mmlab.rml.model.RDFTerm.TermMap;
import static be.ugent.mmlab.rml.model.RDFTerm.TermMap.TermMapType.CONSTANT_VALUED;
import static be.ugent.mmlab.rml.model.RDFTerm.TermMap.TermMapType.REFERENCE_VALUED;
import static be.ugent.mmlab.rml.model.RDFTerm.TermMap.TermMapType.TEMPLATE_VALUED;
import be.ugent.mmlab.rml.model.RDFTerm.TermType;
import be.ugent.mmlab.rml.model.std.StdTemplateMap;
import be.ugent.mmlab.rml.model.termMap.ReferenceMap;
import be.ugent.mmlab.rml.vocabularies.QLVocabulary.QLTerm;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RML Processor
 *
 * @author andimou
 */
public abstract class AbstractTermMapProcessor implements TermMapProcessor{
    
    // Log
    private static final Logger log = 
            LoggerFactory.getLogger(AbstractTermMapProcessor.class);
    
    @Override
    public List<String> processTermMap(TermMap map, Object node) {

        List<String> values = new ArrayList<>(), valueList = new ArrayList<>();
        
        switch (map.getTermMapType()) {
            case REFERENCE_VALUED:
                //Get the expression and extract the value
                ReferenceMap identifier = map.getReferenceMap();
                values = extractValueFromNode(
                        node, identifier.getReference().toString().trim());

                for (String value : values) {
                    if (valueList.isEmpty()) {
                        valueList.add(value.trim().replace("\n", " "));
                    }
                }
                return valueList;

            case CONSTANT_VALUED:
                //Extract the value directly from the mapping
                values.add(map.getConstantValue().stringValue().trim());
                return values;

            case TEMPLATE_VALUED:
                //Resolve the template
                String template = map.getStringTemplate();
                //Set<String> tokens = R2RMLToolkit.extractColumnNamesFromStringTemplate(template);
                Set<String> tokens = 
                        StdTemplateMap.extractVariablesFromStringTemplate(template);
                for (String expression : tokens) {
                    List<String> replacements = extractValueFromNode(node, expression);
                    for (int i = 0; i < replacements.size(); i++) {
                        if (values.size() < (i + 1)) {
                            values.add(template);
                        }
                        String replacement = null;
                        if (replacements.get(i) != null) {
                            replacement = replacements.get(i).trim();
                        }

                        if (replacement == null || replacement.equals("")) {
                            //if the replacement value is null or empty, the reulting uri would be invalid, skip this.
                            //The placeholders remain which removes them in the end.
                            continue;
                        }

                        String temp = values.get(i).trim();
                        if (expression.contains("[")) {
                            expression = expression.replaceAll("\\[", "").replaceAll("\\]", "");
                            temp = temp.replaceAll("\\[", "").replaceAll("\\]", "");
                        }
                        //JSONPath expression cause problems when replacing, remove the $ first
                        if ((map.getOwnTriplesMap().getLogicalSource().getReferenceFormulation() == 
                                QLTerm.JSONPATH_CLASS)
                                && expression.contains("$")) {
                            expression = expression.replaceAll("\\$", "");
                            temp = temp.replaceAll("\\$", "");
                        }
                        try {
                            if (map.getTermType().toString().equals(TermType.IRI.toString())) {
                                //TODO: replace the following with URIbuilder
                                temp = temp.replaceAll("\\{" + Pattern.quote(expression) + "\\}",
                                        URLEncoder.encode(replacement, "UTF-8")
                                        .replaceAll("\\+", "%20")
                                        .replaceAll("\\%21", "!")
                                        .replaceAll("\\%27", "'")
                                        .replaceAll("\\%28", "(")
                                        .replaceAll("\\%29", ")")
                                        .replaceAll("\\%7E", "~"));
                            } else {
                                temp = temp.replaceAll("\\{" + expression + "\\}", replacement);
                            }
                            //Use encoding UTF-8 explicit URL encode; other one is deprecated 
                        } catch (UnsupportedEncodingException ex) {
                            log.error("UnsupportedEncodingException " + ex);
                        }
                        values.set(i, temp.toString());

                    }
                }
                
                //Check if there are any placeholders left in the templates and remove uris that are not
                List<String> validValues = new ArrayList<>();
                for (String uri : values){
                    StdTemplateMap templateMap = new StdTemplateMap(uri);
                    if (templateMap.extractVariablesFromStringTemplate(uri).isEmpty()){
                        validValues.add(uri);
                    }
                }
                return validValues;

            default:
                return values;
        }

    }
    
    @Override
    public String processTemplate(
            TermMap map, String expression, String template, String replacement) {
        log.error("Template processing...");
        if (expression.contains("[")) {
            expression = expression.replaceAll("\\[", "").replaceAll("\\]", "");
            template = template.replaceAll("\\[", "").replaceAll("\\]", "");
        }
        //JSONPath expression cause problems when replacing, remove the $ first
        if ((map.getOwnTriplesMap().getLogicalSource().getReferenceFormulation() == QLTerm.JSONPATH_CLASS)
                && expression.contains("$")) {
            expression = expression.replaceAll("\\$", "");
            template = template.replaceAll("\\$", "");
        }
        try {
            if (map.getTermType().toString().equals(TermType.IRI.toString())) {
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
            log.error("UnsupportedEncodingException " + ex);
        }
        return template.toString();
    }

}
