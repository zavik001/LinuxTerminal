package com.example;

import java.util.ArrayList;
import java.util.List;

public class CommandHistory {
    private List<String> history;
    private int historyIndex;

    public CommandHistory() {
        history = new ArrayList<>();
        historyIndex = 0;  
    }

    public void add(String command) {
        history.add(command);
        historyIndex = history.size();  
    }

    public String getPreviousCommand() {
        if (history.isEmpty()) {
            return "";
        }
        historyIndex = Math.max(0, historyIndex - 1);
        return history.get(historyIndex);
    }

    public String getNextCommand() {
        if (history.isEmpty()) {
            return "";
        }
        historyIndex = Math.min(history.size() - 1, historyIndex + 1);
        return history.get(historyIndex);
    }

    public List<String> getHistory() {
        return history;
    }
}
