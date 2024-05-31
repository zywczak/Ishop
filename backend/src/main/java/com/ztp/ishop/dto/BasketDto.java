package com.ztp.ishop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BasketDto {
    private Long productId;
    private int quantity;
}//////////////////////////////////
