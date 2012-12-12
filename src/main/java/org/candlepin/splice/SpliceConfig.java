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

import com.google.inject.Inject;

import org.apache.log4j.Logger;
import org.ini4j.Ini;

import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

/**
 * SpliceConfig
 */
public class SpliceConfig extends Config {
    private static Logger log = Logger.getLogger(SpliceConfig.class);

    private static final String CERTMAKER_CONF_FILE =
                        "/etc/splice/conf.d/server.conf";
    private static final String CERTMAKER_SHARED_CONF_FILE =
                        "/etc/splice/splice.conf";
    private static final String CERTMAKER_INI_SECTION_CERTMAKER = "entitlement";
    private static final String CERTMAKER_INI_SECTION_CERTS = "security";

    public SpliceConfig(Ini ini, String filename, String sharedFilename) {
        log.info("loading server config from " + filename);
        Map<String, String> serverConfigMap = loadConfigFile(ini, filename,
                CERTMAKER_INI_SECTION_CERTMAKER);

        log.info("loading shared config from " + sharedFilename);
        Map<String, String> sharedConfigMap = loadConfigFile(ini, sharedFilename,
                CERTMAKER_INI_SECTION_CERTS);

        configuration.putAll(serverConfigMap);
        configuration.putAll(sharedConfigMap);
        configuration.put("candlepin.ca_key", configuration.get("rhic_ca_key"));
        configuration.put("candlepin.ca_cert", configuration.get("rhic_ca_cert"));
        configuration.put("candlepin.upstream_ca_cert",
                                configuration.get("splice_server_identity_ca"));
    }

    private Map<String, String> loadConfigFile(Ini ini, String filename, String section) {
        try {
            ini.load(new FileReader(filename));
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new RuntimeException("Unable to read or parse config file");
        }
        Map<String, String> configMap = ini.get(section);

        if (configMap == null) {
            throw new RuntimeException(section +
                    " section not found in conf file.");
        }
        return configMap;
    }

    @Inject
    public SpliceConfig(Ini ini) {
        this(ini, CERTMAKER_CONF_FILE, CERTMAKER_SHARED_CONF_FILE);
    }
}
