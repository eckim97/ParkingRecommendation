package com.example.parking.park.entity;

import com.example.parking.BaseTimeEntity;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.example.parking.park.dto.ParkingDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "parking")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Parking extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String parkingName;
    private String parkingAddress;
    private double latitude;
    private double longitude;

    // ParkingDto를 받는 새로운 생성자
    public Parking(ParkingDto parkingDto) {
        this.parkingName = parkingDto.getParkingName();
        this.parkingAddress = parkingDto.getParkingAddress();
        this.latitude = parkingDto.getLatitude();
        this.longitude = parkingDto.getLongitude();
    }

    public void changeParkingAddress(String address){
        this.parkingAddress = address;
    }
}
