package com.example.finsmart.main_activity.profile_page;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.finsmart.R;
import com.example.finsmart.main_activity.MainActivity;
import com.example.finsmart.main_activity.home_page.HomePageFragment;

public class ToolbarUtils {

    // Для использования из Fragment
    public static void setToolbar(Fragment fragment, String tabName) {
        Activity activity = fragment.requireActivity();
        setToolbar(activity, activity.getLayoutInflater(), tabName);
    }

    // Для использования из Activity
    public static void setToolbar(Activity activity, LayoutInflater inflater, String tabName) {
        if (activity instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) activity;
            LinearLayout toolbarLinearLayout = mainActivity.toolbarLinearLayout;
            View gradientDelimeter = mainActivity.findViewById(R.id.gradientDelimeter);

            toolbarLinearLayout.setVisibility(LinearLayout.VISIBLE);
            gradientDelimeter.setVisibility(View.VISIBLE);

            // Скрываем логотип
            ImageView logoImageView = toolbarLinearLayout.findViewById(R.id.logo);
            logoImageView.setVisibility(View.GONE);

            // Вставляем кастомный toolbar
            View toolbarNavBack = inflater.inflate(R.layout.toolbar_nav_back, toolbarLinearLayout, false);
            TextView toolbarNavBackTab = toolbarNavBack.findViewById(R.id.tab);
            toolbarNavBackTab.setText(tabName);

            toolbarLinearLayout.addView(toolbarNavBack);

            ImageView arrowBack = toolbarNavBack.findViewById(R.id.arrowBack);
            arrowBack.setOnClickListener(v -> {
                // Возвращаем toolbar к исходному
                toolbarLinearLayout.removeViewAt(1);
                logoImageView.setVisibility(View.VISIBLE);

                // Открываем HomePageFragment (если вызвано из фрагмента)
                if (activity instanceof MainActivity) {
                    ((MainActivity) activity).getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, new HomePageFragment())
                            .commit();
                }
            });
        }
    }
}
