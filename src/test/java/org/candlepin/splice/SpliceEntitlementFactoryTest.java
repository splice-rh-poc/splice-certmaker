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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.candlepin.model.Entitlement;
import org.candlepin.model.EntitlementCertificate;
import org.candlepin.model.Product;
import org.candlepin.pki.PKIUtility;
import org.candlepin.util.X509ExtensionUtil;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Appender;
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
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


@RunWith(MockitoJUnitRunner.class)
public class SpliceEntitlementFactoryTest {

    @Mock private SpliceConfig spliceConfig;
    @Mock private X509ExtensionUtil x509ExtensionUtil;
    @Mock private SpliceProductList spliceProductList;
    @Mock private PKIUtility pkiUtility;
    @Mock private RhicKeypairFactory rhicKeypairFactory;

//    @Test
//    public void testRhicIdInEntitlement() throws IOException {
//        when(spliceConfig.getString("splice.product_json")).thenReturn("/tmp/test.json");
//        KeyPair kp = createKeyPair();
//
//        when(rhicKeypairFactory.getKeyPair(any(String.class))).thenReturn(kp);
//
//
//        Set<Product> mockSpliceProductList = new HashSet<Product>();
//        mockSpliceProductList.add(new Product("100", "test product number 100"));
//
//        when(spliceProductList.getProducts(new String[] {"100"}))
//             .thenReturn(mockSpliceProductList);
//
//        SpliceEntitlementFactory sef = new SpliceEntitlementFactory(spliceConfig,
//             x509ExtensionUtil, spliceProductList, pkiUtility, rhicKeypairFactory);
//        Date now = new Date();
//        Date later = DateUtils.addHours(now, 1);
//
//        String[] productList = {"100"};
//
//        Entitlement e1 = sef.createEntitlement(now, later, productList, "unit-test");
//        for (EntitlementCertificate eci: e1.getCertificates()) {
//            //assert that rhic is in the cert
//        }
//    }

    @Test
    public void testUniqueCertSerial() throws IOException {
        when(spliceConfig.getString("product_json")).thenReturn(this.getClass()
                .getClassLoader().getResource("test-products.json").getPath());
        KeyPair kp = createKeyPair();

        when(rhicKeypairFactory.getKeyPair(any(String.class))).thenReturn(kp);


        Set<Product> mockSpliceProductList = new HashSet<Product>();
        mockSpliceProductList.add(new Product("100", "test product number 100"));

        when(spliceProductList.getProducts(new String[] {"100"}))
                    .thenReturn(mockSpliceProductList);

        SpliceEntitlementFactory sef = new SpliceEntitlementFactory(spliceConfig,
                x509ExtensionUtil, spliceProductList, pkiUtility, rhicKeypairFactory);
        Date now = new Date();
        Date later = DateUtils.addHours(now, 1);

        String[] productList = {"100"};

        Entitlement e1 = sef.createEntitlement(now, later, productList, "unit-test");
        Entitlement e2 = sef.createEntitlement(now, later, productList, "unit-test");

        // make sure serials aren't being duplicated
        for (EntitlementCertificate eci : e1.getCertificates()) {
            for (EntitlementCertificate ecj : e2.getCertificates()) {
                assertTrue(eci.getSerial().getSerial()
                        .compareTo(ecj.getSerial().getSerial()) != 0);
            }
        }
    }

    @Test(expected = RuntimeException.class)
    public void testNullProductFilename() throws IOException {

        when(spliceConfig.getString("product_json")).thenReturn(null);
        KeyPair kp = createKeyPair();
        when(rhicKeypairFactory.getKeyPair(any(String.class))).thenReturn(kp);


        Set<Product> mockSpliceProductList = new HashSet<Product>();
        mockSpliceProductList.add(new Product("100", "test product number 100"));

        when(spliceProductList.getProducts(new String[] {"100"}))
                .thenReturn(mockSpliceProductList);

        SpliceEntitlementFactory sef = new SpliceEntitlementFactory(spliceConfig,
                x509ExtensionUtil, spliceProductList, pkiUtility, rhicKeypairFactory);
        Date now = new Date();
        Date later = DateUtils.addHours(now, 1);

        String[] productList = {"100"};

        Entitlement e = sef.createEntitlement(now, later, productList, "unit-test");

    }

