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

import com.simolecule.centres.MolApi;
import uk.ac.ebi.centres.Node;

/**
 * An abstract class for constitutional priority based on mass number. A mass
 * number accessor ({@link uk.ac.ebi.centres.priority.access.MassNumberAccessor})
 * can be provided to allow the comparator to work on a custom atom type.
 *
 * @author John May
 */
public class Rule2<A>
        extends AbstractPriorityRule<A> {

  private MolApi<?, A, ?> api;

  public Rule2(MolApi<?, A, ?> api)
  {
    super(Type.CONSTITUTIONAL);
    this.api = api;
  }

  @Override
  public int compare(Node<A> o1, Node<A> o2)
  {
    return Integer.compare(api.getMassNum(null, o1.getAtom()),
                           api.getMassNum(null, o2.getAtom()));
  }

}
