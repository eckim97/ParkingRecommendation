package com.example.parking.api.service;

import com.example.parking.api.dto.KakaoApiResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoCategorySearchService {

    private final KakaoUriBuilderService kakaoUriBuilderService;

    private final RestTemplate restTemplate;

    private static final String PARKING_CATEGORY = "PK6";

    @Value("${KAKAO.REST.API.KEY}")
    private String kakaoRestApiKey;

    public KakaoApiResponseDto requestParkingCategorySearch(double latitude, double longitude, double radius) {
        URI uri = kakaoUriBuilderService.buildUriByCategorySearch(latitude, longitude, radius, PARKING_CATEGORY);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "KakaoAK " + kakaoRestApiKey);
        HttpEntity<?> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<KakaoApiResponseDto> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, KakaoApiResponseDto.class);
        KakaoApiResponseDto response = responseEntity.getBody();
        log.info("Kakao API Response: {}", response);
        log.info("Kakao API Response Status Code: {}", responseEntity.getStatusCode());
        log.info("Kakao API Response Headers: {}", responseEntity.getHeaders());
        return response;
    }
}
