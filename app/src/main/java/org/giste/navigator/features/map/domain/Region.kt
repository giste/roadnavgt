/*
 * Copyright 2025 Giste Trappiste
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.giste.navigator.features.map.domain

enum class Region(val path: String) {
    AFRICA("africa/"),
    ASIA("asia/"),
    CHINA("asia/china/"),
    AUSTRALIA_OCEANIA("australia-oceania/"),
    CENTRAL_AMERICA("central-america/"),
    EUROPE("europe/"),
    NORTH_AMERICA("north-america/"),
    CANADA("north-america/canada/"),
    RUSSIA("russia/"),
    SOUTH_AMERICA("south-america/"),
}