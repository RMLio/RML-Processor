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
/***************************************************************************
 *
 * R2RML Model : Standard SubjectMap Class
 *
 * A subject map is a term map. It specifies a rule
 * for generating the subjects of the RDF triples generated 
 * by a triples map.
 * 
 * modified by mielvandersande, andimou
 *
 ****************************************************************************/
package be.ugent.mmlab.rml.model.std;

import be.ugent.mmlab.rml.model.AbstractTermMap;
import be.ugent.mmlab.rml.model.GraphMap;
import be.ugent.mmlab.rml.model.SubjectMap;
import be.ugent.mmlab.rml.model.TermType;
import be.ugent.mmlab.rml.model.TriplesMap;
import be.ugent.mmlab.rml.condition.model.BindCondition;
import be.ugent.mmlab.rml.condition.model.EqualCondition;
import be.ugent.mmlab.rml.condition.model.ProcessCondition;
import be.ugent.mmlab.rml.condition.model.SplitCondition;
import be.ugent.mmlab.rml.model.reference.ReferenceIdentifier;
import java.util.HashSet;
import java.util.Set;

import net.antidot.semantic.rdf.model.tools.RDFDataValidator;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.InvalidR2RMLStructureException;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.InvalidR2RMLSyntaxException;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.R2RMLDataError;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.openrdf.model.URI;
import org.openrdf.model.Value;

public class StdSubjectMap extends AbstractTermMap implements SubjectMap {

	private Set<URI> classIRIs;
	private HashSet<GraphMap> graphMaps;
        private static Log log = LogFactory.getLog(StdSubjectMap.class);

	public StdSubjectMap(TriplesMap ownTriplesMap, Value constantValue,
			String stringTemplate, URI termType, String inverseExpression,
			ReferenceIdentifier referenceValue, Set<URI> classIRIs, Set<GraphMap> graphMaps)
			throws R2RMLDataError, InvalidR2RMLStructureException,
			InvalidR2RMLSyntaxException {
		// No Literal term type
		// ==> No datatype
		// ==> No specified language tag
		super(constantValue, null, null, stringTemplate, termType,
				inverseExpression, referenceValue);
		setClassIRIs(classIRIs);
		setGraphMaps(graphMaps);
		setOwnTriplesMap(ownTriplesMap);
	}
        
        public StdSubjectMap(TriplesMap ownTriplesMap, Value constantValue,
			String stringTemplate, URI termType, String inverseExpression,
			ReferenceIdentifier referenceValue, Set<URI> classIRIs, Set<GraphMap> graphMaps, 
                        String split, String process, String replace) throws R2RMLDataError,
			InvalidR2RMLStructureException, InvalidR2RMLSyntaxException {
		super(constantValue, null, null, stringTemplate, termType,
				inverseExpression, referenceValue,
                                split, process, replace);
		setClassIRIs(classIRIs);
		setGraphMaps(graphMaps);
		setOwnTriplesMap(ownTriplesMap);
	}
        
        /**
     *
     * @param ownTriplesMap
     * @param constantValue
     * @param stringTemplate
     * @param termType
     * @param inverseExpression
     * @param referenceValue
     * @param classIRIs
     * @param graphMaps
     * @param split
     * @param process
     * @param replace
     * @param equalCondition
     * @param processCondition
     * @param splitCondition
     * @param bindCondition
     * @throws R2RMLDataError
     * @throws InvalidR2RMLStructureException
     * @throws InvalidR2RMLSyntaxException
     */
    public StdSubjectMap(TriplesMap ownTriplesMap, Value constantValue,
			String stringTemplate, URI termType, String inverseExpression,
			ReferenceIdentifier referenceValue, Set<URI> classIRIs, Set<GraphMap> graphMaps, 
                        String split, String process, String replace, 
                        Set<EqualCondition> equalCondition, Set<ProcessCondition> processCondition, 
                        Set<SplitCondition> splitCondition, Set<BindCondition> bindCondition) 
                throws R2RMLDataError, InvalidR2RMLStructureException, InvalidR2RMLSyntaxException {
		super(constantValue, null, null, stringTemplate, termType,
				inverseExpression, referenceValue, split, process, replace, 
                                equalCondition, processCondition, splitCondition, bindCondition);
		setClassIRIs(classIRIs);
		setGraphMaps(graphMaps);
		setOwnTriplesMap(ownTriplesMap);
	}

        @Override
	public void setOwnTriplesMap(TriplesMap ownTriplesMap)
			throws InvalidR2RMLStructureException {
		// Update triples map if not contains this subject map
		if (ownTriplesMap != null && ownTriplesMap.getSubjectMap() != null)
			if (ownTriplesMap.getSubjectMap() != this)
				throw new IllegalStateException(
						"[StdSubjectMap:setSubjectMap] "
								+ "The own triples map "
								+ "already contains another Subject Map !");
			else
				ownTriplesMap.setSubjectMap(this);
		this.ownTriplesMap = ownTriplesMap;
	}

	private void setGraphMaps(Set<GraphMap> graphMaps) {
		this.graphMaps = new HashSet<GraphMap>();
		if (graphMaps != null)
			this.graphMaps.addAll(graphMaps);
	}

	private void setClassIRIs(Set<URI> classIRIs2) throws R2RMLDataError {
		this.classIRIs = new HashSet<URI>();
		if (classIRIs2 != null) {
			checkClassIRIs(classIRIs);
			classIRIs.addAll(classIRIs2);
		}
	}

	private void checkClassIRIs(Set<URI> classIRIs2) throws R2RMLDataError {
		// The values of the rr:class property must be IRIs.
		for (URI classIRI : classIRIs) {
			if (!RDFDataValidator.isValidURI(classIRI.stringValue()))
				throw new R2RMLDataError(
						"[AbstractTermMap:checkClassIRIs] Not a valid URI : "
								+ classIRI);
		}
	}

        @Override
	public Set<URI> getClassIRIs() {
		return classIRIs;
	}

        @Override
	protected void checkSpecificTermType(TermType tt)
			throws InvalidR2RMLStructureException {
		// If the term map is a subject map: rr:IRI or rr:BlankNode
		if ((tt != TermType.IRI) && (tt != TermType.BLANK_NODE)) {
			throw new InvalidR2RMLStructureException(
					"[StdSubjectMap:checkSpecificTermType] If the term map is a "
							+ "subject map: only rr:IRI or rr:BlankNode is required");
		}
	}

        @Override
	protected void checkConstantValue(Value constantValue)
			throws R2RMLDataError {
		// If the constant-valued term map is a subject map then its constant
		// value must be an IRI.
		if (!RDFDataValidator.isValidURI(constantValue.stringValue()))
			throw new R2RMLDataError(
					"[StdSubjectMap:checkConstantValue] Not a valid URI : "
							+ constantValue);
	}

        @Override
	public Set<GraphMap> getGraphMaps() {
		return graphMaps;
	}

        @Override
	public TriplesMap getOwnTriplesMap() {
		return ownTriplesMap;
	}

        @Override
	public String toString() {
		String result = super.toString() 
                        + " [StdSubjectMap : classIRIs = [";
		for (URI uri : classIRIs)
			result += uri.getLocalName() + ",";
		result += "], graphMaps = [";
		for (GraphMap graphMap : graphMaps)
			result += graphMap + ",";
		result += "]]";
		return result;
	}

}
