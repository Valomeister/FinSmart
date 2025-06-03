# Finsmart

Finsmart - это мобильное приложение для учета личных финансов, помогающее контролировать активы, управлять бюджетом и анализировать расходы.

## 📱 Основные функции
- **💰 Учет активов** (счета, наличные, инвестиции)
- **📊 Бюджетирование** с лимитами по категориям
- **📈 Визуализация** финансовых данных (графики/диаграммы)
- **🔍 Анализ** расходов по категориям и периодам
- **⏱️ История операций** с фильтрацией

## 🛠 Технологический стек
| Компонент       | Технология       |
|----------------|-----------------|
| Язык           | Java            |
| База данных    | Room 2.6.1      |
| Графика        | MPAndroidChart  |
| Архитектура    | MVC             |
| UI             | Material Design |

## 📥 Установка
1. Клонировать репозиторий:
```bash
git clone https://github.com/Valomeister/Finsmart
```
2. Импортировать проект в Android Studio
3. Запустить на эмуляторе (API 26+) или устройстве

## 🧩 Зависимости
```gradle
dependencies {
    // Базовые
    implementation(libs.appcompat)
    implementation(libs.material)
    
    // Графика
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    
    // База данных
    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")
    
    // Java 8+ поддержка
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.3")
}
```