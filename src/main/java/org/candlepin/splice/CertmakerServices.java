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

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

/**
 * CertmakerServices
 */
public class CertmakerServices extends Application {

    private static Set<Object> services = new HashSet<Object>();

    public CertmakerServices() {
        // this is initialized via servlet configs, so we can't pass an injector in
        Injector injector = Guice.createInjector(Stage.PRODUCTION, new CertgenModule());

        services.add(injector.getInstance(PingResource.class));
        services.add(injector.getInstance(CertgenResource.class));

    }

    @SuppressWarnings("unchecked")
    @Override
    public  Set getSingletons() {
        return services;
    }

    @SuppressWarnings("rawtypes")
    public  static Set getServices() {
        return services;
    }
}
