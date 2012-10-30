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

import org.candlepin.model.CertificateSerial;
import org.candlepin.model.Consumer;
import org.candlepin.model.Entitlement;
import org.candlepin.model.EntitlementCertificate;
import org.candlepin.model.EnvironmentContent;
import org.candlepin.model.Pool;
import org.candlepin.model.Product;
import org.candlepin.model.Subscription;
import org.candlepin.pki.PKIUtility;
import org.candlepin.pki.X509ExtensionWrapper;
import org.candlepin.util.CertificateSizeException;
import org.candlepin.util.X509ExtensionUtil;
import org.candlepin.util.X509Util;

import com.google.common.collect.Collections2;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Singleton
public class SpliceEntitlementFactory {

	private static Logger log = Logger.getLogger(SpliceEntitlementFactory.class);
	
	PKIUtility pkiUtility;
	static KeyPair keypair;
	X509ExtensionUtil extensionUtil;
	SpliceProductList spliceProductList;
	
	@Inject
	public SpliceEntitlementFactory(SpliceConfig config, X509ExtensionUtil extensionUtil,
			SpliceProductList spliceProductList, PKIUtility pkiUtility) throws IOException {
		
    	this.extensionUtil= extensionUtil;
    	this.spliceProductList = spliceProductList;
    	this.pkiUtility = pkiUtility;
    	String productFilename = config.getString("splice.product_json");
    	if (productFilename == null) {
    	    throw new RuntimeException("splice.product_json is not defined in config file!");
    	}
    	spliceProductList.loadProducts(productFilename);

		log.debug("creating keypair");
		try {
			keypair = pkiUtility.generateNewKeyPair();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		log.debug("keypair created");

	}
	
	public Entitlement createEntitlement(Date startDate, Date endDate, String[] productIds, String rhicId) {
		// build up objects for the Entitlement
    	CertificateSerial serial = new CertificateSerial(1L);
		
	   	Consumer consumer = new Consumer();
    	consumer.setEnvironment(null);

    	Product phonyProduct = new Product("rhic", "rhic for " + rhicId);
    	Pool pool = createPhonyProduct(startDate, endDate, phonyProduct);
    	Entitlement ent = createPhonyEntitlement(pool);
    	Subscription sub = createPhonySubscription(startDate, endDate,
				phonyProduct);
    	
    	
    	Map<String, EnvironmentContent> promotedContent = new HashMap<String, EnvironmentContent>();

    	log.debug("generating certificate for products: " + Arrays.toString(productIds));
    	Set<X509ExtensionWrapper> extensions = createX509Extensions(productIds,
				consumer, ent, sub, promotedContent);
    	
        X509Certificate x509Cert = null;
		try {
            x509Cert = pkiUtility.createX509Certificate(
                    "cn=testtesttest", extensions, null, startDate,
                    endDate, keypair, serial.getSerial(), null);
        }
        catch (GeneralSecurityException e1) {
            e1.printStackTrace();
            throw new RuntimeException(e1);
        }
        catch (IOException e1) {
            e1.printStackTrace();
            throw new RuntimeException(e1);
        }
        
		EntitlementCertificate ec = new EntitlementCertificate();
		try {
			ec.setCertAsBytes(pkiUtility.getPemEncoded(x509Cert));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		

		ec.setSerial(serial);
		
		try {
			ec.setKeyAsBytes(pkiUtility.getPemEncoded(keypair.getPrivate()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Set<EntitlementCertificate> certs = new HashSet<EntitlementCertificate>();
		certs.add(ec);
		ent.setCertificates(certs);
		ent.setStartDate(startDate);
		ent.setEndDate(endDate);
		return ent;
		
	}

	private Set<X509ExtensionWrapper> createX509Extensions(String[] productIds,
			Consumer consumer, Entitlement ent, Subscription sub,
			Map<String, EnvironmentContent> promotedContent)
			throws RuntimeException {
		Set<Product> products = spliceProductList.getProducts(productIds);
		
		// build up a list of found product IDs
		ArrayList<String> foundProductIds = new ArrayList<String>();
		for (Product p: products) {
		    foundProductIds.add(p.getId());
		}

		// if productIds is a superset of foundProductIds, warn the user
		for (String pid : productIds) {
		    if (!foundProductIds.contains(pid)) {
		        log.warn("product ID " + pid + " not found, not adding to entitlement certificate");
		    }
		}

		// build up extensions
        Set<X509ExtensionWrapper> extensions = new LinkedHashSet<X509ExtensionWrapper>();
        for (Product prod : Collections2
            .filter(products, X509Util.PROD_FILTER_PREDICATE)) {
            log.debug("adding [" + prod.getId() + "] " + prod.getName() + " to entitlement certificate");
            extensions.addAll(extensionUtil.productExtensions(prod));
            try {
                extensions.addAll(extensionUtil.contentExtensions(prod.getProductContent(), null, promotedContent, consumer));
            } catch (CertificateSizeException cse ){
                throw new RuntimeException(cse);
                
            }
        }
        extensions.addAll(extensionUtil.subscriptionExtensions(sub, ent));
		return extensions;
	}

	private Entitlement createPhonyEntitlement(Pool pool) {
		Entitlement ent = new Entitlement();
    	ent.setPool(pool);
    	ent.setAccountNumber(pool.getProductName());
		return ent;
	}

	private Subscription createPhonySubscription(Date startDate, Date endDate,
			Product phonyProduct) {
		Subscription sub = new Subscription();
    	sub.setId(phonyProduct.getName());
    	sub.setQuantity(1L);
    	sub.setStartDate(startDate);
    	sub.setEndDate(endDate);
    	sub.setProduct(phonyProduct);
		return sub;
	}

	private Pool createPhonyProduct(Date startDate, Date endDate,
			Product phonyProduct) {
		Pool pool = new Pool();
    	pool.setProductName(phonyProduct.getName());
    	pool.setStartDate(startDate);
    	pool.setEndDate(endDate);
    	pool.setQuantity(1L);
		return pool;
	}
	 
}
