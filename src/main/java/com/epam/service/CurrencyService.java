package com.epam.service;

import com.epam.model.Currency;
import com.epam.model.CurrencyDailyPrice;
import com.epam.repository.CurrencyRepository;
import com.epam.utils.MathUtils;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author Egor Piankov
 */
@Service
@Transactional
@AllArgsConstructor
public class CurrencyService {

    private final CurrencyRepository currencyRepository;
    private final MathUtils mathUtils;

    /**
     * <p>Calculating monthly statistics for particular cryptocurrency</p>
     *
     * @param dailyPrices  list of handled monthly prices to search min and max
     * @param currencyCode specifies the concrete currency (i.g. BTC)
     * @return calculated summary month statistic for particular cryptocurrency
     */
    private Currency calculateCurrencyInfo(List<CurrencyDailyPrice> dailyPrices, String currencyCode) {
        Currency currency = new Currency();
        currency.setCurrencyCode(currencyCode);
        currency.setMinPrice(Integer.MAX_VALUE);
        currency.setMaxPrice(Integer.MIN_VALUE);

        Date oldestPriceDate = null;
        Date newestPriceDate = null;

        for (CurrencyDailyPrice dailyPrice : dailyPrices) {
            if (newestPriceDate == null || dailyPrice.getDate().after(newestPriceDate)) {
                newestPriceDate = dailyPrice.getDate();
                currency.setNewestPrice(dailyPrice.getOldestPrice());
            }
            if (oldestPriceDate == null || dailyPrice.getDate().before(oldestPriceDate)) {
                oldestPriceDate = dailyPrice.getDate();
                currency.setOldestPrice(dailyPrice.getNewestPrice());
            }
            if (dailyPrice.getMaxPrice() > currency.getMaxPrice()) {
                currency.setMaxPrice(dailyPrice.getMaxPrice());
            }
            if (dailyPrice.getMinPrice() < currency.getMinPrice()) {
                currency.setMinPrice(dailyPrice.getMinPrice());
            }
        }

        currency.setNormalizedPrice(
                mathUtils.calculateNormalizedPrice(currency.getMinPrice(), currency.getMaxPrice()));

        return currency;
    }

    public Optional<Currency> findCurrencyByCode(String currencyCode) {
        return currencyRepository.findByCurrencyCode(currencyCode);
    }

    public List<Currency> findAllCurrencies(String sort) {
        return currencyRepository.findAll(Sort.by(Sort.Direction.fromString(sort), "normalizedPrice"));
    }

    public void calculateAndSaveCurrency(List<CurrencyDailyPrice> dailyPrices, String currencyCode) {
        currencyRepository.deleteByCurrencyCode(currencyCode);
        currencyRepository.save(calculateCurrencyInfo(dailyPrices, currencyCode));
    }
}
