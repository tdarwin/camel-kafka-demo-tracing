package com.iaa.camelkafkademoconsumer;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.TraceFlags;
import io.opentelemetry.api.trace.TraceState;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;

public class TraceEnrichingProcessor implements Processor {

    private final Processor delegate;

    public TraceEnrichingProcessor(Processor delegate) {
        this.delegate = delegate;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        // Get the existing traceparent header from the Exchange
        String traceparent = exchange.getIn().getHeader("traceparent", String.class);
        if (traceparent != null && !traceparent.isEmpty()) {
            // Extract the traceId, parentSpanId and sampleFlag
            String[] parts = traceparent.split("-");
            String traceId = parts[1];
            String parentSpanId = parts[2];
            boolean isSampled = parts[3].equals("01");

            // Create the parent SpanContext
            SpanContext parentContext = SpanContext.create(
                traceId,
                parentSpanId,
                isSampled ? TraceFlags.getSampled() : TraceFlags.getDefault(),
                TraceState.getDefault()
            );

            // Attach the parent SpanContext to the current Context
            try (Scope scope = Context.current().with(Span.wrap(parentContext)).makeCurrent()) {
                // Now, the current Context has the parent SpanContext attached,
                // and any new spans created within this scope will use it as their parent
                
                // Pass control to the delegate processor
                delegate.process(exchange);
            }
        } else {
            // If no traceparent header is found, just delegate without modifying the Context
            delegate.process(exchange);
        }
    }
} 
