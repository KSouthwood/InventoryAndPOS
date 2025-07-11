package com.github.ksouthwood.possystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.Enumeration;
import java.util.stream.IntStream;

public class RootWindow extends JFrame {
    private final Controller controller;

    private JPanel                            orderInputPanel;
    private ButtonGroup                       wineSelector;
    private JComboBox<Integer>                amountComboBox;
    private JComboBox<String>                 supplierComboBox;
    private JTextField                        priceTextField;
    private JButton                           submitButton;
    private JLabel                            messageLabel;
    private JLabel                            successLabel;
    private DefaultTableModel                 ordersTableModel;
    private JComboBox<String>                 supplierNames;
    private JTable                            ordersTable;
    private TableRowSorter<DefaultTableModel> ordersTableSorter;

    public RootWindow() {
        this("orders.db");
    }

    public RootWindow(final String databaseName) {
        super(WindowLabels.WINDOW_TITLE);
        this.setName(WindowLabels.WINDOW_NAME);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setPreferredSize(new Dimension(500, 500));
        this.setMinimumSize(new Dimension(300, 300));
        this.setLocationRelativeTo(null);
        this.setLayout(new FlowLayout());
        this.controller = new Controller(this, databaseName);
        buildWindow();
        this.pack();
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
        topPanel.setLayout(new GridBagLayout());

        // define instructionLabel
        var instructionLabel = new JLabel();
        var instructionConstraints = new GridBagConstraints();
        instructionLabel.setName(WindowLabels.INSTRUCTION_LABEL_NAME);
        instructionLabel.setText(WindowLabels.INSTRUCTION_LABEL_TEXT);
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        instructionConstraints.gridx = 0;
        instructionConstraints.gridy = 0;
        instructionConstraints.gridwidth = 3;
        instructionConstraints.fill = GridBagConstraints.BOTH;
        topPanel.add(instructionLabel, instructionConstraints);

        // define merlotButton
        wineSelector = new ButtonGroup();
        JRadioButton merlotButton = new JRadioButton(WindowLabels.MERLOT_BUTTON_TEXT);
        var merlotConstraints = new GridBagConstraints();
        merlotButton.setName(WindowLabels.MERLOT_BUTTON);
        merlotButton.addActionListener(controller);
        merlotConstraints.gridx = 0;
        merlotConstraints.gridy = 1;
        merlotConstraints.weightx = 0.33;
        wineSelector.add(merlotButton);
        topPanel.add(merlotButton, merlotConstraints);

        // define roseButton
        JRadioButton roseButton = new JRadioButton(WindowLabels.ROSE_BUTTON_TEXT);
        var roseConstraints = new GridBagConstraints();
        roseButton.setName(WindowLabels.ROSE_BUTTON);
        roseButton.addActionListener(controller);
        roseConstraints.gridx = 1;
        roseConstraints.gridy = 1;
        roseConstraints.weightx = 0.34;
        wineSelector.add(roseButton);
        topPanel.add(roseButton, roseConstraints);

        // define sauvignonButton
        JRadioButton sauvignonButton = new JRadioButton(WindowLabels.SAUVIGNON_BUTTON_TEXT);
        var sauvignonConstraints = new GridBagConstraints();
        sauvignonButton.setName(WindowLabels.SAUVIGNON_BUTTON);
        sauvignonButton.addActionListener(controller);
        sauvignonConstraints.gridx = 2;
        sauvignonConstraints.gridy = 1;
        sauvignonConstraints.weightx = 0.33;
        wineSelector.add(sauvignonButton);
        topPanel.add(sauvignonButton, sauvignonConstraints);

        return topPanel;
    }

    private JPanel buildOrderInputPanel() {
        orderInputPanel = new JPanel();
        orderInputPanel.setName(WindowLabels.MIDDLE_PANEL_NAME);
        orderInputPanel.setLayout(new GridBagLayout());
        orderInputPanel.setVisible(false);

        // define labels
        var labelConstraints = new GridBagConstraints();
        JLabel amountLabel = new JLabel();
        amountLabel.setText("Cases:");
        labelConstraints.gridx = 0;
        labelConstraints.gridy = 0;
        labelConstraints.anchor = GridBagConstraints.LINE_END;
        labelConstraints.insets = new Insets(0, 0, 5, 5);
        orderInputPanel.add(amountLabel, labelConstraints);
        JLabel supplierLabel = new JLabel();
        supplierLabel.setText("Supplier:");
        labelConstraints.gridy = 1;
        orderInputPanel.add(supplierLabel, labelConstraints);
        JLabel priceLabel = new JLabel();
        priceLabel.setText("Purchased Price:");
        labelConstraints.gridy = 2;
        orderInputPanel.add(priceLabel, labelConstraints);

        // define amountComboBox
        amountComboBox = new JComboBox<>(
                IntStream.rangeClosed(1, 10)
                         .boxed()
                         .toArray(Integer[]::new));
        amountComboBox.setName(WindowLabels.AMOUNT_COMBO_BOX);
        amountComboBox.setSelectedIndex(-1);
        var amountConstraints = new GridBagConstraints();
        amountConstraints.gridx = 1;
        amountConstraints.gridy = 0;
        amountConstraints.anchor = GridBagConstraints.LINE_START;
        amountConstraints.insets = new Insets(0, 0, 5, 0);
        orderInputPanel.add(amountComboBox, amountConstraints);

        // define supplierComboBox
        supplierComboBox = new JComboBox<>();
        supplierComboBox.setName(WindowLabels.SUPPLIER_COMBO_BOX);
        supplierComboBox.setEditable(true);
        var supplierConstraints = new GridBagConstraints();
        supplierConstraints.gridx = 1;
        supplierConstraints.gridy = 1;
        supplierConstraints.anchor = GridBagConstraints.LINE_START;
        supplierConstraints.insets = new Insets(0, 0, 5, 0);
        orderInputPanel.add(supplierComboBox, supplierConstraints);

        // define priceTextField
        priceTextField = new JTextField();
        priceTextField.setName(WindowLabels.PURCHASED_PRICE_TEXT_FIELD);
        var priceConstraints = new GridBagConstraints();
        priceConstraints.gridx = 1;
        priceConstraints.gridy = 2;
        priceConstraints.anchor = GridBagConstraints.LINE_START;
        priceConstraints.fill = GridBagConstraints.HORIZONTAL;
        orderInputPanel.add(priceTextField, priceConstraints);

        return orderInputPanel;
    }

