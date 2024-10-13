package com.example.parking.kafka.service;

import com.example.parking.direction.entity.Direction;
import com.example.parking.direction.repository.DirectionRepository;
import com.example.parking.park.cache.ParkingRedisTemplateService;
import com.example.parking.park.dto.ParkingDto;
import com.example.parking.park.entity.Parking;
import com.example.parking.park.repository.ParkingRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParkingConsumer {

    private final DirectionRepository directionRepository;
    private final ParkingRepository parkingRepository;
    private final ParkingRedisTemplateService parkingRedisTemplateService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "parking-topic", groupId = "parking-group")
    public void consume(String parkingData) {
        try {
            Direction direction = objectMapper.readValue(parkingData, Direction.class);

            // DB에 저장 (Direction)
            directionRepository.save(direction);
            log.info("Direction data saved to DB: {}", direction.getTargetParkingName());

            // DB에 저장 (Parking)
            Parking parking = convertToParking(direction);
            parkingRepository.save(parking);
            log.info("Parking data saved to DB: {}", parking.getParkingName());

            // Redis에 저장
            ParkingDto parkingDto = convertToParkingDto(direction);
            parkingRedisTemplateService.save(parkingDto);
            log.info("Parking data saved to Redis: {}", parkingDto.getParkingName());

        } catch (Exception e) {
            log.error("Error processing parking data", e);
        }
    }

    private Parking convertToParking(Direction direction) {
        return Parking.builder()
                .parkingName(direction.getTargetParkingName())
                .parkingAddress(direction.getTargetAddress())
                .latitude(direction.getTargetLatitude())
                .longitude(direction.getTargetLongitude())
                .build();
    }

    private ParkingDto convertToParkingDto(Direction direction) {
        return ParkingDto.builder()
                .id(direction.getId())
                .parkingName(direction.getTargetParkingName())
                .parkingAddress(direction.getTargetAddress())
                .latitude(direction.getTargetLatitude())
                .longitude(direction.getTargetLongitude())
                .build();
    }
}