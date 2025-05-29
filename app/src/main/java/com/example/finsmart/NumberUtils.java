package com.example.finsmart;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.Format;

public class NumberUtils {

    public static String roundToThreeSignificantDigits(double value) {
        // Преобразуем double в BigDecimal с точностью 3 значащие цифры
        MathContext mathContext = new MathContext(3, RoundingMode.HALF_UP);
        BigDecimal rounded = new BigDecimal(Double.toString(value), mathContext);

        // Убираем лишние нули и используем десятичную запись (без E-нотации)
        String result = rounded.stripTrailingZeros().toPlainString();

        return result.replace('.', ',');
    }

    public static String roundDecimalPartTo3SD(double value) {
        int integerPart = (int) value;
        double decimalPart = Math.abs(value - (int) value);
        String result = "";
        if (integerPart != 0) {
            result =  integerPart + roundToThreeSignificantDigits(decimalPart);
        } else {
            result = roundToThreeSignificantDigits(decimalPart);
        }
        return divideThousands(result);

    }

    public static String divideThousands(String value) {
        String result = "";
        int index = Math.max(value.indexOf(','), value.indexOf('.'));
        int intergerPartLength;
        if (index == -1) {
            intergerPartLength = value.length();
        } else {
            intergerPartLength = index;
        }
        for (int i = 0; i < intergerPartLength; i++) {
            result += value.charAt(i);
            if ((intergerPartLength - i) % 3 == 1 && i != intergerPartLength - 1) {
                result += ' ';
            }
        }
        if (index != -1)
            result += value.substring(index);
        return result;
    }

}