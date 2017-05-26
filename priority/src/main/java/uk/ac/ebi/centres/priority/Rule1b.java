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

package uk.ac.ebi.centres.priority;

import uk.ac.ebi.centres.Ligand;

public class Rule1b<A>
        extends AbstractPriorityRule<A> {

    public Rule1b(){
        super(Type.CONSTITUTIONAL);
    }

    @Override
    public int compare(Ligand<A> o1, Ligand<A> o2) {
        if (o1.isDuplicate() && !o2.isDuplicate()) {
            return 1;
        } else if (o2.isDuplicate() && !o1.isDuplicate()) {
            return -1;
        } else
            return 0;
    }

}
