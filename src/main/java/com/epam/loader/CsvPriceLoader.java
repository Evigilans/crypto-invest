package com.epam.loader;

import com.epam.configuration.Constants;
import com.epam.model.CurrencyDailyPrice;
import com.epam.service.CurrencyService;
import com.epam.service.PriceService;
import com.epam.utils.MathUtils;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import static com.epam.configuration.Constants.FILENAME_PATTERN;

/**
 * Implementation of PriceLoader for importing prices from CSV files
 *
 * @author Egor Piankov
 */
@Service
@ConditionalOnProperty(name = Constants.CONFIGURATION_LOADER_TYPE, havingValue = "csv")
@Transactional
@Log4j2
public class CsvPriceLoader implements PriceLoader {

    private final PriceService priceService;
    private final CurrencyService currencyService;
    private final MathUtils mathUtils;
    private final String pricesFolder;

    public CsvPriceLoader(PriceService priceService,
                          CurrencyService currencyService,
                          MathUtils mathUtils,
                          @Value("${price-loader.folder}") String pricesFolder) {
        this.priceService = priceService;
        this.currencyService = currencyService;
        this.mathUtils = mathUtils;
        this.pricesFolder = pricesFolder;
    }

    /**
     * <p>Method called by scheduler: looking up to specified folder and trying to parse all files with prices</p>
     */
    public void loadPrices() {
        try (Stream<Path> paths = Files.walk(Paths.get(pricesFolder))) {
            paths.filter(Files::isRegularFile).forEach(this::importFilePrices);
        } catch (IOException exception) {
            log.error("An error occurred during reading files from specified folder: ", exception);
        }
    }

    /**
     * <p>Parsing the file with prices by CSVParser library and saving imported prices to the repository
     * </p>
     *
     * @param path the path to the file CRYPTO_NAME_values.csv (i.g. BTC_values.csv)
     */
    private void importFilePrices(Path path) {
        log.debug("Attempt to parse file: " + path);

        String currencyCode = StringUtils.removeEnd(path.getFileName().toString(), FILENAME_PATTERN).toLowerCase();
        log.debug("Currency code: " + currencyCode);

        List<String[]> rawPrices = new ArrayList<>();
        try (Reader reader = Files.newBufferedReader(path)) {
            try (CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build()) {
                rawPrices = csvReader.readAll();
            }
        } catch (IOException | CsvException exception) {
            log.error("An error occurred during reading prices from file: " + path, exception);
        }

        log.debug("Rows to handle: " + rawPrices.size());
        if (!rawPrices.isEmpty() && StringUtils.isNotBlank(currencyCode)) {
            List<CurrencyDailyPrice> dailyPrices = handleDailyPrices(rawPrices, currencyCode);

            log.debug("Parse daily prices: " + dailyPrices.size());
            if (!dailyPrices.isEmpty()) {
                priceService.saveImportedPrices(currencyCode, dailyPrices);
            }
        }
    }

    /**
     * <p>Method for transforming string data into entities
     * </p>
     *
     * @param rawPrices    array of raw string data of prices
     * @param currencyCode specifies the concrete currency to safe (i.g. BTC)
     * @return the list of entities for saving into database
     */
    private List<CurrencyDailyPrice> handleDailyPrices(List<String[]> rawPrices, String currencyCode) {
        List<CurrencyDailyPrice> dailyPrices = new ArrayList<>();

        rawPrices.sort(Comparator.comparing(columns -> columns[0]));

        CurrencyDailyPrice handledPrice = null;
        for (String[] rawPrice : rawPrices) {
            try {
                if (handledPrice == null) {
                    handledPrice = initializeEmptyDailyPrice(rawPrice, currencyCode);
                }
                if (!mathUtils.isSameDay(handledPrice.getDate(), new Date(Long.parseLong(rawPrice[0])))) {
                    handledPrice.setNormalizedPrice(mathUtils.calculateNormalizedPrice(
                            handledPrice.getMinPrice(),
                            handledPrice.getMaxPrice()));

                    dailyPrices.add(handledPrice);
                    handledPrice = initializeEmptyDailyPrice(rawPrice, currencyCode);
                }

                double currentPrice = Double.parseDouble(rawPrice[2]);
                if (currentPrice > handledPrice.getMaxPrice()) {
                    handledPrice.setMaxPrice(currentPrice);
                }
                if (currentPrice < handledPrice.getMinPrice()) {
                    handledPrice.setMinPrice(currentPrice);
                }
                handledPrice.setNewestPrice(currentPrice);
            } catch (Exception exception) {
                log.error("An error occurred during processing price raw: "
                        + Arrays.toString(rawPrice) + ", the raw was skipped.");
            }
        }

        return dailyPrices;
    }


    /**
     * <p>Method for generating "dummy" object of daily price.
     * Setting maxPrice to Double.MIN_VALUE in order to algorithmicly optimize search of real max price for the specific day
     * Setting minPrice to Double.MAX_VALUE for the same reason
     * </p>
     *
     * @param rawPrice     array of raw string data of prices
     * @param currencyCode specifies the concrete currency (i.g. BTC)
     * @return "dummy" daily price object
     */
    private CurrencyDailyPrice initializeEmptyDailyPrice(String[] rawPrice, String currencyCode) {
        CurrencyDailyPrice dailyPrice = new CurrencyDailyPrice();

        dailyPrice.setCurrencyCode(currencyCode);
        dailyPrice.setDate(new Date(Long.parseLong(rawPrice[0])));
        dailyPrice.setOldestPrice(Double.parseDouble(rawPrice[2]));
        dailyPrice.setMaxPrice(Double.MIN_VALUE);
        dailyPrice.setMinPrice(Double.MAX_VALUE);

        return dailyPrice;
    }
}
