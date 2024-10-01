package com.example.parking.park.service

import com.example.parking.park.cache.ParkingRedisTemplateService
import com.example.parking.park.entity.Parking
import com.google.common.collect.Lists
import spock.lang.Specification

class ParkingSearchServiceTest extends Specification {
    private ParkingSearchService parkingSearchService

    private ParkingRepositoryService parkingRepositoryService = Mock()
    private ParkingRedisTemplateService parkingRedisTemplateService = Mock()

    private List<Parking> parkingList

    def setup() {
        parkingSearchService = new ParkingSearchService(parkingRepositoryService, parkingRedisTemplateService)

        parkingList = Lists.newArrayList(
                parking.builder()
                        .id(1L)
                        .parkingName("호수온누리약국")
                        .latitude(37.60894036)
                        .longitude(127.029052)
                        .build(),
                Parking.builder()
                        .id(2L)
                        .parkingName("돌곶이온누리약국")
                        .latitude(37.61040424)
                        .longitude(127.0569046)
                        .build()
        )
    }

    def "레디스 장애시 DB를 이용 하여 주차장 데이터 조회"() {

        when:
        parkingRedisTemplateService.findAll() >> []
        parkingRepositoryService.findAll() >> parkingList

        def result = parkingSearchService.searchParkingDtoList()

        then:
        result.size() == 2
    }
}