package com.iaa.camelkafkademoproducer;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;

@Component
public class CamelRouteBuilder extends RouteBuilder {
  @Override
  public void configure() throws Exception {
    // from("timer:foo").to("log:bar");
    from("kafka:pageviews?brokers=localhost:9092")
        .process(processor -> {
          Tracer tracer = GlobalOpenTelemetry.getTracer("camel-consumer-tracer");
          Span span = tracer.spanBuilder("producer-mapper").startSpan();
          // Custom processing logic
          String body = processor.getIn().getBody(String.class);
          String modifiedBody = "Processed: " + body;
          processor.getIn().setBody(modifiedBody);
          span.end();
        })
        .to("kafka:viewedpages?brokers=localhost:9092")
        .to("log:partone-done");
  }
}
