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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.EOFException;
import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

public class SpliceProductListTest {

    ObjectMapper mapper;
    
    @Before
    public void init() {
        this.mapper = new ObjectMapper();
    }
    	
	@Test(expected = EOFException.class)
	public void testEmptyFileLoad() throws IOException {
		SpliceProductList spl = new SpliceProductList(mapper);
		spl.loadProducts(this.getClass().getClassLoader().getResource("empty-file.json").getPath());
	}
	
	@Test
	public void testNoProductsLoaded() throws IOException {
		SpliceProductList spl = new SpliceProductList(mapper);
		assertEquals(0, spl.getAllProducts().size());
	}
	
	@Test
	public void testProductLoad() throws IOException {
		SpliceProductList spl = new SpliceProductList(mapper);
		spl.loadProducts(this.getClass().getClassLoader().getResource("test-products.json").getPath());
		assertEquals(3, spl.getAllProducts().size());
	}
	
	@Test
	public void testGetProductList() throws IOException {
		SpliceProductList spl = new SpliceProductList(mapper);
		spl.loadProducts(this.getClass().getClassLoader().getResource("test-products.json").getPath());
		
		String[] shouldExist = {"69"};
		assertEquals(1, spl.getProducts(shouldExist).size());
	}
	
	@Test
	public void testNoProductFound() throws IOException {
		SpliceProductList spl = new SpliceProductList(mapper);
		spl.loadProducts(this.getClass().getClassLoader().getResource("test-products.json").getPath());
		
		String[] shouldNotExist = {"123456"};
		assertEquals(0, spl.getProducts(shouldNotExist).size());
	}
	
	@Test
	public void testReloadProductList() throws IOException {
		SpliceProductList spl = new SpliceProductList(mapper);
		// first load
		spl.loadProducts(this.getClass().getClassLoader().getResource("test-products.json").getPath());
		assertEquals(3, spl.getAllProducts().size());
		// second load
		spl.loadProducts(this.getClass().getClassLoader().getResource("test-products.json").getPath());
		assertEquals(3, spl.getAllProducts().size());
	}
	
	@Test(expected = RuntimeException.class)
	public void testDuplicateIdInProductList() throws IOException {
		SpliceProductList spl = new SpliceProductList(mapper);
		spl.loadProducts(this.getClass().getClassLoader().getResource("test-products-duplicates.json").getPath());
	}
	
	@Test
	public void testGetTwoProducts() throws IOException {
	    SpliceProductList spl = new SpliceProductList(mapper);

		spl.loadProducts(this.getClass().getClassLoader().getResource("test-products.json").getPath());
		
		String[] twoProducts = {"69", "83"};
		assertEquals(2, spl.getProducts(twoProducts).size());

	}

}
