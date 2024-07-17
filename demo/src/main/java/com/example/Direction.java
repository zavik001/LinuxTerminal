package com.example;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class Direction {
    private String commandName;
    private ArrayList<String> args;

    public boolean parse(String input) {
        StringTokenizer tokenizer = new StringTokenizer(input);
        if (tokenizer.hasMoreTokens()) {
            commandName = tokenizer.nextToken();
            args = new ArrayList<>();
            while (tokenizer.hasMoreTokens()) {
                args.add(tokenizer.nextToken());
            }
            return true;
        }
        return false;
    }

    public String getCommandName() {
        return commandName;
    }

    public ArrayList<String> getArgs() {
        return args;
    }
}
