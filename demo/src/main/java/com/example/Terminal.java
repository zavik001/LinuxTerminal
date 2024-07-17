package com.example;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Terminal {
    private String currentDirectory = System.getProperty("user.dir");

    private JTextPane textPane;

    public void setOutputArea(JTextPane textPane) {
        this.textPane = textPane;
    }

    private void printToOutput(String message) {
        if (textPane != null) {
            try {
                Document doc = textPane.getDocument();
                doc.insertString(doc.getLength(), message + "\n", null);
                textPane.setCaretPosition(doc.getLength());
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println(message);
        }
    }

    public void chooseCommandAction(String command, ArrayList<String> args) {
        switch (command) {
            case "echo":
                echo(args);
                break;

            case "pwd":
                pwd();
                break;

            case "cd":
                cd(args);
                break;

            case "touch":
                touch(args.get(0));
                break;

            case "mkdir":
                if (args.size() > 0) {
                    mkdir(args);
                    break;
                } else {
                    printToOutput("Please Enter an argument/arguments");
                    break;
                }

            case "rmdir":
                rmdir(args.get(0));
                break;

            case "help":
                help();
                break;

            case "ls":
                ls(args);
                break;

            case "wc":
                wc(args);
                break;

            case "cp":
                cp(args);
                break;

            case "cp-r":
                if (args.size() != 2) {
                    printToOutput("Usage: cp-r source_directory destination_directory");
                } else {
                    //copyDirectory(args);
                }
                break;

            case "rm":
                rm(args);
                break;

            case "cat":
                cat(args);
                break;

            default:
                printToOutput("Command not recognized. Type 'help' for available commands.");
        }
    }

    public void echo(ArrayList<String> args) {
        String output = String.join(" ", args);
        printToOutput(output);
    }

    public void pwd() {
        printToOutput(currentDirectory);
    }

    public void cd(ArrayList<String> args) {
        if (args.size() != 1) {
            printToOutput("Usage: cd <directory>");
            return;
        }
        Path path = Paths.get(currentDirectory, args.get(0)).normalize();
        if (Files.isDirectory(path)) {
            currentDirectory = path.toString();
            printToOutput("Directory changed to " + currentDirectory);
        } else {
            printToOutput("No such directory: " + path);
        }
    }

    public boolean touch(String arg) {
        try {
            Paths.get(arg);
        } catch (InvalidPathException e) {
            printToOutput("Wrong path");
            return false;
        }
        Path path = Paths.get(arg);
        path = path.toAbsolutePath();

        File new_file = new File(path.toString());
        try {
            if (new_file.createNewFile()) {
                printToOutput("File created: " + new_file.getName());
            } else {
                printToOutput("File already exists");
            }
        } catch (IOException e) {
            printToOutput("Error!");
            e.printStackTrace();
        }

        return true;
    }

    public void mkdir(ArrayList<String> args) {
        for (String arg : args) {
            Path path = Paths.get(currentDirectory, arg).normalize();
            File file = new File(path.toString());
            if (!file.exists()) {
                file.mkdirs();
                printToOutput("Directory created: " + file.getPath());
            } else {
                printToOutput("Directory already exists");
            }
        }
    }

    public void rmdir(String arg) {
        if (arg.equals("*")) {
            File file = new File(currentDirectory);
            for (File subFile : file.listFiles()) {
                if (subFile.isDirectory() && subFile.length() == 0) {
                    subFile.delete();
                }
            }
        } else {
            try {
                Paths.get(arg);
            } catch (InvalidPathException e) {
                printToOutput("No such file or directory");
                return;
            }

            Path path = Paths.get(arg);
            path = path.toAbsolutePath();

            File file = new File(path.toString());
            if (file.length() == 0 && file.isDirectory()) {
                file.delete();
            }
        }
    }

    public void help() {
        String help = "Available commands:\n" +
                "echo [args] - Prints the arguments to the terminal\n" +
                "pwd - Prints the current working directory\n" +
                "cd [directory] - Changes the current working directory\n" +
                "touch [file] - Creates a new file\n" +
                "mkdir [directory] - Creates a new directory\n" +
                "rmdir [directory] - Removes a directory\n" +
                "help - Prints this help message\n" +
                "ls - Lists files and directories\n" +
                "wc [file] - Counts lines, words, and characters in a file\n" +
                "cp [source] [destination] - Copies a file\n" +
                "cp-r [source_directory] [destination_directory] - Copies a directory recursively\n" +
                "rm [file] - Removes a file\n" +
                "cat [file] - Displays the contents of a file\n";
        printToOutput(help);
    }

    public void ls(ArrayList<String> args) {
        boolean showAll = args.contains("-a");
        boolean showLong = args.contains("-l");

        File dir = new File(currentDirectory);
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (!showAll && file.isHidden()) {
                    continue;
                }
                if (showLong) {
                    printToOutput(file.getAbsolutePath());
                } else {
                    printToOutput(file.getName());
                }
            }
        }
    }

    public void wc(ArrayList<String> args) {
        if (args.size() != 1) {
            printToOutput("Usage: wc [file]");
            return;
        }

        File file = new File(currentDirectory, args.get(0));
        if (!file.exists()) {
            printToOutput("File not found: " + file.getPath());
            return;
        }

        try (Scanner scanner = new Scanner(file)) {
            int lines = 0, words = 0, chars = 0;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                lines++;
                words += line.split("\\s+").length;
                chars += line.length();
            }
            printToOutput("Lines: " + lines);
            printToOutput("Words: " + words);
            printToOutput("Characters: " + chars);
        } catch (IOException e) {
            printToOutput("Error reading file: " + file.getPath());
            e.printStackTrace();
        }
    }

    public void cp(ArrayList<String> args) {
        if (args.size() != 2) {
            printToOutput("Usage: cp [source] [destination]");
            return;
        }

        Path source = Paths.get(currentDirectory, args.get(0)).normalize();
        Path destination = Paths.get(currentDirectory, args.get(1)).normalize();

        try {
            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
            printToOutput("File copied from " + source + " to " + destination);
        } catch (IOException e) {
            printToOutput("Error copying file: " + e.getMessage());
        }
    }

    /*public void copyDirectory(ArrayList<String> args) {
        Path sourceDirectory = Paths.get(currentDirectory, args.get(0)).normalize();
        Path destinationDirectory = Paths.get(currentDirectory, args.get(1)).normalize();

        try {
            Files.walk(sourceDirectory).forEach(source -> {
                Path destination = destinationDirectory.resolve(sourceDirectory.relativize(source));
                try {
                    Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    printToOutput("Error copying directory: " + e.getMessage());
                }
            });
            printToOutput("Directory copied from " + sourceDirectory + " to " + destinationDirectory);
        } catch (IOException e) {
            printToOutput("Error copying directory: " + e.getMessage());
        }
    }*/

    public void rm(ArrayList<String> args) {
        for (String arg : args) {
            Path path = Paths.get(currentDirectory, arg).normalize();
            try {
                Files.delete(path);
                printToOutput("File deleted: " + path);
            } catch (IOException e) {
                printToOutput("Error deleting file: " + e.getMessage());
            }
        }
    }

    public void cat(ArrayList<String> args) {
        if (args.size() != 1) {
            printToOutput("Usage: cat [file]");
            return;
        }

        File file = new File(currentDirectory, args.get(0));
        if (!file.exists()) {
            printToOutput("File not found: " + file.getPath());
            return;
        }

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                printToOutput(scanner.nextLine());
            }
        } catch (IOException e) {
            printToOutput("Error reading file: " + file.getPath());
            e.printStackTrace();
        }
    }
}