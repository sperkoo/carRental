package com.cars.cars.Specification;

import com.cars.cars.Model.Car;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.time.LocalDate;

public class CarSpecification {

    public static Specification<Car> isAvailableBetween(LocalDate startDate, LocalDate endDate) {
        return (Root<Car> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            Subquery<Integer> subquery = query.subquery(Integer.class);
            Root<Car> subRoot = subquery.from(Car.class);
            subquery.select(subRoot.get("carId"));
            Predicate carIdMatch = criteriaBuilder.equal(subRoot.get("carId"), root.get("carId"));
            Predicate dateBetween = criteriaBuilder.between(subRoot.get("rentalDate"), startDate, endDate);
            Predicate dateOverlap = criteriaBuilder.or(
                    criteriaBuilder.between(root.get("rentalDate"), startDate, endDate),
                    criteriaBuilder.and(
                            criteriaBuilder.lessThanOrEqualTo(root.get("rentalDate"), endDate),
                            criteriaBuilder.greaterThanOrEqualTo(root.get("rentalDate"), startDate)
                    )
            );
            subquery.where(carIdMatch, dateOverlap);
            return criteriaBuilder.not(criteriaBuilder.exists(subquery));
        };
    }

    public static Specification<Car> hasType(String type) {
        return (Root<Car> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) ->
                criteriaBuilder.equal(root.get("carType"), type);
    }

    public static Specification<Car> hasPriceRange(Integer minPrice, Integer maxPrice) {
        return (Root<Car> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            if (minPrice != null && maxPrice != null) {
                return criteriaBuilder.between(root.get("carPrice"), minPrice, maxPrice);
            } else if (minPrice != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("carPrice"), minPrice);
            } else if (maxPrice != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("carPrice"), maxPrice);
            } else {
                return criteriaBuilder.conjunction();
            }
        };
    }
}