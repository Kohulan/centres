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

package uk.ac.ebi.centres;

import uk.ac.ebi.centres.descriptor.General;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author John May
 */
public class DefaultPerceptor<A> implements Perceptor<A> {

    private final CentrePerceptor<A> mainPerceptor;
    private final CentrePerceptor<A> auxPerceptor;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private long            timeout  = 250;


    public DefaultPerceptor(final PriorityRule<A> rule,
                            final PriorityRule<A> auxRule,
                            final SignCalculator<A> calculator) {

        // create the main and aux perceptors
        this.mainPerceptor = new CentrePerceptor<A>(rule) {
            @Override
            public Descriptor perceive(Centre<A> centre, Collection<Centre<A>> centres) {
                return centre.perceive(rule, calculator);
            }
        };
        this.auxPerceptor = new CentrePerceptor<A>(auxRule) {
            @Override
            public Descriptor perceive(Centre<A> centre, Collection<Centre<A>> centres) {
                centre.perceiveAuxiliary(centres, rule, calculator);
                return centre.perceive(auxRule, calculator);
            }
        };
    }


    private List<Centre<A>> _perceive(Collection<Centre<A>> unperceived,
                                      CentrePerceptor<A> perceptor) {

        List<Centre<A>> perceived = new ArrayList<Centre<A>>();
        Map<Centre<A>, Descriptor> map = new LinkedHashMap<Centre<A>, Descriptor>();

        do {

            map.clear();

            for (Centre<A> centre : unperceived) {

                Descriptor descriptor = perceptor.perceive(centre, unperceived);

                if (descriptor != General.UNKNOWN)
                    map.put(centre, descriptor);


            }


            // transfer descriptors
            for (Map.Entry<Centre<A>, Descriptor> entry : map.entrySet()) {
                unperceived.remove(entry.getKey());
                perceived.add(entry.getKey());
                entry.getKey().dispose();
                entry.getKey().setDescriptor(entry.getValue());
            }


        } while (!map.isEmpty());

        return perceived;

    }


    @Override
    public void perceive(final CentreProvider<A> provider, final DescriptorManager<A> manager) {

        // timeout fo the centre provider incase we have a huge molecule and the spanning tree can't
        // be constructed
        Collection<Centre<A>> unperceived = provider.getCentres(manager);

        if (unperceived.isEmpty())
            return;

        // could switch to only use this on large molecule
        List<Centre<A>> perceived = _perceive(unperceived, mainPerceptor);

        // no centres perceived, perform auxiliary perception
        if (!unperceived.isEmpty() && perceived.isEmpty())
            perceived.addAll(_perceive(unperceived, auxPerceptor));

        // set all unperceived centres to 'none'
        for (Centre<A> centre : unperceived) {
            centre.setDescriptor(General.NONE);
            centre.dispose();
        }

        unperceived.clear();
        unperceived = null;
        manager.clear();

    }

    /**
     * Shutdown the internal executor
     */
    public void shutdown() {
        executor.shutdownNow();
    }


    abstract class CentrePerceptor<A> {

        private PriorityRule<A> rule;


        protected CentrePerceptor(PriorityRule<A> rule) {
            this.rule = rule;
        }

        public abstract Descriptor perceive(Centre<A> centre, Collection<Centre<A>> centres);
    }


}
