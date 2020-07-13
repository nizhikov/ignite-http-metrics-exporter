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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.ignite.internal.processors.metric.impl.HitRateMetric;
import org.apache.ignite.spi.metric.ObjectMetric;
import org.apache.ignite.viewer.config.MetricsGroup;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 * Metrics group handler.
 */
public class MetricsGroupHandler extends AbstractHandler {
    /** Metrics group. */
    private final MetricsGroup mgrp;

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
            if (m instanceof HitRateMetric ||
                m instanceof ObjectMetric)
                return;

            writer.print(m.name());
            writer.print('=');
            writer.print(m.getAsString());
            writer.print('\n');
        });
    }
}
