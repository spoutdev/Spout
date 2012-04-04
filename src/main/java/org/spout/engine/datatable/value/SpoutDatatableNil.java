/*
 * This file is part of Spout (http://www.spout.org/).
 *
 * Spout is licensed under the SpoutDev License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Vanilla is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */

package org.spout.engine.datatable.value;


import org.spout.api.datatable.DatatableTuple;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SpoutDatatableNil implements DatatableTuple {
    @Override
    public void output(OutputStream out) throws IOException {
        throw new RuntimeException("This value doesn't exist!");
    }

    @Override
    public void input(InputStream in) throws IOException {
        throw new RuntimeException("This value doesn't exist!");
    }

    @Override
    public void set(int key, Object value) {
       throw new RuntimeException("This value doesn't exist!");
    }

    @Override
    public void setFlags(byte flags) {
        throw new RuntimeException("This value doesn't exist!");
    }

    @Override
    public void setPersistant(boolean value) {
        throw new RuntimeException("This value doesn't exist!");
    }

    @Override
    public void setSynced(boolean value) {
        throw new RuntimeException("This value doesn't exist!");
    }

    @Override
    public Object get() {
        return null;
    }

    @Override
    public int asInt() {
        return 0;
    }

    @Override
    public float asFloat() {
        return 0;
    }

    @Override
    public boolean asBool() {
        return false;
    }
}
