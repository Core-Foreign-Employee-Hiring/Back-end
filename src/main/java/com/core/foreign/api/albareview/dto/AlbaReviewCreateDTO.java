package com.core.foreign.api.albareview.dto;

import com.core.foreign.api.business_field.BusinessField;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class AlbaReviewCreateDTO {

    private String title;
    private String content;
    private String region1;
    private String region2;
    private BusinessField businessFields;
}
