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

import org.candlepin.model.Product;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * SpliceProductListCache
 */
@Singleton
public class SpliceProductListCache {

    private JsonDataContainer jdc;
    private String cacheFilename;
    private ObjectMapper mapper;

    private static Logger log = Logger.getLogger(JsonDataContainer.class);

    @Inject
    public SpliceProductListCache(SpliceConfig config, ObjectMapper mapper) {
        this.mapper = mapper;
        cacheFilename = config.getString("product_json_cache");
        if (cacheFilename == null) {
            throw new RuntimeException("Config value for product_json_cache not found!");
        }
        log.info("reading cached product data from " + cacheFilename);
        File f = new File(cacheFilename);

        if (f.exists()) {
            try {
                jdc = mapper.readValue(f, JsonDataContainer.class);
                log.info("loaded cache, serial is " + jdc.getCreationSerialNumber());
            }
            catch (JsonParseException e) {
                throw new RuntimeException("Unable to parse json from cache", e);
            }
            catch (JsonMappingException e) {
                throw new RuntimeException("Unable to map json from cache", e);
            }
            catch (IOException e) {
                throw new RuntimeException("Unable to read json from cache", e);
            }
        }
        else {
            log.info("cache does not exist, tried " + cacheFilename);
        }
    }
    public List<Product> getProductList() {
        // if there's no JDC populated, just return null
        if (!isCachePopulated()) {
            return null;
        }
        return jdc.getProducts();
    }

    // is cache populated

    public boolean isCachePopulated() {
        if (jdc != null) {
            return true;
        }
        return false;
    }

    public long getCachedSerial() {
        if (jdc == null) {
            return 0;
        }
        return jdc.getCreationSerialNumber();
    }


    public void writeCache(String json) throws IOException {
        JsonDataContainer inputJdc = mapper.readValue(json, JsonDataContainer.class);
        // throw an exception if the data isn't newer than what we have
        verifySerials(inputJdc);
        jdc = inputJdc;
        // let the caller handle the exceptions
        File f = new File(cacheFilename);       
        mapper.writeValue(f, jdc);
    }

    private void verifySerials(JsonDataContainer inputJdc) {
        // if we don't already have a jdc, assume the new serial works
        if (!isCachePopulated()) {
            return;
        }
        // we allow the same serial to be re-used, but older serials are not allowed.
        if (inputJdc.getCreationSerialNumber() < jdc.getCreationSerialNumber()) {
            throw new RuntimeException("imported data cannot have lower serial number" +
                    " than existing data. Existing: " + jdc.getCreationSerialNumber() +
                    ", import: " + inputJdc);
        }
    }


    /*
     * A convenience method for finding products via an array of IDs
     */
    public Set<Product> getProducts(String[] ids) {
        // a Product's .equals() method needs the ID plus the name
        // to match. Thus, we need to loop here.

        Set<Product> foundProducts = new HashSet<Product>();
        if (ids == null) {
            return foundProducts;
        }
        for (String id : ids) {
            for (Product p : jdc.getProducts()) {
                if (p.getId().equals(id)) {
                    foundProducts.add(p);
                }
            }
        }
        return foundProducts;
    }


}
