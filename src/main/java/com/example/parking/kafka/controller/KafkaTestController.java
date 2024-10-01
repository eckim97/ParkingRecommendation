package com.example.parking.kafka.controller;

import com.example.parking.kafka.service.ParkingProducer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kafka")
public class KafkaTestController {

    private final ParkingProducer parkingProducer;

    public KafkaTestController(ParkingProducer parkingProducer) {
        this.parkingProducer = parkingProducer;
    }

    @PostMapping("/send-parking-data-to-redis")
    public String sendparkingDataToRedis() {
        long startTime = System.currentTimeMillis();
        int count = parkingProducer.sendParkingDataToRedis();
        long endTime = System.currentTimeMillis();
        return "Parking data sent to Redis. Total messages sent: " + count + ". Time taken: " + (endTime - startTime) + " ms";
    }

    @PostMapping("/send-parking-data-to-kafka")
    public String sendparkingDataToKafka() {
        long startTime = System.currentTimeMillis();
        int count = parkingProducer.sendParkingDataFromCsv();
        long endTime = System.currentTimeMillis();
        return "Parking data sent to Kafka. Total messages sent: " + count + ". Time taken: " + (endTime - startTime) + " ms";
    }
}
