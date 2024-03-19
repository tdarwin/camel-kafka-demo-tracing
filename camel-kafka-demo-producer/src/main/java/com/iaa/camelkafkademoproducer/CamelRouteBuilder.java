package com.iaa.camelkafkademoproducer;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;

@Component
public class CamelRouteBuilder extends RouteBuilder {
  private final OpenTelemetry openTelemetry;

  public CamelRouteBuilder(@Autowired OpenTelemetry openTelemetry) {
    this.openTelemetry = openTelemetry;
  }

  @Override
  public void configure() throws Exception {
    // from("timer:foo").to("log:bar");
    from("kafka:pageviews?brokers=localhost:9092")
        .process(expression -> {
          Tracer tracer = openTelemetry.getTracer("camel-producer-tracer");
          Span mapperSpan = tracer.spanBuilder("producer-mapper").startSpan();
          // Custom processing logic
          String body = expression.getIn().getBody(String.class);
          String modifiedBody = "Processed: " + body;
          expression.getIn().setBody(modifiedBody);
          mapperSpan.end();
        })
        .to("kafka:viewedpages?brokers=localhost:9092")
        .to("log:partone-done");
  }
}