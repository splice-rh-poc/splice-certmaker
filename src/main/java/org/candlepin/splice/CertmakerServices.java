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

import com.google.inject.ConfigurationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.ProvisionException;
import com.google.inject.Stage;

import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

/**
 * CertmakerServices
 */
public class CertmakerServices extends Application {

    private static Logger log = Logger.getLogger(CertmakerServices.class);

    private static Set<Object> services = new HashSet<Object>();

    public CertmakerServices() {
        
        log.info("initializing guice injector");
        // this is initialized via servlet configs, so we can't pass an injector in
        Injector injector = Guice.createInjector(Stage.PRODUCTION, new CertmakerModule());

        try {
            services.add(injector.getInstance(PingResource.class));
            services.add(injector.getInstance(CertmakerResource.class));
            services.add(injector.getInstance(ProductDefinitionResource.class));
        }
        catch (Exception e) {
            log.error("error during provisioning", e);
        }
        log.info("completed guice injector initialization");

    }

    @SuppressWarnings("unchecked")
    @Override
    public Set getSingletons() {
        return services;
    }

    @SuppressWarnings("rawtypes")
    public  static Set getServices() {
        return services;
    }
}
