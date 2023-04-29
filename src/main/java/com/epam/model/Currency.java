package com.epam.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The model class for daily general cryptocurrency information
 *
 * @author Egor Piankov
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "currencies")
public class Currency {

    @Id
    @GeneratedValue
    @Column(name = "currency_id", nullable = false)
    @JsonIgnore
    private long id;

    @Column(name = "currency_code", nullable = false)
    private String currencyCode;

    @Column(name = "oldest_price")
    private double oldestPrice;

    @Column(name = "newest_price")
    private double newestPrice;

    @Column(name = "min_price")
    private double minPrice;

    @Column(name = "max_price")
    private double maxPrice;

    /**
     * The normalized price is calculated by formula: (max-min)/min
     */
    @Column(name = "normalized_price")
    private double normalizedPrice;
}
