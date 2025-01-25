package com.core.foreign.api.business_field;

import lombok.Getter;

@Getter
public enum BusinessField {
    FOOD_BEVERAGE("외식/음료"),
    STORE_SALES("매장/판매"),
    PRODUCTION_CONSTRUCTION("생산-건설"),
    PRODUCTION_TECHNICAL("생산-기술"),
    OFFICE_SALES("사무/영업"),
    DRIVING_DELIVERY("운전/배달"),
    LOGISTICS_TRANSPORT("물류/운송"),
    ACCOMMODATION_CLEANING("숙박/청소"),
    CULTURE_LEISURE_LIFESTYLE("문화/여가/생활"),
    RURAL_FISHING("농어촌/선원"),
    MODEL_SHOPPING_MALL("모델/쇼핑몰"),
    EDUCATION("교육"),
    OTHER_SERVICE("기타/서비스");

    private final String displayName;

    BusinessField(String displayName) {
        this.displayName = displayName;
    }


}
