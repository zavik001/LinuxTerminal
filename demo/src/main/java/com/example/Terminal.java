package com.example;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.io.*;
import java.net.InetAddress;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Terminal {
    private String currentDirectory = System.getProperty("user.dir");
    private JTextPane textPane;
    private TerminalUI terminalUI;
    private ArrayList<String> commandHistory = new ArrayList<>();
    private int historyIndex = -1;

    private void addToCommandHistory(String command) {
        commandHistory.add(command);
        historyIndex = commandHistory.size(); 
    }

    private String getPreviousCommand() {
        if (historyIndex > 0) {
            historyIndex--;
        }
        return commandHistory.get(historyIndex);
    }

    private String getNextCommand() {
        if (historyIndex < commandHistory.size() - 1) {
            historyIndex++;
        } else if (historyIndex == commandHistory.size() - 1) {
            historyIndex = commandHistory.size();
            return "";
        }
        return commandHistory.get(historyIndex);
    }

    private void executeCommand(String input) {
        addToCommandHistory(input); 

        Direction parser = new Direction();
        if (parser.parse(input)) {
            String command = parser.getCommandName();
            addToCommandHistory(command);
            ArrayList<String> args = parser.getArgs();
            chooseCommandAction(command, args);
        } else {
            printToOutput("Invalid command. Type 'help' for available commands.\n");
        }
    }

    public void setOutputArea(JTextPane textPane) {
        this.textPane = textPane;
        
    }

    public void setTerminalUI(TerminalUI terminalUI) {
        this.terminalUI = terminalUI;
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
                    copyDirectory(args);
                }
                break;

            case "rm":
                rm(args);
                break;

            case "cat":
                cat(args);
                break;

            case "date":
                date(args);
                break;

            case "clean":
                clean(args);
                break;

            case "exit":
                exit();
                break;

            case "find":
                find(args);
                break;

            case "df":
                df();
                break;

            case "free":
                free();
                break;

            case "uname":
                uname(args);
                break;

            case "uptime":
                uptime();
                break;

            case "whoami":
                whoami();
                break;

            case "ping":
                ping(args);
                break;

            case "history":
                history(args);
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
        Path path = Paths.get(currentDirectory, arg).normalize();
        try {
            Files.createFile(path);
            printToOutput("File created: " + path);
            return true;
        } catch (IOException e) {
            printToOutput("Error creating file: " + e.getMessage());
            return false;
        }
    }

    public void mkdir(ArrayList<String> args) {
        for (String arg : args) {
            Path path = Paths.get(currentDirectory, arg).normalize();
            try {
                Files.createDirectories(path);
                printToOutput("Directory created: " + path);
            } catch (IOException e) {
                printToOutput("Error creating directory: " + e.getMessage());
            }
        }
    }

    public void rmdir(String arg) {
        Path path = Paths.get(currentDirectory, arg).normalize();
        try {
            Files.delete(path);
            printToOutput("Directory deleted: " + path);
        } catch (IOException e) {
            printToOutput("Error deleting directory: " + e.getMessage());
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
                "cat [file] - Displays the contents of a file\n" +
                "date - Prints date and time\n" +
                "clean - Clears the window\n" +
                "exit - Closes the terminal\n" +
                "find [name/type] - Finds files by name or type\n" +
                "df - Shows disk usage\n" +
                "free - Shows memory usage\n" +
                "uname - Shows system information\n" +
                "uptime - Shows system uptime\n" +
                "whoami - Shows current user\n" +
                "ping [host] - Pings a host";
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

    public void copyDirectory(ArrayList<String> args) {
        final Path sourceDirectory = Paths.get(currentDirectory, args.get(0)).normalize();
        final Path destinationDirectory = Paths.get(currentDirectory, args.get(1)).normalize();

        try {
            Files.walkFileTree(sourceDirectory, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    Path targetPath = destinationDirectory.resolve(sourceDirectory.relativize(dir));
                    if (!Files.exists(targetPath)) {
                        Files.createDirectory(targetPath);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.copy(file, destinationDirectory.resolve(sourceDirectory.relativize(file)),
                            StandardCopyOption.REPLACE_EXISTING);
                    return FileVisitResult.CONTINUE;
                }
            });
            printToOutput("Directory copied from " + sourceDirectory + " to " + destinationDirectory);
        } catch (IOException e) {
            printToOutput("Error copying directory: " + e.getMessage());
        }
    }

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
        for (String arg : args) {
            Path path = Paths.get(currentDirectory, arg).normalize();
            try {
                List<String> lines = Files.readAllLines(path);
                for (String line : lines) {
                    printToOutput(line);
                }
            } catch (IOException e) {
                printToOutput("Error reading file: " + e.getMessage());
            }
        }
    }

    public void date(ArrayList<String> args) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        printToOutput(now.format(formatter));
    }

    public void clean(ArrayList<String> args) {
        if (textPane != null) {
            textPane.setText("");
        } else {
            printToOutput("TextPane is null. Cannot clean the output.");
        }
    }

    public void exit() {
        printToOutput("Exiting terminal...");
        if (terminalUI != null) {
            terminalUI.exit();
        } else {
            System.exit(0);
        }
    }
    

    public void find(ArrayList<String> args) {
        if (args.size() != 1) {
            printToOutput("Usage: find [name/type]");
            return;
        }
    
        final String pattern = args.get(0); 
    
        try {
            final List<Path> result = new ArrayList<>(); 
    
            if (pattern.startsWith(".")) {
                Files.walkFileTree(Paths.get(currentDirectory), new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        if (file.toString().endsWith(pattern)) {
                            result.add(file);
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
            } else {
                Files.walkFileTree(Paths.get(currentDirectory), new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        if (file.getFileName().toString().contains(pattern)) {
                            result.add(file);
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
            }
    
            if (result.isEmpty()) {
                printToOutput("No matches found for: " + pattern);
            } else {
                for (Path path : result) {
                    printToOutput(path.toString());
                }
            }
        } catch (IOException e) {
            printToOutput("Error finding files: " + e.getMessage());
        }
    }    

    public void df() {
        File[] roots = File.listRoots();
        for (File root : roots) {
            long totalSpace = root.getTotalSpace();
            long freeSpace = root.getFreeSpace();
            long usableSpace = root.getUsableSpace();

            printToOutput("File system root: " + root.getAbsolutePath());
            printToOutput("Total space (bytes): " + totalSpace);
            printToOutput("Free space (bytes): " + freeSpace);
            printToOutput("Usable space (bytes): " + usableSpace);
        }
    }

    public void free() {
        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();
        long maxMemory = Runtime.getRuntime().maxMemory();

        printToOutput("Total memory (bytes): " + totalMemory);
        printToOutput("Free memory (bytes): " + freeMemory);
        printToOutput("Max memory (bytes): " + maxMemory);
    }

    public void uname(ArrayList<String> args) {
        String os = System.getProperty("os.name");
        String version = System.getProperty("os.version");
        String arch = System.getProperty("os.arch");

        printToOutput("OS: " + os);
        printToOutput("Version: " + version);
        printToOutput("Architecture: " + arch);
    }

    public void uptime() {
        long uptime = System.currentTimeMillis() - System.nanoTime() / 1000000;
        long uptimeSeconds = uptime / 1000;

        long hours = uptimeSeconds / 3600;
        long minutes = (uptimeSeconds % 3600) / 60;
        long seconds = uptimeSeconds % 60;

        printToOutput(String.format("Uptime: %d hours %d minutes %d seconds", hours, minutes, seconds));
    }

    public void whoami() {
        String user = System.getProperty("user.name");
        printToOutput("Current user: " + user);
    }

    public void ping(ArrayList<String> args) {
        if (args.size() != 1) {
            printToOutput("Usage: ping [host]");
            return;
        }

        String host = args.get(0);
        try {
            InetAddress address = InetAddress.getByName(host);
            boolean reachable = address.isReachable(5000);
            printToOutput(host + " is " + (reachable ? "reachable" : "unreachable"));
        } catch (IOException e) {
            printToOutput("Error pinging host: " + e.getMessage());
        }
    }

    public void history(ArrayList<String> args) {
        int limit = commandHistory.size(); 
        int startIndex = 0;
    
        if (args.size() > 0) {
            try {
                limit = Integer.parseInt(args.get(0));
            } catch (NumberFormatException e) {
                printToOutput("Invalid argument for history. Please enter a number.");
                return;
            }
        }
    
        if (args.size() > 1) {
            printToOutput("Usage: history [limit]");
            return;
        }
    
        startIndex = Math.max(0, commandHistory.size() - limit); 
    
        for (int i = startIndex; i < commandHistory.size(); i++) {
            printToOutput(i + 1 + ": " + commandHistory.get(i));
        }
    }
    
}