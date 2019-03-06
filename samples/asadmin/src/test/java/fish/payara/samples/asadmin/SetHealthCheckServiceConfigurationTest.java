/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2019 Payara Foundation and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://github.com/payara/Payara/blob/master/LICENSE.txt
 * See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * The Payara Foundation designates this particular file as subject to the "Classpath"
 * exception as provided by the Payara Foundation in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package fish.payara.samples.asadmin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.util.concurrent.TimeUnit;

import org.glassfish.embeddable.CommandResult;
import org.junit.Before;
import org.junit.Test;

import fish.payara.nucleus.healthcheck.HealthCheckConstants;
import fish.payara.nucleus.healthcheck.HealthCheckService;
import fish.payara.nucleus.healthcheck.configuration.Checker;
import fish.payara.nucleus.healthcheck.configuration.GarbageCollectorChecker;
import fish.payara.nucleus.healthcheck.configuration.HealthCheckServiceConfiguration;
import fish.payara.nucleus.healthcheck.configuration.HoggingThreadsChecker;
import fish.payara.nucleus.healthcheck.configuration.StuckThreadsChecker;
import fish.payara.nucleus.healthcheck.preliminary.BaseHealthCheck;
import fish.payara.nucleus.healthcheck.preliminary.GarbageCollectorHealthCheck;
import fish.payara.nucleus.healthcheck.preliminary.HoggingThreadsHealthCheck;
import fish.payara.nucleus.healthcheck.stuck.StuckThreadsHealthCheck;

/**
 * Verifies the correctness of the {@code SetHealthCheckServiceConfiguration} command.
 */
public class SetHealthCheckServiceConfigurationTest extends AsAdminTest {

    private HealthCheckServiceConfiguration config;
    private HealthCheckService service;
    private GarbageCollectorHealthCheck garbageCollection;
    private HoggingThreadsHealthCheck hoggingThreads;
    private StuckThreadsHealthCheck stuckThreads;

    @Before
    public void setUp() {
        config = getConfigExtensionByType("server-config", HealthCheckServiceConfiguration.class);
        service = getService(HealthCheckService.class);
        garbageCollection = getService(GarbageCollectorHealthCheck.class);
        hoggingThreads = getService(HoggingThreadsHealthCheck.class);
        stuckThreads = getService(StuckThreadsHealthCheck.class);
    }

    @Test
    public void setHealthCheckServiceConfiguration_EnabledIsMandatory() {
        assertMissingParameter("enabled", asadmin("set-healthcheck-service-configuration", 
                "--service", "gc"));
    }

    @Test
    public void setHealthCheckServiceConfiguration_ServiceIsMandatory() {
        assertMissingParameter("serviceName", asadmin("set-healthcheck-service-configuration", 
                "--enabled", "true"));
    }

    @Test
    public void setHealthCheckServiceConfiguration_ShortNames() {
        String[] shortNames = { "cp", "cu", "gc", "hmu", "ht", "mmu", "st", "mh" };
        setHealthCheckServiceConfigurationNames(shortNames);
    }

    @Test
    public void setHealthCheckServiceConfiguration_FullNames() {
        String[] fullNames = { "CONNECTION_POOL", "CPU_USAGE", "GARBAGE_COLLECTOR", "HEAP_MEMORY_USAGE",
                "HOGGING_THREADS", "MACHINE_MEMORY_USAGE", "STUCK_THREAD", "MP_HEALTH" };
        setHealthCheckServiceConfigurationNames(fullNames);
    }

    private void setHealthCheckServiceConfigurationNames(String[] serviceNames) {
        for (String serviceName : serviceNames) {
            CommandResult result = asadmin("set-healthcheck-service-configuration", 
                    "--service", serviceName, 
                    "--enabled", "true");
            assertSuccess(result); // just check the name got accepted
        }
    }

