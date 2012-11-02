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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.Response;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

public class CertgenHandlerTest {

    @Test
    public void testHandle() throws JsonMappingException, ServletException, IOException {
        SpliceEntitlementFactory sef = mock(SpliceEntitlementFactory.class);
        when(sef.createEntitlement(any(Date.class), any(Date.class), any(String[].class),
                            any(String.class))).thenThrow(new RuntimeException("oh no!"));
        CertgenHandler handler = new CertgenHandler(sef, mock(ObjectMapper.class));
        HttpServletResponse resp = mock(Response.class);
        handler.handle(new String(), mock(Request.class), resp, 0);
        verify(resp).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

}
