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
import java.util.concurrent.ThreadLocalRandom;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.internal.util.typedef.G;
import org.apache.ignite.viewer.config.NamesListGroup;
import org.apache.ignite.viewer.config.RegExpGroup;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

public class TestHttpMetricsExporterSpi {
    @Test
    public void testCodeConfig() throws Exception {
        Ignite g1 = G.start(new IgniteConfiguration()
            .setIgniteInstanceName("ignite-1")
            .setMetricExporterSpi(spi(8080)));

        Ignite g2 = G.start(new IgniteConfiguration()
            .setIgniteInstanceName("ignite-2")
            .setMetricExporterSpi(spi(8081)));

        CacheConfiguration cfg = new CacheConfiguration("my-cache")
            .setBackups(2);

        IgniteCache<Object, Object> cache = g1.createCache(cfg);

        doOps(cache);

        Thread.sleep(60_000*60);
    }

    @Test
    public void testXmlConfig() throws Exception {
        Ignite g1 = G.start("ignite.xml");

        IgniteCache<Object, Object> cache = g1.cache("my-cache");

        doOps(cache);

        Thread.sleep(60_000*60);
    }

    private void doOps(IgniteCache<Object, Object> cache) {
        for (int i=0; i< 10_000; i++) {
            if (ThreadLocalRandom.current().nextBoolean())
                cache.put(i, i);
            else {
                cache.put(i, new byte[16*1024]);
            }
        }
    }

    @NotNull private IgniteHttpMetricsExporterSpi spi(int port) {
        IgniteHttpMetricsExporterSpi httpSpi = new IgniteHttpMetricsExporterSpi();

        httpSpi.setPort(port);

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
        return httpSpi;
    }
}
