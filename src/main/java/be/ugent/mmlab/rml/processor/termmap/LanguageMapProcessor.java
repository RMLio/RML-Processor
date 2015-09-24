package be.ugent.mmlab.rml.processor.termmap;

import be.ugent.mmlab.rml.model.RDFTerm.LanguageMap;
import be.ugent.mmlab.rml.model.RDFTerm.TermType;
import be.ugent.mmlab.rml.model.std.StdTemplateMap;
import be.ugent.mmlab.rml.model.termMap.ReferenceMap;
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
public class LanguageMapProcessor {
    
    // Log
    private static final Logger log = 
            LoggerFactory.getLogger(LanguageMapProcessor.class);
    
    public List<String> processLanguageMap(
            TermMapProcessor termMapProcessor, LanguageMap map, Object node) {
        
        if (map != null) {
            List<String> values = new ArrayList<>(), valueList = new ArrayList<>();

            switch (map.getLanguageMapType()) {
                case REFERENCE_VALUED:
                    //Get the expression and extract the value
                    ReferenceMap identifier = map.getReferenceMap();

                    values = termMapProcessor.extractValueFromNode(
                            node, identifier.getReference().toString().trim());

                    for (String value : values) {
                        if (valueList.isEmpty()) {
                            valueList.add(value.trim().replace("\n", " "));
                        }
                    }
                    return valueList;

                case CONSTANT_VALUED:
                    values.add(map.getConstantValue().stringValue().trim());
                    return values;

                case TEMPLATE_VALUED:
                    //Resolve the template
                    String template = map.getStringTemplate();
                    //Set<String> tokens = R2RMLToolkit.extractColumnNamesFromStringTemplate(template);
                    Set<String> tokens =
                            StdTemplateMap.extractVariablesFromStringTemplate(template);
                    for (String expression : tokens) {
                        List<String> replacements =
                                termMapProcessor.extractValueFromNode(node, expression);
                        for (int i = 0; i < replacements.size(); i++) {
                            if (values.size() < (i + 1)) {
                                values.add(template);
                            }
                            String replacement = null;
                            if (replacements.get(i) != null) {
                                replacement = replacements.get(i).trim();
                            }

                            if (replacement == null || replacement.equals("")) {
                                continue;
                            }

                            String temp = values.get(i).trim();
                            if (expression.contains("[")) {
                                expression = expression.replaceAll("\\[", "").replaceAll("\\]", "");
                                temp = temp.replaceAll("\\[", "").replaceAll("\\]", "");
                            }

                            try {
                                if (map.getLanguageMapType().toString().equals(TermType.IRI.toString())) {
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
                                log.error("Unsupported Encoding Exception " + ex);
                            }
                            values.set(i, temp.toString());

                        }
                    }

                    //Check if there are any placeholders left in the templates and remove uris that are not
                    List<String> validValues = new ArrayList<>();
                    for (String uri : values) {
                        StdTemplateMap templateMap = new StdTemplateMap(uri);
                        if (templateMap.extractVariablesFromStringTemplate(uri).isEmpty()) {
                            validValues.add(uri);
                        }
                    }
                    return validValues;

                default:
                    return values;
            }
        }

        return null;

    }

}
