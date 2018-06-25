package com.mindhaq.micrometerbasics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MicrometerConfiguration {

    @Bean
    public MeterRegistry meterRegistry() {
        return new SimpleMeterRegistry();
    }

    @Bean
    public Counter counter(MeterRegistry meterRegistry) {
        return Counter
                .builder("theCounter")
                .tag("theTag", "theTagValue")
                .register(meterRegistry);
    }

}
