package com.github.ksouthwood.possystem;

import javax.swing.*;
import java.awt.*;
import java.util.Enumeration;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

public class RootWindow extends JFrame {
    private final Controller controller;

    private JPanel             orderPanel;
    private ButtonGroup        wineSelector;
    private JComboBox<Integer> amountComboBox;
    private JComboBox<String>  supplierComboBox;
    private JTextField         priceTextField;
    private JButton            submitButton;
    private JLabel             messageLabel;
    private JLabel             successLabel;

    public RootWindow() {
        super(WindowLabels.WINDOW_TITLE);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(300, 300);
        this.setLocationRelativeTo(null);
        this.setLayout(null);
        this.setName(WindowLabels.WINDOW_NAME);
        this.controller = new Controller(this);
        buildWindow();
        this.setVisible(true);
    }

    public void buildWindow() {
        this.add(buildTopPanel());
        this.add(buildOrderPanel());
        this.add(buildBottomPanel());
    }

    private JPanel buildTopPanel() {
        var topPanel = new JPanel();
        topPanel.setName(WindowLabels.TOP_PANEL_NAME);
        topPanel.setLayout(new BorderLayout());
        topPanel.setBounds(0, 0, 300, 50);

        // define instructionLabel
        var instructionLabel = new JLabel();
        instructionLabel.setName(WindowLabels.INSTRUCTION_LABEL_NAME);
        instructionLabel.setText(WindowLabels.INSTRUCTION_LABEL_TEXT);
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        instructionLabel.setBounds(10, 10, 280, 20);

        // define merlotButton
        wineSelector = new ButtonGroup();
        JRadioButton merlotButton = new JRadioButton(WindowLabels.MERLOT_BUTTON_TEXT);
        merlotButton.setName(WindowLabels.MERLOT_BUTTON);
        merlotButton.setHorizontalAlignment(SwingConstants.CENTER);
        merlotButton.addActionListener(controller);
        wineSelector.add(merlotButton);

        // define roseButton
        JRadioButton roseButton = new JRadioButton(WindowLabels.ROSE_BUTTON_TEXT);
        roseButton.setName(WindowLabels.ROSE_BUTTON);
        roseButton.setHorizontalAlignment(SwingConstants.CENTER);
        roseButton.addActionListener(controller);
        wineSelector.add(roseButton);

        // define sauvignonButton
        JRadioButton sauvignonButton = new JRadioButton(WindowLabels.SAUVIGNON_BUTTON_TEXT);
        sauvignonButton.setName(WindowLabels.SAUVIGNON_BUTTON);
        sauvignonButton.setHorizontalAlignment(SwingConstants.CENTER);
        sauvignonButton.addActionListener(controller);
        wineSelector.add(sauvignonButton);

        // add components to the topPanel
        topPanel.add(instructionLabel, BorderLayout.PAGE_START);
        topPanel.add(merlotButton, BorderLayout.LINE_START);
        topPanel.add(roseButton, BorderLayout.CENTER);
        topPanel.add(sauvignonButton, BorderLayout.LINE_END);

        return topPanel;
    }

    private JPanel buildOrderPanel() {
        orderPanel = new JPanel();
        orderPanel.setName(WindowLabels.MIDDLE_PANEL_NAME);
        orderPanel.setLayout(new FlowLayout());
        orderPanel.setBounds(0, 50, 300, 150);
        orderPanel.setVisible(false);

        // define amountComboBox
        amountComboBox = new JComboBox<>(
                IntStream.rangeClosed(1, 10)
                         .boxed()
                         .toArray(Integer[]::new));
        amountComboBox.setName(WindowLabels.AMOUNT_COMBO_BOX);
        amountComboBox.setSelectedIndex(-1);

        // define supplierComboBox
        supplierComboBox = new JComboBox<>();
        supplierComboBox.setName(WindowLabels.SUPPLIER_COMBO_BOX);
        supplierComboBox.setEditable(true);

        // define priceTextField
        priceTextField = new JTextField();
        priceTextField.setName(WindowLabels.PURCHASED_PRICE_TEXT_FIELD);
        priceTextField.setColumns(10);

        // add components to the middlePanel
        orderPanel.add(amountComboBox);
        orderPanel.add(supplierComboBox);
        orderPanel.add(priceTextField);
        return orderPanel;
    }

    private JPanel buildBottomPanel() {
        var bottomPanel = new JPanel();
        bottomPanel.setName(WindowLabels.BOTTOM_PANEL_NAME);
        bottomPanel.setLayout(new FlowLayout());
        bottomPanel.setBounds(0, 205, 300, 50);

        // define successLabel
        successLabel = new JLabel();
        successLabel.setName(WindowLabels.SUCCESS_LABEL_NAME);
        successLabel.setText(WindowLabels.SUCCESS_LABEL_TEXT);
        successLabel.setHorizontalAlignment(SwingConstants.CENTER);
        successLabel.setVisible(false);
        bottomPanel.add(successLabel);

        // define submitButton
        submitButton = new JButton();
        submitButton.setName(WindowLabels.SUBMIT_BUTTON_NAME);
        submitButton.setText(WindowLabels.SUBMIT_BUTTON_TEXT);
        submitButton.setEnabled(false);
        submitButton.addActionListener(controller);
        bottomPanel.add(submitButton);

        // define messageLabel
        messageLabel = new JLabel();
        messageLabel.setName(WindowLabels.MESSAGE_LABEL_NAME);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bottomPanel.add(messageLabel);

        return bottomPanel;
    }

    JPanel getOrderPanel() { return orderPanel; }

    JButton getSubmitButton() { return submitButton; }

    JLabel getSuccessLabel() { return successLabel; }

    JLabel getMessageLabel() { return messageLabel; }

    void setMessageLabelText(String text) {
        messageLabel.setText(text);
    }

    void updateSupplierComboBox(String supplier) {
        supplierComboBox.addItem(supplier);
    }

    void orderSuccessful() {
        successLabel.setVisible(true);
        submitButton.setEnabled(false);
        orderPanel.setVisible(false);
        amountComboBox.setSelectedIndex(-1);
        supplierComboBox.setSelectedIndex(-1);
        priceTextField.setText("");
    }

    String getSelectedWine() {
        Enumeration<AbstractButton> radioButtons = wineSelector.getElements();
        while (radioButtons.hasMoreElements()) {
            AbstractButton button = radioButtons.nextElement();
            if (button.isSelected()) {
                return button.getText();
            }
        }
        return null;
    }

    void resetOrderPanel() {
        supplierComboBox.setSelectedIndex(-1);
        priceTextField.setText("");
    }

    Map<String, String> getSupplier() {
        return Map.of("supplier", (String) supplierComboBox.getSelectedItem(),
                      "index", String.valueOf(supplierComboBox.getSelectedIndex()));
    }

    Optional<Integer> getAmount() {
        int index = amountComboBox.getSelectedIndex();
        if (index == -1) {
            setMessageLabelText("Error: Please choose an amount");
            return Optional.empty();
        }
        return Optional.of(amountComboBox.getItemAt(index));
    }

    Optional<Double> getPrice() {
        try {
            return Optional.of(Double.parseDouble(priceTextField.getText()));
        } catch (NumberFormatException e) {
            setMessageLabelText("Error: price must be a number");
            resetOrderPanel();
            return Optional.empty();
        }
    }
}
