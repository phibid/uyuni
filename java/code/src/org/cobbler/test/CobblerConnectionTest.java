/*
 * Copyright (c) 2022 SUSE LLC
 *
 * This software is licensed to you under the GNU General Public License,
 * version 2 (GPLv2). There is NO WARRANTY for this software, express or
 * implied, including the implied warranties of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. You should have received a copy of GPLv2
 * along with this software; if not, see
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt.
 *
 * Red Hat trademarks are not licensed under GPLv2. No permission is
 * granted to use or replicate Red Hat trademarks that are incorporated
 * in this software or its documentation.
 */
package org.cobbler.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.cobbler.CobblerConnection;
import org.junit.Test;

public class CobblerConnectionTest {
    @Test
    public void testCobblerConnectionUrl() {
        // Arrange
        String expectedResult = "http://localhost/cobbler_api";

        // Act
        CobblerConnection result = new CobblerConnection("http://localhost");

        // Assert
        assertEquals(result.getUrl(), expectedResult);
    }

    @Test
    public void testLogin() {
        // Arrange
        String username = "test";
        String password = "test";
        String expectedResult = "test";
        CobblerConnection connection = new CobblerConnection("http://localhost");

        // Act
        String result = connection.login(username, password);

        // Assert
        assertEquals(result, expectedResult);
    }

    @Test
    public void testInvokeMethod() {
        // Arrange
        CobblerConnection connection = new CobblerConnection("http://localhost");

        // Act
        Object result = connection.invokeMethod("", "argument");

        // Assert
        assertEquals(null, result);
    }

    @Test
    public void testInvokeTokenMethod() {
        // Arrange
        CobblerConnection connection = new CobblerConnection("http://localhost");

        // Act
        Object result = connection.invokeTokenMethod("", "argument");

        // Assert
        assertEquals(null, result);
    }

    @Test
    public void testUrl() {
        // Arrange
        String expectedResult = "http://localhost/cobbler_api";
        CobblerConnection connection = new CobblerConnection("http://localhost");

        // Act
        String result = connection.getUrl();

        // Assert
        assertEquals(expectedResult, result);
    }

    @Test
    public void testVersion() {
        // Arrange
        Double expectedResult = 2.2;
        CobblerConnection connection = new CobblerConnection("http://localhost");

        // Act
        Double result = connection.getVersion();

        // Assert
        assertEquals(expectedResult, result);
    }
}
