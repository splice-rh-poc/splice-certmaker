/**
 * Copyright (c) 2009 - 2012 Red Hat, Inc.
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

import org.mortbay.thread.QueuedThreadPool;


public class SpliceQueuedThreadPool extends QueuedThreadPool {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = -7561685094165165475L;

    public SpliceQueuedThreadPool() {
            super();
            
            // TODO: read this from a conf file, and log what we did!
            this.setMinThreads(5);
            this.setMaxThreads(100);
    }

    public SpliceQueuedThreadPool(int maxThreads) {
        super(maxThreads);
    }

}
