package org.candlepin.splice;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.candlepin.config.Config;
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
import org.candlepin.pki.impl.BouncyCastlePKIReader;
import org.candlepin.pki.impl.BouncyCastlePKIUtility;
import org.candlepin.pki.impl.DefaultSubjectKeyIdentifierWriter;
import org.candlepin.util.CertificateSizeException;
import org.candlepin.util.X509ExtensionUtil;
import org.candlepin.util.X509Util;

import com.google.common.collect.Collections2;

public class SpliceEntitlementFactory {

	static PKIUtility pkiUtility;
	
	static KeyPair keypair;
	
	static X509ExtensionUtil extensionUtil;
	
	private static Logger log = Logger.getLogger(SpliceEntitlementFactory.class);
	
	private static SpliceProductList spliceProductList;
	
	public SpliceEntitlementFactory(Config config, String productJsonFile) throws IOException {
		// initialize one-time-use items
		
    	extensionUtil = new X509ExtensionUtil(config);
    	spliceProductList = new SpliceProductList();
    	
    	spliceProductList.loadProducts(productJsonFile);

    	try {
			pkiUtility = new BouncyCastlePKIUtility(new BouncyCastlePKIReader(config), new DefaultSubjectKeyIdentifierWriter());
		} catch (CertificateException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
    	

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

    	Set<X509ExtensionWrapper> extensions = createX509Extensions(productIds,
				consumer, ent, sub, promotedContent);
    	
        X509Certificate x509Cert = null;
		try {
			x509Cert = pkiUtility.createX509Certificate(
			        "cn=testtesttest", extensions, null, startDate,
			        endDate, keypair, serial.getSerial(), null);
		} catch (GeneralSecurityException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		EntitlementCertificate ec = new EntitlementCertificate();
		try {
			ec.setCertAsBytes(pkiUtility.getPemEncoded(x509Cert));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
    	// build up extensions
        Set<X509ExtensionWrapper> extensions = new LinkedHashSet<X509ExtensionWrapper>();
        for (Product prod : Collections2
            .filter(products, X509Util.PROD_FILTER_PREDICATE)) {
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
    	ent.setAccountNumber("foo-account");
		return ent;
	}

	private Subscription createPhonySubscription(Date startDate, Date endDate,
			Product phonyProduct) {
		Subscription sub = new Subscription();
    	sub.setId("foo-sub-id");
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
		return pool;
	}
	 
}
