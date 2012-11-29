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

import static org.junit.Assert.assertEquals;

import org.ini4j.Ini;
import org.junit.Before;
import org.junit.Test;


public class SpliceConfigTest {

    private String testConfigFilename;
    private String testSharedConfigFilename;
    private String testConfigNoSectionFilename;


    @Before
    public void initialize() {
        testConfigFilename = this.getClass().getClassLoader()
                .getResource("test_config.conf").getPath();
        testSharedConfigFilename = this.getClass().getClassLoader()
                .getResource("test_config_common.conf").getPath();
        testConfigNoSectionFilename = this.getClass().getClassLoader()
                .getResource("test_config_no_section.conf").getPath();
    }

    @Test
    public void testGetInt() {
        SpliceConfig conf = new SpliceConfig(new Ini(), testConfigFilename,
                                        testSharedConfigFilename);
        assertEquals(8080, conf.getInt("port", 123));
        assertEquals(123, conf.getInt("not_exist", 123));
    }

    @Test
    public void testGetString() {
        SpliceConfig conf = new SpliceConfig(new Ini(), testConfigFilename,
                                                testSharedConfigFilename);
        assertEquals("foo", conf.getString("some_value"));
    }

    @Test
    public void testNoKey() {
        SpliceConfig conf = new SpliceConfig(new Ini(), testConfigFilename,
                                            testSharedConfigFilename);

        assertEquals(null, conf.getString("does_not_exist"));
    }

    @Test(expected = RuntimeException.class)
    public void testNoSection() {
        new SpliceConfig(new Ini(), testConfigNoSectionFilename, testSharedConfigFilename);
    }
}
