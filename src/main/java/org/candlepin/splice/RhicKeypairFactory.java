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

import org.candlepin.pki.PKIUtility;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.apache.log4j.Logger;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RhicKeypairFactory
 */
@Singleton
public class RhicKeypairFactory {
    private static Logger log = Logger.getLogger(RhicKeypairFactory.class);

    private Map<String, KeyPair> keypairMap;

    private PKIUtility pkiUtility;

    @Inject
    public RhicKeypairFactory(PKIUtility pkiUtility) {
        log.debug("creating keypair factory");
        this.pkiUtility = pkiUtility;
        keypairMap = new ConcurrentHashMap<String, KeyPair>();
    }

    public synchronized KeyPair getKeyPair(String rhicId) {
        KeyPair kp = keypairMap.get(rhicId);
        if (kp == null) {
            log.info("keypair cache miss for " + rhicId + ". Generating new keypair");
            try {
                kp = pkiUtility.generateNewKeyPair();
                keypairMap.put(rhicId, kp);
            }
            catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            log.debug("new pubkey for " + rhicId + " is " + kp.getPublic());
        }
        else {
            log.debug("cache hit, pubkey for " + rhicId + " is " + kp.getPublic());
        }
        return kp;
    }


}
