package com.example;

import java.util.ArrayList;

public class Direction {
    private String commandName;
    private ArrayList<String> args;

    public boolean parse(String input) {
        String[] parts = input.split(" ");
        if (parts.length == 0) {
            return false;
        }
        commandName = parts[0];
        args = new ArrayList<>();
        for (int i = 1; i < parts.length; i++) {
            args.add(parts[i]);
        }
        return true;
    }

    public String getCommandName() {
        return commandName;
    }

    public ArrayList<String> getArgs() {
        return args;
    }
}
