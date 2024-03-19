package com.iaa.camelkafkademoconsumer;

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
    from("kafka:viewedpages?brokers=localhost:9092")
        // .process(new TraceEnrichingProcessor(null))
        .process(expression -> {
          Tracer tracer = GlobalOpenTelemetry.getTracer("camel-consumer-tracer");
          Span mapperSpan = tracer.spanBuilder("consumer-mapper").startSpan();
          // Custom processing logic
          String body = expression.getIn().getBody(String.class);
          String modifiedBody = "Processed: " + body;
          expression.getIn().setBody(modifiedBody);
          mapperSpan.end();
        })
        .to("kafka:processedviews?brokers=localhost:9092")
        .to("log:processedviews");
  }
}
