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

package org.apache.ignite.viewer.config;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;
import org.apache.ignite.internal.util.typedef.F;
import org.apache.ignite.spi.metric.Metric;
import org.apache.ignite.spi.metric.ReadOnlyMetricManager;
import org.apache.ignite.spi.metric.ReadOnlyMetricRegistry;
import org.jetbrains.annotations.NotNull;

/**
 * Filterable metric registry group.
 */
public abstract class AbstractFilterGroup extends AbstractMetricsGroup {
    /** Groups. */
    Set<ReadOnlyMetricRegistry> group = new HashSet<>();

    /** {@inheritDoc} */
    @NotNull @Override public Iterator<Metric> iterator() {
        return F.flat(group);
    }

    /** {@inheritDoc} */
    @Override public void init(ReadOnlyMetricManager mmgr) {
        Consumer<ReadOnlyMetricRegistry> adder = mreg -> {
            if (included(mreg))
                group.add(mreg);
        };

        mmgr.addMetricRegistryCreationListener(adder);
        mmgr.addMetricRegistryRemoveListener(group::remove);

        for (ReadOnlyMetricRegistry mreg : mmgr)
            adder.accept(mreg);
    }

    /**
     * @return {@code True} if registry should be included in group.
     */
    abstract boolean included(ReadOnlyMetricRegistry mreg);
}
