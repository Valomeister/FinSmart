package com.example.finsmart;

import java.util.HashMap;
import java.util.Map;

public class CryptoSymbolMapper {

    // Создаем карту соответствий один раз при загрузке класса
    private static final Map<String, String> cryptoMap = new HashMap<>();

    static {
        cryptoMap.put("bitcoin", "BTC");
        cryptoMap.put("ethereum", "ETH");
        cryptoMap.put("binance coin", "BNB");
        cryptoMap.put("cardano", "ADA");
        cryptoMap.put("solana", "SOL");
        cryptoMap.put("dogecoin", "DOGE");
        cryptoMap.put("xrp", "XRP");
        cryptoMap.put("polkadot", "DOT");
        cryptoMap.put("avalanche", "AVAX");
        cryptoMap.put("chainlink", "LINK");
        cryptoMap.put("litecoin", "LTC");
        cryptoMap.put("uniswap", "UNI");
        cryptoMap.put("stellar", "XLM");
        cryptoMap.put("monero", "XMR");
        cryptoMap.put("dash", "DASH");
        cryptoMap.put("ethereum classic", "ETC");
        cryptoMap.put("filecoin", "FIL");
        cryptoMap.put("tezos", "XTZ");
        cryptoMap.put("eos", "EOS");
    }

    public static String generateSymbol(String name) {
        return cryptoMap.get(name.toLowerCase());
    }

    public static void main(String[] args) {
        System.out.println(generateSymbol("Bitcoin"));     // BTC
        System.out.println(generateSymbol("Ethereum"));    // ETH
        System.out.println(generateSymbol("Solana"));      // SOL
        System.out.println(generateSymbol("Ripple"));      // XRP
        System.out.println(generateSymbol("Unknown"));     // null
    }
}