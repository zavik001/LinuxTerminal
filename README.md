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

## Запуск проекта

1. Убедитесь, что у вас установлен Java и Maven.
2. Склонируйте репозиторий.
3. Соберите проект с помощью команды `mvn clean install`.
4. Запустите приложение, например, с помощью `java -cp target/MyDemoProject.jar com.example.TerminalUI`.