package com.smartusers.logitrackapi.dto.category;
import com.smartusers.logitrackapi.dto.Product.ProductResponse;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse {
    private Long id;
    private String name;
}