    @Test
    public void setHealthCheckServiceConfiguration_Enabled() {
        CommandResult result = asadmin("set-healthcheck-service-configuration", 
                "--service", "gc", 
                "--enabled", "true");
        assertSuccess(result);
        Checker gcConfig = config.getCheckerByType(garbageCollection.getCheckerType());
        assertTrue(gcConfig.getEnabled());
        result = asadmin("set-healthcheck-service-configuration", 
                "--service", "gc", 
                "--enabled", "false");
        assertFalse(gcConfig.getEnabled());
    }

    @Test
    public void setHealthCheckServiceConfiguration_EnabledDynamic() {
        ensureHealthChecksAreEnabled();
        CommandResult result = asadmin("set-healthcheck-service-configuration", 
                "--service", "gc", 
                "--enabled", "true",
                "--dynamic", "true");
        assertSuccess(result);
        Checker gcConfig = config.getCheckerByType(garbageCollection.getCheckerType());
        BaseHealthCheck<?,?> check = service.getCheck(gcConfig.getName());
        assertNotNull(check);
        assertTrue(check.getOptions().isEnabled());
        result = asadmin("set-healthcheck-service-configuration", 
                "--service", "gc", 
                "--enabled", "false",
                "--dynamic", "true");
        assertFalse(check.getOptions().isEnabled());
    }

    @Test
    public void setHealthCheckServiceConfiguration_Time() {
        CommandResult result = asadmin("set-healthcheck-service-configuration", 
                "--service", "gc",
                "--enabled", "true",
                "--time", "33");
        assertSuccess(result);
        Checker gcConfig = config.getCheckerByType(garbageCollection.getCheckerType());
        assertEquals(33L, Long.parseLong(gcConfig.getTime()));
        assertNotEquals(33L, service.getCheck(gcConfig.getName()).getOptions().getTime());
    }

    @Test
    public void setHealthCheckServiceConfiguration_TimeDynamic() {
        ensureHealthChecksAreEnabled();
        CommandResult result = asadmin("set-healthcheck-service-configuration", 
                "--service", "gc",
                "--enabled", "true",
                "--time", "42",
                "--dynamic", "true");
        assertSuccess(result);
        Checker gcConfig = config.getCheckerByType(garbageCollection.getCheckerType());
        assertEquals(42L, Long.parseLong(gcConfig.getTime()));
        assertEquals(42L, service.getCheck(gcConfig.getName()).getOptions().getTime());
    }

    @Test
    public void setHealthCheckServiceConfiguration_TimeUnitUnknownName() {
        CommandResult result = asadmin("set-healthcheck-service-configuration", 
                "--service", "gc",
                "--enabled", "true",
                "--time-unit", "YEARS");
        assertUnacceptableParameter("time-unit", result);
        assertContains("DAYS,HOURS,MICROSECONDS,MILLISECONDS,MINUTES,NANOSECONDS,SECONDS", result.getOutput());
    }

    @Test
    public void setHealthCheckServiceConfiguration_TimeUnit() {
        CommandResult result = asadmin("set-healthcheck-service-configuration", 
                "--service", "gc",
                "--enabled", "true",
                "--time-unit", TimeUnit.DAYS.name());
        assertSuccess(result);
        Checker gcConfig = config.getCheckerByType(garbageCollection.getCheckerType());
        assertEquals(TimeUnit.DAYS.name(), gcConfig.getUnit());
        assertNotEquals(TimeUnit.DAYS, service.getCheck(gcConfig.getName()).getOptions().getUnit());
    }

    @Test
    public void setHealthCheckServiceConfiguration_TimeUnitDynamic() {
        ensureHealthChecksAreEnabled();
        CommandResult result = asadmin("set-healthcheck-service-configuration", 
                "--service", "gc",
                "--enabled", "true",
                "--time-unit", TimeUnit.HOURS.name(),
                "--dynamic", "true"
                );
        assertSuccess(result);
        Checker gcConfig = config.getCheckerByType(garbageCollection.getCheckerType());
        assertEquals(TimeUnit.HOURS.name(), gcConfig.getUnit());
        assertEquals(TimeUnit.HOURS, service.getCheck(gcConfig.getName()).getOptions().getUnit());
    }

