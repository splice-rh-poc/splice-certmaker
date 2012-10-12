package org.candlepin.splice;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.candlepin.model.Content;
import org.candlepin.model.Product;
import org.candlepin.model.ProductAttribute;
import org.candlepin.model.ProductContent;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

public class SpliceProductList {
	private static ObjectMapper mapper;
	
	private static Logger log = Logger.getLogger(SpliceProductList.class);
	private List<Product> productList;
	
	public SpliceProductList() {
		mapper = new ObjectMapper();
		productList = new ArrayList<Product>();
	}
	
		
	public void loadProducts(String filename) throws JsonParseException, JsonMappingException, IOException {
		log.debug("loading product from " + filename);
		File file = new File(filename);
		productList = mapper.readValue(file, new TypeReference<List<Product>>() { });
		
		// sanity check data, do not allow duplicates
				
		for (Product p: productList) {
			if (getProducts(new String[] {p.getId()}).size() > 1) {
				throw new RuntimeException("duplicate entry found during load, " + p.getId());
			}
		}

		if (log.isDebugEnabled()) {
			logProductList();
		}
	}


	private void logProductList() {
		log.debug("products loaded:");
		for (Product p: productList) {
			log.debug("name: " + p.getName());
			log.debug("attributes:");
			for (ProductAttribute pa: p.getAttributes()) {
				log.debug("\t" + pa.getName() + ": " + pa.getValue());
			}
			log.debug("content:");
			for (ProductContent pc: p.getProductContent()) {
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
		for (String id: ids) {
			for (Product p: productList) {
				if (p.getId().equals(id)) {
					foundProducts.add(p);
				}
			}
		}
		return foundProducts;
	
	}
}
