package com.example.parking.park.controller;

import com.example.parking.kafka.service.ParkingProducer;
import com.example.parking.park.dto.ParkingDto;
import com.example.parking.park.service.ParkingSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ParkingController {

    private final ParkingProducer parkingProducer;
    private final ParkingSearchService parkingSearchService;

    @PostMapping("/parking/kafka/send")
    public String sendparkingDataToKafka() {
        int count = parkingProducer.sendParkingDataFromCsv();
        return "parking data sent to Kafka. Total messages sent: " + count;
    }

    @GetMapping("/parking/search")
    public List<ParkingDto> searchparking() {
        return parkingSearchService.searchParkingDtoList();
    }
}