    @Test
    public void setHealthCheckServiceConfiguration_HogginThreadsThresholdBelowMinumum() {
        CommandResult result = asadmin("set-healthcheck-service-configuration", 
                "--service", "ht",
                "--enabled", "true",
                "--hogging-threads-threshold", "-1");
        assertUnacceptableParameter("hogginThreadsThreshold", result);
        assertContains("Hogging threads threshold is a percentage so must be greater than zero", result.getOutput());
    }

    @Test
    public void setHealthCheckServiceConfiguration_HogginThreadsThresholdAboveMaximum() {
        CommandResult result = asadmin("set-healthcheck-service-configuration", 
                "--service", "ht",
                "--enabled", "true",
                "--hogging-threads-threshold", "101");
        assertUnacceptableParameter("hogginThreadsThreshold", result);
        assertContains("Hogging threads threshold is a percentage so must be less than 100", result.getOutput());
    }

    @Test
    public void setHealthCheckServiceConfiguration_HogginThreadsThreshold() {
        CommandResult result = asadmin("set-healthcheck-service-configuration", 
                "--service", "ht",
                "--enabled", "true",
                "--hogging-threads-threshold", "33");
        assertSuccess(result);
        HoggingThreadsChecker htConfig = config.getCheckerByType(hoggingThreads.getCheckerType());
        assertEquals(33, Integer.parseInt(htConfig.getThresholdPercentage()));
        HoggingThreadsHealthCheck checkTask = (HoggingThreadsHealthCheck) service.getCheck(htConfig.getName());
        assertNotEquals(Long.valueOf(33), checkTask.getOptions().getThresholdPercentage());
    }

    @Test
    public void setHealthCheckServiceConfiguration_HogginThreadsThresholdDynamic() {
        ensureHealthChecksAreEnabled();
        CommandResult result = asadmin("set-healthcheck-service-configuration", 
                "--service", "ht",
                "--enabled", "true",
                "--hogging-threads-threshold", "42",
                "--dynamic", "true");
        assertSuccess(result);
        HoggingThreadsChecker htConfig = config.getCheckerByType(hoggingThreads.getCheckerType());
        assertEquals(42, Integer.parseInt(htConfig.getThresholdPercentage()));
        HoggingThreadsHealthCheck checkTask = (HoggingThreadsHealthCheck) service.getCheck(htConfig.getName());
        assertEquals(Long.valueOf(42), checkTask.getOptions().getThresholdPercentage());
    }

    @Test
    public void setHealthCheckServiceConfiguration_HogginThreadsRetryCountBelowMinimum() {
        CommandResult result = asadmin("set-healthcheck-service-configuration", 
                "--service", "ht",
                "--enabled", "true",
                "--hogging-threads-retry-count", "0");
        assertUnacceptableParameter("hogginThreadsRetryCount", result);
        assertContains("Hogging threads retry count must be 1 or more", result.getOutput());
    }

    @Test
    public void setHealthCheckServiceConfiguration_HogginThreadsRetryCount() {
        CommandResult result = asadmin("set-healthcheck-service-configuration", 
                "--service", "ht",
                "--enabled", "true",
                "--hogging-threads-retry-count", "13");
        assertSuccess(result);
        HoggingThreadsChecker htConfig = config.getCheckerByType(hoggingThreads.getCheckerType());
        assertEquals(13, Integer.parseInt(htConfig.getRetryCount()));
        HoggingThreadsHealthCheck checkTask = (HoggingThreadsHealthCheck) service.getCheck(htConfig.getName());
        assertNotEquals(13, checkTask.getOptions().getRetryCount());
    }

