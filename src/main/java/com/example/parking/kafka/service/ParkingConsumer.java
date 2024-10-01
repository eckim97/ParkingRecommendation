package com.example.parking.kafka.service;

import com.example.parking.park.entity.Parking;
import com.example.parking.park.repository.ParkingRepository;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParkingConsumer {

    private static final Logger log = LoggerFactory.getLogger(ParkingConsumer.class);
    private final ParkingRepository parkingRepository;

    @KafkaListener(topics = "parking-data", groupId = "parking-group")
    public void consume(ConsumerRecord<String, String> record) {
        try {
            log.info("Received message: key = {}, value = {}", record.key(), record.value());
            String[] values = record.value().split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

            if (values.length < 5) {
                log.error("Invalid message format: {}", record.value());
                return;
            }

            Parking parking = Parking.builder()
                    .parkingName(values[1].replace("\"", "").trim())
                    .parkingAddress(values[2].replace("\"", "").trim())
                    .latitude(Double.parseDouble(values[3].trim()))
                    .longitude(Double.parseDouble(values[4].trim()))
                    .build();

            parkingRepository.save(parking);
            log.info("Parking saved to database: {}", parking.getParkingName());

        } catch (Exception e) {
            log.error("Error processing message: {}", record.value(), e);
        }
    }
}