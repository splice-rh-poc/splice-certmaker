/**
 * Copyright (c) 2012 Red Hat, Inc.
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
package org.candlepin.splice;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

public class CertgenResourceTest {

    @Test(expected = RuntimeException.class)
    public void testHandleException() throws RuntimeException {
        SpliceEntitlementFactory sef = mock(SpliceEntitlementFactory.class);
        when(sef.createEntitlement(any(Date.class), any(Date.class), any(String[].class),
                            any(String.class))).thenThrow(new RuntimeException("oh no!"));
        CertgenResource certgenResource =
                    new CertgenResource(sef, mock(ObjectMapper.class));

        certgenResource.getCert(new ArrayList<String>(), new String());
    }

}
