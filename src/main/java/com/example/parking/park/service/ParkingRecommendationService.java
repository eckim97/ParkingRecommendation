package com.example.parking.park.service;

import com.example.parking.api.dto.DocumentDto;
import com.example.parking.api.dto.KakaoApiResponseDto;
import com.example.parking.api.service.KakaoAddressSearchService;
import com.example.parking.direction.entity.Direction;
import com.example.parking.direction.service.Base62Service;
import com.example.parking.direction.service.DirectionService;
import com.example.parking.dto.OutputDto;
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
        // 공공기관 주차장 데이터
//        List<Direction> directionList = directionService.buildDriectionList(documentDto);

        // kakao api
        List<Direction> directionList = directionService.buildDirectionListByCategoryApi(documentDto);

        // 방향 리스트를 DB에 저장
        List<Direction> savedDirections = directionService.saveAll(directionList);
        log.info("Saved Directions: {}", savedDirections);

        return savedDirections.stream()
                .map(this::convertToOutputDto)
                .collect(Collectors.toList());
    }

    private OutputDto convertToOutputDto(Direction direction) {
        return OutputDto.builder()
                .parkingName(direction.getTargetParkingName())
                .parkingAddress(direction.getTargetAddress())
                .directionUrl(baseUrl + base62Service.encodeDirectionId(direction.getId()))
                .roadViewUrl(ROAD_VIEW_BASE_URL + direction.getTargetLatitude() + "," + direction.getTargetLongitude())
                .distance(String.format("%.2f", direction.getDistance()))
                .build();
    }

}
