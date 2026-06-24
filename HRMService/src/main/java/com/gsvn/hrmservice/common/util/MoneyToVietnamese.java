package com.gsvn.hrmservice.common.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MoneyToVietnamese {
    private static final String[] DIGITS = {"không", "một", "hai", "ba", "bốn", "năm", "sáu", "bảy", "tám", "chín"};
    private static final String[] UNITS = {"", "nghìn", "triệu", "tỷ", "nghìn tỷ", "triệu tỷ"};

    public static String toVietnamese(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
            return "Không đồng";
        }

        String amountStr = String.valueOf(amount.longValue());
        List<String> digitGroups = new ArrayList<>();
        int length = amountStr.length();

        // Split the string into groups of 3 digits
        while (length > 0) {
            int start = Math.max(0, length - 3);
            digitGroups.add(amountStr.substring(start, length));
            length -= 3;
        }

        List<String> resultParts = new ArrayList<>();
        // Process from the most significant group to the least
        for (int i = digitGroups.size() - 1; i >= 0; i--) {
            String groupText = convertGroupOfThree(digitGroups.get(i));
            if (!groupText.isEmpty()) {
                resultParts.add(groupText);
                if (i < UNITS.length && !UNITS[i].isEmpty()) {
                    resultParts.add(UNITS[i]);
                }
            }
        }

        String finalResult = String.join(" ", resultParts).replaceAll("\\s+", " ").trim();
        return finalResult.substring(0, 1).toUpperCase() + finalResult.substring(1) + " đồng.";
    }

    private static String convertGroupOfThree(String groupStr) {
        int number = Integer.parseInt(groupStr);
        if (number == 0) return "";

        int hundreds = number / 100;
        int tens = (number % 100) / 10;
        int units = number % 10;

        StringBuilder sb = new StringBuilder();

        // Hundreds place
        if (groupStr.length() == 3) {
            sb.append(DIGITS[hundreds]).append(" trăm ");
        }

        // Tens and Units place
        if (tens > 1) {
            sb.append(DIGITS[tens]).append(" mươi ");
            if (units == 1) sb.append("mốt");
            else if (units == 5) sb.append("lăm");
            else if (units > 0) sb.append(DIGITS[units]);
        } else if (tens == 1) {
            sb.append("mười ");
            if (units == 5) sb.append("lăm");
            else if (units > 0) sb.append(DIGITS[units]);
        } else { // tens == 0
            if (hundreds > 0 && units > 0) sb.append("lẻ ");
            if (units > 0) sb.append(DIGITS[units]);
        }

        return sb.toString().trim();
    }
}