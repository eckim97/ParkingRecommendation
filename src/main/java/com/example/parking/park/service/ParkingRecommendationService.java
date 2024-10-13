package com.example.parking.park.service;

import com.example.parking.api.dto.DocumentDto;
import com.example.parking.api.dto.KakaoApiResponseDto;
import com.example.parking.api.service.KakaoAddressSearchService;
import com.example.parking.direction.entity.Direction;
import com.example.parking.direction.service.Base62Service;
import com.example.parking.direction.service.DirectionService;
import com.example.parking.dto.OutputDto;
import com.example.parking.kafka.service.ParkingProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParkingRecommendationService {

    private final KakaoAddressSearchService kakaoAddressSearchService;
    private final DirectionService directionService;
    private final Base62Service base62Service;
    private final ParkingProducer parkingProducer;

    private static final String ROAD_VIEW_BASE_URL = "https://map.kakao.com/link/roadview/";

    @Value("${parking.recommendation.base.url}")
    private String baseUrl;

    public List<OutputDto> recommendParkingList(String address) {
        KakaoApiResponseDto kakaoApiResponseDto = kakaoAddressSearchService.requestAddressSearch(address);

        if (Objects.isNull(kakaoApiResponseDto) || CollectionUtils.isEmpty(kakaoApiResponseDto.getDocumentList())) {
            log.error("[ParkingRecommendationService] Input address: {}", address);
            return Collections.emptyList();
        }

        DocumentDto documentDto = kakaoApiResponseDto.getDocumentList().get(0);
        List<Direction> directionList = directionService.buildDirectionListByCategoryApi(documentDto);

        // Kafka를 통해 데이터 전송
        directionList.forEach(parkingProducer::sendParkingData);

        return directionList.stream()
                .map(this::convertToOutputDto)
                .collect(Collectors.toList());
    }

    private OutputDto convertToOutputDto(Direction direction) {
        return OutputDto.builder()
                .parkingName(direction.getTargetParkingName())
                .parkingAddress(direction.getTargetAddress())
                .directionUrl(baseUrl + base62Service.encodeDirectionId(direction.getId()))
                .roadViewUrl(ROAD_VIEW_BASE_URL + direction.getTargetLatitude() + "," + direction.getTargetLongitude())
                .distance(String.format("%.2f km", direction.getDistance()))
                .build();
    }
}