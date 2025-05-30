package com.example.finsmart;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {
    public LinearLayout toolbarLinearLayout;

    // Ресурсы для активных и неактивных состояний
    private static final int[] ACTIVE_ICONS = {
            R.drawable.home_icon_active,
            R.drawable.budget_icon_active,
            R.drawable.operations_icon_active,
            R.drawable.profile_icon_active
    };

    private static final int[] INACTIVE_ICONS = {
            R.drawable.home_icon_inactive,
            R.drawable.budget_icon_inactive,
            R.drawable.operations_icon_inactive,
            R.drawable.profile_icon_inactive
    };

    // Элементы интерфейса
    private ImageView homeIcon, budgetIcon, operationsIcon, profileIcon;
    private TextView homeIconText, budgetIconText, operationsIconText, profileIconText;
    private LinearLayout homeLayout, budgetLayout, operationsLayout, profileLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        // Инициализация элементов интерфейса
        initUI();

        // Установка начального фрагмента (например, HomeFragment)
        loadFragment(new HomePageFragment(), 0); // Загружаем первый фрагмент и выделяем первую иконку
    }

    /**
     * Инициализация UI-элементов
     */
    private void initUI() {
        // toolbar
        toolbarLinearLayout = findViewById(R.id.toolbar);

        // Иконки
        homeIcon = findViewById(R.id.home_icon);
        budgetIcon = findViewById(R.id.budget_icon);
        operationsIcon = findViewById(R.id.operations_icon);
        profileIcon = findViewById(R.id.profile_icon);

        // Надписи иконок
        homeIconText = findViewById(R.id.homeIconTextView);
        budgetIconText = findViewById(R.id.budgetIconTextView);
        operationsIconText = findViewById(R.id.operationsIconTextView);
        profileIconText = findViewById(R.id.profileIconTextView);


        // Лэйауты для каждой категории
        homeLayout = findViewById(R.id.home_layout);
        budgetLayout = findViewById(R.id.budget_layout);
        operationsLayout = findViewById(R.id.operations_layout);
        profileLayout = findViewById(R.id.profile_layout);

        // Назначение слушателей кликов
        homeLayout.setOnClickListener(v -> onMenuItemClicked(0));
        budgetLayout.setOnClickListener(v -> onMenuItemClicked(1));
        operationsLayout.setOnClickListener(v -> onMenuItemClicked(2));
        profileLayout.setOnClickListener(v -> onMenuItemClicked(3));
    }

    /**
     * Обработка клика на элемент меню
     *
     * @param position Позиция выбранного элемента меню (0 - Главная, 1 - Бюджет, 2 - Операции, 3 - Профиль)
     */
    private void onMenuItemClicked(int position) {
        // Загрузка соответствующего фрагмента
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new HomePageFragment();
                break;
            case 1:
                fragment = new BudgetPageFragment();
                break;
            case 2:
                fragment = new OperationsPageFragment();
                break;
            case 3:
                fragment = new ProfilePageFragment();
                break;
        }

        // Загружаем фрагмент и выделяем активную иконку
        loadFragment(fragment, position);
    }

    /**
     * Загружает фрагмент в контейнер и выделяет активную иконку
     *
     * @param fragment Фрагмент для загрузки
     * @param position Позиция активной иконки
     */
    private void loadFragment(Fragment fragment, int position) {
        if (fragment == null) return;

        // Получаем менеджер фрагментов
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Заменяем текущий фрагмент
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();

        // Сбрасываем все иконки на неактивные
        resetIcons();

        // Выделяем активную иконку
        setActiveIcon(position);
    }

    /**
     * Сбрасывает все иконки на неактивные
     */
    private void resetIcons() {
        // иконки
        homeIcon.setImageResource(INACTIVE_ICONS[0]);
        budgetIcon.setImageResource(INACTIVE_ICONS[1]);
        operationsIcon.setImageResource(INACTIVE_ICONS[2]);
        profileIcon.setImageResource(INACTIVE_ICONS[3]);

        // напдиси иконок
        homeIconText.setTextColor(Color.parseColor("#4D4D4D"));
        budgetIconText.setTextColor(Color.parseColor("#4D4D4D"));
        operationsIconText.setTextColor(Color.parseColor("#4D4D4D"));
        profileIconText.setTextColor(Color.parseColor("#4D4D4D"));
    }

    /**
     * Устанавливает активную иконку по позиции
     *
     * @param position Позиция активной иконки
     */
    private void setActiveIcon(int position) {
        switch (position) {
            case 0:
                homeIcon.setImageResource(ACTIVE_ICONS[0]);
                homeIconText.setTextColor(Color.parseColor("#7D79FF"));
                break;
            case 1:
                budgetIcon.setImageResource(ACTIVE_ICONS[1]);
                budgetIconText.setTextColor(Color.parseColor("#7D79FF"));
                break;
            case 2:
                operationsIcon.setImageResource(ACTIVE_ICONS[2]);
                operationsIconText.setTextColor(Color.parseColor("#7D79FF"));
                break;
            case 3:
                profileIcon.setImageResource(ACTIVE_ICONS[3]);
                profileIconText.setTextColor(Color.parseColor("#7D79FF"));
                break;
        }
    }
}