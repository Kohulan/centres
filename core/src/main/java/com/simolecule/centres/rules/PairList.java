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

package com.simolecule.centres.rules;


import com.simolecule.centres.Descriptor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Implementation of a descriptor list that allows descriptors to be added and
 * ignored. The list maintains an integer value throughout which stores the
 * pairing of descriptors and allows easy comparison between descriptor lists in
 * that higher priority descriptor pairing will always have a higher integer
 * value. The integer value can be access via the {@link #getPairing()} method.
 *
 * @author John May
 * @see Descriptor
 */
public class PairList implements Comparable<PairList> {

  private final List<Descriptor> descriptors = new ArrayList<Descriptor>();
  private       int              pairing     = 0;

  public PairList() {

  }

  public PairList(Descriptor ref) {
    add(ref);
  }

  /**
   * Creates a new list from a provided head and tail. The head and tail
   * ignored descriptors are first transferred and then their descriptors. In
   * either list, descriptors that are ignored by the other will be not be
   * added to the new instance.
   *
   * @param head the head of the list (prefix)
   * @param tail the tail of the list (suffix)
   */
  public PairList(PairList head, PairList tail)
  {
    // add descriptors to the new instance (ignored descriptors not added)
    addAll(head.descriptors);
    addAll(tail.descriptors);
  }

  public Descriptor getRefDescriptor() {
    return ref(descriptors.get(0));
  }

  /**
   * Adds a descriptor to the descriptor list. If the provided descriptor is
   * present in the ignore set the descriptor will not be added.
   *
   * @param descriptor the descriptor to add.
   * @return whether the descriptor was added to the list
   */
  public boolean add(Descriptor descriptor)
  {
    if (descriptor == null)
      return false;
    switch (descriptor) {
      case R:
      case S:
      case M:
      case P:
      case seqTrans:
      case seqCis:
        addAndPair(descriptor);
        return true;
      default:
        return false;
    }
  }

  static Descriptor ref(Descriptor descriptor) {
    switch (descriptor) {
      case R:
      case M:
      case seqCis:
        return Descriptor.R;
      case S:
      case P:
      case seqTrans:
        return Descriptor.S;
      default:
        return null;
        // throw new IllegalArgumentException("Unknown descriptor: " + descriptor);
    }
  }

  /**
   * Adds the descriptor to the descriptor list and stores the pair in an set
   * bit (32-bit integer).
   *
   * @param descriptor the descriptor to add an pair
   * @return whether the descriptor was added
   */
  private void addAndPair(Descriptor descriptor)
  {
    // if this isn't the first descriptor - check the pairing
    if (!descriptors.isEmpty() && descriptors.get(0) == descriptor) {
      // set the bit to indicate a pair
      pairing |= 0x1 << 31 - descriptors.size();
    }
    descriptors.add(ref(descriptor));
  }

  /**
   * Adds multiple descriptors to the descriptor list. If the descriptor is
   * present in the ignore set it will not be added to the list.
   *
   * @param descriptors a collection of descriptors to be added
   * @return whether any of the provided descriptors was added
   */
  public void addAll(Collection<? extends Descriptor> descriptors)
  {
    for (Descriptor descriptor : descriptors)
      add(descriptor);
  }


  /**
   * Access a positive integer that represents the like/unlike pairings of
   * this descriptor list. The like/unlike is represented by set bits in an
   * integer value and means larger integer values indicates a higher
   * descriptor pairing preference.
   *
   * @return an integer representing the descriptor pairings
   */
  public int getPairing()
  {
    return pairing;
  }


  /**
   * Appends multiple descriptor lists. If more then one list is provided the
   * head (this list) is duplicate across the multiple tails (provided). If
   * the contents of this list is 'RRSS' and we invoke append with two lists
   * 'SRS' and 'RSR'. Two new lists will be returned with their contents
   * 'RRSSSRS' and 'RRSSRSR' respectively.
   * <p/>
   * Empty descriptor lists are not appended, if all descriptor lists are
   * empty then 'this' list is the single returned list
   *
   * @param lists multiple descriptor lists to be appended to this list.
   * @return modified list of descriptors based on the provided input lists
   */
  public List<PairList> append(Collection<? extends PairList> lists)
  {

    List<PairList> created = new ArrayList<PairList>(lists.size());

    for (PairList list : lists) {

      // tail isn't empty  - create a new list with this list as the head
      if (!list.descriptors.isEmpty()) {
        created.add(new PairList(this, list));
      }

    }

    // no modifications - make sure we maintain this descriptor list
    if (created.isEmpty())
      created.add(this);

    return created;

  }


  /**
   * @inheritDoc
   */
  @Override
  public String toString()
  {

    StringBuilder sb = new StringBuilder();

    // handles cases that would break the toString method
    if (descriptors.isEmpty() || descriptors.get(0) == null)
      return sb.toString();

    Iterator<Descriptor> it    = descriptors.iterator();
    Descriptor           basis = it.next();

    sb.append(basis).append(":");

    basis = ref(basis);

    // build like (l) / unlike (u) descriptor pairing
    if (it.hasNext()) it.next(); // reference appears twice...
    while (it.hasNext())
      sb.append(basis.equals(ref(it.next())) ? "l" : "u");

    return sb.toString();
  }


  @Override
  public int compareTo(PairList that)
  {
    if (descriptors.size() != that.descriptors.size())
      throw new IllegalArgumentException("Descriptor lists should be the same length!");
    if (descriptors.size() < 63) {
      return Integer.compare(pairing, that.pairing);
    } else {
      Descriptor thisRef = this.descriptors.get(0);
      Descriptor thatRef = that.descriptors.get(0);
      for (int i = 1; i < this.descriptors.size(); i++) {
        if (thisRef == this.descriptors.get(i) && thatRef != that.descriptors.get(i))
          return +1;
        if (thisRef != this.descriptors.get(i) && thatRef == that.descriptors.get(i))
          return -1;
      }
    }
    return 0;
  }


  /**
   * Clear the descriptor list and resets the pair value. The ignore list is
   * not cleared.
   */
  public void clear()
  {
    pairing = 0;
    descriptors.clear();
  }
}
