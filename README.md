This module contains `org.apache.ignite.viewer.IgniteHttpMetricsExporterSpi` Spi to export some gorups of the Apache Ignite metrics.
Example of XML configuration:
```xml
    <bean id="ignite.cfg" class="org.apache.ignite.configuration.IgniteConfiguration">
        <property name="metricExporterSpi">
            <bean class="org.apache.ignite.viewer.IgniteHttpMetricsExporterSpi">
                <property name="groups">
                    <list>
                        <bean class="org.apache.ignite.viewer.config.RegExpGroup">
                            <property name="context" value="/all"/>
                            <property name="regExp" value=".*"/>
                        </bean>
                        <bean class="org.apache.ignite.viewer.config.NamesListGroup">
                            <property name="context" value="/sys"/>
                            <property name="names">
                                <list>
                                    <value>sys</value>
                                </list>
                            </property>
                        </bean>
                        <bean class="org.apache.ignite.viewer.config.RegExpGroup">
                            <property name="context" value="/caches"/>
                            <property name="regExp" value="cache\..*"/>
                        </bean>
                        <bean class="org.apache.ignite.viewer.config.NamesListGroup">
                            <property name="context" value="/my-cache"/>
                            <property name="names">
                                <list>
                                    <value>cache.my-cache</value>
                                    <value>cacheGroups.my-cache</value>
                                </list>
                            </property>
                        </bean>
                    </list>
                </property>
            </bean>
        </property>
    </bean>
```