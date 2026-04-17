package kafka

import (
	"encoding/json"
	"log"

	"github.com/IBM/sarama"
	"github.com/Larry-Baltodano/go-analytics-service/internal/core/domain"
	"github.com/Larry-Baltodano/go-analytics-service/internal/core/ports"
)

type Consumer struct {
	consumer sarama.Consumer
	topic string
	events chan domain.TransactionEvent
	errors chan error
}

func NewConsumer(brokers []string, topic string) (ports.KafkaConsumer, error) {
	log.Printf("Connecting to kafka brokers: %v", brokers)

	config := sarama.NewConfig()
	config.Version = sarama.V3_0_0_0
	config.Consumer.Return.Errors = true

	consumer, err := sarama.NewConsumer(brokers, config)
	if err != nil {
		log.Printf("Error creating kafka consumer: %v", err)
		return nil, err
	}

	log.Printf("Connected to Kafka successfully")
	log.Printf("Subscribing to topic: %s", topic)

	return &Consumer{
		consumer: consumer,
		topic: topic,
		events: make(chan domain.TransactionEvent, 100),
		errors: make(chan error, 100),
	}, nil
}

func (c *Consumer) ConsumeEvents() (<-chan domain.TransactionEvent, <-chan error) {
	log.Printf("Getting partitions for topic: %s", c.topic)

	partitions, err := c.consumer.Partitions(c.topic)
	if err != nil {
		log.Printf("Error getting partitions: %v", err)
		c.errors <- err
		return c.events, c.errors
	}

	log.Printf("Found %d partitions", len(partitions))

	for _, partition := range partitions {
		log.Printf("Consumming partition: %d", partition)

		pc, err := c.consumer.ConsumePartition(c.topic, partition, sarama.OffsetNewest)
		if err != nil {
			log.Printf("Error consuming partition %d: %v", partition, err)
			c.errors <- err
			continue
		}

		go func(pc sarama.PartitionConsumer, partition int32)  {
			log.Printf("Started consumer for partition %d", partition)

			for msg := range pc.Messages() {
				log.Printf("-> Received message from partition %d, offset %d", partition, msg.Offset)
				log.Printf("Message value: %s", string(msg.Value))

				var event domain.TransactionEvent
				if err := json.Unmarshal(msg.Value, &event); err != nil {
					log.Printf("X - Error unmarshaling message: %v", err)
					continue
				}
				log.Printf("✓ - Event unmarshaled: %+v", event)
				c.events <- event
			}
			log.Printf("Stopped consumer for partition %d", partition)
		}(pc, partition)
	}

	return c.events, c.errors
}

func (c *Consumer) Close() error {
	log.Println("Closing kafka consumer")
	return c.consumer.Close()
}