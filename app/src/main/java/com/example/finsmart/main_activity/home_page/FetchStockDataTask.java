package com.example.finsmart.main_activity.home_page;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FetchStockDataTask extends AsyncTask<Void, Void, String> {

    private final String urlString;
    private final OnDataFetchedListener listener;

    public interface OnDataFetchedListener {
        void onDataFetched(List<StockDataPoint> data);
    }

    public FetchStockDataTask(String urlString, OnDataFetchedListener listener) {
        this.urlString = urlString;
        this.listener = listener;
    }

    @Override
    protected String doInBackground(Void... voids) {
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            reader.close();
        } catch (Exception e) {
            Log.e("FetchStockDataTask", "Error fetching data from: " + urlString, e);
        }
        return result.toString();
    }

    @Override
    protected void onPostExecute(String jsonResult) {
        List<StockDataPoint> dataPoints = parseJsonAndReturnList(jsonResult);
        if (listener != null) {
            listener.onDataFetched(dataPoints);
        }
    }

    private List<StockDataPoint> parseJsonAndReturnList(String jsonData) {
        List<StockDataPoint> list = new ArrayList<>();
        Map<String, Double> dailyPrices = new HashMap<>(); // ключ: дата, значение: цена

        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONObject history = jsonObject.getJSONObject("history");
            JSONArray columns = history.getJSONArray("columns");
            JSONArray data = history.getJSONArray("data");

            int tradeDateIndex = -1;
            int closeIndex = -1;

            for (int i = 0; i < columns.length(); i++) {
                String columnName = columns.getString(i);
                if ("TRADEDATE".equals(columnName)) tradeDateIndex = i;
                else if ("CLOSE".equals(columnName)) closeIndex = i;
            }

            if (tradeDateIndex == -1 || closeIndex == -1) return list;

            for (int i = 0; i < data.length(); i++) {
                JSONArray row = data.getJSONArray(i);
                String date = row.getString(tradeDateIndex);
                double price = row.getDouble(closeIndex);

                // Перезаписываем, если дата уже есть → остаётся последняя цена
                dailyPrices.put(date, price);
            }

            // Преобразуем Map обратно в список
            for (Map.Entry<String, Double> entry : dailyPrices.entrySet()) {

                list.add(new StockDataPoint(entry.getKey(), entry.getValue()));
            }

            // Сортировка по дате (если нужно)
            list.sort(Comparator.comparing(StockDataPoint::getDate));

        } catch (JSONException e) {
            Log.e("JSON_PARSE", "Ошибка парсинга JSON", e);
        }
        return list;
    }



}