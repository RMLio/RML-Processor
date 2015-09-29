package be.ugent.mmlab.rml.dataset;

import java.io.OutputStream;
import org.openrdf.rio.RDFFormat;

/**
 *
 * @author andimou
 */
public interface RMLDataset {

    public void closeRepository();

    public void addFile(String file, RDFFormat NQUADS);

    public void dumpRDF(OutputStream out, RDFFormat outform);

    public boolean isEqualTo(RMLDataset assertMap);

    public int getSize();
    
}
