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

import com.google.inject.Guice;
import com.google.inject.Injector;

import org.apache.log4j.Logger;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;

/**
 * Main
 */
public class Main {
	
	private static Logger log = Logger.getLogger(Main.class);
	
	static Injector injector;
	
	static Server server;
	
	static Config config;
	 
    private Main() {
        // silence checkstyle
    }

    /**
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) {

        // wrap the server start so we can print a full stacktrace if needed
        try {
            startServer();
        }
        catch (Exception e) {
            log.error("unhandled error from server");
            e.printStackTrace();
        }
        
    }

    private static void startServer() throws Exception {
        injector = Guice.createInjector(new CertgenModule());
        
        config = injector.getInstance(SpliceConfig.class);
        
        int listenPort = config.getInt("splice.certmaker_listen_port", 8080);

        log.info("starting server on port " + listenPort);
    	server = new Server();
    	// use NIO connector. I didn't benchmark this, I am just going off the docs here
    	Connector conn = injector.getInstance(SelectChannelConnector.class);
    	conn.setPort(listenPort);
    	conn.setServer(server);
    	server.addConnector(conn);
    	server.setThreadPool(injector.getInstance(SpliceQueuedThreadPool.class));
    	server.setHandler(injector.getInstance(CertgenHandler.class));
    	server.start();
    	log.info("server started!");
    }

}
