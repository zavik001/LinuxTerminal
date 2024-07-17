## Структура проекта
```
    :___
        │   .gitignore
        │   README.md
        │
        └───demo
            │   pom.xml
            │
            └───src
                ├───main
                │   ├───java
                │   │   └───com
                │   │       └───example
                │   │               Main.java
                │   │               Parser.java
                │   │               Terminal.java
                │   │               TerminalUI.java
                │   │
                │   └───resources
                │       └───META-INF
                │               MANIFEST.MF
                │
                └───test
                    └───java
                        └───com
                            └───example
                                    AppTest.java
```


## Описание файлов и директорий

- `.gitignore`: Файл, указывающий Git, какие файлы и папки игнорировать при коммите.
- `pom.xml`: Файл конфигурации Maven для управления зависимостями и сборки проекта.
- `src/main/java/com/example`: Здесь находятся исходные файлы Java.
  - `Main.java`: Главный класс приложения.
  - `Parser.java`: Класс для обработки данных.
  - `Terminal.java`: Класс для работы с командной строкой.
  - `TerminalUI.java`: Класс для пользовательского интерфейса.
- `src/main/resources/META-INF/MANIFEST.MF`: Файл манифеста для JAR-файла.
- `src/test/java/com/example`: Здесь находятся тестовые файлы 

## Запуск проекта

1. Убедитесь, что у вас установлен Java и Maven.
2. Склонируйте репозиторий.
3. Соберите проект с помощью команды `mvn clean install`.
4. Запустите приложение, например, с помощью `java -cp target/MyDemoProject.jar com.example.Main`.