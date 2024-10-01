package com.example.parking.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OutputDto {

    private String parkingName;
    private String parkingAddress;
    private String directionUrl;
    private String roadViewUrl;
    private String distance;
}
