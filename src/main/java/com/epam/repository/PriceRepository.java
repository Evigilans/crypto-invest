package com.epam.repository;


import com.epam.model.CurrencyDailyPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * <p>Repository for CRUD operation with CurrencyDailyPrice entities</p>
 *
 * @author Egor Piankov
 */
public interface PriceRepository extends JpaRepository<CurrencyDailyPrice, Long> {

    void deleteAllByCurrencyCode(String currencyCode);

    Optional<CurrencyDailyPrice> findFirstByDateOrderByNormalizedPriceDesc(Date date);

    Optional<CurrencyDailyPrice> findPriceByCurrencyCodeAndDate(String currencyCode, Date date);

    List<CurrencyDailyPrice> findPricesByCurrencyCodeAndDateBetween(String currencyCode, Date fromDate, Date toDate);
}
