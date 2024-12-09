package com.cars.cars.Specification;

import com.cars.cars.Model.Car;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;

public class CarSpecification {

    public static Specification<Car> hasType(String type) {
        return (root, query, criteriaBuilder) ->
                type == null ? null : criteriaBuilder.equal(root.get("carType"), type);
    }

    public static Specification<Car> hasPriceRange(Integer minPrice, Integer maxPrice) {
        return (root, query, criteriaBuilder) -> {
            if (minPrice == null && maxPrice == null) {
                return null;
            } else if (minPrice == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("carPrice"), maxPrice);
            } else if (maxPrice == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("carPrice"), minPrice);
            } else {
                return criteriaBuilder.between(root.get("carPrice"), minPrice, maxPrice);
            }
        };
    }

    public static Specification<Car> isAvailableBetween(Date startDate, Date endDate) {
        return (root, query, criteriaBuilder) -> {
            if (startDate == null || endDate == null) {
                return null;
            }
            return criteriaBuilder.and(
                criteriaBuilder.lessThanOrEqualTo(root.get("availableFrom"), startDate),
                criteriaBuilder.greaterThanOrEqualTo(root.get("availableTo"), endDate)
            );
        };
    }
}