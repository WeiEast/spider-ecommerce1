/**
 * This document and its contents are protected by copyright 2005 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.common.pipeline;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * The first valve associated with this Pipeline.
     */
    protected Valve first = null;

    /**
     * The last valve associated with this Pipeline.
     */
    protected Valve last  = null;

    public void addValve(Valve valve) {
        Valve old = last;
        last = valve;

        if (old == null) {
            first = valve;
        } else {
            old.setNext(valve);
        }
    }

    public Valve[] getValves() {
        List<Valve> valveList = new ArrayList<>();

        Valve current = first;
        while (current != null) {
            valveList.add(current);
            current = current.getNext();
        }

        return valveList.toArray(new Valve[valveList.size()]);
    }

    public void removeValve(Valve valve) {
        if (first == valve) {
            first = first.getNext();
            if (first == null) {
                last = null;
            }
            return;
        }

        Valve current = first;
        while (current != null) {
            if (current.getNext() == valve) {
                current.setNext(valve.getNext());
                if (current.getNext() == null) {
                    last = current;
                }
                break;
            }

            current = current.getNext();
        }
    }

    public Valve getFirst() {
        return first;
    }
}