    @Test
    public void setHealthCheckServiceConfiguration_HogginThreadsRetryCountDynamic() {
        ensureHealthChecksAreEnabled();
        CommandResult result = asadmin("set-healthcheck-service-configuration", 
                "--service", "ht",
                "--enabled", "true",
                "--hogging-threads-retry-count", "24",
                "--dynamic", "true");
        assertSuccess(result);
        HoggingThreadsChecker htConfig = config.getCheckerByType(hoggingThreads.getCheckerType());
        assertEquals(24, Integer.parseInt(htConfig.getRetryCount()));
        HoggingThreadsHealthCheck checkTask = (HoggingThreadsHealthCheck) service.getCheck(htConfig.getName());
        assertEquals(24, checkTask.getOptions().getRetryCount());
    }

    @Test
    public void setHealthCheckServiceConfiguration_StuckThreadsThresholdBelowMinumum() {
        CommandResult result = asadmin("set-healthcheck-service-configuration", 
                "--service", "st",
                "--enabled", "true",
                "--stuck-threads-threshold", "0");
        assertUnacceptableParameter("stuckThreadsThreshold", result);
    }

    @Test
    public void setHealthCheckServiceConfiguration_StuckThreadsThreshold() {
        CommandResult result = asadmin("set-healthcheck-service-configuration", 
                "--service", "st",
                "--enabled", "true",
                "--stuck-threads-threshold", "13");
        assertSuccess(result);
        StuckThreadsChecker stConfig = config.getCheckerByType(stuckThreads.getCheckerType());
        assertEquals(13, Integer.parseInt(stConfig.getThreshold()));
        StuckThreadsHealthCheck activeService = (StuckThreadsHealthCheck) service.getCheck(stConfig.getName());
        if (activeService != null) { 
            assertNotEquals(Long.valueOf(13), activeService.getOptions().getTimeStuck());
        }
    }

    @Test
    public void setHealthCheckServiceConfiguration_StuckThreadsThresholdDynamic() {
        ensureHealthChecksAreEnabled();
        CommandResult result = asadmin("set-healthcheck-service-configuration", 
                "--service", "st",
                "--enabled", "true",
                "--stuck-threads-threshold", "17",
                "--dynamic", "true");
        assertSuccess(result);
        StuckThreadsChecker stConfig = config.getCheckerByType(stuckThreads.getCheckerType());
        assertEquals(17, Integer.parseInt(stConfig.getThreshold()));
        StuckThreadsHealthCheck activeService = (StuckThreadsHealthCheck) service.getCheck(stConfig.getName());
        assertEquals(Long.valueOf(17), activeService.getOptions().getTimeStuck());
    }

    @Test
    public void setHealthCheckServiceConfiguration_StuckThreadsThresholdUnitUnknownValue() {
        CommandResult result = asadmin("set-healthcheck-service-configuration", 
                "--service", "st",
                "--enabled", "true",
                "--stuck-threads-threshold-unit", "YEARS");
        assertUnacceptableParameter("stuck-threads-threshold-unit", result);
    }

    @Test
    public void setHealthCheckServiceConfiguration_StuckThreadsThresholdUnit() {
        CommandResult result = asadmin("set-healthcheck-service-configuration", 
                "--service", "st",
                "--enabled", "true",
                "--stuck-threads-threshold-unit", TimeUnit.DAYS.name());
        assertSuccess(result);
        StuckThreadsChecker stConfig = config.getCheckerByType(stuckThreads.getCheckerType());
        assertEquals(TimeUnit.DAYS.name(), stConfig.getThresholdTimeUnit());
        StuckThreadsHealthCheck activeService = (StuckThreadsHealthCheck) service.getCheck(stConfig.getName());
        if (activeService != null) { 
            assertNotEquals(TimeUnit.DAYS, activeService.getOptions().getUnitStuck());
        }
    }

    @Test
    public void setHealthCheckServiceConfiguration_StuckThreadsThresholdUnitDynamic() {
        ensureHealthChecksAreEnabled();
        CommandResult result = asadmin("set-healthcheck-service-configuration", 
                "--service", "st",
                "--enabled", "true",
                "--stuck-threads-threshold-unit", TimeUnit.HOURS.name(),
                "--dynamic", "true");
        assertSuccess(result);
        StuckThreadsChecker stConfig = config.getCheckerByType(stuckThreads.getCheckerType());
        assertEquals(TimeUnit.HOURS.name(), stConfig.getThresholdTimeUnit());
        StuckThreadsHealthCheck activeService = (StuckThreadsHealthCheck) service.getCheck(stConfig.getName());
        assertEquals(TimeUnit.HOURS, activeService.getOptions().getUnitStuck());
    }

