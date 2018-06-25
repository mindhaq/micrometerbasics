package com.mindhaq.micrometerbasics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.search.Search;
import org.slf4j.Logger;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.TimeUnit;

import static io.micrometer.core.instrument.Metrics.globalRegistry;
import static org.slf4j.LoggerFactory.getLogger;

@SpringBootApplication
public class MicrometerbasicsApplication {

	private final Logger logger = getLogger(MicrometerbasicsApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(MicrometerbasicsApplication.class, args);
	}

	@Bean
	public ApplicationRunner mainRunner(MeterRegistry meterRegistry, Counter counter) {
		Metrics.addRegistry(meterRegistry);		// without that, counter and counter2 will not point to the same metric!

		Counter counter2 = Metrics.counter("theCounter", Tags.of("theTag", "theTagValue"));
		Counter counter3 = Metrics.counter("theCounter", Tags.of("theTag", "theOtherTagValue"));
		Timer timer = Metrics.timer("theTimer");

		return args -> {
			logger.info("Starting!");
			timer.record(10, TimeUnit.SECONDS);

			counter.increment();
			counter2.increment();
			counter3.increment(5);

			logger.info("Counter: {}", counter.count());	// == 2
			logger.info("Registry: {}", meterRegistry);

			globalRegistry.getMeters().forEach(meter -> logger.info("Measured {}: {}", meter.getId(), meter.measure()));

			Search search = globalRegistry.find("theCounter");
			logger.info("Found counters: {}", search.counter().count());

			int sumOfAllCounters = search
					.counters()
					.stream()
					.map(Counter::count)
					.mapToInt(Double::intValue)
					.sum();
			logger.info("All counters counted {}", sumOfAllCounters);

			logger.info("THE END");
		};
	}
}
