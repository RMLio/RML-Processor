/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.mmlab.rml.vocabulary;

import org.apache.log4j.LogManager;

/**
 *
 * @author andimou
 */
public class HydraVocabulary {
    // Log
    private static final org.apache.log4j.Logger log = LogManager.getLogger(HydraVocabulary.class);
    
    public static String HYDRA_NAMESPACE = "http://www.w3.org/ns/hydra/core#";
    
    public enum HydraTerm implements Term{

        // RML CLASSES
        API_DOCUMENTATION_CLASS("APIDocumentation"),
        // RPROPERTIES
        REFERENCE_FORMULATION("template");
        
        private String displayName;

        private HydraTerm(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
        
    }
    
}
