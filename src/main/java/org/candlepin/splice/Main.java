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
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.candlepin.config.Config;
import org.candlepin.model.Entitlement;
import org.candlepin.model.EntitlementCertificate;

/**
 * Main
 */
public class Main {
	
	
	private static Logger log = Logger.getLogger(Main.class);
	
	private static SpliceEntitlementFactory spliceEntitlementFactory;
	
	 
    private Main() {
        // silence checkstyle
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
    	
    	Config config = new Config("candlepin.conf");
    	try {
			spliceEntitlementFactory = new SpliceEntitlementFactory(config, args[0]);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
    	
    	String[] productIds = {"69"};
    	Entitlement ent = spliceEntitlementFactory.createEntitlement(new Date(), DateUtils.addHours(new Date(), 1), productIds, "foo-rhic-id");
    	
		for (EntitlementCertificate c: ent.getCertificates()) {
			System.out.println(c.getCert());
			System.out.println(c.getKey());
		}
		
        
    }

}
