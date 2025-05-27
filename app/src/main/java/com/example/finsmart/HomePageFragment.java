package com.example.finsmart;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HomePageFragment extends Fragment {
    private List<Button> timeRangeButtons = new ArrayList<>();
    List<Entry> lineChartDataAllTime;
    LineDataSet fullDataSet;
    LineChart lineChart;
    private boolean isAnimationRunning = false;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home_page, container, false);

        // Круговая диаграмма
        // 1. Получаем данные из БД (пример)
        List<ChartItem> chartData = getChartDataFromDatabase();

        // 2. Настраиваем диаграмму
        PieChart pieChart = view.findViewById(R.id.pieChart);
        setupPieChart(pieChart, chartData);

        // 3. Создаем кастомную легенду
        LinearLayout legendContainer = view.findViewById(R.id.legend_container);
        createCustomLegend(legendContainer, chartData);


        // Линейный график
        // 1. Получаем данные из хранилища (пример)
        List<Entry> lineChartData = loadCsvFromInternalStorage();
        lineChartDataAllTime = lineChartData; // запоминаем полный набор данных
        // запоминаем полный датасет
        fullDataSet = new LineDataSet(lineChartDataAllTime, "Full dataset");

        // 2. Настраиваем диаграмму
        lineChart = view.findViewById(R.id.lineChart);
        setupLineChart(lineChart, lineChartData);

        // 3. Настраиваем кнопки переключения временного отрезка
        // Инициализация кнопок
        timeRangeButtons.add(view.findViewById(R.id.btn1D));
        timeRangeButtons.add(view.findViewById(R.id.btn1W));
        timeRangeButtons.add(view.findViewById(R.id.btn1M));
        timeRangeButtons.add(view.findViewById(R.id.btn1Y));
        timeRangeButtons.add(view.findViewById(R.id.btnAllTime));


        for (Button button : timeRangeButtons) {
            // Установка обработчиков
            button.setOnClickListener(v -> onTimeRangeClicked(button));
        }

        // Изначальная подгрузка графика в интервале "1Г"
        onTimeRangeClicked(view.findViewById(R.id.btn1Y));

        return view;
    }

    private void onTimeRangeClicked(Button selectedButton) {
        boolean animateChartUpdate = true;
        // Если выбран активный интервал
        if (selectedButton.isSelected()) {
            animateChartUpdate = false;
        } else {
            setActiveButton(selectedButton);
        }

        int numOfDays = 0;
        int selectedButtonId = selectedButton.getId();
        if (selectedButtonId == R.id.btn1D) {
            // Функционал для кнопки "1Д"
            numOfDays = 1 + 1; // "+ 1" так как у n промежутков n + 1 границ
        } else if (selectedButtonId == R.id.btn1W) {
            // Функционал для кнопки "1Н"
            numOfDays = 7 + 1;
        } else if (selectedButtonId == R.id.btn1M) {
            // Функционал для кнопки "1М"
            numOfDays = 30 + 1;
        } else if (selectedButtonId == R.id.btn1Y) {
            // Функционал для кнопки "1Г"
            numOfDays = 365 + 1;
        } else if (selectedButtonId == R.id.btnAllTime) {
            // Функционал для кнопки "Все"
            numOfDays = lineChartDataAllTime.size();
        }

        List<Entry> lastNEntries = lineChartDataAllTime.subList(lineChartDataAllTime.size() - numOfDays, lineChartDataAllTime.size());

        updateLineChart(lineChart, lastNEntries, animateChartUpdate);
    }

    private void setActiveButton(Button activeButton) {
        for (Button button : timeRangeButtons) {
            boolean isSelected = button == activeButton;
            button.setSelected(isSelected);

        }
    }

    private List<ChartItem> getChartDataFromDatabase() {
        // временный метод-пустышка - возвращает готовые данные, а не берет их из реальной БД
        List<ChartItem> chartItems = new ArrayList<>();

        chartItems.add(new ChartItem("Вклады 521 693 ₽", 521_693f, Color.rgb(125, 122, 255)));
        chartItems.add(new ChartItem("Фонды 433 606 ₽", 433_606f, Color.rgb(164, 123, 255)));
        chartItems.add(new ChartItem("Акции 322 501 ₽", 322_501f, Color.rgb(189, 160, 255)));
        chartItems.add(new ChartItem("Валюта 266 892 ₽", 266_892f, Color.rgb(203, 180, 255)));
        chartItems.add(new ChartItem("Крипта 125 821 ₽", 125_821f, Color.rgb(214, 200, 246)));

        return chartItems;
    }

    private void setupPieChart(PieChart pieChart, List<ChartItem> data) {
        // Нагоняем стиля
        pieChart.setDrawEntryLabels(false);      // Надписи у секторов
        pieChart.getLegend().setEnabled(false);  // Легенда
        pieChart.getDescription().setEnabled(false); // Описание
        pieChart.setTransparentCircleAlpha(0); // Устанавливаем полную прозрачность
        pieChart.setHoleRadius(76f); // 76% радиуса (размер отверстия в центре)
        pieChart.setCenterText("1 634 123 ₽");
        pieChart.setCenterTextSize(22f); // Размер в dp
        pieChart.setCenterTextTypeface(Typeface.DEFAULT_BOLD);
        pieChart.setCenterTextColor(Color.parseColor("#4D4D4D"));

        // Создаем данные для диаграммы
        ArrayList<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();
        for (ChartItem item : data) {
            entries.add(new PieEntry(item.getValue(), item.getLabel()));
            colors.add(item.getColor());
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setDrawValues(false);
        dataSet.setColors(colors);

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }

    private void createCustomLegend(LinearLayout container, List<ChartItem> data) {
        // Очищаем контейнер
        container.removeAllViews();

        int maxItemsPerRow = 2; // Максимум элементов в строке

        // Создаем горизонтальный контейнер для первого ряда
        LinearLayout currentRow = createNewRow(container.getContext(), maxItemsPerRow);
        container.addView(currentRow);

        int itemsInRow = 0;
        for (int i = 0; i < data.size(); i++) {
            ChartItem item = data.get(i);

            // Если текущая строка заполнена, создаем новую
            if (itemsInRow >= maxItemsPerRow) {
                currentRow = createNewRow(container.getContext(), maxItemsPerRow);
                container.addView(currentRow);
                itemsInRow = 0;
            }

            // Создаем элемент легенды
            View legendItem = createLegendItem(container.getContext(),
                    item.getLabel(),
                    data.get(i).getColor());

            currentRow.addView(legendItem);
            itemsInRow++;

            // Если текущая строка не заполнена до конца, но элементы закончились
            if (i == data.size() - 1 && itemsInRow < maxItemsPerRow) {
                // Создаем невидимый элемент легенды, чтобы ровно отображалось
                View legendItemPlaceHolder = createLegendItem(container.getContext(),
                        item.getLabel(),
                        data.get(i).getColor());
                legendItemPlaceHolder.setVisibility(View.INVISIBLE);
                currentRow.addView(legendItemPlaceHolder);

            }
        }
    }

    private LinearLayout createNewRow(Context context, int maxItemsPerRow) {
        LinearLayout row = new LinearLayout(context);
        row.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setWeightSum(maxItemsPerRow);
        row.setPadding(0, 8, 0, 8);
        return row;
    }

    private View createLegendItem(Context context, String label, int color) {
        // Создаем элемент через LayoutInflater (лучший способ)
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_legend, null);

        if (label.startsWith("Вклады")) {
            // Задаем обработчик клика на весь itemView
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Здесь обрабатываем нажатие
                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, new DepositsFragment())
                            .commit();
                }
            });
        }

        TextView labelView = itemView.findViewById(R.id.legend_label);

        labelView.setText(label);
        Typeface typeface = ResourcesCompat.getFont(context, R.font.roboto_medium);
        labelView.setTypeface(typeface);


        // Создаем GradientDrawable
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadius(16f); // Скругление в пикселях (16px)
        shape.setColor(color); // Цвет фона

        // Применяем к TextView
        labelView.setBackground(shape);

        // Параметры для одинаковой ширины
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0, // Ширина 0, так как используем weight
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f // Вес 1 для всех элементов (одинаковая ширина)
        );
        params.setMargins(12, 4, 12, 4); // Горизонтальные отступы

        itemView.setLayoutParams(params);
        return itemView;
    }

    private List<Entry> loadCsvFromInternalStorage() {
        List<Entry> entries = new ArrayList<>();
        File file = new File(requireContext().getFilesDir(), "portfolio_data.csv");

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isFirstLine = true;
            int linesLeft = 12313 + 1; // + 1 так как первая .csv строка содержит названия колонок
            while ((line = reader.readLine()) != null && --linesLeft >= 0) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] values = line.split(",");
                if (values.length >= 2) {
                    float value = Float.parseFloat(values[1].trim());
                    String date = values[0].trim();
                    entries.add(new Entry(entries.size(), value, date));
                }
            }
        } catch (IOException e) {
            Log.d("CSV", "Ошибка чтения файла", e);
        }

        return entries;
    }

    private void setupLineChart(LineChart lineChart, List<Entry> entries) {

        // Создание набора данных
        LineDataSet dataSet = new LineDataSet(entries, "Стоимость портфеля");
        fixLineDataSetFormatting(dataSet);

        // Настройка графика
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.getDescription().setEnabled(false);
        lineChart.setAutoScaleMinMaxEnabled(true);
        lineChart.getLegend().setEnabled(false);
        lineChart.setExtraOffsets(0, 0, 0, 5); // left, top, right, bottom
        // Отключаем сетку для оси X
        XAxis xAxis = lineChart.getXAxis();
        // Отключаем сетку для левой оси Y
        YAxis leftAxis = lineChart.getAxisLeft();
        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setDrawGridLines(true);
        rightAxis.setGridColor(Color.parseColor("#DDDDDD"));
        rightAxis.setGridLineWidth(1f);
        rightAxis.setDrawAxisLine(false);
        rightAxis.setTextSize(14f);
        rightAxis.setValueFormatter(new LineChartAxisValueFormatter());
        leftAxis.setDrawGridLines(false);
        leftAxis.setEnabled(false);
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // Подписи снизу
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setTextSize(14f);
        xAxis.setLabelCount(2, true); // граничные метки графика
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                // value - это Entry.x (в нашем случае entries.size())
                int index = (int) value;
                if (index >= 0 && index < entries.size()) {
                    Entry entry = entries.get(index);
                    return (String) entry.getData(); // Возвращаем дату из data
                }
                return "";
            }
        });

        // настройка разделения графика при нажатии
        updateLineChartSelectedListener(lineChart, entries);

        lineChart.animateX(1000); // Анимация
        lineChart.invalidate(); // Обновление

        // установка данных над графиком
        updateDataAboveLineChart(entries.size() - 1, entries);
    }

    private void updateLineChartSelectedListener(LineChart lineChart, List<Entry> entries) {
        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                // Сбрасываем визуальное выделение
                lineChart.highlightValue(null);
                // TODO: 26.05.2025 обводить кружочком выбранный data point

                if (isAnimationRunning) {
                    return;
                }

                // Находим индекс точки, по которой произошло нажатие
                int splitIndex = -1;
                for (int i = 0; i < entries.size(); i++) {
                    if (entries.get(i).getX() >= e.getX()) {
                        splitIndex = i;
                        break;
                    }
                }

                if (splitIndex == -1) return;

                // Разделяем данные на две части
                List<Entry> leftEntries = entries.subList(0, splitIndex + 1);
                List<Entry> rightEntries = entries.subList(splitIndex, entries.size());

                // Левый DataSet (цветной)
                LineDataSet leftDataSet = new LineDataSet(leftEntries, "Active");
                leftDataSet.setColor(Color.parseColor("#7D7AFF"));
                leftDataSet.setLineWidth(2f);
                leftDataSet.setDrawCircles(false);
                leftDataSet.setDrawValues(false); // Отключает все числовые значения на линиях

                // Правый DataSet (серый)
                LineDataSet rightDataSet = new LineDataSet(rightEntries, "Inactive");
                rightDataSet.setColor(Color.GRAY);
                rightDataSet.setLineWidth(2f);
                rightDataSet.setDrawCircles(false);
                rightDataSet.setDrawValues(false); // Отключает все числовые значения на линиях


                // Обновляем данные графика
                LineData newData = new LineData(leftDataSet, rightDataSet);
                lineChart.setData(newData);
                // Отключаем перетаскивание (drag)
                lineChart.setDragEnabled(false);

                // Отключаем масштабирование (zoom)
                lineChart.setScaleEnabled(false);
                lineChart.setPinchZoom(false); // Двойное масштабирование щипком

                // Можно отключить отдельно по осям:
                lineChart.setDragXEnabled(false);
                lineChart.setDragYEnabled(false);
                lineChart.invalidate();

                // обновляем данные цены над графиком
                TextView section2PriceTextView = view.findViewById(R.id.section2Price);
                Entry selectedEntry = entries.get(splitIndex);
                float selectedPrice = selectedEntry.getY();
                String formattedPrice = String.format("%,.0f ₽", selectedPrice)
                        .replace(',', ' ');
                section2PriceTextView.setText(formattedPrice);

                // обновляем данные изменения цены над графиком
                TextView section2PriceChangeTextView = view.findViewById(R.id.section2PriceChange);
                Entry firstEntry = entries.get(0);
                float firstPrice = firstEntry.getY();
                float priceDelta = selectedPrice - firstPrice;
                float percentDelta = priceDelta / firstPrice * 100;
                String trendSymbol = "+";
                if (priceDelta < 0) {
                    priceDelta *= -1;
                    percentDelta *= -1;
                    section2PriceChangeTextView.setBackgroundResource(R.drawable.red_background);
                    section2PriceChangeTextView.setTextColor(Color.parseColor("#DB3B3B"));
                    trendSymbol = "-";
                } else {
                    section2PriceChangeTextView.setBackgroundResource(R.drawable.green_background);
                    section2PriceChangeTextView.setTextColor(Color.parseColor("#59C736"));
                }
                String formattedPriceDelta = String.format("%,.0f ₽", priceDelta)
                        .replace(',', ' ');
                String formattedPercentDelta = String.format("%.1f%%", percentDelta)
                        .replace('.', ',');;
                String combinedDelta = String.format("%s %s ( %s )",
                        trendSymbol, formattedPercentDelta, formattedPriceDelta);
                section2PriceChangeTextView.setText(combinedDelta);

                // обновляем дату над графиком
                TextView section2PriceDateTextView = view.findViewById(R.id.section2PriceDate);
                String selectedDate = (String)selectedEntry.getData();
                section2PriceDateTextView.setText(selectedDate);

                // обновление данных над графиком
                updateDataAboveLineChart(splitIndex, entries);

            }


            @Override
            public void onNothingSelected() {
                // Возвращаем исходный график при отмене выбора
                LineDataSet fullDataSet = new LineDataSet(entries, "Active");
                fixLineDataSetFormatting(fullDataSet);
                lineChart.setData(new LineData(fullDataSet));
                lineChart.getXAxis().removeAllLimitLines(); // Удаляем линии разделения
                lineChart.invalidate();
            }
        });
    }

    public void updateDataAboveLineChart(int lastIndex, List<Entry> entries) {
        // обновляем данные цены над графиком
        TextView section2PriceTextView = view.findViewById(R.id.section2Price);
        Entry lastEntry = entries.get(lastIndex);
        float lastPrice = lastEntry.getY();
        String formattedPrice = String.format("%,.0f ₽", lastPrice)
                .replace(',', ' ');
        section2PriceTextView.setText(formattedPrice);

        // обновляем данные изменения цены над графиком
        TextView section2PriceChangeTextView = view.findViewById(R.id.section2PriceChange);
        Entry firstEntry = entries.get(0);
        float firstPrice = firstEntry.getY();
        float priceDelta = lastPrice - firstPrice;
        float percentDelta = priceDelta / firstPrice * 100;
        String trendSymbol = "+";
        if (priceDelta < 0) {
            priceDelta *= -1;
            percentDelta *= -1;
            section2PriceChangeTextView.setBackgroundResource(R.drawable.red_background);
            section2PriceChangeTextView.setTextColor(Color.parseColor("#DB3B3B"));
            trendSymbol = "-";
        } else {
            section2PriceChangeTextView.setBackgroundResource(R.drawable.green_background);
            section2PriceChangeTextView.setTextColor(Color.parseColor("#59C736"));
        }
        String formattedPriceDelta = String.format("%,.0f ₽", priceDelta)
                .replace(',', ' ');
        String formattedPercentDelta = String.format("%.1f%%", percentDelta)
                .replace('.', ',');;
        String combinedDelta = String.format("%s %s ( %s )",
                trendSymbol, formattedPercentDelta, formattedPriceDelta);
        section2PriceChangeTextView.setText(combinedDelta);

        // обновляем дату над графиком
        TextView section2PriceDateTextView = view.findViewById(R.id.section2PriceDate);
        String selectedDate = (String)lastEntry.getData();
        section2PriceDateTextView.setText(selectedDate);
    }

    public void fixLineDataSetFormatting(LineDataSet lineDataSet) {
        lineDataSet.setColor(Color.parseColor("#7D7AFF"));
        lineDataSet.setValueTextColor(Color.BLACK);
        lineDataSet.setDrawCircles(false); // Отключает кружки
        lineDataSet.setDrawValues(false);  // Дополнительно отключает подписи значений
        lineDataSet.setLineWidth(2f);
    }
    private void updateLineChart(LineChart lineChart, List<Entry> entries,
                                 boolean animateChartUpdate) {
        LineDataSet newDataSet = new LineDataSet(entries, "Новые данные");
        // настройка нужного стиля (цвет и тд)
        fixLineDataSetFormatting(newDataSet);

        // Получаем текущие данные
        LineData lineData = lineChart.getData();
        // Заменяем старый набор данных
        lineData.clearValues(); // Удаляем старый(е) набор(ы)
        lineData.addDataSet(newDataSet); // Добавляем новый
        if (animateChartUpdate) {
            // Обновляем график
            isAnimationRunning = true;
            lineChart.animateX(1000);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                isAnimationRunning = false;
            }, 1000); // Match duration of animation
        }

        // обновление разделения графика при нажатии (entries у разных интервалов отличаются)
        updateLineChartSelectedListener(lineChart, entries);

        // обновление данных над графиком
        updateDataAboveLineChart(entries.size() - 1, entries);

        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }

    public class ChartItem {
        private String label;  // Название категории
        private float value;  // Числовое значение
        private int color;    // Цвет

        // Конструктор
        public ChartItem(String label, float value, int color) {
            this.label = label;
            this.value = value;
            this.color = color;
        }

        // Геттеры и сеттеры
        public String getLabel() {
            return label;
        }

        public float getValue() {
            return value;
        }

        public int getColor() {
            return color;
        }
    }
    // TODO: 26.05.2025 не излишен не этот POJO, можно ли использовать просто Entry?



}