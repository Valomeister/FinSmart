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
import java.util.ArrayList;
import java.util.List;

public class FetchCryptoDataTask extends AsyncTask<Void, Void, String> {

    private final String urlString;
    private final OnDataFetchedListener listener;

    public interface OnDataFetchedListener {
        void onDataFetched(List<CryptoDataPoint> data);
    }

    public FetchCryptoDataTask(String urlString, OnDataFetchedListener listener) {
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
            Log.e("FetchCryptoDataTask", "Error fetching data from: " + urlString, e);
        }
        return result.toString();
    }

    @Override
    protected void onPostExecute(String jsonResult) {
        List<CryptoDataPoint> dataPoints = parseJsonAndReturnList(jsonResult);
        if (listener != null) {
            listener.onDataFetched(dataPoints);
        }
    }

    private List<CryptoDataPoint> parseJsonAndReturnList(String jsonData) {
        List<CryptoDataPoint> list = new ArrayList<>();
        try {
            JSONObject obj = new JSONObject(jsonData);
            JSONArray pricesArray = obj.getJSONArray("prices");

            for (int i = 0; i < pricesArray.length() - 1; i++) {
                JSONArray priceData = pricesArray.getJSONArray(i);
                long timestamp = priceData.getLong(0); // мс
                double price = priceData.getDouble(1);

                // Преобразуем timestamp в дату (например, "2024-03-15")
                String date = android.text.format.DateFormat.format("yyyy-MM-dd", timestamp).toString();

                list.add(new CryptoDataPoint(date, price));
            }


        } catch (JSONException e) {
            Log.e("JSON_PARSE", "Ошибка парсинга JSON", e);
        }
        return list;
    }
}