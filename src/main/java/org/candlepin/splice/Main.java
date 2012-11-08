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
import com.sun.akuma.Daemon;

import org.apache.log4j.Logger;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;

/**
 * Main
 */
public class Main {

    private static Logger log = Logger.getLogger(Main.class);

    private static Injector injector;
    private static Server server;
    private static Config config;

    private static final int ERROR_DAEMON_INIT = -1;
    private static final int ERROR_DAEMON_DAEMONIZE = -2;
    private static final int ERROR_NO_CONFIG = -4;


    private Main() {
        // silence checkstyle
    }

    public static void main(String[] args) throws Exception {

        // daemonization stuff
        Daemon daemon = new Daemon();

        //TODO: read from config
        boolean shouldDaemonize = false;
        if (System.getProperty("daemonize") != null) {
            shouldDaemonize = Boolean.valueOf(System.getProperty("daemonize"));
        }

        String pidfile = "/var/run/splice/splice-certmaker.pid";

        if (daemon.isDaemonized()) {
            // if we are in here, we are the forked copy
            daemon.init(pidfile);
        }
        else {
            // if we are in here, we are *not* the forked copy
            if (shouldDaemonize) {
                daemon.daemonize();
                System.out.println("Running certmaker in daemon mode.");
                System.exit(0);
            }
        }

        // wrap the server start so we can print a full stacktrace if needed
        try {
            startServer();
        }
        catch (Exception e) {
            log.error("unhandled error from server");
            e.printStackTrace();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                try {
                    shutdownServer();
                }
                catch (Exception e) {
                    log.error("unhandled error from server during shutdown!");
                    e.printStackTrace();
                }
            }
        }, "shutdownHook"));

    }

    private static void startServer() throws Exception {
        injector = Guice.createInjector(new CertgenModule());

        config = injector.getInstance(SpliceConfig.class);

        int listenPort = config.getInt("splice.certmaker_listen_port", 8080);

        log.info("starting server on port " + listenPort);
        server = new Server();
        // use NIO connector. I didn't benchmark this, I am just going off the docs
        Connector conn = injector.getInstance(SelectChannelConnector.class);
        conn.setPort(listenPort);
        conn.setServer(server);
        server.addConnector(conn);
        server.setThreadPool(injector.getInstance(SpliceQueuedThreadPool.class));
        server.setHandler(injector.getInstance(CertgenHandler.class));
        server.start();
        log.info("server started!");
    }

    private static void shutdownServer() throws Exception {
        log.warn("Shutting down...");
        server.stop();
        log.warn("Shutdown complete");
    }

}
