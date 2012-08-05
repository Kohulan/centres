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

package uk.ac.ebi.centres.test;

import uk.ac.ebi.centres.Descriptor;
import uk.ac.ebi.centres.Ligand;

import java.util.List;
import java.util.Set;

/**
 * @author John May
 */
public class TestLigand implements Ligand<TestAtom> {

    private TestAtom atom;


    public TestLigand(TestAtom atom) {
        this.atom = atom;
    }


    @Override
    public TestAtom getAtom() {
        return atom;
    }


    @Override
    public List<Ligand<TestAtom>> getLigands() {
        return null;
    }


    @Override
    public Set<TestAtom> getVisited() {
        return null;
    }


    @Override
    public Boolean isParent(TestAtom atom) {
        return null;
    }


    @Override
    public Boolean isVisited(TestAtom atom) {
        return null;
    }


    @Override
    public void setAuxiliary(Descriptor descriptor) {

    }


    @Override
    public Descriptor getAuxiliary() {
        return null;
    }


    @Override
    public void setDescriptor(Descriptor descriptor) {

    }


    @Override
    public Descriptor getDescriptor() {
        return null;
    }
}
