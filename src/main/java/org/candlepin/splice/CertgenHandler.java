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

import org.candlepin.model.Entitlement;

import com.google.inject.Inject;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.handler.AbstractHandler;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * CertgenHandler
 */
public class CertgenHandler extends AbstractHandler {

    private static Logger log = Logger.getLogger(CertgenHandler.class);

    private ObjectMapper mapper;
    private SpliceEntitlementFactory spliceEntitlementFactory;

    @Inject
    public CertgenHandler(SpliceEntitlementFactory spliceEntitlementFactory,
                            ObjectMapper mapper) {
        this.spliceEntitlementFactory = spliceEntitlementFactory;
        this.mapper = mapper;
    }

    @Override
    public void handle(String target, HttpServletRequest request,
                        HttpServletResponse response,
                        int dispatch) throws IOException, ServletException {
        response.setContentType("application/json");
        try {
            Entitlement ent = spliceEntitlementFactory.createEntitlement(new Date(),
                    DateUtils.addHours(new Date(), 1),
                    request.getParameterValues("product"),
                    request.getParameter("rhicUUID"));
            response.getWriter().println(mapper.writeValueAsString(ent));
            response.setStatus(HttpServletResponse.SC_OK);
        }
        catch (Exception e) {
            log.error("error generating certificate", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        finally {
            ((Request) request).setHandled(true);
        }
    }
}
