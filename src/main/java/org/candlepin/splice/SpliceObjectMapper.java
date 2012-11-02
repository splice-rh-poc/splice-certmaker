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

import org.candlepin.jackson.ExportBeanPropertyFilter;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.DeserializerProvider;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.impl.SimpleFilterProvider;


/**
 * SpliceObjectMapper
 */
public class SpliceObjectMapper extends ObjectMapper {

    public SpliceObjectMapper() {
        super();
        // set a filter so we can serialize Entitlement objects
        SimpleFilterProvider filterProvider = new SimpleFilterProvider();
        filterProvider.setDefaultFilter(new ExportBeanPropertyFilter());
        this.setFilters(filterProvider);
        this.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);

    }

    public SpliceObjectMapper(JsonFactory jf) {
        super(jf);
    }

    public SpliceObjectMapper(JsonFactory jf, SerializerProvider sp,
            DeserializerProvider dp) {
        super(jf, sp, dp);
    }

    public SpliceObjectMapper(JsonFactory jf, SerializerProvider sp,
            DeserializerProvider dp, SerializationConfig sconfig,
            DeserializationConfig dconfig) {
        super(jf, sp, dp, sconfig, dconfig);
    }

}
