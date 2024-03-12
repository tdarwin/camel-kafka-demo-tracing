# Camel Kafka Demo

This has two services that are basically identical applications that will sort of daisy-chain a kafka message sent to the `pageviews` topic through the producer service, and then to a `viewedpages` topic, which is then picked up by the consumer service and pushed to the `processedviews` topic.

## Requirements

- kcat utility (for producing messages to kick off the process)
  - install it with `brew install kcat` on a Mac
- Docker (for running the kafka containers in docker-compose.yml)
- A honeycomb API key for sending trace data to Honeycomb
- Java >= OpenJDK version 17.0
- Gradle
  - install it with `brew install gradle` on a Mac

## Setup

### Start Kafka

- `docker-compose up -d`

### Create Topics

- `docker exec -it broker /bin/sh`

Inside the container, run the following commands:

```shell
kafka-topics --bootstrap-server localhost:9092 --create --topic pageviews
kafka-topics --bootstrap-server localhost:9092 --create --topic viewedpages
kafka-topics --bootstrap-server localhost:9092 --create --topic processedviews
```

note: the `pageviews` topic might already exist... I don't know

## Get apps running

You'll want separate terminals, one in the `camel-kafka-demo-producer` directory, and another in the `camel-kafka-demo-consumer` directory.

1. Export the HONEYCOMB_API_KEY environment variable with your Honeycomb API key

```shell
export HONEYCOMB_API_KEY="hcaik_01hpsbj0b3xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
```

2.Run the run.sh executable

```shell
./run.sh
```

## Making Things happen

In another shell window, we can use `kcat` to send messages to the `pageviews` topic:

```shell
echo "test-001" | kcat -b localhost:9092 -t pageviews -p 0
echo "test-002" | kcat -b localhost:9092 -t pageviews -p 0
echo "test-003" | kcat -b localhost:9092 -t pageviews -p 0
echo "test-004" | kcat -b localhost:9092 -t pageviews -p 0
echo "test-005" | kcat -b localhost:9092 -t pageviews -p 0
```

Assuming you've properly put your Honeycomb API key in the enivoronment variable, you should now see traces in your Honeycomb environment under the two different service names.
