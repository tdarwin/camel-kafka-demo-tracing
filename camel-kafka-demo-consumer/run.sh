#!/bin/bash

export OTEL_TRACES_EXPORTER="otlp"
export OTEL_METRICS_EXPORTER="none"
export OTEL_EXPORTER_OTLP_ENDPOINT="https://api.honeycomb.io"
export OTEL_EXPORTER_OTLP_HEADERS="x-honeycomb-team=$HONEYCOMB_API_KEY"
export OTEL_SERVICE_NAME="camel-kafka-demo-consumer"

gradle bootJar

java -javaagent:../lib/honeycomb-opentelemetry-javaagent.jar -jar build/libs/camel-kafka-demo-consumer-0.0.1-SNAPSHOT.jar
