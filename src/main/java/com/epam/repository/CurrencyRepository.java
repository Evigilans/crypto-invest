package com.epam.repository;

import com.epam.model.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * <p>Repository for CRUD operation with Currency entities</p>
 *
 * @author Egor Piankov
 */
public interface CurrencyRepository extends JpaRepository<Currency, Long> {

    Optional<Currency> findByCurrencyCode(String currencyCode);

    void deleteByCurrencyCode(String currencyCode);
}
