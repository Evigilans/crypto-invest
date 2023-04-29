package com.epam.utils;

import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

/**
 * <p>Common util component for mathematical calculations</p>
 *
 * @author Egor Piankov
 */
@Component
public class MathUtils {

    /**
     * <p>Check if two different dates belong to the same day</p>
     *
     * @param firstDay  the first day to compare
     * @param secondDay the second day to compare
     * @return true if two dates belongs to same day and false otherwise
     */
    public boolean isSameDay(Date firstDay, Date secondDay) {
        Calendar firstCalendar = Calendar.getInstance();
        firstCalendar.setTime(firstDay);

        Calendar secondCalendar = Calendar.getInstance();
        secondCalendar.setTime(secondDay);

        return firstCalendar.get(Calendar.DAY_OF_YEAR) == secondCalendar.get(Calendar.DAY_OF_YEAR)
                && firstCalendar.get(Calendar.YEAR) == secondCalendar.get(Calendar.YEAR);
    }

    /**
     * <p>Calculating normalized price by formula: (max-min)/min </p>
     *
     * @param minPrice minimal price of currency for some period of time
     * @param maxPrice maximal price of currency for some period of time
     * @return calculate normalized price
     */
    public double calculateNormalizedPrice(double minPrice, double maxPrice) {
        return minPrice == 0 ? Double.MAX_VALUE : (maxPrice - minPrice) / minPrice;
    }
}
