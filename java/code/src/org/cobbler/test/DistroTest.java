/*
 * Copyright (c) 2009--2014 Red Hat, Inc.
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
import org.cobbler.Distro;
import org.junit.jupiter.api.Test;

import java.util.HashMap;


/**
 * @author paji
 *
 */
public class DistroTest  {
   private CobblerConnection client;

   public void setUpXX() throws Exception {
       client = new CobblerConnection("http://localhost/cobbler_api_rw",
                       "admin", "foo");
   }

    @Test
   public void testFoo() {
     //no op to keep junit happy
   }
   public void xxxtestDistroCreate() throws Exception {
        String name = "Partha-Test";
        String kernel =
            "/var/satellite/rhn/kickstart/ks-rhel-i386-as-4-u2//images/pxeboot/vmlinuz";
        String initrd =
            "/var/satellite/rhn/kickstart/ks-rhel-i386-as-4-u2//images/pxeboot/initrd.img";
        String breed = "redhat";
        String osVersion = "rhel4";
        String arch = "i386";
        Distro newDistro = new Distro.Builder()
                .setName(name)
                .setKernel(kernel)
                .setInitrd(initrd)
                .setKsmeta(new HashMap<>())
                .setBreed(breed)
                .setOsVersion(osVersion)
                .setArch(arch)
                .build(client);
        assertEquals(name, newDistro.getName());
        assertEquals(kernel, newDistro.getKernel());
        assertEquals(initrd, newDistro.getInitrd());
        newDistro.remove();
    }
}
