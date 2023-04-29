package com.epam.controller;

import com.epam.model.Currency;
import com.epam.service.CurrencyService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


/**
 * <p>Controller for fetching currency info, more info available in
 * <a href="/swagger-ui/index.html">Swagger UI</a>
 * </p>
 *
 * @author Egor Piankov
 */
@Log4j2
@AllArgsConstructor
@RestController
@RequestMapping(value = "/currencies")
public class CurrencyController {

    private final CurrencyService currencyService;

    @Operation(
            summary = "Retrieve a descending sorted list of all the cryptos",
            description = "Retrieve a descending sorted list of all the cryptos, comparing the normalized range. The default sorting value is descending, but can be returned in ascending way by using \"asc\" request parameter.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = Currency.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})})
    @RateLimiter(name = "controllerLimiter")
    @GetMapping()
    public ResponseEntity<List<Currency>> findAllCurrencies(
            @RequestParam(defaultValue = "desc") String sort) {
        List<Currency> sortedCurrencies = currencyService.findAllCurrencies(sort);
        return ResponseEntity.ok().body(sortedCurrencies);
    }

    @Operation(
            summary = "Retrieve all information about crypto by it code.",
            description = "Retrieve the oldest/newest/min/max values for a requested crypto. The \"currencyCode\" parameter is case-insensitive.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = Currency.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})})
    @RateLimiter(name = "controllerLimiter")
    @GetMapping("/{currencyCode}")
    public ResponseEntity<Currency> findCurrency(
            @PathVariable @ApiParam(name = "currencyCode", value = "Currency Code", example = "BTC") String currencyCode) {
        Optional<Currency> optionalCurrency = currencyService.findCurrencyByCode(currencyCode.toLowerCase());
        return optionalCurrency.map(currency -> ResponseEntity.ok().body(currency)).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
