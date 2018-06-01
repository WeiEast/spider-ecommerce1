/**
 * This document and its contents are protected by copyright 2005 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * 
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.common.pipeline;


/**
 * 
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 18, 2014 2:04:52 PM
 */
public interface Pipeline {

    // ------------------------------------------------------------- Properties


    /**
     * <p>
     * Return the Valve instance that has been distinguished as the basic Valve for this Pipeline
     * (if any).
     */
    public Valve getBasic();


    /**
     * <p>
     * Set the Valve instance that has been distinguished as the basic Valve for this Pipeline (if
     * any). Prioer to setting the basic Valve, the Valve's <code>setContainer()</code> will be
     * called, if it implements <code>Contained</code>, with the owning Container as an argument.
     * The method may throw an <code>IllegalArgumentException</code> if this Valve chooses not to be
     * associated with this Container, or <code>IllegalStateException</code> if it is already
     * associated with a different Container.
     * </p>
     * 
     * @param valve Valve to be distinguished as the basic Valve
     */
    public void setBasic(Valve valve);


    // --------------------------------------------------------- Public Methods


    /**
     * <p>
     * Add a new Valve to the end of the pipeline associated with this Container. Prior to adding
     * the Valve, the Valve's <code>setContainer()</code> method will be called, if it implements
     * <code>Contained</code>, with the owning Container as an argument. The method may throw an
     * <code>IllegalArgumentException</code> if this Valve chooses not to be associated with this
     * Container, or <code>IllegalStateException</code> if it is already associated with a different
     * Container.
     * </p>
     * 
     * @param valve Valve to be added
     * 
     * @exception IllegalArgumentException if this Container refused to accept the specified Valve
     * @exception IllegalArgumentException if the specifie Valve refuses to be associated with this
     *            Container
     * @exception IllegalStateException if the specified Valve is already associated with a
     *            different Container
     */
    public void addValve(Valve valve);


    /**
     * Return the set of Valves in the pipeline associated with this Container, including the basic
     * Valve (if any). If there are no such Valves, a zero-length array is returned.
     */
    public Valve[] getValves();


    /**
     * Remove the specified Valve from the pipeline associated with this Container, if it is found;
     * otherwise, do nothing. If the Valve is found and removed, the Valve's
     * <code>setContainer(null)</code> method will be called if it implements <code>Contained</code>
     * .
     * 
     * @param valve Valve to be removed
     */
    public void removeValve(Valve valve);


    /**
     * <p>
     * Return the Valve instance that has been distinguished as the basic Valve for this Pipeline
     * (if any).
     */
    public Valve getFirst();

}
