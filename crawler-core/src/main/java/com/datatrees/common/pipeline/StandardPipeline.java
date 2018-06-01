/**
 * This document and its contents are protected by copyright 2005 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * 
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.common.pipeline;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Standard implementation of a processing <b>Pipeline</b> that will invoke a
 * series of Valves that have been configured to be called in order. This
 * implementation can be used for any type of Container.
 * 
 * <b>IMPLEMENTATION WARNING</b> - This implementation assumes that no calls to
 * <code>addValve()</code> or <code>removeValve</code> are allowed while a
 * request is currently being processed. Otherwise, the mechanism by which
 * per-thread state is maintained will need to be modified.
 * 
 * @author Craig R. McClanahan
 */

public class StandardPipeline implements Pipeline {
  private static final Logger log = LoggerFactory.getLogger(ValveBase.class);

  // ----------------------------------------------------------- Constructors

  /**
   * Construct a new StandardPipeline instance with no associated Container.
   */
  public StandardPipeline() {
  }

  // ----------------------------------------------------- Instance Variables

  /**
   * The basic Valve (if any) associated with this Pipeline.
   */
  protected Valve  basic = null;

  /**
   * The Container with which this Pipeline is associated.
   */
  /**
   * Descriptive information about this implementation.
   */
  protected String info  = "org.apache.catalina.core.StandardPipeline/1.0";

  /**
   * The first valve associated with this Pipeline.
   */
  protected Valve  first = null;

  // --------------------------------------------------------- Public Methods

  /**
   * Return descriptive information about this implementation class.
   */
  public String getInfo() {

    return (this.info);

  }

  /**
   * <p>
   * Return the Valve instance that has been distinguished as the basic Valve
   * for this Pipeline (if any).
   */
  public Valve getBasic() {

    return (this.basic);

  }

  /**
   * <p>
   * Set the Valve instance that has been distinguished as the basic Valve for
   * this Pipeline (if any). Prioer to setting the basic Valve, the Valve's
   * <code>setContainer()</code> will be called, if it implements
   * <code>Contained</code>, with the owning Container as an argument. The
   * method may throw an <code>IllegalArgumentException</code> if this Valve
   * chooses not to be associated with this Container, or
   * <code>IllegalStateException</code> if it is already associated with a
   * different Container.
   * </p>
   * 
   * @param valve
   *          Valve to be distinguished as the basic Valve
   */
  public void setBasic(Valve valve) {

    // Change components if necessary
    Valve oldBasic = this.basic;
    if (oldBasic == valve)
      return;

    // Start the new component if necessary
    if (valve == null)
      return;
    // Update the pipeline
    Valve current = first;
    while (current != null) {
      if (current.getNext() == oldBasic) {
        current.setNext(valve);
        break;
      }
      current = current.getNext();
    }
    this.basic = valve;
  }

  /**
   * <p>
   * Add a new Valve to the end of the pipeline associated with this Container.
   * Prior to adding the Valve, the Valve's <code>setContainer()</code> method
   * will be called, if it implements <code>Contained</code>, with the owning
   * Container as an argument. The method may throw an
   * <code>IllegalArgumentException</code> if this Valve chooses not to be
   * associated with this Container, or <code>IllegalStateException</code> if it
   * is already associated with a different Container.
   * </p>
   * 
   * @param valve
   *          Valve to be added
   * 
   * @exception IllegalArgumentException
   *              if this Container refused to accept the specified Valve
   * @exception IllegalArgumentException
   *              if the specifie Valve refuses to be associated with this
   *              Container
   * @exception IllegalStateException
   *              if the specified Valve is already associated with a different
   *              Container
   */
  public void addValve(Valve valve) {
    // Add this Valve to the set associated with this Pipeline
    if (first == null) {
      first = valve;
      valve.setNext(basic);
    } else {
      Valve current = first;
      while (current != null) {
        if (current.getNext() == basic) {
          current.setNext(valve);
          valve.setNext(basic);
          break;
        }
        current = current.getNext();
      }
    }

  }

  /**
   * Return the set of Valves in the pipeline associated with this Container,
   * including the basic Valve (if any). If there are no such Valves, a
   * zero-length array is returned.
   */
  public Valve[] getValves() {

    ArrayList valveList = new ArrayList();
    Valve current = first;
    if (current == null) {
      current = basic;
    }
    while (current != null) {
      valveList.add(current);
      current = current.getNext();
    }

    return ((Valve[]) valveList.toArray(new Valve[0]));

  }

  /**
   * Remove the specified Valve from the pipeline associated with this
   * Container, if it is found; otherwise, do nothing. If the Valve is found and
   * removed, the Valve's <code>setContainer(null)</code> method will be called
   * if it implements <code>Contained</code>.
   * 
   * @param valve
   *          Valve to be removed
   */
  public void removeValve(Valve valve) {

    Valve current;
    if (first == valve) {
      first = first.getNext();
      current = null;
    } else {
      current = first;
    }
    while (current != null) {
      if (current.getNext() == valve) {
        current.setNext(valve.getNext());
        break;
      }
      current = current.getNext();
    }

    if (first == basic)
      first = null;
  }

  public Valve getFirst() {
    if (first != null) {
      return first;
    } else {
      return basic;
    }
  }
}
