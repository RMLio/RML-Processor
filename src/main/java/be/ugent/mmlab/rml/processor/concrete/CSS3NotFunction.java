package be.ugent.mmlab.rml.processor.concrete;

import java.util.Arrays;
import jodd.csselly.selector.PseudoFunction;
import jodd.lagarto.dom.Node;

/**
 *
 * @author pheyvaer
 */
public class CSS3NotFunction extends PseudoFunction {

    @Override
    public Object parseExpression(String expression) {
        return expression;
    }

    @Override
    public boolean match(Node node, Object expression) {
        String tag = (String) expression;
        return (node.getChildElementsCount() == 0) || getChildElementsCount(node, tag) == 0;
    }

    @Override
    public String getPseudoFunctionName() {
        return "not";
    }

    private int getChildElementsCount(Node node, String tag) {
        if (node.getChildElementsCount() == 0) {
            return 0;
        } else {
            Node[] childNodes = node.getChildNodes();
            int tagFound = 0;
            for (Node n : childNodes) {
                if (n.getNodeName() != null && n.getNodeName().equals(tag)) {
                    tagFound++;
                }
            }

            return tagFound;
        }
    }
}
