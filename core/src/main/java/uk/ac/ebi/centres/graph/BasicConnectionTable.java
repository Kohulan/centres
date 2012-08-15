/*
 * Copyright (c) 2012. John May
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA
 */

package uk.ac.ebi.centres.graph;

import uk.ac.ebi.centres.ConnectionTable;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author John May
 */

public class BasicConnectionTable<A> implements ConnectionTable<A> {

    private Map<A, Map<A, Map.Entry<Integer, Integer>>> connections = new HashMap<A, Map<A, Map.Entry<Integer, Integer>>>();
    private Map<A, Map<A, Map.Entry<Integer, Integer>>> stereo      = new HashMap<A, Map<A, Map.Entry<Integer, Integer>>>();


    public void addConnection(A first, A second, int order) {
        addConnection(first, second, order, 0);
    }


    public void addConnection(A first, A second, int order, int sign) {
        newConnection(first, second, order,
                      sign >= 1 ? 1 : sign <= -1 ? -1 : 0);
        newConnection(second, first, order, sign >= -1 ? 1 : sign <= 1 ? -1
                                                                       : 0); // note the sign is inverted
    }


    private void newConnection(A first, A second, int order, Integer sign) {
        if (!connections.containsKey(first)) {
            connections.put(first, new HashMap<A, Map.Entry<Integer, Integer>>());
        }
        connections.get(first).put(second, new AbstractMap.SimpleEntry<Integer, Integer>(order, sign));
    }


    @Override
    public Collection<A> getConnected(A atom) {
        return connections.get(atom).keySet();
    }


    @Override
    public int getOrder(A first, A second) {
        return connections.get(first).get(second).getKey();
    }


    @Override
    public Integer getDepth(A first, A second) {
        return connections.get(first).get(second).getValue();
    }


    @Override
    public Integer getAtomCount() {
        return connections.keySet().size();
    }
}



