package com.example.parking.park.service;

import com.example.parking.park.entity.Parking;
import com.example.parking.park.repository.ParkingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParkingRepositoryService {
    private final ParkingRepository parkingRepository;

    // self invocation test
    public void bar(List<Parking> parkingList) {
        log.info("CurrentTransactionName: "+ TransactionSynchronizationManager.getCurrentTransactionName());
        foo(parkingList);
    }

    // self invocation test
    @Transactional
    public void foo(List<Parking> parkingList) {
        parkingList.forEach(parking -> {
            parkingRepository.save(parking);
            throw new RuntimeException("error");
        });
    }


    // read only test
    @Transactional(readOnly = true)
    public void startReadOnlyMethod(Long id) {
        parkingRepository.findById(id).ifPresent(parking ->
                parking.changeParkingAddress("서울 특별시 광진구"));
    }


    @Transactional
    public List<Parking> saveAll(List<Parking> parkingList) {
        if(CollectionUtils.isEmpty(parkingList)) return Collections.emptyList();
        return parkingRepository.saveAll(parkingList);
    }

    @Transactional
    public void updateAddress(Long id, String address) {
        Parking entity = parkingRepository.findById(id).orElse(null);

        if (Objects.isNull(entity)) {
            log.error("[parkingRepositoryService updateAddress] not found id : {}", id);
            return;
        }

        entity.changeParkingAddress(address);
    }

    public void updateAddressWithoutTransaction(Long id, String address) {
        Parking entity = parkingRepository.findById(id).orElse(null);

        if (Objects.isNull(entity)) {
            log.error("[parkingRepositoryService updateAddress] not found id : {}", id);
            return;
        }

        entity.changeParkingAddress(address);
    }

    @Transactional(readOnly = true)
    public List<Parking> findAll() {
        return parkingRepository.findAll();
    }
}
