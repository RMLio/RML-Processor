package be.ugent.mmlab.rml.input.model.std;

import be.ugent.mmlab.rml.input.std.StdInputSource;



/**
 *
 * @author andimou
 */
public class ApiInputSource extends StdInputSource {
    private String template;
    
    public ApiInputSource(String name){
        super(name);
    }
    
    public ApiInputSource(String name, String template){
        super(name, template);
        setTemplate(template);
    }
    
    private void setTemplate(String template){
        this.template = template;
    }
    
    public String getTemplate(){
        return this.template ;
    }
    
}
