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

import com.google.inject.Inject;

import org.apache.log4j.Logger;
import org.ini4j.Ini;

import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

/**
 * SpliceConfig
 */
public class SpliceConfig {
    private static Logger log = Logger.getLogger(SpliceConfig.class);

    private static final String CERTGEN_CONF_FILE =
                        "/etc/splice/server.conf";
    private static final String CERTGEN_INI_SECTION = "certmaker";

    private static Map<String, String> configMap;

    public SpliceConfig(Ini ini, String filename) {
        log.info("loaded config from " + filename);
        try {
            ini.load(new FileReader(filename));
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new RuntimeException("Unable to read or parse config file");
        }
        configMap = ini.get(CERTGEN_INI_SECTION);
        if (configMap == null) {
            throw new RuntimeException(CERTGEN_INI_SECTION +
                    " section not found in conf file.");
        }
    }

    @Inject
    public SpliceConfig(Ini ini) {
        this(ini, CERTGEN_CONF_FILE);
    }

    public int getInt(String key, int defaultValue) {
        if (configMap.containsKey(key)) {
            return Integer.parseInt(configMap.get(key));
        }
        return defaultValue;
    }

    public String getString(String key) {
        return configMap.get(key);
    }
}
