package com.epam.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * The model class for daily prices of crypto
 *
 * @author Egor Piankov
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "currency_daily_prices")
public class CurrencyDailyPrice {

    @Id
    @GeneratedValue
    @Column(name = "price_id", nullable = false)
    @JsonIgnore
    private long id;

    @Column(name = "currency_code", nullable = false)
    private String currencyCode;

    @Column(name = "date", nullable = false)
    private Date date;

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
