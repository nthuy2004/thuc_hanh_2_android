package com.loader.thuc_hanh_2_android.utils;

import java.text.NumberFormat;
import java.util.Locale;

public class FormatUtils {

    
    public static String formatVND(double amount) {
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);
        return currencyVN.format(amount);
    }

    
    public static double roundHours(double hours) {
        return Math.round(hours * 100.0) / 100.0;
    }
}

