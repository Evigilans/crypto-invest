package com.epam.service;

import com.epam.model.CurrencyDailyPrice;
import com.epam.repository.PriceRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * <p>Price service </p>
 *
 * @author Egor Piankov
 */
@Service
@AllArgsConstructor
public class PriceService {

    private final PriceRepository priceRepository;
    private final CurrencyService currencyService;

    public Optional<CurrencyDailyPrice> findWithMaxNormalizedPrice(Date day) {
        if (null == day) {
            day = new Date();
        }
        return priceRepository.findFirstByDateOrderByNormalizedPriceDesc(day);
    }

    public List<CurrencyDailyPrice> findPriceHistoryByCurrency(String currencyCode, Date fromDay, Date toDay) {
        return priceRepository.findPricesByCurrencyCodeAndDateBetween(currencyCode, fromDay, toDay);
    }

    /**
     * <p>Method for saving imported new prices or updating already existing prices.
     * </p>
     *
     * @param currencyCode specifies the concrete currency to safe (i.g. BTC)
     * @param dailyPrices  array of monthly prices for the currency needed to calculate total imformation
     */
    public void saveImportedPrices(String currencyCode, List<CurrencyDailyPrice> dailyPrices) {
        for (CurrencyDailyPrice dailyPrice : dailyPrices) {
            Optional<CurrencyDailyPrice> optionalPrice = priceRepository.findPriceByCurrencyCodeAndDate(currencyCode, dailyPrice.getDate());
            if (optionalPrice.isPresent()) {
                CurrencyDailyPrice existingPrice = optionalPrice.get();
                existingPrice.setOldestPrice(dailyPrice.getOldestPrice());
                existingPrice.setNewestPrice(dailyPrice.getNewestPrice());
                existingPrice.setMinPrice(dailyPrice.getMinPrice());
                existingPrice.setMaxPrice(dailyPrice.getMaxPrice());
                existingPrice.setNormalizedPrice(dailyPrice.getNormalizedPrice());
                dailyPrice = existingPrice;
            }

            priceRepository.save(dailyPrice);
        }

        currencyService.calculateAndSaveCurrency(dailyPrices, currencyCode);
    }
}
