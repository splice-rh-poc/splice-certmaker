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

import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;


/**
 * CertgenResource
 */
@Path("/cert")
public class CertmakerResource {

    private static Logger log = Logger.getLogger(CertmakerResource.class);

    private SpliceEntitlementFactory spliceEntitlementFactory;
    private ObjectMapper mapper;


    @Inject
    public CertmakerResource(SpliceEntitlementFactory spliceEntitlementFactory,
            ObjectMapper mapper) {
        this.spliceEntitlementFactory = spliceEntitlementFactory;
        this.mapper = mapper;
    }

    @GET
    @Path("{rhic_uuid}")
    @Produces({MediaType.APPLICATION_JSON})
    public String getCert(@QueryParam("product") List<String> products,
            @PathParam("rhic_uuid") String rhicUuid) {

        Entitlement ent = spliceEntitlementFactory.createEntitlement(new Date(),
                DateUtils.addHours(new Date(), 1),
                products.toArray(new String[] {}), rhicUuid);
        // TODO: return Entitlement, and let jackson serialize on its own
        try {
            return mapper.writeValueAsString(ent);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error during serialization", e);
        }
    }

}
