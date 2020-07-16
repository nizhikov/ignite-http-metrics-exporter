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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.ignite.internal.util.typedef.T2;
import org.apache.ignite.spi.metric.BooleanMetric;
import org.apache.ignite.spi.metric.HistogramMetric;
import org.apache.ignite.spi.metric.Metric;
import org.apache.ignite.spi.metric.ObjectMetric;
import org.apache.ignite.viewer.config.MetricsGroup;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.jetbrains.annotations.NotNull;

/**
 * Metrics group handler.
 */
public class MetricsGroupHandler extends AbstractHandler {
    /** Metrics group. */
    private final MetricsGroup mgrp;

    /** Cached histogram metrics intervals names. */
    private final Map<String, T2<long[], String[]>> histogramNames = new HashMap<>();

    /**
     * @param mgrp Metrics group.
     */
    public MetricsGroupHandler(MetricsGroup mgrp) {
        this.mgrp = mgrp;
    }

    /** {@inheritDoc} */
    @Override public void handle(String target, Request baseRequest, HttpServletRequest request,
        HttpServletResponse response) throws IOException {
        baseRequest.setHandled(true);

        response.addHeader("Access-Control-Allow-Origin", "*");
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("text/plain");

        PrintWriter writer = response.getWriter();

        mgrp.forEach(m -> {
            if (m instanceof HistogramMetric) {
                long[] bounds = ((HistogramMetric)m).bounds();
                long[] vals = ((HistogramMetric)m).value();

                String name = prepareName(m);

                for (int i = 0; i < bounds.length; i++) {
                    writer.print(name);
                    writer.print("{le=\"");
                    writer.print(bounds[i]);
                    writer.print("\"} ");
                    writer.print(vals[i]);
                    writer.print('\n');
                }

                writer.print(name);
                writer.print("{le=\"+Inf\"} ");
                writer.print(vals[vals.length - 1]);
                writer.print('\n');
            }
            else if (m instanceof ObjectMetric) {
            }
            else {
                writer.print(prepareName(m));
                writer.print(' ');

                if (m instanceof BooleanMetric)
                    writer.print(((BooleanMetric)m).value() ? '1' : '0');
                else
                    writer.print(m.getAsString());
                writer.print('\n');
            }
        });
    }

    @NotNull private String prepareName(Metric m) {
        return m.name()
            .replace('.', '_')
            .replace('-', '_');
    }
}
