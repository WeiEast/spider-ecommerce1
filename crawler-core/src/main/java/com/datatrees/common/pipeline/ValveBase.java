package com.datatrees.common.pipeline;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Convenience base class for implementations of the <b>Valve</b> interface. A
 * subclass <strong>MUST</strong> implement an <code>invoke()</code> method to
 * provide the required functionality, and <strong>MAY</strong> implement the
 * <code>Lifecycle</code> interface to provide configuration management and
 * lifecycle support.
 * 
 * @author Craig R. McClanahan
 * @version $Revision: 467222 $ $Date: 2006-10-24 11:17:11 +0800 (Tue, 24 Oct
 *          2006) $
 */

public abstract class ValveBase implements Valve {
  private static final Logger logger = LoggerFactory.getLogger(ValveBase.class);

  // ------------------------------------------------------ Instance Variables

  protected static     String info   = "org.apache.catalina.core.ValveBase/1.0";

  /**
   * The next Valve in the pipeline this Valve is a component of.
   */
  protected            Valve  next   = null;

  /**
   * Return descriptive information about this Valve implementation.
   */
  public String getInfo() {

    return (info);

  }

  /**
   * Return the next Valve in this pipeline, or <code>null</code> if this is the
   * last Valve in the pipeline.
   */
  public Valve getNext() {

    return (next);

  }

  /**
   * Set the Valve that follows this one in the pipeline it is part of.
   * 
   * @param valve
   *          The new next valve
   */
  public void setNext(Valve valve) {

    this.next = valve;

  }

  // ---------------------------------------------------------- Public Methods

  /**
   * Execute a periodic task, such as reloading, etc. This method will be
   * invoked inside the classloading context of this container. Unexpected
   * throwables will be caught and logged.
   */
  public void backgroundProcess() {
  }

  /**
   * The implementation-specific logic represented by this Valve. See the Valve
   * description for the normal design patterns for this method.
   * <p>
   * This method <strong>MUST</strong> be provided by a subclass.
   * 
   * @param request
   *          The servlet request to be processed
   * @param response
   *          The servlet response to be created
   * 
   * @exception IOException
   *              if an input/output error occurs
   * @exception ServletException
   *              if a servlet error occurs
   */
  public abstract void invoke(final Request request, final Response response)
      throws Exception;
}
