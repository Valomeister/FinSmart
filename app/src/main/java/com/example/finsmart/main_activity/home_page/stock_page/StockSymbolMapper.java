package com.example.finsmart.main_activity.home_page.stock_page;

import java.util.HashMap;
import java.util.Map;

public class StockSymbolMapper {

    // Создаем карту соответствий один раз при загрузке класса
    private static final Map<String, String> stockMap = new HashMap<>();

    static {
        // Добавляем уже существующие и новые акции
        stockMap.put("газпром", "GAZP");
        stockMap.put("роснефть", "ROSN");
        stockMap.put("лукойл", "LKOH");
        stockMap.put("норильский никель", "GMKN");
        stockMap.put("сбербанк", "SBER");
        stockMap.put("альфа-банк", "ALRS");
        stockMap.put("мтс", "MTSS");
        stockMap.put("мегафон", "MGNT");
        stockMap.put("ржд", "RZDLP");
        stockMap.put("банк втб", "VTBR");
        stockMap.put("полюс", "PLZL");
        stockMap.put("полиметалл", "POLY");
        stockMap.put("северсталь", "CHMF");
        stockMap.put("евросеть", "ESRT");
        stockMap.put("яндекс", "YDEX");
        stockMap.put("татнефть", "TATN");
        stockMap.put("аэрофлот", "AFLT");
        stockMap.put("новатэк", "NVTK");
        stockMap.put("фск еэс", "FEES");
        stockMap.put("интер рао", "IRAO");
        stockMap.put("дом.рф", "DOMR");
        stockMap.put("пик", "PIKK");
        stockMap.put("русгидро", "HYDR");
        stockMap.put("чтпз", "CHTP");
        stockMap.put("ммк", "MAGN");
        stockMap.put("уралкалий", "URKA");
        stockMap.put("фосагро", "PHOR");
        stockMap.put("яндекс", "YNDX");
        stockMap.put("московская биржа", "MOEX");
    }

    public static String getStockCode(String name) {
        return stockMap.get(name.toLowerCase());
    }

    public static void main(String[] args) {
        System.out.println(getStockCode("ФосАгро"));            // PHOR
        System.out.println(getStockCode("Яндекс"));             // YNDX
        System.out.println(getStockCode("Московская Биржа"));   // MOEX
        System.out.println(getStockCode("Сбербанк"));           // SBER
        System.out.println(getStockCode("Неизвестная компания"));// null
    }
}