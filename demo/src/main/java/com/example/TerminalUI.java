package com.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class TerminalUI {
    private JFrame frame;
    private JTextPane textPane;
    private Terminal terminal;
    private String userName;
    private String prompt;
    private int commandStart;
    private CommandHistory commandHistory;

    public TerminalUI() {
        commandHistory = new CommandHistory();
        terminal = new Terminal(commandHistory);

        ImageIcon icon = new ImageIcon("resources/materials/linux.png");

        userName = (String) JOptionPane.showInputDialog(
                frame,
                "Enter your name:",
                "Login",
                JOptionPane.PLAIN_MESSAGE,
                icon,
                null,
                "user"
        );
        if (userName == null || userName.isEmpty()) {
            userName = "user";
        }
        prompt = "┌──(Linux@" + userName + ")-[~#]\n└─$ ";
        initialize();
    }

    private void initialize() {
        frame = new JFrame("LinuxTerminal");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);  // Center the window

        // Set custom border to mimic terminal window with rounded corners
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.DARK_GRAY); // Dark gray background
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 0, 0);
            }
        };
        panel.setBorder(new EmptyBorder(0, 0, 0, 0));

        textPane = new JTextPane();
        textPane.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textPane.setBackground(Color.BLACK);  // Black background
        textPane.setForeground(Color.WHITE);  // White text
        textPane.setCaretColor(Color.WHITE);  // White caret
        textPane.setCaret(new DefaultCaret() {
            {
                setBlinkRate(500);  // Set caret blink rate for quicker appearance
            }
        });
        textPane.setEditable(true);
        appendToPane("LinuxTerminal" + "\n" +
                "Copyright (C)" + "\n" +
                "Install the latest LinuxTerminal for new features and improvements!" + "\n" +
                "https://github.com/zavik001/LinuxTerminal" + "\n\n");
        printIPandTime();
        appendToPane("Hello, " + userName + "\n");
        appendToPane(prompt);
        commandStart = textPane.getDocument().getLength();

        textPane.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                int caretPosition = textPane.getCaretPosition();
                if (caretPosition < commandStart) {
                    textPane.setCaretPosition(commandStart);
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                int caretPosition = textPane.getCaretPosition();
                if (caretPosition < commandStart) {
                    textPane.setCaretPosition(commandStart);
                }

                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String input = getCurrentCommand();
                    appendToPane("\n");
                    executeCommand(input);
                    appendToPane(prompt);
                    commandStart = textPane.getDocument().getLength();
                    e.consume();
                } else if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    handleArrowKeys(e);
                    e.consume();
                }

                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    if (textPane.getCaretPosition() <= commandStart) {
                        e.consume();
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(textPane);
        panel.add(scrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.setVisible(true);
        terminal.setOutputArea(textPane);

        // Focus the text area to start typing immediately
        textPane.requestFocusInWindow();
    }

    private void printIPandTime() {
        try {
            InetAddress ip = InetAddress.getLocalHost();
            appendToPane("IP: " + ip.getHostAddress() + "\n");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedNow = now.format(formatter);
        appendToPane("date and time: " + formattedNow + "\n");
    }

    private void appendToPane(String text) {
        try {
            Document doc = textPane.getDocument();
            doc.insertString(doc.getLength(), text, null);
            textPane.setCaretPosition(doc.getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private String getCurrentCommand() {
        try {
            int end = textPane.getDocument().getLength();
            return textPane.getText(commandStart, end - commandStart).trim();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private void executeCommand(String input) {
        addToCommandHistory(input); 

        Direction parser = new Direction();
        if (parser.parse(input)) {
            String command = parser.getCommandName();
            ArrayList<String> args = parser.getArgs();
            terminal.chooseCommandAction(command, args);
        } else {
            appendToPane("Invalid command. Type 'help' for available commands.\n");
        }
    }

    private void handleArrowKeys(KeyEvent e) {
        int caretPosition = textPane.getCaretPosition();
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            String previousCommand = getPreviousCommandFromHistory();
            updateTextPaneWithCommand(previousCommand);
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            String nextCommand = getNextCommandFromHistory();
            updateTextPaneWithCommand(nextCommand);
        }
    }

    private void updateTextPaneWithCommand(String command) {
        try {
            Document doc = textPane.getDocument();
            doc.remove(commandStart, doc.getLength() - commandStart);
            doc.insertString(commandStart, command, null);
            textPane.setCaretPosition(doc.getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    

    private void addToCommandHistory(String command) {
        commandHistory.add(command);
    }

    private String getPreviousCommandFromHistory() {
        return commandHistory.getPreviousCommand();
    }

    private String getNextCommandFromHistory() {
        return commandHistory.getNextCommand();
    }

    public void close() {
        closeWindow();
    }

    private void closeWindow() {
        frame.dispose();
    }

    public void exit() {
        appendToPane("Exiting terminal...");
        if (frame != null) {
            frame.dispose();
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    TerminalUI window = new TerminalUI();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
