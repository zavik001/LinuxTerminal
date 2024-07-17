package com.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class TerminalUI {
    private JFrame frame;
    private JTextPane textPane;
    private Terminal terminal;
    private String userName;
    private String prompt;
    private int commandStart;

    public TerminalUI() {
        terminal = new Terminal();
        userName = (String) JOptionPane.showInputDialog(
            frame,
            "Enter your name:",
            "Linux Login",
            JOptionPane.PLAIN_MESSAGE,
            null,
            null,
            "user"
        );
        if (userName == null || userName.isEmpty()) {
            userName = "user";
        }
        prompt = "┌──(kali㉿" + userName + ")-[~]\n└─$ ";
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Terminal");
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
                g2d.setColor(Color.WHITE); // White background
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        textPane = new JTextPane();
        textPane.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textPane.setBackground(Color.WHITE);  // White background
        textPane.setForeground(Color.BLACK);  // Black text
        textPane.setCaretColor(Color.BLACK);  // Black caret
        textPane.setCaret(new DefaultCaret() {
            {
                setBlinkRate(500);  // Set caret blink rate for quicker appearance
            }
        });
        textPane.setEditable(true);
        appendToPane("Приветствую, " + userName + "\n");
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
        Parser parser = new Parser();
        if (parser.parse(input)) {
            String command = parser.getCommandName();
            ArrayList<String> args = parser.getArgs();
            terminal.chooseCommandAction(command, args);
        } else {
            appendToPane("Invalid command. Type 'help' for available commands.\n");
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