/**
 * Copyright (c) 2011 Red Hat, Inc.
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

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.time.DateUtils;
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
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import com.google.common.collect.Collections2;

/**
 * Main
 */
public class Main {
	
	static PKIUtility pkiUtility;
	
	static KeyPair keypair;
	
	static X509ExtensionUtil extensionUtil;
	
	private static Logger log = Logger.getLogger(Main.class);
	
	private static SpliceProductList spliceProductList;
	 
    private Main() {
        // silence checkstyle
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
    	
    	// read config
    	
    	Config config = new Config("candlepin.conf");
    	
    	spliceProductList = new SpliceProductList();
    	
    	try {
			spliceProductList.loadProducts(args[0]);
		} catch (JsonParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.exit(-1);
		} catch (JsonMappingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.exit(-1);

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.exit(-1);

		}
    	
    	
    	try {
			pkiUtility = new BouncyCastlePKIUtility(new BouncyCastlePKIReader(config), new DefaultSubjectKeyIdentifierWriter());
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	        
        Date startDate = new Date();
        Date endDate = DateUtils.addHours(startDate, 1);


    	try {
    		log.debug("creating keypair");
			keypair = pkiUtility.generateNewKeyPair();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	extensionUtil = new X509ExtensionUtil(config);
    	
    	CertificateSerial serial = new CertificateSerial();
    	serial.setSerial(1L);
    	
    	// one-time use stuff
    	
    	Consumer consumer = new Consumer();
    	consumer.setEnvironment(null);
    	Entitlement ent = new Entitlement();
    	Subscription sub = new Subscription();
    	Product phonyProduct = new Product("rhic", "rhic for foo");
    	Pool pool = new Pool();
    	
    	pool.setProductName(phonyProduct.getName());
    	pool.setStartDate(startDate);
    	pool.setEndDate(endDate);
    	
    	ent.setPool(pool);
    	ent.setAccountNumber("foo");
    	
    	sub.setId("foo");
    	sub.setQuantity(1L);
    	sub.setStartDate(startDate);
    	sub.setEndDate(endDate);
    	sub.setProduct(phonyProduct);
    	
    	Set<Product> products = new HashSet<Product>();
    	products.addAll(spliceProductList.getProductList());
    	Map<String, EnvironmentContent> promotedContent = new HashMap<String, EnvironmentContent>();

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
		
		for (EntitlementCertificate c: ent.getCertificates()) {
			System.out.println(c.getCert());
			System.out.println(c.getKey());
		}
		
        
    }

}
