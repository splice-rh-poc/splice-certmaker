package org.candlepin.splice;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
		//productList = new ArrayList<Product>();
		mapper = new ObjectMapper();
	}
	
		
	@SuppressWarnings("unchecked")
	public void loadProducts(String filename) throws JsonParseException, JsonMappingException, IOException {
		log.debug("loading product from " + filename);
		File file = new File(filename);
		productList = mapper.readValue(file, new TypeReference<List<Product>>() { });
		if (log.isDebugEnabled()) {
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
	}
	
	public int size() {
		return productList.size();
	}
	
	public List<Product> getProductList() {
		return productList;
	}

}
