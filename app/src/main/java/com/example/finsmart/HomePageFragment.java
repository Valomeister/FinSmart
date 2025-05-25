package com.example.finsmart;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomePageFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);



        // Создаем список данных для диаграммы
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(25f, "Вклады"));
        entries.add(new PieEntry(35f, "Фонды"));
        entries.add(new PieEntry(40f, "Акции"));
        entries.add(new PieEntry(27f, "Валюта"));
        entries.add(new PieEntry(30f, "Крипта"));

        // Создаем массив с собственными цветами (в формате ARGB)
        int[] myColors = {
                Color.rgb(125, 122, 255),  //
                Color.rgb(164, 123, 255), //
                Color.rgb(189, 160, 255),  //
                Color.rgb(203, 180, 255),  //
                Color.rgb(214, 200, 246)    //
        };

        // 1. Получаем данные из БД (пример)
        List<ChartItem> chartData = getChartDataFromDatabase();

        // 2. Настраиваем диаграмму
        PieChart pieChart = view.findViewById(R.id.pieChart);
        setupPieChart(pieChart, chartData);

        // 3. Создаем кастомную легенду
        LinearLayout legendContainer = view.findViewById(R.id.legend_container);
        createCustomLegend(legendContainer, chartData);

        return view;
    }

    private List<ChartItem> getChartDataFromDatabase() {
        // временный метод-пустышка - возвращает готовые данные, а не берет их из реальной БД
        List<ChartItem> chartItems = new ArrayList<>();

        chartItems.add(new ChartItem("Вклады", 33f, Color.rgb(125, 122, 255)));
        chartItems.add(new ChartItem("Фонды", 27f, Color.rgb(164, 123, 255)));
        chartItems.add(new ChartItem("Акции", 20f, Color.rgb(189, 160, 255)));
        chartItems.add(new ChartItem("Валюта", 45f, Color.rgb(203, 180, 255)));
        chartItems.add(new ChartItem("Крипта", 37f, Color.rgb(214, 200, 246)));

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

        // Создаем горизонтальный контейнер для первого ряда
        LinearLayout currentRow = createNewRow(container.getContext());
        container.addView(currentRow);

        int itemsInRow = 0;
        int maxItemsPerRow = 2; // Максимум элементов в строке

        for (int i = 0; i < data.size(); i++) {
            ChartItem item = data.get(i);

            // Если текущая строка заполнена, создаем новую
            if (itemsInRow >= maxItemsPerRow) {
                currentRow = createNewRow(container.getContext());
                container.addView(currentRow);
                itemsInRow = 0;
            }

            // Создаем элемент легенды
            View legendItem = createLegendItem(container.getContext(),
                    item.getLabel(),
                    data.get(i).getColor());


            currentRow.addView(legendItem);
            itemsInRow++;
        }
    }

    private LinearLayout createNewRow(Context context) {
        LinearLayout row = new LinearLayout(context);
        row.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setWeightSum(2f);
        row.setPadding(0, 8, 0, 8);
        return row;
    }

    private View createLegendItem(Context context, String label, int color) {
        // Создаем элемент через LayoutInflater (лучший способ)
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_legend, null);

        TextView labelView = itemView.findViewById(R.id.legend_label);

//        labelView.setBackgroundColor(color);
        labelView.setText(label);


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
        params.setMargins(8, 0, 8, 0); // Горизонтальные отступы

        itemView.setLayoutParams(params);
        return itemView;
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

}