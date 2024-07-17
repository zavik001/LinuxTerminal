package com.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class TerminalUI {
    private JFrame frame;
    private JTextArea textArea;
    private JTextField textField;
    private Terminal terminal;

    public TerminalUI() {
        terminal = new Terminal();
        initialize();
        terminal.setOutputArea(textArea); // Set the text area for Terminal output
    }

    private void initialize() {
        frame = new JFrame("LinuxZavik Terminal");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);

        textField = new JTextField();
        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String input = textField.getText();
                textArea.append(">" + input + "\n");
                executeCommand(input);
                textField.setText("");
            }
        });

        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(textField, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private void executeCommand(String input) {
        Parser parser = new Parser();
        if (parser.parse(input)) {
            String command = parser.getCommandName();
            ArrayList<String> args = parser.getArgs();
            terminal.chooseCommandAction(command, args);
        } else {
            textArea.append("Invalid command. Type 'help' for available commands.\n");
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
