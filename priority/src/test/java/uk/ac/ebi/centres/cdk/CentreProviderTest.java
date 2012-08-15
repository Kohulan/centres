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

package uk.ac.ebi.centres.cdk;

import org.junit.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.periodictable.PeriodicTable;
import uk.ac.ebi.centres.Centre;
import uk.ac.ebi.centres.CentreProvider;
import uk.ac.ebi.centres.Descriptor;
import uk.ac.ebi.centres.PriorityRule;
import uk.ac.ebi.centres.SignCalculator;
import uk.ac.ebi.centres.descriptor.General;
import uk.ac.ebi.centres.graph.ConnectionTableDigraph;
import uk.ac.ebi.centres.graph.DefaultDescriptorManager;
import uk.ac.ebi.centres.priority.AtomicNumberRule;
import uk.ac.ebi.centres.priority.access.AtomicNumberAccessor;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author John May
 */
public class CentreProviderTest {

    @Test
    public void testGetCentres() throws CDKException {

        IAtomContainer container = CMLLoader.loadCML(getClass().getResourceAsStream("(2Z,5R,7E)-4,6-bis[(1E)-prop-1-en-1-yl]nona-2,7-dien-5-ol.xml"));

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(container);
        for (IAtom atom : container.atoms())
            atom.setAtomicNumber(PeriodicTable.getAtomicNumber(atom.getSymbol()));

        ConnectionTableDigraph<IAtom> digraph = new ConnectionTableDigraph<IAtom>(new DefaultDescriptorManager<IAtom>(),
                                                                                  new CDKConnectionTable(container));

        CentreProvider<IAtom> provider = new CDKCentreProvider(container);
        Collection<Centre<IAtom>> centres = provider.getCentres(digraph, new DefaultDescriptorManager<IAtom>());


        PriorityRule<IAtom> rule = new AtomicNumberRule<IAtom>(new AtomicNumberAccessor<IAtom>() {
            @Override
            public int getAtomicNumber(IAtom atom) {
                return atom.getAtomicNumber();
            }
        });

        List<Centre<IAtom>> unperceived = new LinkedList<Centre<IAtom>>();

        List<Centre<IAtom>> perceived = new LinkedList<Centre<IAtom>>();
        unperceived.addAll(centres);
        SignCalculator<IAtom> calc = new CDK2DSignCalculator();

        Boolean found = Boolean.FALSE;
        do {

            Map<Centre<IAtom>, Descriptor> map = new HashMap<Centre<IAtom>, Descriptor>();

            for (Centre<IAtom> centre : unperceived) {

                digraph.setRoot(centre);
                Descriptor descriptor = centre.perceive(rule, calc);

                if (descriptor != General.UNKNOWN)
                    map.put(centre, descriptor);


            }

            found = !map.isEmpty();
            // transfer descriptors
            for (Map.Entry<Centre<IAtom>, Descriptor> entry : map.entrySet()) {
                unperceived.remove(entry.getKey());
                perceived.add(entry.getKey());
                entry.getKey().setDescriptor(entry.getValue());
            }


        } while (found);

        // check for aux calculations otherwise these don't have stereo
        for (Centre<IAtom> centre : unperceived) {
            centre.setDescriptor(General.NONE);
            System.out.println(centre + ": " + centre.getDescriptor());
        }
        for (Centre<IAtom> centre : perceived) {
            System.out.println(centre + ": " + centre.getDescriptor());
        }

    }

}
