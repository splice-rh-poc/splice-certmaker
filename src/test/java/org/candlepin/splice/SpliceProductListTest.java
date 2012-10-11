package org.candlepin.splice;

import static org.junit.Assert.assertEquals;

import java.io.EOFException;
import java.io.IOException;

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

}
