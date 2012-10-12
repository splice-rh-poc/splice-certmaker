package org.candlepin.splice;

import org.candlepin.config.Config;

public class SpliceConfig extends Config {
	
	final static String CERTGEN_CONF_FILE = "/etc/splice-certgen.conf";

	public SpliceConfig() {
		super(CERTGEN_CONF_FILE);
	}


}
