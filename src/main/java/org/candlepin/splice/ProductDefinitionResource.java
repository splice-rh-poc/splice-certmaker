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

import com.google.inject.Inject;


import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * SpliceEntitlementFactory
 */
@Path("/productlist")
public class ProductDefinitionResource {

    private static Logger log = Logger.getLogger(ProductDefinitionResource.class);

    private SpliceProductListCache splc;

    @Inject
    public ProductDefinitionResource(SpliceProductListCache splc) {
        this.splc = splc;
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void uploadProductList(@FormParam("product_list") String productListData,
            @FormParam("product_list_digest") String digest) {
        try {
            splc.writeCache(productListData);
        }
        catch (JsonParseException e) {
            log.error("error parsing json data", e);
        }
        catch (JsonMappingException e) {
            log.error("error mapping json data", e);
        }
        catch (IOException e) {
            log.error("exception while reading json data", e);
        }
        log.info("loaded product from json, new serial is " +
                            splc.getCachedSerial());
    }

    @GET
    @Path("/serial")
    @Produces({MediaType.TEXT_PLAIN})
    public long getProductSerial() {
        return splc.getCachedSerial();
    }

}
