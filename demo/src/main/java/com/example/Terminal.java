package com.example;

import javax.swing.JTextArea;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

public class Terminal {
    private String currentDirectory = System.getProperty("user.dir");

    private JTextArea textArea;

    public void setOutputArea(JTextArea textArea) {
        this.textArea = textArea;
    }

    private void printToOutput(String message) {
        if (textArea != null) {
            textArea.append(message + "\n");
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
                    copyDirectory(args);
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
        String mystr = currentDirectory.toString();
        for (String arg : args) {
            mystr += "\\" + arg;
        }
        File file = new File(mystr);
        if (!file.exists()) {
            file.mkdirs();
            printToOutput("Directory created: " + file.getPath());
        } else {
            printToOutput("Directory already exists");
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

    public void cat(ArrayList<String> args) {
        for (String arg : args) {
            try (Scanner input = new Scanner(Paths.get(arg))) {
                while (input.hasNextLine()) {
                    printToOutput(input.nextLine());
                }
            } catch (IOException e) {
                printToOutput("An error occurred: " + e.getMessage());
            }
        }
    }

    public void rm(ArrayList<String> arg) {
        File myObj = Paths.get(arg.get(0)).toFile();
        if (myObj.delete()) {
            printToOutput("Deleted the file: " + myObj.getName());
        } else {
            printToOutput("Failed to delete the file.");
        }
    }

    public void copyDirectory(ArrayList<String> args) {
        try {
            String sourceDirectory = args.get(0);
            String destinationDirectory = args.get(1);
            Files.copy(Paths.get(sourceDirectory), Paths.get(destinationDirectory), StandardCopyOption.REPLACE_EXISTING);
            printToOutput("Directory copied successfully.");
        } catch (IOException e) {
            printToOutput("An error occurred: " + e.getMessage());
        }
    }

    public void cp(ArrayList<String> args) {
        if (args.size() != 2) {
            printToOutput("Usage: cp source destination");
            return;
        }
        String source = args.get(0);
        String destination = args.get(1);
        Path sourcePath = Paths.get(source);
        Path destinationPath = Paths.get(destination);

        try {
            Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            printToOutput("File copied successfully.");
        } catch (IOException e) {
            printToOutput("An error occurred: " + e.getMessage());
        }
    }

    public void pwd() {
        printToOutput(currentDirectory);
    }

    public void cd(ArrayList<String> args) {
        if (args.isEmpty()) {
            currentDirectory = System.getProperty("user.home");
        } else if (args.size() == 1) {
            if (args.get(0).equals("..")) {
                File currentDir = new File(currentDirectory);
                File parentDir = currentDir.getParentFile();
                if (parentDir != null) {
                    currentDirectory = parentDir.getAbsolutePath();
                } else {
                    printToOutput("Already in the root directory.");
                }
            } else {
                File newDir = new File(args.get(0));
                if (newDir.isAbsolute()) {
                    if (newDir.isDirectory()) {
                        currentDirectory = newDir.getAbsolutePath();
                    } else {
                        printToOutput("Invalid directory path: " + args.get(0));
                    }
                } else {
                    File updatedDir = new File(currentDirectory, args.get(0));
                    if (updatedDir.isDirectory()) {
                        currentDirectory = updatedDir.getAbsolutePath();
                    } else {
                        printToOutput("Invalid directory path: " + args.get(0));
                    }
                }
            }
        } else {
            printToOutput("Invalid number of arguments. Usage: cd [directory]");
        }
    }

    public void ls(ArrayList<String> args) {
        File currentDir = new File(currentDirectory);

        if (!currentDir.isDirectory()) {
            printToOutput("Invalid directory path: " + currentDirectory);
            return;
        }

        if (args.size() > 0 && !args.get(0).equals("-r")) {
            printToOutput("Invalid argument: " + args.get(0));
            return;
        }

        File[] files = currentDir.listFiles();

        if (args.size() == 1 && args.get(0).equals("-r")) {
            Arrays.sort(files, Collections.reverseOrder());
        } else {
            Arrays.sort(files);
        }

        for (File file : files) {
            printToOutput(file.getName());
        }
    }

    public void wc(ArrayList<String> args) {
        if (args.size() != 1) {
            printToOutput("Usage: wc <file>");
            return;
        }

        String fileName = args.get(0);
        String filePath = currentDirectory + File.separator + fileName;

        try {
            File file = new File(filePath);

            if (!file.exists() || !file.isFile()) {
                printToOutput("File not found: " + fileName);
                return;
            }

            int lineCount = 0;
            int wordCount = 0;
            int charCount = 0;

            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                lineCount++;
                charCount += line.length();
                String[] words = line.split("\\s+");
                wordCount += words.length;
            }

            scanner.close();
            printToOutput(lineCount + " " + wordCount + " " + charCount + " " + fileName);
        } catch (FileNotFoundException e) {
            printToOutput("Error: " + e.getMessage());
        }
    }

    public void help() {
        printToOutput("Available commands:");
        printToOutput("echo <text>     	  ----> Prints the provided text.");
        printToOutput("pwd             	  ----> Prints the current directory path.");
        printToOutput("cd <directory>            ----> Change the current directory");
        printToOutput("ls [-r]         	  ----> Lists directory contents alphabetically or in reverse order.");
        printToOutput("wc <file>        	  ----> Count lines, words, and characters in a file");
        printToOutput("touch <file>    	  ----> Create a new file");
        printToOutput("mkdir <directory> 	  ----> Create a new directory");
        printToOutput("rmdir <directory> 	  ----> Remove an empty directory");
        printToOutput("cp <file> <file>          ----> Copy the first file into the second one");
        printToOutput("rm <file>        	  ----> removes this file.");
        printToOutput("cat <file> <file>         ----> 1 argument and prints the file’s content \n " +
                "        	          ----> or takes 2 arguments and concatenates the content of the 2 files and prints it.");
        printToOutput("exit               	  ----> Exits the program.");
        printToOutput("help                      ----> For help with commands and concepts");
    }
}