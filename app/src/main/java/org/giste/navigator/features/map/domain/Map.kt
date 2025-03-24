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

enum class Map(val region: Region, val path: String) {
    // Australia-Oceania
    ILE_DE_CLIPPERTON(Region.AUSTRALIA_OCEANIA, "ile-de-clipperton.map"),

    // Europe
    SPAIN(Region.EUROPE, "spain.map"),
    PORTUGAL(Region.EUROPE, "portugal.map"),

    // North America
    GREENLAND(Region.NORTH_AMERICA, "greenland.map"),
    MEXICO(Region.NORTH_AMERICA, "mexico.map"),
    US_MIDWEST(Region.NORTH_AMERICA, "us-midwest.map"),
    US_NORTHEAST(Region.NORTH_AMERICA, "us-northeast.map"),
    US_SOUTH(Region.NORTH_AMERICA, "us-south.map"),
    US_WEST(Region.NORTH_AMERICA, "us-west.map"),

}