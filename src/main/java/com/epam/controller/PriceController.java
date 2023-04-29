package com.epam.controller;

import com.epam.model.CurrencyDailyPrice;
import com.epam.service.PriceService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * <p>Controller for fetching price info, more info available in
 * <a href="/swagger-ui/index.html">Swagger UI</a>
 * </p>
 *
 * @author Egor Piankov
 */
@Log4j2
@AllArgsConstructor
@RestController
@RequestMapping(value = "/prices")
public class PriceController {

    private final PriceService priceService;

    @Operation(
            summary = "Retrieve all information about crypto by it code.",
            description = "Retrieve the oldest/newest/min/max values for a requested crypto. The \"currencyCode\" parameter is case-insensitive.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = CurrencyDailyPrice.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})})
    @RateLimiter(name = "controllerLimiter")
    @GetMapping()
    public ResponseEntity<CurrencyDailyPrice> findMaxNormalizedPriceByDay(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date day) {
        Optional<CurrencyDailyPrice> optionalPrice = priceService.findWithMaxNormalizedPrice(day);
        return optionalPrice.map(price -> ResponseEntity.ok().body(price)).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Retrieve all information about crypto by it code.",
            description = "Retrieve the oldest/newest/min/max values for a requested crypto. The \"currencyCode\" parameter is case-insensitive.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = List.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})})
    @RateLimiter(name = "controllerLimiter")
    @GetMapping("/{currencyCode}")
    public ResponseEntity<List<CurrencyDailyPrice>> findPriceHistoryByCurrency(
            @PathVariable String currencyCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fromDay,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date toDay) {
        List<CurrencyDailyPrice> priceHistory = priceService.findPriceHistoryByCurrency(currencyCode.toLowerCase(), fromDay, toDay);
        return ResponseEntity.ok().body(priceHistory);
    }
}
