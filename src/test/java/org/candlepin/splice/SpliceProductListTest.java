package org.candlepin.splice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.EOFException;
import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.junit.Test;

public class SpliceProductListTest {

	@Test(expected = IOException.class)
	public void testBadFilenameLoad() throws IOException {
		SpliceProductList spl = new SpliceProductList();
		spl.loadProducts("fake-filename.json");
	}
	
	@Test(expected = EOFException.class)
	public void testEmptyFileLoad() throws IOException {
		SpliceProductList spl = new SpliceProductList();
		spl.loadProducts(this.getClass().getClassLoader().getResource("empty-file.json").getPath());
	}
	
	@Test
	public void testNoProductsLoaded() throws IOException {
		SpliceProductList spl = new SpliceProductList();
		assertEquals(0, spl.getAllProducts().size());
	}
	
	@Test
	public void testProductLoad() throws IOException {
		SpliceProductList spl = new SpliceProductList();
		spl.loadProducts(this.getClass().getClassLoader().getResource("test-products.json").getPath());
		assertEquals(3, spl.getAllProducts().size());
	}
	
	@Test
	public void testGetProductList() throws IOException {
		SpliceProductList spl = new SpliceProductList();
		spl.loadProducts(this.getClass().getClassLoader().getResource("test-products.json").getPath());
		
		String[] shouldExist = {"69"};
		assertEquals(1, spl.getProducts(shouldExist).size());
	}
	
	@Test
	public void testNoProductFound() throws IOException {
		SpliceProductList spl = new SpliceProductList();
		spl.loadProducts(this.getClass().getClassLoader().getResource("test-products.json").getPath());
		
		String[] shouldNotExist = {"123456"};
		assertEquals(0, spl.getProducts(shouldNotExist).size());
	}
	
	@Test
	public void testReloadProductList() throws IOException {
		SpliceProductList spl = new SpliceProductList();
		// first load
		spl.loadProducts(this.getClass().getClassLoader().getResource("test-products.json").getPath());
		assertEquals(3, spl.getAllProducts().size());
		// second load
		spl.loadProducts(this.getClass().getClassLoader().getResource("test-products.json").getPath());
		assertEquals(3, spl.getAllProducts().size());
	}
	
	@Test(expected = RuntimeException.class)
	public void testDuplicateIdInProductList() throws IOException {
		SpliceProductList spl = new SpliceProductList();
		spl.loadProducts(this.getClass().getClassLoader().getResource("test-products-duplicates.json").getPath());
	}
	
	@Test
	public void testGetTwoProducts() throws IOException {
		SpliceProductList spl = new SpliceProductList();
		spl.loadProducts(this.getClass().getClassLoader().getResource("test-products.json").getPath());
		
		String[] twoProducts = {"69", "83"};
		assertEquals(2, spl.getProducts(twoProducts).size());
		
		

	}




}
