#/bin/bash
# This script is used to create a topic in Kafka for testing purposes.
docker exec -it ${CONTAINER_NAME_KAFKA} kafka-topics.sh --bootstrap-server ${HOST_PORT_KAFKA} --create --topic ${TEST_TOPIC_NAME} --partitions 1 --replication-factor 1
echo "topic $TEST_TOPIC_NAME was create"