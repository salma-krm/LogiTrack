package com.smartusers.logitrackapi.dto.warehouse;



import lombok.Data;

@Data
public class WarehouseResponse {

    private Long id;
    private String name;
    private Boolean active;
    private Long managerId;
    private String managerName;
    private String managerEmail;
}
