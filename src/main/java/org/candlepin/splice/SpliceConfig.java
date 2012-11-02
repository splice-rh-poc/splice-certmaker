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

import org.apache.log4j.Logger;

/**
 * SpliceConfig
 */
public class SpliceConfig extends Config {
    private static Logger log = Logger.getLogger(SpliceConfig.class);

    private static final String CERTGEN_CONF_FILE =
                        "/etc/splice-certmaker/splice-certmaker.conf";

    public SpliceConfig() {
        super(CERTGEN_CONF_FILE);
        log.info("loaded config from " + CERTGEN_CONF_FILE);
    }
}
