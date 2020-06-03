package com.spbstu.util;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 */
// can be verified using http://www.axicon.com/checkdigitcalculator.html
// understanding barcodes: https://www.tec-it.com/en/support/knowbase/barcode-overview/linear/Default.aspx#EAN13
public class EAN13Generator {
    public static String generate() {
        long value = ThreadLocalRandom.current().nextLong(1000_0000_0000L, 1_0000_0000_0000L);
        int checkDigit = genCheckDigit(value);
        value = value * 10 + checkDigit;
        return String.valueOf(value);
    }

    private static int genCheckDigit(long value) {
        String valueStr = String.valueOf(value);
        int a = 0;
        int b = 0;
        for (int i = 1; i < valueStr.length(); i += 2) {
            a += Character.getNumericValue(valueStr.charAt(i));
        }
        for (int i = 0; i < valueStr.length(); i += 2) {
            b += Character.getNumericValue(valueStr.charAt(i));
        }
        int c = a * 3 + b;
        int d = (int) (Math.ceil(c / 10.0) * 10.0);
        return d - c;
    }
}
