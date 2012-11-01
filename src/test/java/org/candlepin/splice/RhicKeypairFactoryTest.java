/**
 * Copyright (c) 2009 - 2012 Red Hat, Inc.
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.candlepin.pki.PKIUtility;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

@RunWith(MockitoJUnitRunner.class)
public class RhicKeypairFactoryTest {
    
    @Mock private PKIUtility pkiUtility;   


//    @Test
//    public void testKeypairFactory() {
//        fail("Not yet implemented");
//    }

    @Test
    public void testGetKeyPair() throws NoSuchAlgorithmException {
        // run this twice, so we get different mocks back for the comparison later. Thx dgoodwin!
        when(pkiUtility.generateNewKeyPair()).thenReturn(createKeyPair(), createKeyPair());
        RhicKeypairFactory kpf = new RhicKeypairFactory(pkiUtility);
        KeyPair kp1 = kpf.getKeyPair("foo");
        KeyPair kp2 = kpf.getKeyPair("foo");
        KeyPair kp3 = kpf.getKeyPair("bar");
        
        // kp1 should be the same as kp2
        assertTrue(kp1.getPublic().equals(kp2.getPublic()));
        // kp1 should be different than kp3
        assertFalse(kp1.getPublic().equals(kp3.getPublic()));
    }

    private KeyPair createKeyPair() {
        PublicKey pk = mock(PublicKey.class);
        PrivateKey ppk = mock(PrivateKey.class);
        return new KeyPair(pk, ppk);
    }
    

}
