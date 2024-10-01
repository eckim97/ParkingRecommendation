package com.example.parking.parking.cache

import com.example.parking.AbstractIntegrationContainerBaseTest
import com.example.parking.parking.dto.ParkingDto
import org.springframework.beans.factory.annotation.Autowired

class ParkingRedisTemplateServiceTest extends AbstractIntegrationContainerBaseTest {

    @Autowired
    private ParkingRedisTemplateService parkingRedisTemplateService

    def setup() {
        parkingRedisTemplateService.findAll()
        .forEach(dto -> {
            parkingRedisTemplateService.delete(dto.getId())
        })
    }

    def "save success"() {
        given:
        String parkingName = "name"
        String parkingAddress = "address"
        ParkingDto dto =
                ParkingDto.builder()
                        .id(1L)
                        .parkingName(parkingName)
                        .parkingAddress(parkingAddress)
                        .build()

        when:
        parkingRedisTemplateService.save(dto)
        List<ParkingDto> result = parkingRedisTemplateService.findAll()

        then:
        result.size() == 1
        result.get(0).id == 1L
        result.get(0).parkingName == parkingName
        result.get(0).parkingAddress == parkingAddress
    }

    def "success fail"() {
        given:
        ParkingDto dto =
                parkingDto.builder()
                        .build()

        when:
        parkingRedisTemplateService.save(dto)
        List<ParkingDto> result = parkingRedisTemplateService.findAll()

        then:
        result.size() == 0
    }

    def "delete"() {
        given:
        String parkingName = "name"
        String parkingAddress = "address"
        ParkingDto dto =
                parkingDto.builder()
                        .id(1L)
                        .parkingName(parkingName)
                        .parkingAddress(parkingAddress)
                        .build()

        when:
        parkingRedisTemplateService.save(dto)
        parkingRedisTemplateService.delete(dto.getId())
        def result = parkingRedisTemplateService.findAll()

        then:
        result.size() == 0
    }
}
