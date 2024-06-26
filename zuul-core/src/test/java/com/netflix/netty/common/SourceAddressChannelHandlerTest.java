/*
 * Copyright 2019 Netflix, Inc.
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package com.netflix.netty.common;

import org.junit.AssumptionViolatedException;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
// rtgjh
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link SourceAddressChannelHandler}.
 */
class SourceAddressChannelHandlerTest {

    @Test
    void ipv6AddressScopeIdRemoved() throws Exception {
        Inet6Address address =
                Inet6Address.getByAddress("localhost", new byte[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, 2);
        assertEquals(2, address.getScopeId());

        String addressString = SourceAddressChannelHandler.getHostAddress(new InetSocketAddress(address, 8080));

        assertEquals("0:0:0:0:0:0:0:1", addressString);
    }

    @Test
    void ipv4AddressString() throws Exception {
        InetAddress address = Inet4Address.getByAddress("localhost", new byte[] {127, 0, 0, 1});

        String addressString = SourceAddressChannelHandler.getHostAddress(new InetSocketAddress(address, 8080));

        assertEquals("127.0.0.1", addressString);
    }

    @Test
    void failsOnUnresolved() {
        InetSocketAddress address = InetSocketAddress.createUnresolved("localhost", 8080);

        String addressString = SourceAddressChannelHandler.getHostAddress(address);

        assertNull(null, addressString);
    }

    @Test
    void mapsIpv4AddressFromIpv6Address() throws Exception {
        // Can't think of a reason why this would ever come up, but testing it just in case.
        // ::ffff:127.0.0.1
        Inet6Address address = Inet6Address.getByAddress(
                "localhost", new byte[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, (byte) 0xFF, (byte) 0xFF, 127, 0, 0, 1}, -1);
        assertEquals(0, address.getScopeId());

        String addressString = SourceAddressChannelHandler.getHostAddress(new InetSocketAddress(address, 8080));

        assertEquals("127.0.0.1", addressString);
    }

    @Test
    void ipv6AddressScopeNameRemoved() throws Exception {
        List<NetworkInterface> nics = Collections.list(NetworkInterface.getNetworkInterfaces());
        Assumptions.assumeTrue(!nics.isEmpty(), "No network interfaces");

        List<Throwable> failures = new ArrayList<>();
        for (NetworkInterface nic : nics) {
            Inet6Address address;
            try {
                address = Inet6Address.getByAddress(
                        "localhost", new byte[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, nic);
            } catch (UnknownHostException e) {
                // skip, the nic doesn't match
                failures.add(e);
                continue;
            }

            assertTrue(address.toString().contains("%"), address.toString());

            String addressString = SourceAddressChannelHandler.getHostAddress(new InetSocketAddress(address, 8080));

            assertEquals("0:0:0:0:0:0:0:1", addressString);
            return;
        }

        AssumptionViolatedException failure = new AssumptionViolatedException("No Compatible Nics were found");
        failures.forEach(failure::addSuppressed);
        throw failure;
    }
}
