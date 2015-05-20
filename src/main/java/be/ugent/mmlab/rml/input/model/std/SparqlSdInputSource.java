package be.ugent.mmlab.rml.input.model.std;

import be.ugent.mmlab.rml.input.std.StdInputSource;

/**
 *
 * @author andimou
 */
public class SparqlSdInputSource extends StdInputSource {
    
    private String template;
    
    public SparqlSdInputSource(String name, String template){
        super(name);
        setTemplate(template);
    }
    
    private void setTemplate(String template){
        this.template = template;
    }
    
    public String setTemplate(){
        return this.template ;
    }
}
