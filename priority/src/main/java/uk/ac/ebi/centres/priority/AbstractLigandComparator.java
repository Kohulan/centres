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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package uk.ac.ebi.centres.priority;

import uk.ac.ebi.centres.Comparison;
import uk.ac.ebi.centres.Descriptor;
import uk.ac.ebi.centres.Ligand;
import uk.ac.ebi.centres.LigandComparator;
import uk.ac.ebi.centres.LigandOrder;

/**
 * @author John May
 */
public abstract class AbstractLigandComparator<A>
        implements LigandComparator<A> {

    private final Descriptor.Type type;

    public AbstractLigandComparator() {
        this(Descriptor.Type.ASYMMETRIC);
    }

    public AbstractLigandComparator(Descriptor.Type type) {
        this.type = type;
    }

    @Override
    public final Comparison compareLigands(Ligand<A> o1, Ligand<A> o2) {
        return new LigandOrder(compare(o1, o2), type);
    }


}
