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
                │   │               CommandHistory.java
                │   │               Direction.java
                │   │               Terminal.java
                │   │               TerminalUI.java
                │   │
                │   └───resources
                │       ├───materials
                │       │       linux.png
                │       │
                │       └───META-INF
                │               MANIFEST.MF
                │
                └───test
                    └───java
                        └───com
                            └───example
                                    TerminalUI.java
```

## Запуск проекта

1. Убедитесь, что у вас установлен Java и Maven.
2. Склонируйте репозиторий.
3. Соберите проект с помощью команды `mvn clean install`.
4. Запустите приложение, например, с помощью `java -cp .\target\demo-1.0.jar com.example.TerminalUI`.