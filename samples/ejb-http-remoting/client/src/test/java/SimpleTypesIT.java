/*
 *    DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *    Copyright (c) [2019] Payara Foundation and/or its affiliates. All rights reserved.
 *
 *    The contents of this file are subject to the terms of either the GNU
 *    General Public License Version 2 only ("GPL") or the Common Development
 *    and Distribution License("CDDL") (collectively, the "License").  You
 *    may not use this file except in compliance with the License.  You can
 *    obtain a copy of the License at
 *    https://github.com/payara/Payara/blob/master/LICENSE.txt
 *    See the License for the specific
 *    language governing permissions and limitations under the License.
 *
 *    When distributing the software, include this License Header Notice in each
 *    file and include the License file at glassfish/legal/LICENSE.txt.
 *
 *    GPL Classpath Exception:
 *    The Payara Foundation designates this particular file as subject to the "Classpath"
 *    exception as provided by the Payara Foundation in the GPL Version 2 section of the License
 *    file that accompanied this code.
 *
 *    Modifications:
 *    If applicable, add the following below the License Header, with the fields
 *    enclosed by brackets [] replaced by your own identifying information:
 *    "Portions Copyright [year] [name of copyright owner]"
 *
 *    Contributor(s):
 *    If you wish your version of this file to be governed by only the CDDL or
 *    only the GPL Version 2, indicate your decision by adding "[Contributor]
 *    elects to include this software in this distribution under the [CDDL or GPL
 *    Version 2] license."  If you don't indicate a single choice of license, a
 *    recipient has the option to distribute your version of this file under
 *    either the CDDL, the GPL Version 2 or to extend the choice of license to
 *    its licensees as provided above.  However, if you add GPL Version 2 code
 *    and therefore, elected the GPL Version 2 license, then the option applies
 *    only if the new code is made subject to such option by the copyright
 *    holder.
 */

import fish.payara.samples.ejbhttp.api.RemoteService;
import fish.payara.samples.ejbhttp.client.RemoteConnector;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.naming.NamingException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class SimpleTypesIT {
    static RemoteService remoteService;

    @BeforeClass
    public static void lookup() throws NamingException {
        remoteService = RemoteConnector.INSTANCE.lookup("java:global/server-app/RemoteServiceBean");
    }

    @Test
    public void testPrimitiveValue() {
        assertEquals(-1, remoteService.simpleOperation(null, null));
        assertEquals(0, remoteService.simpleOperation("", null));
        assertEquals(4, remoteService.simpleOperation("test", null));
        assertEquals(6, remoteService.simpleOperation("test", 2.1));
    }

    @Test
    public void testSimpleLists() {
        // yasson bug: Cannot deserialize null
        // assertNull(remoteService.elementaryListOperation(-1));
        assertEquals(0, remoteService.elementaryListOperation(0).size());
        assertEquals(16, remoteService.elementaryListOperation(16).size());
    }

    @Test
    public void testSimpleMaps() {
        // yasson bug: Cannot deserialize null
        // assertNull(remoteService.elementaryMapOperation(-1));
        assertEquals(0, remoteService.elementaryMapOperation(0).size());
        assertEquals(16, remoteService.elementaryMapOperation(16).size());
    }
}