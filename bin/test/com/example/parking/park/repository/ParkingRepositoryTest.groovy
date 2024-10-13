package com.example.parking.park.repository

import com.example.parking.AbstractIntegrationContainerBaseTest
import com.example.parking.park.entity.Parking
import org.springframework.beans.factory.annotation.Autowired

import java.time.LocalDateTime

class ParkingRepositoryTest extends AbstractIntegrationContainerBaseTest {

    @Autowired
    private ParkingRepository parkingRepository

    def setup() {
        parkingRepository.deleteAll()
    }

    def "ParkingRepository save"() {
        given:
        String address = "서울 특별시 성북구 종암동"
        String name = "기린 민영 주차장"
        double latitude = 37.57
        double longitude = 126.98

        def parking = Parking.builder()
                .parkingAddress(address)
                .parkingName(name)
                .latitude(latitude)
                .longitude(longitude)
                .build()
        when:
        def result = parkingRepository.save(parking)

        then:
        result.getParkingAddress() == address
        result.getParkingName() == name
        result.getLatitude() == latitude
        result.getLongitude() == longitude

    }

    def "ParkingRepository saveAll"() {
        given:
        String address = "서울 특별시 성북구 종암동"
        String name = "기린 민영 주차장"
        double latitude = 37.57
        double longitude = 126.98

        def parking = Parking.builder()
                .parkingAddress(address)
                .parkingName(name)
                .latitude(latitude)
                .longitude(longitude)
                .build()

        when:
        parkingRepository.saveAll(Arrays.asList(parking))
        def result = parkingRepository.findAll()

        then:
        result.size() == 1
    }

    def "BaseTimeEntity 등록"() {
        given:
        LocalDateTime now = LocalDateTime.now()
        String address = "서울 특별시 성북구 종암동"
        String name = "기린 민영 주차장"

        def pharmcy = Parking.builder()
                .parkingAddress(address)
                .parkingName(name)
                .build()
        when:
        parkingRepository.save(pharmcy)
        def result = parkingRepository.findAll()

        then:
        result.get(0).getCreatedDate().isAfter(now)
        result.get(0).getModifiedDate().isAfter(now)
    }
}