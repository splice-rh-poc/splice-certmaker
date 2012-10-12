package org.candlepin.splice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;


import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.candlepin.model.Entitlement;
import org.candlepin.pki.PKIUtility;
import org.candlepin.util.X509ExtensionUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


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
		
		
			SpliceEntitlementFactory sef = new SpliceEntitlementFactory(spliceConfig, x509ExtensionUtil, spliceProductList, pkiUtility);
			Date now = new Date();
			Date later = DateUtils.addHours(now, 1);
			
			String[] productList = {"69"};
			
			Entitlement e = sef.createEntitlement(now, later, productList, "unit-test");
			
			assertEquals(now, e.getStartDate());
			assertEquals(later, e.getEndDate());
			assertEquals(1, e.getCertificates().size());
			verify(spliceProductList).getProducts(productList);
			
	}
	
    private KeyPair createKeyPair() {
        PublicKey pk = mock(PublicKey.class);
        PrivateKey ppk = mock(PrivateKey.class);
        return new KeyPair(pk, ppk);
    }


}
