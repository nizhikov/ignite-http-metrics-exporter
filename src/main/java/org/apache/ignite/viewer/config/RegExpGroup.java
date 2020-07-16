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

import java.util.regex.Pattern;
import org.apache.ignite.spi.metric.ReadOnlyMetricRegistry;

/**
 * Regular expression group.
 */
public class RegExpGroup extends AbstractFilterGroup {
    /** Expression to filter registries. */
    private Pattern includePattern;

    /** Expression to filter registries. */
    private Pattern excludePattern;

    /** {@inheritDoc} */
    @Override boolean included(ReadOnlyMetricRegistry mreg) {
        return includePattern.matcher(mreg.name()).matches() &&
            (excludePattern == null || !excludePattern.matcher(mreg.name()).matches());
    }

    /**
     * Sets regular expression string to filter registries.
     * @param regExp Regular expression.
     */
    public void setRegExp(String regExp) {
        this.includePattern = Pattern.compile(regExp);
    }

    /**
     * Sets regular expression string to exclude registries.
     * @param regExp Regular expression.
     */
    public void setExcludeRegExp(String regExp) {
        this.excludePattern = Pattern.compile(regExp);
    }
}
