package be.ugent.mmlab.rml.input.model.std;

import be.ugent.mmlab.rml.input.std.StdInputSource;

/**
 *
 * @author andimou
 */
public class LocalFileSource extends StdInputSource  {
    //private String source;
    
    public LocalFileSource(String name){
        super(name);
    }
    
    public LocalFileSource(String name, String source){
        super(name, source);
    }
}
