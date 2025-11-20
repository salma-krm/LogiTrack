package com.smartusers.logitrackapi.dto.carrier;

import com.smartusers.logitrackapi.enums.CarrierStatus;
import lombok.Data;

@Data
public class CarrierRequest {
    private String code;
    private String name;
    private String contactEmail;
    private String contactPhone;
    private Integer capacity;
    private CarrierStatus status;
}
