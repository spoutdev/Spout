/*
 * This file is part of SpoutAPI (http://www.getspout.org/).
 *
 * Spout API is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Spout API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Afforess
 */
@Documented
@Target(value = ElementType.METHOD)
@Retention(value = RetentionPolicy.SOURCE)
public @interface ClientOnly {

	public String author() default "Afforess";

	public String version() default "1.1";

	public String shortDescription() default "Indicates that the function requires the use of the Spout SinglePlayer client mod to have any effect";

}
