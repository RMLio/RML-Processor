/* 
 * Copyright 2011 Antidot opensource@antidot.net
 * https://github.com/antidot/db2triples
 * 
 * DB2Triples is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU General Public License as 
 * published by the Free Software Foundation; either version 2 of 
 * the License, or (at your option) any later version.
 * 
 * DB2Triples is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * *************************************************************************
 *
 * R2RML Vocabulary
 *
 * The R2RML vocabulary is the set of IRIs defined in this specification that
 * start with the rr: namespace IRI: http://www.w3.org/ns/r2rml#
 *
 ***************************************************************************
 */
package be.ugent.mmlab.rmlmapper;

import java.util.ArrayList;
import java.util.List;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.core.R2RMLVocabulary;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.core.R2RMLVocabulary.R2RMLTerm;

public class RMLVocabulary {

    // In this document, examples assume the following namespace 
    // prefix bindings unless otherwise stated:
    public static String RML_NAMESPACE = "http://mmlab.be/rml#";
    public static String QL_NAMESPACE = "http://mmlab.be/ql#";

    public enum RMLTerm {

        // RML CLASSES
        LOGICAL_SOURCE_CLASS("LogicalSource"),
        // RPROPERTIES
        QUERY_LANGUAGE("queryLanguage"),
        LOGICAL_SOURCE("logicalSource"),
        SELECTOR("selector"),
        QUERY("query"),
        VERSION("version"),
        SOURCE_NAME("sourceName");
        private String displayName;

        private RMLTerm(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    public enum QLTerm {

        XPATH_CLASS("XPath"),
        SQL_CLASS("SQL");
        private String displayName;

        private QLTerm(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }
    // Each property is included in a particular type of parent
    public static List<RMLTerm> triplesMapProperties = new ArrayList<RMLTerm>();

    static {
        triplesMapProperties.add(RMLTerm.LOGICAL_SOURCE);
    }
    public static List<RMLTerm> rmlViewProperties = new ArrayList<RMLTerm>();

    static {
        rmlViewProperties.add(RMLTerm.VERSION);
        rmlViewProperties.add(RMLTerm.QUERY);
    }
    public static List<RMLTerm> baseSourceOrViewProperties = new ArrayList<RMLTerm>();

    static {
        baseSourceOrViewProperties.add(RMLTerm.SOURCE_NAME);
    }

    // 
    public static QLTerm getQLTerms(String uri) {
        for (QLTerm qlterm : QLTerm.values()) {
            if ((QL_NAMESPACE + qlterm.toString()).equals(uri)){
                return qlterm;
            }
        }
        return null;
    }
    
    public static List<Enum> expressionProperties = new ArrayList<Enum>(); 
	static {
		expressionProperties.add(RMLTerm.QUERY);
                expressionProperties.add(RMLTerm.SELECTOR);
                expressionProperties.add(R2RMLTerm.TEMPLATE);
                expressionProperties.add(R2RMLTerm.PARENT);
                expressionProperties.add(R2RMLTerm.CHILD);
	} 
}
