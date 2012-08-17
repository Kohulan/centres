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

package uk.ac.ebi.centres.ligand;

import com.google.common.collect.Sets;
import org.openscience.cdk.interfaces.IAtom;
import uk.ac.ebi.centres.Centre;
import uk.ac.ebi.centres.ConnectionProvider;
import uk.ac.ebi.centres.Descriptor;
import uk.ac.ebi.centres.Ligand;
import uk.ac.ebi.centres.MutableDescriptor;
import uk.ac.ebi.centres.PriorityRule;
import uk.ac.ebi.centres.SignCalculator;
import uk.ac.ebi.centres.descriptor.General;
import uk.ac.ebi.centres.descriptor.Planar;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author John May
 */
public class PlanarCentre<A> extends AbstractLigand<A> implements Centre<A> {

    private final Ligand<A> first;
    private final Ligand<A> second;
    private final Set<A>    atoms;


    @SuppressWarnings("unchecked")
    public PlanarCentre(A first, A second,
                        ConnectionProvider<A> provider,
                        MutableDescriptor descriptor) {

        super(provider, descriptor);

        Ligand<A> self = this;

        // create two ligand delegates
        this.first = new NonterminalLigand<A>(provider, descriptor, first, second);
        this.second = new NonterminalLigand<A>(provider, descriptor, second, first);

        atoms = Sets.newHashSet(first, second);

    }


    @Override
    public List<Ligand<A>> getLigands() {
        List<Ligand<A>> ligands = new ArrayList<Ligand<A>>(16);
        ligands.addAll(first.getLigands());
        ligands.addAll(second.getLigands());
        return ligands;
    }


    @Override
    public A getAtom() {
        // might need a rethink...
        throw new NoSuchMethodError("Centre does not have a single atom");
    }


    @Override
    public String toString() {
        if (first.getAtom() instanceof IAtom) {
            return ((IAtom) first.getAtom()).getSymbol() + "" + ((IAtom) first.getAtom()).getProperty("number") + "=" +
                    ((IAtom) second.getAtom()).getSymbol() + "" + ((IAtom) second.getAtom()).getProperty("number");
        }
        return "eh?";
    }


    @Override
    public Boolean isParent(Object atom) {
        return atoms.contains(atom);
    }


    @Override
    public Set<A> getAtoms() {
        return atoms;
    }


    @Override
    public Descriptor perceive(PriorityRule<A> rule, SignCalculator<A> calculator) {

        List<Ligand<A>> firstLigands = first.getLigands();
        List<Ligand<A>> secondLigands = second.getLigands();

        // check for pseudo
        boolean unique = rule.prioritise(firstLigands) && rule.prioritise(secondLigands);

        if (!unique) {
            return General.UNKNOWN;
        }

        int firstSign = calculator.getSign(firstLigands.iterator().next().getAtom(),
                                           first.getAtom(),
                                           second.getAtom());
        int secondSign = calculator.getSign(secondLigands.iterator().next().getAtom(),
                                            second.getAtom(),
                                            first.getAtom());


        // also check for psuedo (from prioritise)
        return firstSign == secondSign ? Planar.E : Planar.Z;

    }
}
