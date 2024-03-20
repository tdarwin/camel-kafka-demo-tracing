package com.iaa.camelkafkademoconsumer;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.tracing.ActiveSpanManager;
import org.springframework.stereotype.Component;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.instrumentation.annotations.WithSpan;

@Component
public class CamelRouteBuilder extends RouteBuilder {

  @Override
  public void configure() throws Exception {
    from("kafka:viewedpages?brokers=localhost:9092")
      .process(exchange -> {
        try (AutoCloseable scope = ActiveSpanManager.getSpan(exchange).makeCurrent()) {
          modifyBody(exchange);
        }
      })
      .to("kafka:processedviews?brokers=localhost:9092")
      .to("log:processedviews");
  }

  @WithSpan("consumer-mapper")
  private void modifyBody(Exchange exchange) {
    Span span = Span.current();
    String body = exchange.getIn().getBody(String.class);
    span.setAttribute("app.body.original", body);
    String modifiedBody = "Processed: " + body;
		span.setAttribute("app.body.modified", modifiedBody);
    exchange.getIn().setBody(modifiedBody);
  }
}
