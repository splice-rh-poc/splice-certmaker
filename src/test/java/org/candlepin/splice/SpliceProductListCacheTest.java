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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;

/**
 * SplceProductListCacheTest
 */
@RunWith(MockitoJUnitRunner.class)
public class SpliceProductListCacheTest {

    @Mock private SpliceConfig config;
    @Mock private ObjectMapper mapper;
    private ObjectMapper realMapper;


    @Before
    public void setUp() {
        realMapper = new ObjectMapper();
    }
    @Test(expected = RuntimeException.class)
    public void testConstuctorNoConfig() {
        when(config.getString("product_json_cache")).thenReturn(null); 
        new SpliceProductListCache(config, mapper);
    }

    @Test
    public void testConstuctorNoFile() {
        // this is the typical initial startup scenario
        when(config.getString("product_json_cache")).thenReturn("/tmp/stub_filename.json");
        SpliceProductListCache splc = new SpliceProductListCache(config, mapper);
        assertEquals(null, splc.getProductList());
    }

    @Test (expected = RuntimeException.class)
    public void testConstuctorUnreadableFile() throws IOException {
        when(config.getString("product_json_cache")).thenReturn(this.getClass().getClassLoader()
                .getResource("test-products.json").getPath());
        when(mapper.readValue(any(File.class), any(Class.class))).thenThrow(new IOException());

        new SpliceProductListCache(config, mapper);
    }

    @Test
    public void testConstructor() {
        when(config.getString("product_json_cache")).thenReturn(this.getClass().getClassLoader()
                .getResource("test-products.json").getPath());
        ObjectMapper realMapper = new ObjectMapper();

        SpliceProductListCache splc = new SpliceProductListCache(config, realMapper);
        assertEquals(3, splc.getProductList().size());
        assertEquals(1354222276, splc.getCachedSerial());
    }

    @Test
    public void writeValue() throws IOException {
        File cacheFile = File.createTempFile("splice_junit_writeValue", ".json");
        String cacheFilename = cacheFile.getAbsolutePath();
        cacheFile.delete(); // we just want the filename!

        when(config.getString("product_json_cache")).thenReturn(cacheFilename);
        SpliceProductListCache splc = new SpliceProductListCache(config, realMapper);

        // the data we want to write to the cache        
        String testJson = FileUtils.readFileToString(new File(this.getClass()
                .getClassLoader().getResource("test-products.json").getPath()));

        splc.writeCache(testJson);
        assertEquals(3, splc.getProductList().size());
        assertEquals(1354222276, splc.getCachedSerial());

        // confirm that the cache got persisted
        File outputFile = new File(cacheFilename);
        assertEquals(2770, outputFile.length());

        //cleanup
        outputFile.delete();
    }

}
