package com.iaa.camelkafkademoproducer;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.opentelemetry.OpenTelemetrySpanAdapter;
import org.apache.camel.tracing.ActiveSpanManager;
import org.springframework.stereotype.Component;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.instrumentation.annotations.WithSpan;


@Component
public class CamelRouteBuilder extends RouteBuilder {
  @Override
  public void configure() throws Exception {
    from("kafka:pageviews?brokers=localhost:9092")
      .process(exchange -> {
        // We're not supposed to use ActiveSpanManager directly, but
        // camel-opentelemetry doesn't seem to provide a way to get the current span.
        OpenTelemetrySpanAdapter camelSpan = (OpenTelemetrySpanAdapter) ActiveSpanManager.getSpan(exchange);

        try (AutoCloseable scope = camelSpan.makeCurrent()) {
          // Custom processing logic
          modifyBody(exchange);
        }
      })
      .to("kafka:viewedpages?brokers=localhost:9092")
      .to("log:partone-done");
  }

  @WithSpan("producer-mapper")
  private void modifyBody(Exchange exchange) {
    Span span = Span.current();
    String body = exchange.getIn().getBody(String.class);
    span.setAttribute("app.body.original", body);
    String modifiedBody = "Processed: " + body;
		span.setAttribute("app.body.modified", modifiedBody);
    exchange.getIn().setBody(modifiedBody);
  }
}
