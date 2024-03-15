package com.iaa.camelkafkademoproducer;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.opentelemetry.OpenTelemetryTracer;
import org.springframework.stereotype.Component;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;

@Component
public class CamelRouteBuilder extends RouteBuilder {
  @Override
  public void configure() throws Exception {

    Tracer tracer = GlobalOpenTelemetry.getTracer("camel-producer-tracer");
    OpenTelemetryTracer ott = new OpenTelemetryTracer();
    ott.setTracer(tracer);
    ott.init(this.getContext());

    // from("timer:foo").to("log:bar");
    from("kafka:pageviews?brokers=localhost:9092")
        .process(expression -> {
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