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

import static org.junit.Assert.assertEquals;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.io.EOFException;
import java.io.IOException;

public class SpliceProductListTest {

    private ObjectMapper mapper;

    @Before
    public void init() {
        this.mapper = new ObjectMapper();
    }

    @Test(expected = EOFException.class)
    public void testEmptyFileLoad() throws IOException {
        SpliceProductList spl = new SpliceProductList(mapper);
        spl.loadProducts(this.getClass().getClassLoader()
                .getResource("empty-file.json").getPath());
    }

    @Test
    public void testNoProductsLoaded() throws IOException {
        SpliceProductList spl = new SpliceProductList(mapper);
        assertEquals(0, spl.getAllProducts().size());
    }

    @Test
    public void testProductLoad() throws IOException {
        SpliceProductList spl = new SpliceProductList(mapper);
        spl.loadProducts(this.getClass().getClassLoader()
                .getResource("test-products.json").getPath());
        assertEquals(3, spl.getAllProducts().size());
    }

    @Test
    public void testGetProductList() throws IOException {
        SpliceProductList spl = new SpliceProductList(mapper);
        spl.loadProducts(this.getClass().getClassLoader()
                .getResource("test-products.json").getPath());
        String[] shouldExist = {"69"};
        assertEquals(1, spl.getProducts(shouldExist).size());
    }

    @Test
    public void testGetProductListNull() throws IOException {
        // this happens if no products were sent in via the URL query params
        SpliceProductList spl = new SpliceProductList(mapper);
        spl.loadProducts(this.getClass().getClassLoader()
                .getResource("test-products.json").getPath());
        assertEquals(0, spl.getProducts(null).size());
    }

    @Test
    public void testNoProductFound() throws IOException {
        SpliceProductList spl = new SpliceProductList(mapper);
        spl.loadProducts(this.getClass().getClassLoader()
                .getResource("test-products.json").getPath());
        String[] shouldNotExist = {"123456"};
        assertEquals(0, spl.getProducts(shouldNotExist).size());
    }

    @Test(expected = RuntimeException.class)
    public void testReloadProductListSameSerial() throws IOException {
        SpliceProductList spl = new SpliceProductList(mapper);
        // first load
        spl.loadProducts(this.getClass().getClassLoader()
                .getResource("test-products.json").getPath());
        assertEquals(3, spl.getAllProducts().size());
        // second load, should fail due to using same serial again
        spl.loadProducts(this.getClass().getClassLoader()
                .getResource("test-products.json").getPath());
    }

    @Test
    public void testReloadProductListNewSerial() throws IOException {
        SpliceProductList spl = new SpliceProductList(mapper);
        // first load
        spl.loadProducts(this.getClass().getClassLoader()
                .getResource("test-products-old-serial.json").getPath());
        assertEquals(3, spl.getAllProducts().size());
        // second load with newer data
        spl.loadProducts(this.getClass().getClassLoader()
                .getResource("test-products.json").getPath());
        assertEquals(3, spl.getAllProducts().size());
    }

    @Test(expected = RuntimeException.class)
    public void testReloadProductListOldSerial() throws IOException {
        SpliceProductList spl = new SpliceProductList(mapper);
        // first load
        spl.loadProducts(this.getClass().getClassLoader()
                .getResource("test-products.json").getPath());
        assertEquals(3, spl.getAllProducts().size());
        // second load with older data, should fail
        spl.loadProducts(this.getClass().getClassLoader()
                .getResource("test-products-old-serial.json").getPath());
    }



    @Test(expected = RuntimeException.class)
    public void testDuplicateIdInProductList() throws IOException {
        SpliceProductList spl = new SpliceProductList(mapper);
        spl.loadProducts(this.getClass().getClassLoader()
                .getResource("test-products-duplicates.json").getPath());
    }

    @Test
    public void testGetTwoProducts() throws IOException {
        SpliceProductList spl = new SpliceProductList(mapper);
        spl.loadProducts(this.getClass().getClassLoader()
                .getResource("test-products.json").getPath());
        String[] twoProducts = {"69", "83"};
        assertEquals(2, spl.getProducts(twoProducts).size());

    }

    @Test
    public void testGetProductListSerial() throws IOException {
        SpliceProductList spl = new SpliceProductList(mapper);
        spl.loadProducts(this.getClass().getClassLoader()
                .getResource("test-products.json").getPath());
        assertEquals(1354222276, spl.getListCreationSerialNumber());

    }

}
