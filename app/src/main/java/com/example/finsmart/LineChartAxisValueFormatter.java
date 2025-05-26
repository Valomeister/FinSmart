package com.example.finsmart;

import android.util.Log;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DecimalFormat;
import java.util.List;

public class LineChartAxisValueFormatter extends ValueFormatter {
    @Override
    public String getFormattedValue(float value) {
        boolean isNegative = false;
        if (value < 0) {
            isNegative = true;
            value *= -1;
        }
        DecimalFormat df0 = new DecimalFormat("#");
        DecimalFormat df1 = new DecimalFormat("#.#");
        DecimalFormat df2 = new DecimalFormat("#.##");

        List<String> abbreviations = List.of("", "K", "M", "B", "T");

        int digits = Math.max(1, (int)Math.log10(value) + 1);

        String result = "";
        String orderOfMagnitudeAbbreviation = abbreviations.get((digits - 1) / 3);
        if (digits % 3 == 0) {
            // получить первые 3 цифры
            while (value >= 1000) {
                value /= 10;
            }
            result += df0.format((int)value);
        } else if (digits % 3 == 1) {
            result += df2.format(value / Math.pow(10, digits - 1));
        } else {
            result += df1.format(value / Math.pow(10, digits - 2));
        }
        result += orderOfMagnitudeAbbreviation;
        if (isNegative) {
            result = "-" + result;
        }
        return result;
    }
}