    @Test
    public void setHealthCheckServiceConfiguration_ThresholdCriticalBelowMinimum() {
        CommandResult result = asadmin("set-healthcheck-service-configuration", 
                "--service", "gc",
                "--enabled", "true",
                "--threshold-critical", "-1");
        assertUnacceptableParameter("thresholdCritical", result);
        assertContains("Critical threshold is a percentage so must be greater than zero", result.getOutput());
    }

    @Test
    public void setHealthCheckServiceConfiguration_ThresholdCriticalAboveMaximum() {
        CommandResult result = asadmin("set-healthcheck-service-configuration", 
                "--service", "gc",
                "--enabled", "true",
                "--threshold-critical", "101");
        assertUnacceptableParameter("thresholdCritical", result);
        assertContains("Critical threshold is a percentage so must be less than 100", result.getOutput());
    }

    @Test
    public void setHealthCheckServiceConfiguration_ThresholdCritical() {
        CommandResult result = asadmin("set-healthcheck-service-configuration", 
                "--service", "gc",
                "--enabled", "true",
                "--threshold-critical", "99");
        assertSuccess(result);
        GarbageCollectorChecker gcConfig = config.getCheckerByType(garbageCollection.getCheckerType());
        assertEquals(99, Integer.parseInt(gcConfig.getProperty(HealthCheckConstants.THRESHOLD_CRITICAL).getValue()));
        GarbageCollectorHealthCheck activeService = (GarbageCollectorHealthCheck) service.getCheck(gcConfig.getName());
        assertNotEquals(99, activeService.getOptions().getThresholdCritical());
    }

    @Test
    public void setHealthCheckServiceConfiguration_ThresholdCriticalDynamic() {
        ensureHealthChecksAreEnabled();
        CommandResult result = asadmin("set-healthcheck-service-configuration", 
                "--service", "gc",
                "--enabled", "true",
                "--threshold-critical", "88",
                "--dynamic", "true");
        assertSuccess(result);
        GarbageCollectorChecker gcConfig = config.getCheckerByType(garbageCollection.getCheckerType());
        assertEquals(88, Integer.parseInt(gcConfig.getProperty(HealthCheckConstants.THRESHOLD_CRITICAL).getValue()));
        GarbageCollectorHealthCheck activeService = (GarbageCollectorHealthCheck) service.getCheck(gcConfig.getName());
        assertEquals(88, activeService.getOptions().getThresholdCritical());
    }

    @Test
    public void setHealthCheckServiceConfiguration_ThresholdWarningBelowMinimum() {
        CommandResult result = asadmin("set-healthcheck-service-configuration", 
                "--service", "gc",
                "--enabled", "true",
                "--threshold-warning", "-1");
        assertUnacceptableParameter("thresholdWarning", result);
        assertContains("Warning threshold is a percentage so must be greater than zero", result.getOutput());
    }

    @Test
    public void setHealthCheckServiceConfiguration_ThresholdWarningAboveMaximum() {
        CommandResult result = asadmin("set-healthcheck-service-configuration", 
                "--service", "gc",
                "--enabled", "true",
                "--threshold-warning", "101");
        assertUnacceptableParameter("thresholdWarning", result);
        assertContains("Wanring threshold is a percentage so must be less than 100", result.getOutput());
    }

