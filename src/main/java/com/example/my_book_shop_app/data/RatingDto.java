package com.example.my_book_shop_app.data;

import com.example.my_book_shop_app.struct.book.rating.RatingEntity;

import java.util.List;
import java.util.OptionalDouble;

public class RatingDto {

    private final Integer totalCount;
    private final Integer currentRating;
    private final Integer oneRateCount;
    private final Integer twoRatesCount;
    private final Integer threeRatesCount;
    private final Integer fourRatesCount;
    private final Integer fiveRatesCount;

    public RatingDto(List<RatingEntity> rating) {
        this.totalCount = rating.size();
        OptionalDouble average = rating.stream().mapToInt(RatingEntity::getRatingCount).average();
        this.currentRating = average.isPresent() ? (int) Math.round(average.getAsDouble()) : 0;
        this.oneRateCount = (int) rating.stream().filter(rate -> rate.getRatingCount() == 1).count();
        this.twoRatesCount = (int) rating.stream().filter(rate -> rate.getRatingCount() == 2).count();
        this.threeRatesCount = (int) rating.stream().filter(rate -> rate.getRatingCount() == 3).count();
        this.fourRatesCount = (int) rating.stream().filter(rate -> rate.getRatingCount() == 4).count();
        this.fiveRatesCount = (int) rating.stream().filter(rate -> rate.getRatingCount() == 5).count();
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public Integer getCurrentRating() {
        return currentRating;
    }

    public Integer getOneRateCount() {
        return oneRateCount;
    }

    public Integer getTwoRatesCount() {
        return twoRatesCount;
    }

    public Integer getThreeRatesCount() {
        return threeRatesCount;
    }

    public Integer getFourRatesCount() {
        return fourRatesCount;
    }

    public Integer getFiveRatesCount() {
        return fiveRatesCount;
    }
}
