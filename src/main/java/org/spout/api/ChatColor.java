/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev license version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://getspout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */

package org.spout.api;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An enum of color names mapped to their equivalents in the notchian FontRenderer.
 * Names are from  <a href="http://wiki.vg/Chat">http://wiki.vg/Chat</a>
 */
public enum ChatColor {
    BLACK(0x0),
    DARK_BLUE(0x1),
    DARK_GREEN(0x2),
    DARK_CYAN(0x3),
    DARK_RED(0x4),
    PURPLE(0x5),
    GOLD(0x6),
    GRAY(0x7),
    DARK_GRAY(0x8),
    BLUE(0x9),
    BRIGHT_GREEN(0xA),
    CYAN(0xB),
    RED(0xC),
    PINK(0xD),
    YELLOW(0xE),
    WHITE(0xF);
    
    private static final ChatColor[] codeLookup = new ChatColor[ChatColor.values().length];
    private static final Map<String, ChatColor> nameLookup = new HashMap<String, ChatColor>();
    private static final Pattern matchPatern = Pattern.compile("\\u00A7([0-9a-fA-F])");

    static {
        for (ChatColor color : values()) {
            codeLookup[color.code] = color;
            nameLookup.put(color.name(), color);
        }
    }
    
    public static ChatColor byCode(int code) {
        if (code < 0 || code >= codeLookup.length) {
            return null;
        } else {
            return codeLookup[code];
        }
    }
    
    public static ChatColor byName(String name) {
        if (name == null) return null;
        Matcher matcher = matchPatern.matcher(name);
        if (matcher.matches()) {
            int code;
            try {
                code = Integer.parseInt(matcher.group(1), 16);
                return byCode(code);
            } catch (NumberFormatException ignore) {
                // If it isn't a number, we can try by name lookup
            }
        }
        
        return nameLookup.get(name.toUpperCase());
    }

    private final int code;
    
    private ChatColor(int code) {
        this.code = code;
    }
    
    public int getCode() {
        return code;
    }
    
    @Override
    public String toString() {
        return "\u00A7" + Integer.toString(code, 16);
    }
    
    public static String strip(String str) {
        return str.replaceAll("\\u00A7[0-9a-fA-F]", "");
    }
}