    @Test
    public void testNoRhicGiven() throws IOException {

        when(spliceConfig.getString("product_json")).thenReturn(this.getClass()
                .getClassLoader().getResource("test-products.json").getPath());
        when(rhicKeypairFactory.getKeyPair(any(String.class)))
                                    .thenThrow(new RuntimeException("exception!"));

        Set<Product> mockSpliceProductList = new HashSet<Product>();
        mockSpliceProductList.add(new Product("100", "test product number 100"));

        when(spliceProductList.getProducts(new String[] {"100"}))
                .thenReturn(mockSpliceProductList);

        SpliceEntitlementFactory sef = new SpliceEntitlementFactory(spliceConfig,
                x509ExtensionUtil, spliceProductList, pkiUtility, rhicKeypairFactory);
        Date now = new Date();
        Date later = DateUtils.addHours(now, 1);

        String[] productList = {"100"};

        try {
            sef.createEntitlement(now, later, productList, null);
            fail();
        }
        catch (Exception e) {
            // we do not want the "exception!" message
            assertTrue(e.getMessage().equals("no rhic ID specified"));
        }

    }

    @Test
    public void testCreateEntitlement() throws IOException {

        when(spliceConfig.getString("product_json")).thenReturn(this.getClass()
                .getClassLoader().getResource("test-products.json").getPath());
        KeyPair kp = createKeyPair();
        when(rhicKeypairFactory.getKeyPair(any(String.class))).thenReturn(kp);


        Set<Product> mockSpliceProductList = new HashSet<Product>();
        mockSpliceProductList.add(new Product("100", "test product number 100"));

        when(spliceProductList.getProducts(new String[] {"100"}))
                .thenReturn(mockSpliceProductList);

        SpliceEntitlementFactory sef = new SpliceEntitlementFactory(spliceConfig,
                x509ExtensionUtil, spliceProductList, pkiUtility, rhicKeypairFactory);
        Date now = new Date();
        Date later = DateUtils.addHours(now, 1);

        String[] productList = {"100"};

        Entitlement e = sef.createEntitlement(now, later, productList, "unit-test");

        assertEquals(now, e.getStartDate());
        assertEquals(later, e.getEndDate());
        assertEquals(1, e.getCertificates().size());
        verify(spliceProductList).getProducts(productList);

    }

    @Test(expected = RuntimeException.class)
    public void testCreateEntitlementNoProducts() throws IOException {

        when(spliceConfig.getString("product_json")).thenReturn(this.getClass()
                .getClassLoader().getResource("test-products.json").getPath());
        KeyPair kp = createKeyPair();
        when(rhicKeypairFactory.getKeyPair(any(String.class))).thenReturn(kp);


        Set<Product> mockSpliceProductList = new HashSet<Product>();
        mockSpliceProductList.add(new Product("100", "test product number 100"));

        when(spliceProductList.getProducts(new String[] {"100"}))
                .thenReturn(mockSpliceProductList);

        SpliceEntitlementFactory sef = new SpliceEntitlementFactory(spliceConfig,
                x509ExtensionUtil, spliceProductList, pkiUtility, rhicKeypairFactory);
        Date now = new Date();
        Date later = DateUtils.addHours(now, 1);

        Entitlement e = sef.createEntitlement(now, later, null, "unit-test");
    }

    @Test
    public void testLogOnMissingProduct() throws IOException {

        // build up a logger so we can examine output
        Logger logger = Logger.getLogger(SpliceEntitlementFactory.class);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Appender appender = new WriterAppender(new SimpleLayout(), out);
        logger.setLevel(Level.WARN);
        logger.addAppender(appender);

        when(spliceConfig.getString("product_json")).thenReturn(this.getClass()
                .getClassLoader().getResource("test-products.json").getPath());
        KeyPair kp = createKeyPair();
        when(rhicKeypairFactory.getKeyPair(any(String.class))).thenReturn(kp);


        SpliceEntitlementFactory sef = new SpliceEntitlementFactory(spliceConfig,
                x509ExtensionUtil, spliceProductList, pkiUtility, rhicKeypairFactory);
        Date now = new Date();
        Date later = DateUtils.addHours(now, 1);

        String[] productList = {"999"}; // product id that does not exist

        try {
            sef.createEntitlement(now, later, productList, "unit-test");

            String logMsg = out.toString();
            assertNotNull(logMsg);
            assertEquals("WARN - product ID 999 not found, not adding " +
                    "to entitlement certificate\n", logMsg);
        }
        finally {
            logger.removeAppender(appender);
        }
    }

    private KeyPair createKeyPair() {
        PublicKey pk = mock(PublicKey.class);
        PrivateKey ppk = mock(PrivateKey.class);
        return new KeyPair(pk, ppk);
    }
}
