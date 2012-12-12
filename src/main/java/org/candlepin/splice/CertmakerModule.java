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

import org.candlepin.config.Config;
import org.candlepin.pki.PKIReader;
import org.candlepin.pki.PKIUtility;
import org.candlepin.pki.SubjectKeyIdentifierWriter;
import org.candlepin.pki.impl.BouncyCastlePKIReader;
import org.candlepin.pki.impl.BouncyCastlePKIUtility;
import org.candlepin.pki.impl.DefaultSubjectKeyIdentifierWriter;

import com.google.inject.AbstractModule;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * CertgenModule
 */
public class CertmakerModule extends AbstractModule {

    private static Logger log = Logger.getLogger(CertmakerModule.class);


    @Override
    protected void configure() {
        log.info("injecting objects");
        try {
            // most of this was copied from candlepin's injector module
            bind(PKIUtility.class).to(BouncyCastlePKIUtility.class).asEagerSingleton();
            bind(PKIReader.class).to(BouncyCastlePKIReader.class).asEagerSingleton();
            bind(SubjectKeyIdentifierWriter.class)
            .to(DefaultSubjectKeyIdentifierWriter.class).asEagerSingleton();
            bind(ObjectMapper.class).to(SpliceObjectMapper.class).asEagerSingleton();
            bind(Config.class).to(SpliceConfig.class).asEagerSingleton();
        }
        catch (Exception e) {
            log.error("Unable to inject!", e);
        }
    }
}
