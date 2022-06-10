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

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.cobbler.Distro;
import org.cobbler.Profile;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

public class ProfileStaticTest {

    private MockConnection connectionMock;

    @BeforeEach
    public void setUp() {
        connectionMock = new MockConnection("http://localhost", "token");
    }

    @Test
    public void testProfileCreate() {
        // Arrange
        Distro testDistro = new Distro.Builder().build(connectionMock);

        // Act
        Profile result = Profile.create(connectionMock, "", testDistro);

        // Assert
        assertEquals(result.getDistro().getName(), "");
    }

    @Test
    public void testProfileLookupByName() {
        // TODO
        fail("Not implemented");
    }

    @Test
    public void testProfileLookupById() {
        // TODO
        fail("Not implemented");
    }

    @Test
    public void testList() {
        // TODO
        fail("Not implemented");
    }
}
