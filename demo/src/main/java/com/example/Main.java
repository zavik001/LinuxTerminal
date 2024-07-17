package com.example;

import java.util.*;

public class Main {
    public static void main(String[] mainArgs) {
        Terminal terminal = new Terminal(); // Create an instance of the Terminal class.
        Scanner scanner = new Scanner(System.in); // Create a scanner to read user input.

        // Continue reading and processing user input until the user types 'exit'.
        while (true) {
            System.out.print(">"); // Display the command prompt.
            String userInput = scanner.nextLine(); // Read the user's input.
            if (userInput.equals("exit")) {
                break; // Exit the loop and terminate the program when the user types 'exit'.
            }

            Parser parser = new Parser(); // Create an instance of the Parser class to parse the user input.
            if (parser.parse(userInput)) {
                String command = parser.getCommandName(); // Get the main command from the parsed input.
                ArrayList<String> args = parser.getArgs(); // Get a list of additional arguments.
                terminal.chooseCommandAction(command, args); // Call the Terminal to execute the chosen command.
            } else {
                System.out.println("Invalid command. Type 'help' for available commands.");
                // Display an error message when the user enters an invalid or incomplete
                // command.
            }
        }
    }
}
