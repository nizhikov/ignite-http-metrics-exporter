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

import java.util.List;
import java.util.function.Predicate;
import org.apache.ignite.spi.IgniteSpiAdapter;
import org.apache.ignite.spi.IgniteSpiException;
import org.apache.ignite.spi.metric.MetricExporterSpi;
import org.apache.ignite.spi.metric.ReadOnlyMetricManager;
import org.apache.ignite.spi.metric.ReadOnlyMetricRegistry;
import org.apache.ignite.viewer.config.MetricsGroup;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.jetbrains.annotations.Nullable;

/**
 *
 */
public class IgniteHttpMetricsExporterSpi extends IgniteSpiAdapter implements MetricExporterSpi {
    /** Http port. */
    private int port = 8080;

    /** Metric manager. */
    private ReadOnlyMetricManager mreg;

    /** Filter. */
    private Predicate<ReadOnlyMetricRegistry> filter;

    private List<MetricsGroup> groups;

    /** Server. */
    private Server srv;

    /** {@inheritDoc} */
    @Override public void spiStart(@Nullable String igniteInstanceName) throws IgniteSpiException {
        srv = new Server(port);

        ContextHandler[] ctxs = new ContextHandler[groups.size()];

        for (int i = 0; i < groups.size(); i++) {
            MetricsGroup grp = groups.get(i);

            grp.init(mreg);

            ctxs[i] = new ContextHandler(grp.context());
            ctxs[i].setHandler(new MetricsGroupHandler(grp));
        }

        srv.setHandler(new ContextHandlerCollection(ctxs));

        try {
            srv.start();
        }
        catch (Exception e) {
            throw new IgniteSpiException(e);
        }
    }

    /** {@inheritDoc} */
    @Override public void spiStop() throws IgniteSpiException {
        try {
            srv.stop();
        }
        catch (Exception e) {
            log.warning("Error on stop:", e);
        }
    }

    /** {@inheritDoc} */
    @Override public void setMetricRegistry(ReadOnlyMetricManager mreg) {
        this.mreg = mreg;
    }

    /** {@inheritDoc} */
    @Override public void setExportFilter(Predicate<ReadOnlyMetricRegistry> filter) {
        this.filter = filter;
    }

    /** Sets http port to export metrics. */
    public void setPort(int port) {
        this.port = port;
    }

    /** Sets metric groups. */
    public void setGroups(List<MetricsGroup> groups) {
        this.groups = groups;
    }
}
