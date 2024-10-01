package com.example.parking.parking.repository

import com.example.parking.AbstractIntegrationContainerBaseTest
import com.example.parking.Parking.entity.Parking
import org.springframework.beans.factory.annotation.Autowired

import java.time.LocalDateTime

class ParkingRepositoryTest extends AbstractIntegrationContainerBaseTest {

    @Autowired
    private ParkingRepository ParkingRepository

    def setup() {
        ParkingRepository.deleteAll()
    }

    def "ParkingRepository save"() {
        given:
        String address = "서울 특별시 성북구 종암동"
        String name = "은혜 약국"
        double latitude = 37.59
        double longitude = 127.03

        def Parking = Parking.builder()
                .ParkingAddress(address)
                .ParkingName(name)
                .latitude(latitude)
                .longitude(longitude)
                .build()
        when:
        def result = ParkingRepository.save(Parking)

        then:
        result.getParkingAddress() == address
        result.getParkingName() == name
        result.getLatitude() == latitude
        result.getLongitude() == longitude

    }

    def "ParkingRepository saveAll"() {
        given:
        String address = "서울 특별시 성북구 종암동"
        String name = "은혜 약국"
        double latitude = 37.59
        double longitude = 127.03

        def Parking = Parking.builder()
                .ParkingAddress(address)
                .ParkingName(name)
                .latitude(latitude)
                .longitude(longitude)
                .build()

        when:
        ParkingRepository.saveAll(Arrays.asList(Parking))
        def result = ParkingRepository.findAll()

        then:
        result.size() == 1
    }

    def "BaseTimeEntity 등록"() {
        given:
        LocalDateTime now = LocalDateTime.now()
        String address = "서울 특별시 성북구 종암동"
        String name = "은혜 약국"

        def parking = Parking.builder()
                .ParkingAddress(address)
                .ParkingName(name)
                .build()
        when:
        ParkingRepository.save(parking)
        def result = ParkingRepository.findAll()

        then:
        result.get(0).getCreatedDate().isAfter(now)
        result.get(0).getModifiedDate().isAfter(now)
    }
}