package com.example.finsmart.main_activity.home_page;

import android.os.AsyncTask;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class FetchCurrencyDataTask extends AsyncTask<Void, Void, String> {

    private final String urlString;
    private final OnCurrencyDataFetchedListener listener;

    public interface OnCurrencyDataFetchedListener {
        void onDataFetched(List<CurrencyDataPoint> data);
    }

    public FetchCurrencyDataTask(String urlString, OnCurrencyDataFetchedListener listener) {
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
            Log.e("CBR_FETCH", "Ошибка загрузки данных с ЦБ РФ", e);
        }
        return result.toString();
    }

    @Override
    protected void onPostExecute(String xmlResult) {
        List<CurrencyDataPoint> dataPoints = parseXmlAndReturnList(xmlResult);
        if (listener != null && !dataPoints.isEmpty()) {
            listener.onDataFetched(dataPoints);
        }
    }

    private List<CurrencyDataPoint> parseXmlAndReturnList(String xmlData) {
        List<CurrencyDataPoint> list = new ArrayList<>();

        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource source = new InputSource(new StringReader(xmlData));
            Document doc = builder.parse(source);
            doc.getDocumentElement().normalize();

            NodeList records = doc.getElementsByTagName("Record");

            for (int i = 0; i < records.getLength(); i++) {
                Element record = (Element) records.item(i);

                // Дата в формате D.M.YYYY
                String date = record.getAttribute("Date");

                // Получаем значение курса
                String valueStr = record.getElementsByTagName("Value").item(0).getTextContent();
                double rate = Double.parseDouble(valueStr.replace(',', '.'));

                list.add(new CurrencyDataPoint(date, rate));
            }

        } catch (Exception e) {
            Log.e("XML_PARSE", "Ошибка парсинга XML", e);
        }

        return list;
    }
}