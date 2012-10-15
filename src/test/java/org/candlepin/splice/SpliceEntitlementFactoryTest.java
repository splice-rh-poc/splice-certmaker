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
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.candlepin.model.Product;
import org.candlepin.model.Entitlement;
import org.candlepin.pki.PKIUtility;
import org.candlepin.util.X509ExtensionUtil;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.WriterAppender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@RunWith(MockitoJUnitRunner.class)
public class SpliceEntitlementFactoryTest {

	@Mock private SpliceConfig spliceConfig;
	@Mock private X509ExtensionUtil x509ExtensionUtil;
	@Mock private SpliceProductList spliceProductList;
	@Mock private PKIUtility pkiUtility;
	
	@Test
	public void testRhicIdInEntitlement() {
		// TODO: impl me
	}
	
	@Test
	public void testCreateEntitlement() throws IOException {

			when(spliceConfig.getString("splice.product_json")).thenReturn("/tmp/test.json");
			KeyPair kp = createKeyPair();
			try {
				when(pkiUtility.generateNewKeyPair()).thenReturn(kp);
			} catch (NoSuchAlgorithmException e) {
				fail("NoSuchAlgorithmException");
			}
		
            Set<Product> mockSpliceProductList = new HashSet<Product>();
            mockSpliceProductList.add(new Product("100", "test product number 100"));
            
            when(spliceProductList.getProducts( new String[] {"100"} )).thenReturn(mockSpliceProductList);
            
			SpliceEntitlementFactory sef = new SpliceEntitlementFactory(spliceConfig, x509ExtensionUtil, spliceProductList, pkiUtility);
			Date now = new Date();
			Date later = DateUtils.addHours(now, 1);
			
			String[] productList = {"100"};
			
			Entitlement e = sef.createEntitlement(now, later, productList, "unit-test");
			
			assertEquals(now, e.getStartDate());
			assertEquals(later, e.getEndDate());
			assertEquals(1, e.getCertificates().size());
			verify(spliceProductList).getProducts(productList);
			
	}
	
    @Test
    public void testLogOnMissingProduct() throws IOException {

        // build up a logger so we can examine output
        Logger logger = Logger.getLogger(SpliceEntitlementFactory.class);  
        ByteArrayOutputStream out = new ByteArrayOutputStream();  
        Appender appender = new WriterAppender(new SimpleLayout(), out);
        logger.setLevel(Level.WARN);
        logger.addAppender(appender);
        
        when(spliceConfig.getString("splice.product_json")).thenReturn("/tmp/test.json");
        KeyPair kp = createKeyPair();
        try {
            when(pkiUtility.generateNewKeyPair()).thenReturn(kp);
        } catch (NoSuchAlgorithmException e) {
            fail("NoSuchAlgorithmException");
        }
	        

        SpliceEntitlementFactory sef = new SpliceEntitlementFactory(spliceConfig, x509ExtensionUtil, spliceProductList, pkiUtility);
        Date now = new Date();
        Date later = DateUtils.addHours(now, 1);
        
        String[] productList = {"999"}; // product id that does not exist
        
        try {  
            sef.createEntitlement(now, later, productList, "unit-test");
            
            String logMsg = out.toString();  
            assertNotNull(logMsg);  
            assertEquals("WARN - product ID 999 not found, not adding to entitlement certificate\n",logMsg);  
  
        } finally {  
            logger.removeAppender(appender);  
        }  

            
    }
	
	
    private KeyPair createKeyPair() {
        PublicKey pk = mock(PublicKey.class);
        PrivateKey ppk = mock(PrivateKey.class);
        return new KeyPair(pk, ppk);
    }


}
