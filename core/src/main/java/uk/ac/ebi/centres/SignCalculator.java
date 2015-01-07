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

package uk.ac.ebi.centres;

/**
 * @author John May
 */
public interface SignCalculator<A> {

    public abstract double getX(A atom);

    public abstract double getY(A atom);
    
    /**
     * When wedge and hatch bonds are involed
     *
     * @param a1
     * @param o1
     * @param a2
     * @param o2
     * @param a3
     * @param o3
     * @param a4
     * @param o4
     *
     * @return
     */
    public int getSign(Ligand<A> centre, Ligand<A> a1, Ligand<A> a2, Ligand<A> a3, Ligand<A> a4);

    /**
     * For a simple triangle of atoms
     *
     * @param a1
     * @param a2
     * @param a3
     *
     * @return
     */
    public int getSign(A a1, A a2, A a3);

}
