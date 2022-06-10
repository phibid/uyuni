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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.cobbler.Network;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

public class NetworkTest {

    private MockConnection mockConnection;

    @BeforeEach
    public void setUp() {
        mockConnection = new MockConnection("http://localhost", "token");
    }

    @Test
    public void testNetworkConstructor() {
        // Arrange
        String interfaceName = "eth0";

        // Act
        Network network = new Network(mockConnection, interfaceName);

        // Assert: If no exception is raised this is enough
    }

    @Test
    public void testName() {
        // Arrange
        String interfaceName = "eth0";
        Network network = new Network(mockConnection, interfaceName);

        // Act
        String result = network.getName();

        // Assert
        assertEquals(interfaceName, result);
    }

    @Test
    public void testNetmask() {
        // TODO
        fail("Not implemented");
    }

    @Test
    public void testIpAddress() {
        // TODO
        fail("Not implemented");
    }

    @Test
    public void testIpv6Address() {
        // TODO
        fail("Not implemented");
    }

    @Test
    public void testDnsName() {
        // TODO
        fail("Not implemented");
    }

    @Test
    public void testIpv6Secondaries() {
        // TODO
        fail("Not implemented");
    }

    @Test
    public void testIsStaticNetwork() {
        // TODO
        fail("Not implemented");
    }

    @Test
    public void testMacAddress() {
        // TODO
        fail("Not implemented");
    }

    @Test
    public void testBondingMaster() {
        // TODO
        fail("Not implemented");
    }

    @Test
    public void testBondingOptions() {
        // TODO
        fail("Not implemented");
    }

    @Test
    public void testMakeBondingMaster() {
        // TODO
        fail("Not implemented");
    }

    @Test
    public void testMakeBondingSlave() {
        // TODO
        fail("Not implemented");
    }

    @Test
    public void testMakeBondingNa() {
        // TODO
        fail("Not implemented");
    }

    @Test
    public void testBonding() {
        // TODO
        fail("Not implemented");
    }
}
