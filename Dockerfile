FROM gradle:8.6-jdk17 AS android-build

# Установим переменные окружения
ENV ANDROID_SDK_ROOT=/sdk \
    ANDROID_HOME=/sdk \
    PATH=$PATH:/sdk/cmdline-tools/latest/bin:/sdk/platform-tools

# Скачиваем Android Command Line Tools
RUN mkdir -p /sdk/cmdline-tools && \
    curl -o tools.zip https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip && \
    unzip tools.zip -d /sdk/cmdline-tools && \
    mv /sdk/cmdline-tools/cmdline-tools /sdk/cmdline-tools/latest && \
    rm tools.zip

# Устанавливаем необходимые компоненты Android SDK
RUN yes | sdkmanager --sdk_root=${ANDROID_SDK_ROOT} --licenses
RUN sdkmanager --sdk_root=${ANDROID_SDK_ROOT} \
    "platform-tools" \
    "platforms;android-34" \
    "build-tools;34.0.0"

# Копируем проект внутрь контейнера
WORKDIR /app
COPY . .

# Собираем проект (assembleDebug — можно заменить на assembleRelease)
RUN ./gradlew assembleDebug
