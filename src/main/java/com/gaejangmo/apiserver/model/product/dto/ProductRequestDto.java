package com.gaejangmo.apiserver.model.product.dto;

import com.gaejangmo.apiserver.model.product.domain.vo.NaverProductType;
import com.gaejangmo.apiserver.model.product.dto.validator.EnumValue;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class ProductRequestDto {
    @NotBlank
    private String title;

    @URL
    private String link;

    @URL
    private String image;

    @Min(0)
    private Long lowestPrice;

    @Min(0)
    private Long highestPrice;

    @NotBlank
    private String mallName;

    @Min(0)
    private Long productId;

    @EnumValue(enumClass = NaverProductType.class)
    private String naverProductType;
}