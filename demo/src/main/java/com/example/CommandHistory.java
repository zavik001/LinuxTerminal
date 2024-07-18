package com.example;

import java.util.ArrayList;
import java.util.List;

public class CommandHistory {
    private final List<String> history;
    private int index;

    public CommandHistory() {
        history = new ArrayList<>();
        index = 0;
    }

    public void add(String command) {
        history.add(command);
        index = history.size();
    }

    public String getPreviousCommand() {
        if (index > 0) {
            index--;
        }
        return history.get(index);
    }

    public String getNextCommand() {
        if (index < history.size() - 1) {
            index++;
        }
        return history.get(index);
    }
}
