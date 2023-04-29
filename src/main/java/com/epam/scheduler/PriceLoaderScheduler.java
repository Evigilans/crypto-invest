package com.epam.scheduler;

import com.epam.loader.PriceLoader;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * <p>Scheduler for importing new prices for cryptocurrencies</p>
 *
 * @author Egor Piankov
 */
@Log4j2
@AllArgsConstructor
@Component
public class PriceLoaderScheduler {

    private final PriceLoader priceLoader;

    /**
     * <p>PostConstruct method for calling scheduler execution on application startup</p>
     */
    @PostConstruct
    public void onStartup() {
        log.info("Executing scheduled method on application startup");
        importPrices();
    }

    @Scheduled(fixedDelayString = "${price-loader.delay}")
    public void scheduleTask() {
        log.info("Executing scheduled method");
        importPrices();
    }

    private void importPrices() {
        priceLoader.loadPrices();
    }
}
