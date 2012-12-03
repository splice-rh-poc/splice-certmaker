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

import org.candlepin.model.Content;
import org.candlepin.model.Product;
import org.candlepin.model.ProductAttribute;
import org.candlepin.model.ProductContent;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * SpliceProductList
 */
@Singleton
public class SpliceProductList {
    private ObjectMapper mapper;

    private static Logger log = Logger.getLogger(SpliceProductList.class);
    private List<Product> productList;
    // this is just a unix timestamp!
    private long listCreationSerialNumber;

    @Inject
    public SpliceProductList(ObjectMapper mapper) {
        this.mapper = mapper;
        productList = new ArrayList<Product>();
        listCreationSerialNumber = 0;
    }

    // this needs to be static so jackson can create one without having
    // to create the outer class!
    /**
     * JsonDataContainer
     */
    public static class JsonDataContainer {
        private long creationSerialNumber;
        private List<Product> products;

        public long getCreationSerialNumber() {
            return creationSerialNumber;
        }

        public void setCreationSerialNumber(long serial) {
            creationSerialNumber = serial;
        }

        public List<Product> getProducts() {
            return products;
        }

        public void setProducts(List<Product> products) {
            this.products = products;
        }
    }

    public void loadProductsByJson(String json) throws JsonParseException,
    JsonMappingException, IOException {
        log.debug("loading product list from json");
        log.debug(json);
        JsonDataContainer jdc = mapper.readValue(json, JsonDataContainer.class);
        loadJdc(jdc);
    }

    public void loadProducts(String filename) throws JsonParseException,
    JsonMappingException, IOException {
        log.debug("loading product list from " + filename);
        File file = new File(filename);
        JsonDataContainer jdc = mapper.readValue(file, JsonDataContainer.class);
        loadJdc(jdc);
    }

    private void loadJdc(JsonDataContainer jdc) {
        // ensure we are not importing identical or stale data

        if (jdc.creationSerialNumber <= listCreationSerialNumber) {
            throw new RuntimeException("imported data cannot have lower serial number" +
                    " than existing data. Existing: " + listCreationSerialNumber +
                    ", import: " + jdc.creationSerialNumber);
        }

        if (jdc.creationSerialNumber < this.listCreationSerialNumber) {
            throw new RuntimeException("current creation serial number " +
                    this.listCreationSerialNumber + " is newer than import's serial " +
                    jdc.creationSerialNumber);
        }

        productList = jdc.products;
        setListCreationSerialNumber(jdc.creationSerialNumber);

        // sanity check data, do not allow duplicates

        for (Product p : productList) {
            if (getProducts(new String[] {p.getId()}).size() > 1) {
                throw new RuntimeException("duplicate entry found during load, " +
                        p.getId());
            }
        }

        if (log.isDebugEnabled()) {
            logProductList();
        }
    }

    private void logProductList() {
        log.debug("products loaded:");
        for (Product p : productList) {
            log.debug("name: " + p.getName());
            log.debug("attributes:");
            for (ProductAttribute pa : p.getAttributes()) {
                log.debug("\t" + pa.getName() + ": " + pa.getValue());
            }
            log.debug("content:");
            for (ProductContent pc : p.getProductContent()) {
                Content c = pc.getContent();
                log.debug("\tcontent url: " + c.getContentUrl());
                log.debug("\trelease ver: " + c.getReleaseVer());
                log.debug("\tenabled: " + pc.getEnabled());
            }
        }
    }

    public List<Product> getAllProducts() {
        return productList;
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
            for (Product p : productList) {
                if (p.getId().equals(id)) {
                    foundProducts.add(p);
                }
            }
        }
        return foundProducts;
    }

    public long getListCreationSerialNumber() {
        return listCreationSerialNumber;
    }

    public void setListCreationSerialNumber(long listCreationSerialNumber) {
        this.listCreationSerialNumber = listCreationSerialNumber;
    }
}
