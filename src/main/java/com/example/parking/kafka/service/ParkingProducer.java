package com.example.parking.kafka.service;

import com.example.parking.direction.entity.Direction;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParkingProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendParkingData(Direction direction) {
        try {
            String parkingData = objectMapper.writeValueAsString(direction);
            kafkaTemplate.send("parking-topic", parkingData);
            log.info("Parking data sent to Kafka: {}", parkingData);
        } catch (Exception e) {
            log.error("Error sending parking data to Kafka", e);
        }
    }
}