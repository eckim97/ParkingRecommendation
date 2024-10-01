package com.example.parking.park.service;

import com.example.parking.park.cache.ParkingRedisTemplateService;
import com.example.parking.park.dto.ParkingDto;
import com.example.parking.park.entity.Parking;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParkingSearchService {

    private final ParkingRepositoryService parkingRepositoryService;
    private final ParkingRedisTemplateService parkingRedisTemplateService;

    public List<ParkingDto> searchParkingDtoList() {
        long startRequestTime = System.currentTimeMillis();

        try {
            // Redis에서 데이터 조회 시간 측정
            long startRedisTime = System.currentTimeMillis();
            List<ParkingDto> parkingDtoList = parkingRedisTemplateService.findAll();
            long endRedisTime = System.currentTimeMillis();
            log.info("레디스 조회 시간: {} ms, 조회된 항목 수: {}", endRedisTime - startRedisTime, parkingDtoList.size());

            if (!parkingDtoList.isEmpty()) {
                log.info("Redis findAll success!");
                return parkingDtoList;
            }

            // DB에서 데이터 조회 시간 측정
            long startDbTime = System.currentTimeMillis();
            List<Parking> parkingList = parkingRepositoryService.findAll();
            long endDbTime = System.currentTimeMillis();
            log.info("DB 조회 시간: {} ms, 조회된 항목 수: {}", endDbTime - startDbTime, parkingList.size());

            parkingDtoList = parkingList.stream()
                    .map(this::convertToparkingDto)
                    .collect(Collectors.toList());

            // Redis에 데이터 저장 시간 측정
            long startRedisSaveTime = System.currentTimeMillis();
            parkingRedisTemplateService.saveAll(parkingDtoList);
            long endRedisSaveTime = System.currentTimeMillis();
            log.info("Redis 저장 시간: {} ms, 저장된 항목 수: {}", endRedisSaveTime - startRedisSaveTime, parkingDtoList.size());

            return parkingDtoList;
        } catch (Exception e) {
            log.error("데이터 조회 중 오류 발생", e);
            throw e;
        } finally {
            long endRequestTime = System.currentTimeMillis();
            log.info("전체 요청 처리 시간: {} ms", endRequestTime - startRequestTime);
        }
    }

    private ParkingDto convertToparkingDto(Parking parking) {
        return ParkingDto.builder()
                .id(parking.getId())
                .parkingAddress(parking.getParkingAddress())
                .parkingName(parking.getParkingName())
                .latitude(parking.getLatitude())
                .longitude(parking.getLongitude())
                .build();
    }
}