package com.github.ksouthwood.possystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.Enumeration;
import java.util.stream.IntStream;

public class RootWindow extends JFrame {
    private final Controller controller;

    private JPanel      orderInputPanel;
    private ButtonGroup wineSelector;
    private JComboBox<Integer> amountComboBox;
    private JComboBox<String>  supplierComboBox;
    private JTextField         priceTextField;
    private JButton            submitButton;
    private JLabel             messageLabel;
    private JLabel             successLabel;
    private DefaultTableModel  ordersTableModel;
    private JComboBox<String>  supplierNames;
    private JTable ordersTable;
    private TableRowSorter<DefaultTableModel> ordersTableSorter;

    public RootWindow() {
        super(WindowLabels.WINDOW_TITLE);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(500, 500);
        this.setLocationRelativeTo(null);
        this.setLayout(new FlowLayout());
        this.setName(WindowLabels.WINDOW_NAME);
        this.controller = new Controller(this);
        buildWindow();
        this.setVisible(true);
    }

    private void buildWindow() {
        var tabbedPane = new JTabbedPane();
        tabbedPane.setName(WindowLabels.TABBED_PANE_NAME);

        tabbedPane.addTab(WindowLabels.SUPPLIER_PANE_TITLE, buildSupplierOrderPanel());
        tabbedPane.addTab(WindowLabels.ORDERS_PANE_TITLE, buildOrdersPanel());

        this.add(tabbedPane);
    }

    private JPanel buildSupplierOrderPanel() {
        var supplierOrderPane = new JPanel();
        supplierOrderPane.setName(WindowLabels.SUPPLIER_PANE_NAME);
        supplierOrderPane.setLayout(new GridLayout(3, 1));
        supplierOrderPane.add(buildTopPanel());
        supplierOrderPane.add(buildOrderInputPanel());
        supplierOrderPane.add(buildBottomPanel());
        return supplierOrderPane;
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
        topPanel.add(instructionLabel, BorderLayout.NORTH);
        topPanel.add(merlotButton, BorderLayout.WEST);
        topPanel.add(roseButton, BorderLayout.CENTER);
        topPanel.add(sauvignonButton, BorderLayout.EAST);

        return topPanel;
    }

    private JPanel buildOrderInputPanel() {
        orderInputPanel = new JPanel();
        orderInputPanel.setName(WindowLabels.MIDDLE_PANEL_NAME);
        orderInputPanel.setLayout(new FlowLayout());
        orderInputPanel.setBounds(0, 50, 300, 150);
        orderInputPanel.setVisible(false);

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
        orderInputPanel.add(amountComboBox);
        orderInputPanel.add(supplierComboBox);
        orderInputPanel.add(priceTextField);
        return orderInputPanel;
    }

    private JPanel buildBottomPanel() {
        var bottomPanel = new JPanel();
        bottomPanel.setName(WindowLabels.BOTTOM_PANEL_NAME);
        bottomPanel.setLayout(new GridLayout(3, 1));
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

    private JPanel buildOrdersPanel() {
        var ordersPane = new JPanel();
        ordersPane.setName(WindowLabels.ORDERS_PANE_NAME);
        ordersPane.setLayout(new BorderLayout());

        supplierNames = new JComboBox<>();
        supplierNames.setName(WindowLabels.ORDERS_COMBO_BOX);
        supplierNames.setEditable(false);
        supplierNames.addItem(WindowLabels.ORDERS_COMBO_ALL);

        var filterButton = new JButton();
        filterButton.setName(WindowLabels.FILTER_BUTTON_NAME);
        filterButton.setText(WindowLabels.FILTER_BUTTON_TEXT);
        filterButton.addActionListener(controller);

        ordersTableModel = new DefaultTableModel(WindowLabels.ORDERS_TABLE_COLUMNS, 0);
        ordersTable = new JTable(ordersTableModel);
        ordersTable.setName(WindowLabels.ORDERS_TABLE_NAME);
        ordersTableSorter = new TableRowSorter<>(ordersTableModel);

        ordersPane.add(supplierNames, BorderLayout.WEST);
        ordersPane.add(filterButton, BorderLayout.EAST);
        ordersPane.add(ordersTable, BorderLayout.SOUTH);
        return ordersPane;
    }

    JPanel orderInputPanel() { return orderInputPanel; }

    JComboBox<String> supplierComboBox() { return supplierComboBox; }

    JTextField priceTextField() { return priceTextField; }

    JComboBox<Integer> amountComboBox() { return amountComboBox; }

    JButton submitButton() { return submitButton; }

    JLabel successLabel() { return successLabel; }

    JLabel messageLabel() { return messageLabel; }

    JComboBox<String> supplierNamesComboBox() { return supplierNames; }

    JTable ordersTable() { return ordersTable; }

    TableRowSorter<DefaultTableModel> ordersTableSorter() { return ordersTableSorter; }

    DefaultTableModel ordersTableModel() { return ordersTableModel; }

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
}
