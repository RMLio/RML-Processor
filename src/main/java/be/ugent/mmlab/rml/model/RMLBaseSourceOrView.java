/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.mmlab.rml.model;

import net.antidot.semantic.rdf.rdb2rdf.r2rml.model.LogicalTable;

/**
 *
 * @author mielvandersande
 */
public interface RMLBaseSourceOrView extends LogicalTable {

    /**
     * A RML base source or view is represented by a resource that has exactly
     * one rr:sourceName property.
     */
    public String getSourceName();
}
