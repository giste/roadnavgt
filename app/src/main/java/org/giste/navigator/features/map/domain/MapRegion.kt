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

enum class MapRegion(
    val regionName: String,
    val remotePath: String,
    val localDir: String,
) {
    Africa("Africa", "/africa", "/africa"),
    Asia("Asia", "/asia", "/asia"),
    China("China", "/asia/china", "/china"),
    AustraliaOceania("Australia-Oceania", "/australia-oceania", "/australia-oceania"),
    CentralAmerica("Central America", "/central-america", "/central-america"),
    Europe("Europe", "/europe", "europe"),
    NorthAmerica("North America", "/north-america", "/north-america"),
    Canada("Canada", "/north-america/canada", "/canada"),
    Russia("Russia", "/russia", "/russia"),
    SouthAmerica("South America", "/south-america", "/south-america"),
}