    private JPanel buildBottomPanel() {
        var bottomPanel = new JPanel();
        bottomPanel.setName(WindowLabels.BOTTOM_PANEL_NAME);
        bottomPanel.setLayout(new GridBagLayout());

        // define successLabel
        successLabel = new JLabel();
        successLabel.setName(WindowLabels.SUCCESS_LABEL_NAME);
        successLabel.setText(WindowLabels.SUCCESS_LABEL_TEXT);
        successLabel.setHorizontalAlignment(SwingConstants.CENTER);
        successLabel.setVisible(false);
        var successConstraints = new GridBagConstraints();
        successConstraints.gridx = 0;
        successConstraints.gridy = 0;
        successConstraints.fill = GridBagConstraints.NONE;
        bottomPanel.add(successLabel, successConstraints);

        // define messageLabel
        messageLabel = new JLabel();
        messageLabel.setName(WindowLabels.MESSAGE_LABEL_NAME);
        messageLabel.setText("");
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        var messageConstraints = new GridBagConstraints();
        messageConstraints.gridx = 0;
        messageConstraints.gridy = 1;
        bottomPanel.add(messageLabel, messageConstraints);

        // define submitButton
        submitButton = new JButton();
        submitButton.setName(WindowLabels.SUBMIT_BUTTON_NAME);
        submitButton.setText(WindowLabels.SUBMIT_BUTTON_TEXT);
        submitButton.setEnabled(false);
        submitButton.addActionListener(controller);
        var submitConstraints = new GridBagConstraints();
        submitConstraints.gridx = 0;
        submitConstraints.gridy = 2;
        bottomPanel.add(submitButton, submitConstraints);

        return bottomPanel;
    }

    private JPanel buildOrdersPanel() {
        var ordersPane = new JPanel();
        ordersPane.setName(WindowLabels.ORDERS_PANE_NAME);
        ordersPane.setLayout(new GridBagLayout());

        supplierNames = new JComboBox<>();
        supplierNames.setName(WindowLabels.ORDERS_COMBO_BOX);
        supplierNames.setEditable(false);
        supplierNames.addItem(WindowLabels.ORDERS_COMBO_ALL);
        var supplierNamesConstraints = new GridBagConstraints();
        supplierNamesConstraints.gridx = 0;
        supplierNamesConstraints.gridy = 0;
        supplierNamesConstraints.anchor = GridBagConstraints.LINE_END;
        ordersPane.add(supplierNames, supplierNamesConstraints);

        var filterButton = new JButton();
        filterButton.setName(WindowLabels.FILTER_BUTTON_NAME);
        filterButton.setText(WindowLabels.FILTER_BUTTON_TEXT);
        filterButton.addActionListener(controller);
        var filterConstraints = new GridBagConstraints();
        filterConstraints.gridx = 1;
        filterConstraints.gridy = 0;
        filterConstraints.anchor = GridBagConstraints.CENTER;
        ordersPane.add(filterButton, filterConstraints);

        ordersTableModel = new DefaultTableModel(WindowLabels.ORDERS_TABLE_COLUMNS, 0);
        ordersTable = new JTable(ordersTableModel);
        var scrollPane = new JScrollPane(ordersTable);
        scrollPane.setName(WindowLabels.ORDERS_TABLE_NAME.toLowerCase());
        ordersTable.setName(WindowLabels.ORDERS_TABLE_NAME);
        ordersTableSorter = new TableRowSorter<>(ordersTableModel);
        ordersTable.setRowSorter(ordersTableSorter);
        var ordersTableConstraints = new GridBagConstraints();
        ordersTableConstraints.gridx = 0;
        ordersTableConstraints.gridy = 1;
        ordersTableConstraints.gridwidth = 2;
        ordersTableConstraints.fill = GridBagConstraints.BOTH;
        ordersTableConstraints.insets = new Insets(5, 0, 0, 0);
        ordersTableConstraints.weighty = 1.0;
        ordersPane.add(scrollPane, ordersTableConstraints);

        return ordersPane;
    }

    JPanel orderInputPanel() {return orderInputPanel;}

    JComboBox<String> supplierComboBox() {return supplierComboBox;}

    JTextField priceTextField() {return priceTextField;}

    JComboBox<Integer> amountComboBox() {return amountComboBox;}

    JButton submitButton() {return submitButton;}

    JLabel successLabel() {return successLabel;}

    JLabel messageLabel() {return messageLabel;}

    JComboBox<String> supplierNamesComboBox() {return supplierNames;}

    JTable ordersTable() {return ordersTable;}

    TableRowSorter<DefaultTableModel> ordersTableSorter() {return ordersTableSorter;}

    DefaultTableModel ordersTableModel() {return ordersTableModel;}

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
