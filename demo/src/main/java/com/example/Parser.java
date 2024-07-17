package com.example;

import java.util.*;

public class Parser {
    private String commandName; // Stores the main command extracted from the user input.
    private ArrayList<String> args = new ArrayList<String>(); // Stores additional arguments provided with the command.

    /**
     * Parses the user input to separate the main command and its arguments.
     *
     * @param input The user input to be parsed.
     * @return true if parsing is successful, false if the input is invalid.
     */
    public boolean parse(String input) {
        String[] words = input.split("\\s"); // Split the input into words using space as a separator.
        if (words.length > 0) {
            commandName = words[0]; // The first word is the command name.
            for (int i = 1; i < words.length; i++) {
                args.add(words[i]); // The remaining words are arguments.
            }

            return true; // Parsing successful.
        }
        return false; // Invalid input with no command.
    }

    /**
     * Get the main command extracted from the parsed input.
     *
     * @return The main command name.
     */
    public String getCommandName() {
        return commandName;
    }

    /**
     * Get the additional arguments provided with the command.
     *
     * @return A list of arguments.
     */
    public ArrayList<String> getArgs() {
        return args;
    }
}
