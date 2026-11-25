package nl.itqaanconsulting.servicedesk.ticket;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.otel.bridge.OtelTracer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Guards the observability set-up across Spring Boot upgrades. In Boot 4 the tracing
 * auto-configuration moved to separate modules, so tracing can disappear without any error.
 */
@SpringBootTest(properties = "management.prometheus.metrics.export.enabled=true")
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ObservabilityConfigurationTest {

    @Autowired
    private Tracer tracer;

    @Autowired
    private MeterRegistry meterRegistry;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void tracesWithOpenTelemetry() {
        assertThat(tracer).isInstanceOf(OtelTracer.class);
    }

    @Test
    void exportsMetricsThroughPrometheusOnly() throws Exception {
        if (meterRegistry instanceof CompositeMeterRegistry composite) {
            assertThat(composite.getRegistries())
                    .extracting(registry -> registry.getClass().getSimpleName())
                    .doesNotContain("OtlpMeterRegistry");
        }
        mockMvc.perform(get("/actuator/prometheus"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("resilience4j_circuitbreaker_state")));
    }
}
