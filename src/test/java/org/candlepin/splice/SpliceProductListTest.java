package org.candlepin.splice;

import static org.junit.Assert.assertEquals;

import java.io.EOFException;
import java.io.IOException;
import java.util.List;

import org.candlepin.model.Product;
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
	public void testProductLoad() throws IOException {
		SpliceProductList spl = new SpliceProductList();
		spl.loadProducts(this.getClass().getClassLoader().getResource("test-products.json").getPath());
		assertEquals(3, spl.size());
	}
	
	@Test
	public void testGetProductList() throws IOException {
		SpliceProductList spl = new SpliceProductList();
		spl.loadProducts(this.getClass().getClassLoader().getResource("test-products.json").getPath());
		List<Product> list = spl.getProductList();
		//TODO: make sure the product is really there
		assertEquals(3, list.size());
	}


}
