/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ignite.viewer;

import java.util.Arrays;
import org.apache.ignite.Ignite;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.internal.util.typedef.G;
import org.apache.ignite.viewer.config.NamesListGroup;
import org.apache.ignite.viewer.config.RegExpGroup;
import org.junit.Test;

public class TestHttpMetricsExporterSpi {
    @Test
    public void testCodeConfig() throws Exception {
        IgniteHttpMetricsExporterSpi httpSpi = new IgniteHttpMetricsExporterSpi();

        RegExpGroup all = new RegExpGroup();

        all.setContext("/all");
        all.setRegExp(".*");

        NamesListGroup sys = new NamesListGroup();

        sys.setContext("/sys");
        sys.setNames(Arrays.asList("sys"));

        RegExpGroup cacheGrp = new RegExpGroup();

        cacheGrp.setContext("/caches");
        cacheGrp.setRegExp("cache\\..*");

        NamesListGroup myCache = new NamesListGroup();

        myCache.setContext("/my-cache");
        myCache.setNames(Arrays.asList("cache.my-cache", "cacheGroups.my-cache"));

        httpSpi.setGroups(Arrays.asList(all, sys, cacheGrp, myCache));

        Ignite g1 = G.start(new IgniteConfiguration()
            .setIgniteInstanceName("ignite-1")
            .setMetricExporterSpi(httpSpi));

        g1.createCache("my-cache");

        Thread.sleep(60_000*60);
    }

    @Test
    public void testXmlConfig() throws Exception {
        Ignite g1 = G.start("ignite.xml");

        Thread.sleep(60_000*60);
    }
}