    @Test
    public void setHealthCheckServiceConfiguration_ThresholdWarning() {
        CommandResult result = asadmin("set-healthcheck-service-configuration", 
                "--service", "gc",
                "--enabled", "true",
                "--threshold-warning", "99");
        assertSuccess(result);
        GarbageCollectorChecker gcConfig = config.getCheckerByType(garbageCollection.getCheckerType());
        assertEquals(99, Integer.parseInt(gcConfig.getProperty(HealthCheckConstants.THRESHOLD_WARNING).getValue()));
        GarbageCollectorHealthCheck activeService = (GarbageCollectorHealthCheck) service.getCheck(gcConfig.getName());
        assertNotEquals(99, activeService.getOptions().getThresholdWarning());
    }

    @Test
    public void setHealthCheckServiceConfiguration_ThresholdWarningDynamic() {
        ensureHealthChecksAreEnabled();
        CommandResult result = asadmin("set-healthcheck-service-configuration", 
                "--service", "gc",
                "--enabled", "true",
                "--threshold-warning", "88",
                "--dynamic", "true");
        assertSuccess(result);
        GarbageCollectorChecker gcConfig = config.getCheckerByType(garbageCollection.getCheckerType());
        assertEquals(88, Integer.parseInt(gcConfig.getProperty(HealthCheckConstants.THRESHOLD_WARNING).getValue()));
        GarbageCollectorHealthCheck activeService = (GarbageCollectorHealthCheck) service.getCheck(gcConfig.getName());
        assertEquals(88, activeService.getOptions().getThresholdWarning());
    }

    @Test
    public void setHealthCheckServiceConfiguration_ThresholdGoodBelowMinimum() {
        CommandResult result = asadmin("set-healthcheck-service-configuration", 
                "--service", "gc",
                "--enabled", "true",
                "--threshold-good", "-1");
        assertUnacceptableParameter("thresholdGood", result);
        assertContains("Good threshold is a percentage so must be greater than zero", result.getOutput());
    }

    @Test
    public void setHealthCheckServiceConfiguration_ThresholdGoodAboveMaximum() {
        CommandResult result = asadmin("set-healthcheck-service-configuration", 
                "--service", "gc",
                "--enabled", "true",
                "--threshold-good", "101");
        assertUnacceptableParameter("thresholdGood", result);
        assertContains("Good threshold is a percentage so must be less than 100", result.getOutput());
    }

    @Test
    public void setHealthCheckServiceConfiguration_ThresholdGood() {
        CommandResult result = asadmin("set-healthcheck-service-configuration", 
                "--service", "gc",
                "--enabled", "true",
                "--threshold-good", "33");
        assertSuccess(result);
        GarbageCollectorChecker gcConfig = config.getCheckerByType(garbageCollection.getCheckerType());
        assertEquals(33, Integer.parseInt(gcConfig.getProperty(HealthCheckConstants.THRESHOLD_GOOD).getValue()));
        GarbageCollectorHealthCheck activeService = (GarbageCollectorHealthCheck) service.getCheck(gcConfig.getName());
        assertNotEquals(33, activeService.getOptions().getThresholdGood());
    }

    @Test
    public void setHealthCheckServiceConfiguration_ThresholdGoodDynamic() {
        ensureHealthChecksAreEnabled();
        CommandResult result = asadmin("set-healthcheck-service-configuration", 
                "--service", "gc",
                "--enabled", "true",
                "--threshold-good", "22",
                "--dynamic", "true");
        assertSuccess(result);
        GarbageCollectorChecker gcConfig = config.getCheckerByType(garbageCollection.getCheckerType());
        assertEquals(22, Integer.parseInt(gcConfig.getProperty(HealthCheckConstants.THRESHOLD_GOOD).getValue()));
        GarbageCollectorHealthCheck activeService = (GarbageCollectorHealthCheck) service.getCheck(gcConfig.getName());
        assertEquals(22, activeService.getOptions().getThresholdGood());
    }

    /**
     * Dynamic changes only take effect when the health check service is enabled so we make sure it is.
     */
    private void ensureHealthChecksAreEnabled() {
        if (service.isEnabled()) {
            return; // already enabled, fine
        }
        assertSuccess(asadmin("set-healthcheck-configuration", 
                "--enabled", "true", 
                "--dynamic", "true"));
    }

}
