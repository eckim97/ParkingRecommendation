package com.example.parking.park.service

import com.example.parking.AbstractIntegrationContainerBaseTest
import com.example.parking.park.entity.Parking
import com.example.parking.park.repository.ParkingRepository
import org.springframework.beans.factory.annotation.Autowired

class ParkingRepositoryServiceTest extends AbstractIntegrationContainerBaseTest {

    @Autowired
    private ParkingRepositoryService parkingRepositoryService

    @Autowired
    ParkingRepository parkingRepository

    void setup() {
        parkingRepository.deleteAll()
    }

    def "ParkingRepository update - dirty checking success"() {

        given:
        String inputAddress = "서울 특별시 성북구 종암동"
        String modifiedAddress = "서울 광진구 구의동"
        String name = "기린 민영 주차장"

        def parking = parking.builder()
                .parkingAddress(inputAddress)
                .parkingName(name)
                .build()
        when:
        def entity = parkingRepository.save(parking)
        parkingRepositoryService.updateAddress(entity.getId(), modifiedAddress)

        def result = parkingRepository.findAll()

        then:
        result.get(0).getParkingAddress() == modifiedAddress
    }

    def "ParkingRepository update - dirty checking fail"() {

        given:
        String inputAddress = "서울 특별시 성북구 종암동"
        String modifiedAddress = "서울 광진구 구의동"
        String name = "기린 민영 주차장"

        def parking = Parking.builder()
                .parkingAddress(inputAddress)
                .parkingName(name)
                .build()
        when:
        def entity = parkingRepository.save(parking)
        parkingRepositoryService.updateAddressWithoutTransaction(entity.getId(), modifiedAddress)

        def result = parkingRepository.findAll()

        then:
        result.get(0).getParkingAddress() == inputAddress
    }


    def "self invocation"() {

        given:
        String address = "서울 특별시 성북구 종암동"
        String name = "기린 민영 주차장"
        double latitude = 37.57
        double longitude = 126.98

        def parking = parking.builder()
                .parkingAddress(address)
                .parkingName(name)
                .latitude(latitude)
                .longitude(longitude)
                .build()

        when:
        parkingRepositoryService.bar(Arrays.asList(parking))

        then:
        def e = thrown(RuntimeException.class)
        def result = parkingRepositoryService.findAll()
        result.size() == 1 // 트랜잭션이 적용되지 않는다( 롤백 적용 X )
    }

    def "transactional readOnly test"() {

        given:
        String inputAddress = "서울 특별시 성북구"
        String modifiedAddress = "서울 특별시 광진구"
        String name = "기린 민영 주차장"
        double latitude = 37.57
        double longitude = 126.98

        def input = parking.builder()
                .parkingAddress(inputAddress)
                .parkingName(name)
                .latitude(latitude)
                .longitude(longitude)
                .build()

        when:
        def parking = parkingRepository.save(input)
        parkingRepositoryService.startReadOnlyMethod(parking.id)

        then:
        def result = parkingRepositoryService.findAll()
        result.get(0).getParkingAddress() == inputAddress
    }
}
