package com.example.parking.direction.service

import com.example.parking.api.dto.DocumentDto
import com.example.parking.api.service.KakaoCategorySearchService
import com.example.parking.direction.repository.DirectionRepository
import com.example.parking.parking.dto.ParkingDto
import com.example.parking.parking.service.ParkingSearchService
import spock.lang.Specification

class DirectionServiceTest extends Specification {

    private ParkingSearchService parkingSearchService = Mock()
    private DirectionRepository directionRepository = Mock()
    private KakaoCategorySearchService kakaoCategorySearchService = Mock()
    private Base62Service base62Service = Mock()

    private DirectionService directionService = new DirectionService(
            parkingSearchService, directionRepository, kakaoCategorySearchService, base62Service
    )

    private List<parkingDto> parkingList

    def setup() {
        parkingList = new ArrayList<>()
        parkingList.addAll(parkingDto.builder()
                .id(1L)
                .parkingName("돌곶이온누리약국")
                .parkingAddress("주소1")
                .latitude(37.61040424)
                .longitude(127.0569046)
                .build(),
                parkingDto.builder()
                        .id(2L)
                        .parkingName("호수온누리약국")
                        .parkingAddress("주소2")
                        .latitude(37.60894036)
                        .longitude(127.029052)
                        .build())

    }

    def "buildDirectionList - 결과 값이 거리 순으로 정렬이 되는지 확인"() {
        given:
        def addressName = "서울 성북구 종암로10길"
        double inputLatitude = 37.5960650456809
        double inputLongitude = 127.037033003036

        def documentD = DocumentDto.builder()
                .addressName(addressName)
                .latitude(inputLatitude)
                .longitude(inputLongitude)
                .build()

        when:

        parkingSearchService.searchparkingDtoList() >> parkingList

        def results = directionService.buildDriectionList(documentD)

        then:
        results.size() == 2
        results.get(0).targetparkingName == "호수온누리약국"
        results.get(1).targetparkingName == "돌곶이온누리약국"
    }

    def "buildDirectionList - 정해진 반경 10 km 내에 검색이 되는지 확인"() {
        given:
        parkingList.add(parkingDto.builder()
                .id(3L)
                .parkingName("경기약국")
                .parkingAddress("주소3")
                .latitude(37.3825107393401)
                .longitude(127.236707911313)
                .build())

        def addressName = "서울 성북구 종암로10길"
        double inputLatitude = 37.5960650456809
        double inputLongitude = 127.037033003036

        def documentDto = DocumentDto.builder()
                .addressName(addressName)
                .latitude(inputLatitude)
                .longitude(inputLongitude)
                .build()

        when:

        parkingSearchService.searchparkingDtoList() >> parkingList

        def results = directionService.buildDriectionList(documentDto)

        then:
        results.size() == 2
        results.get(0).targetparkingName == "호수온누리약국"
        results.get(1).targetparkingName == "돌곶이온누리약국"
    }
}