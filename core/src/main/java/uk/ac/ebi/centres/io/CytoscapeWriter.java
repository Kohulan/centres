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

package uk.ac.ebi.centres.io;

import uk.ac.ebi.centres.Digraph;
import uk.ac.ebi.centres.Ligand;

import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Allows a digraph to be created
 *
 * @author John May
 */
public abstract class CytoscapeWriter<A> implements Closeable {

    private Digraph<A> digraph;
    private Writer     sif;
    private File       folder;
    private Map<String, Map<String, String>> attributes = new HashMap<String, Map<String, String>>();


    public CytoscapeWriter(File folder, Digraph<A> digraph) throws IOException {

        this.digraph = digraph;

        if (folder.exists() && !folder.isDirectory())
            throw new IllegalArgumentException("Folder should be a directory");

        if (!folder.exists() && !folder.mkdirs()) {
            throw new IllegalArgumentException("Unable to create folder");
        }

        this.folder = folder;
        this.sif = new FileWriter(new File(folder, folder.getName().replace(" ", "-") + ".sif"));

    }


    public void writeSif() throws IOException {
        write(digraph.getProximal(), "1");
    }


    public void writeAttributes() throws IOException {
        if (attributes.isEmpty()) return;
        FileWriter                     wtr      = new FileWriter(new File(folder, "props.tsv"));
        Map<String,Map<String,String>> inverted = new HashMap<String, Map<String, String>>();
        Set<String>                    columns  = new TreeSet<String>();
        for (Map.Entry<String, Map<String, String>> entry : attributes.entrySet()) {
            String prop = entry.getKey().replaceAll(" ", ".");
            columns.add(prop);
            for (Map.Entry<String, String> nodeEntry : entry.getValue().entrySet()) {
                Map<String,String> values = inverted.get(nodeEntry.getKey());
                if (values == null)
                    inverted.put(nodeEntry.getKey(), values = new TreeMap<String, String>());
                values.put(prop, nodeEntry.getValue());
            }
        }
        wtr.write("Node");
        for (String str : columns) {
            wtr.write('\t');
            wtr.write(str);
        }
        wtr.write('\n');
        for (Map.Entry<String,Map<String,String>> e : inverted.entrySet()) {
            wtr.write(e.getKey());
            for (String str : columns) {
                wtr.write('\t');
                wtr.write(e.getValue().get(str));
            }
            wtr.write('\n');
        }

        wtr.close();
    }


    private void write(List<Ligand<A>> ligands, String sourceId) throws IOException {

        for (int i = 0; i < ligands.size(); i++) {

            Ligand<A> ligand = ligands.get(i);

            String targetId = sourceId + (Integer.toString(i + 1));

            sif.write(sourceId + "\t->\t" + targetId + "\n");

            // invert map properties
            Map<String, String> map = new HashMap<String, String>();
            mapAttributes(ligand.getAtom(), map);
            for (Map.Entry<String, String> e : map.entrySet()) {
                if (!attributes.containsKey(e.getKey())) {
                    attributes.put(e.getKey(), new HashMap<String, String>());
                }
                attributes.get(e.getKey()).put(targetId, e.getValue());
            }

            write(ligands.get(i).getLigands(), targetId);

        }

    }


    public abstract void mapAttributes(A atom, Map<String, String> map);


    @Override
    public void close() throws IOException {
        sif.close();
    }

}
