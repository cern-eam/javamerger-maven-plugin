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

/**
 * Exception during merge of Java files
 */
public class JavaMergerException extends RuntimeException {


    public JavaMergerException(Exception e) {
        super(e);
    }

    public JavaMergerException(String message, Exception e) {
        super(message, e);
    }

}
