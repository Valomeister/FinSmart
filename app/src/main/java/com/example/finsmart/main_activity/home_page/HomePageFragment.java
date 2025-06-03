package com.example.finsmart.main_activity.home_page;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.finsmart.data.database.AppDatabase;
import com.example.finsmart.data.repository.AppRepository;
import com.example.finsmart.main_activity.FileUtils;
import com.example.finsmart.main_activity.home_page.crypto_page.CryptosFragment;
import com.example.finsmart.main_activity.home_page.currency_page.CurrenciesFragment;
import com.example.finsmart.main_activity.home_page.deposit_page.DepositsFragment;
import com.example.finsmart.main_activity.home_page.fund_page.FundsFragment;
import com.example.finsmart.R;
import com.example.finsmart.main_activity.home_page.stock_page.StocksFragment;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
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
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HomePageFragment extends Fragment {
    private List<Button> timeRangeButtons = new ArrayList<>();
    List<Entry> lineChartDataAllTime;
    LineDataSet fullDataSet;
    LineChart lineChart;
    private boolean isAnimationRunning = false;
    View view;

    private AppRepository repository;

    /**
     * Создает и возвращает иерархию View, связанную с фрагментом.
     *
     * @param inflater           Объект LayoutInflater, используемый для раздувания XML-макета.
     * @param container          Родительский View, к которому будет прикреплен фрагмент.
     * @param savedInstanceState Если фрагмент восстанавливается, содержит его предыдущие состояния.
     * @return Корневой View созданного макета фрагмента.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO: 28.05.2025 Сделать кликабельными фиол. надписи (за все вермя, в рублях и тд) 

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home_page, container, false);

        // переносим .csv с данными графика во внутренне хранилище устройства
        FileUtils.copyPortfolioDataIfNeeded(requireContext());

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



        // Получаем доступ к БД
        AppDatabase db = AppDatabase.getInstance(requireContext());

        // Создаём Repository
        repository = new AppRepository(
                db.userDao(),
                db.budgetDao(),
                db.categoryDao(),
                db.operationDao(),
                db
        );

        // Запускаем заполнение тестовыми данными
//        repository.populateWithTestData();


        return view;
    }

    /**
     * Обрабатывает нажатие на кнопку выбора временного диапазона.
     * Устанавливает выбранную кнопку активной, определяет количество дней,
     * соответствующее выбранному диапазону, и обновляет график с данными
     * за этот период.
     *
     * @param selectedButton Кнопка, соответствующая выбранному временному диапазону.
     */
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

    /**
     * Устанавливает переданную кнопку активной среди всех кнопок выбора временного диапазона.
     * Все остальные кнопки становятся неактивными.
     *
     * @param activeButton Кнопка, которую необходимо установить как активную.
     */
    private void setActiveButton(Button activeButton) {
        for (Button button : timeRangeButtons) {
            boolean isSelected = button == activeButton;
            button.setSelected(isSelected);

        }
    }

    /**
     * Временный заглушечный метод, возвращающий статически заданный список финансовых данных
     * для отображения на графике. В текущей реализации данные не извлекаются из реальной базы данных.
     *
     * @return Список объектов {@link ChartItem}, содержащих информацию об активах и их стоимости.
     */
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

    /**
     * Настраивает внешний вид круговой диаграммы и заполняет её данными.
     *
     * <p>Метод применяет стилизацию: отключает надписи у секторов, легенду и описание,
     * устанавливает прозрачность и внешний вид центрального текста. После этого
     * преобразует список {@link ChartItem} в формат, подходящий для {@link PieChart},
     * и отображает его.</p>
     *
     * @param pieChart Круговая диаграмма {@link PieChart}, которую нужно настроить.
     * @param data Список объектов {@link ChartItem}, содержащих значения, подписи и цвета секторов диаграммы.
     */
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

    /**
     * Создаёт пользовательскую легенду для круговой диаграммы и размещает её в виде сетки строк с указанным количеством элементов в каждой.
     *
     * <p>Метод очищает переданный контейнер и динамически добавляет элементы легенды — цветовые маркеры с подписями —
     * в горизонтальные ряды. При необходимости создаёт пустые невидимые элементы для выравнивания последнего ряда.</p>
     *
     * @param container Контейнер {@link LinearLayout}, в который будут добавлены ряды с элементами легенды.
     * @param data Список объектов {@link ChartItem}, содержащих подписи и цвета для легенды.
     */
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

    /**
     * Создаёт новую горизонтальную строку {@link LinearLayout} для размещения элементов легенды.
     *
     * <p>Строка имеет ширину MATCH_PARENT и высоту WRAP_CONTENT, горизонтальную ориентацию,
     * фиксированное количество элементов (weightSum) и вертикальные отступы по 8 пикселей сверху и снизу.</p>
     *
     * @param context Контекст приложения, необходимый для создания View.
     * @param maxItemsPerRow Максимальное количество элементов, которое будет размещено в строке (используется для weightSum).
     * @return Новый экземпляр {@link LinearLayout}, настроенный как горизонтальная строка.
     */
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

    /**
     * Создаёт элемент легенды для диаграммы с заданной меткой и цветом.
     *
     * <p>Элемент представляет собой View, созданный из layout `item_legend`,
     * с текстовой меткой и цветным фоном. В зависимости от начала текста метки
     * назначается обработчик клика, который заменяет текущий фрагмент на соответствующий
     * раздел приложения (вклады, фонды, акции, валюта, крипта).</p>
     *
     * <p>Фон метки выполнен в виде прямоугольника с закруглёнными углами и заданным цветом.
     * Элемент получает параметры ширины с весом для равномерного распределения в контейнере.</p>
     *
     * @param context Контекст, необходимый для создания View и доступа к ресурсам.
     * @param label Текст метки элемента легенды (например, "Вклады 521 693 ₽").
     * @param color Цвет фона метки в формате ARGB.
     * @return View, представляющий элемент легенды с заданной меткой, цветом и обработчиком клика.
     */
    private View createLegendItem(Context context, String label, int color) {
        // Создаем элемент через LayoutInflater (лучший способ)
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_legend, null);
        // TODO: 29.05.2025 улучшить логику запуска фрагментов (компактнее сделать) 
        if (label.startsWith("Вклады")) {
            // Задаем обработчик клика на весь itemView
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, new DepositsFragment())
                            .commit();
                }
            });
        } else if (label.startsWith("Фонды")) {
            // Задаем обработчик клика на весь itemView
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, new FundsFragment())
                            .commit();
                }
            });
        } else if (label.startsWith("Акции")) {
            // Задаем обработчик клика на весь itemView
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, new StocksFragment())
                            .commit();
                }
            });

        } else if (label.startsWith("Валюта")) {
            // TODO: 29.05.2025  переименовать "Валюта" в "Валюты"?
            // Задаем обработчик клика на весь itemView
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, new CurrenciesFragment())
                            .commit();
                }
            });
        } else if (label.startsWith("Крипта")) {
            // Задаем обработчик клика на весь itemView
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, new CryptosFragment())
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

    /**
     * Загружает данные из CSV-файла "portfolio_data.csv", расположенного во внутреннем хранилище приложения.
     *
     * <p>Файл должен содержать заголовок в первой строке и данные с двумя колонками: дата и значение.
     * Метод читает не более 12313 строк данных (без учёта заголовка).
     * Каждая строка преобразуется в объект {@link Entry}, где индекс позиции соответствует порядку строки,
     * значение — из второй колонки, а дата — из первой колонки.</p>
     *
     * @return Список объектов {@link Entry}, загруженных из CSV-файла.
     */
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
            Log.d("tmp", "didnt read csv or whatever");

        }

        return entries;
    }

    /**
     * Настраивает и отображает линейный график с переданными данными.
     *
     * <p>Создаёт набор данных на основе списка {@link Entry}, форматирует его,
     * конфигурирует внешний вид графика, осей и их форматирование.
     * Устанавливает слушатель для взаимодействия с графиком и запускает анимацию.</p>
     *
     * @param lineChart   объект {@link LineChart}, который необходимо настроить и отобразить
     * @param entries     список данных {@link Entry} для построения графика, где
     *                    каждый элемент содержит координаты и дату
     */
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

    /**
     * Устанавливает слушатель выбора значения на {@link LineChart}.
     * <p>
     * При выборе точки графика:
     * <ul>
     *     <li>График разбивается на две части — активную (цветную) и неактивную (серую) от выбранной точки.</li>
     *     <li>Обновляются отображаемые над графиком данные: цена, изменение цены в ₽ и % и дата.</li>
     *     <li>Отключается перетаскивание и масштабирование графика для удобства просмотра выбранного среза.</li>
     * </ul>
     * При снятии выбора точек график возвращается в исходное состояние.
     * </p>
     *
     * @param lineChart график {@link LineChart}, к которому устанавливается слушатель.
     * @param entries   полный список данных {@link Entry}, отображаемых на графике.
     */
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

    /**
     * Обновляет отображаемые над графиком данные о цене, изменении цены и дате на основе выбранной точки.
     *
     * @param lastIndex индекс последней выбранной точки в списке {@link Entry}.
     * @param entries   список всех точек данных графика.
     */
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

    /**
     * Применяет стандартное форматирование к переданному {@link LineDataSet}.
     * Устанавливает цвет линии, цвет текста значений, отключает кружки и подписи значений,
     * а также задаёт толщину линии.
     *
     * @param lineDataSet объект {@link LineDataSet}, к которому будет применено форматирование.
     */
    public void fixLineDataSetFormatting(LineDataSet lineDataSet) {
        lineDataSet.setColor(Color.parseColor("#7D7AFF"));
        lineDataSet.setValueTextColor(Color.BLACK);
        lineDataSet.setDrawCircles(false); // Отключает кружки
        lineDataSet.setDrawValues(false);  // Дополнительно отключает подписи значений
        lineDataSet.setLineWidth(2f);
    }

    /**
     * Обновляет данные и оформление LineChart новыми значениями.
     * Заменяет текущий набор данных на новый, применяет стилизацию,
     * при необходимости выполняет анимацию обновления и обновляет UI-компоненты,
     * связанные с графиком (слушатели выбора и данные над графиком).
     *
     * @param lineChart       экземпляр LineChart для обновления.
     * @param entries         список новых точек данных {@link Entry} для отображения.
     * @param animateChartUpdate если true, выполняется анимация обновления графика.
     */
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

    /**
     * Класс представляет элемент диаграммы с меткой, значением и цветом.
     * Используется для передачи данных в диаграмму (например, PieChart).
     */
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