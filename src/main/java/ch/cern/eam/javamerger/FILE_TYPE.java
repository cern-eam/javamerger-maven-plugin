/*
 * Copyright © 2018-2019 CERN European Organization for Nuclear Research
 * Email: eam-service@cern.ch
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package ch.cern.eam.javamerger;

public enum FILE_TYPE {

    CLASS("class"),
    INTERFACE("interface");

    private String code;

    FILE_TYPE(String code) {
        this.code = code;
    }

    /**
     * Get FILE_TYPE element by code
     * @param code
     * @return
     */
    public static FILE_TYPE getByCode(String code) {
        for (FILE_TYPE type : FILE_TYPE.values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalStateException("Unknow code for FILE_TYPE: " + code);
    }

}
