package com.github.ksouthwood.possystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.Enumeration;
import java.util.Vector;
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
    private TableRowSorter<DefaultTableModel> ordersTableSorter;

    private JLabel merlotAmount;
    private JLabel roseAmount;
    private JLabel sauvignonAmount;
    private JLabel totalAmount;

    private Vector<String>    wineTypes;
    private JComboBox<String> singleComboBox;
    private JComboBox<String> mixedComboBox1;
    private JComboBox<String> mixedComboBox2;

    private JPanel            singleTypePanel;
    private JPanel            mixedTypePanel;
    private JLabel            saleMessageLabel;
    private JLabel            saleSuccessMessageLabel;
    private JTextField        customerNameField;
    private JTextField        saleAmountField;
    private JRadioButton      loyaltyDiscountButton;
    private JButton           submitOrderButton;

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
        controller.loadFromDatabase();
        this.pack();
        this.setVisible(true);
    }

    private void buildWindow() {
        var tabbedPane = new JTabbedPane();
        tabbedPane.setName(WindowLabels.TABBED_PANE_NAME);

        tabbedPane.addTab(WindowLabels.SUPPLIER_PANE_TITLE, buildSupplierOrderPanel());
        tabbedPane.addTab(WindowLabels.ORDERS_PANE_TITLE, buildOrdersPanel());
        tabbedPane.addTab(WindowLabels.INVENTORY_TAB_NAME, buildInventoryPanel());
        tabbedPane.addTab(WindowLabels.CUSTOMER_ORDER_TAB_NAME, customerOrderPanel());

        tabbedPane.addChangeListener(controller);
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
        var instructionLabel       = new JLabel();
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
        JRadioButton merlotButton      = new JRadioButton(WindowLabels.MERLOT_BUTTON_TEXT);
        var          merlotConstraints = new GridBagConstraints();
        merlotButton.setName(WindowLabels.MERLOT_BUTTON);
        merlotButton.addActionListener(controller);
        merlotConstraints.gridx = 0;
        merlotConstraints.gridy = 1;
        merlotConstraints.weightx = 0.33;
        wineSelector.add(merlotButton);
        topPanel.add(merlotButton, merlotConstraints);

        // define roseButton
        JRadioButton roseButton      = new JRadioButton(WindowLabels.ROSE_BUTTON_TEXT);
        var          roseConstraints = new GridBagConstraints();
        roseButton.setName(WindowLabels.ROSE_BUTTON);
        roseButton.addActionListener(controller);
        roseConstraints.gridx = 1;
        roseConstraints.gridy = 1;
        roseConstraints.weightx = 0.34;
        wineSelector.add(roseButton);
        topPanel.add(roseButton, roseConstraints);

        // define sauvignonButton
        JRadioButton sauvignonButton      = new JRadioButton(WindowLabels.SAUVIGNON_BUTTON_TEXT);
        var          sauvignonConstraints = new GridBagConstraints();
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
        var    labelConstraints = new GridBagConstraints();
        JLabel amountLabel      = new JLabel();
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
        var ordersTable = new JTable(ordersTableModel);
        var scrollPane  = new JScrollPane(ordersTable);
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

    private JPanel buildInventoryPanel() {
        var inventoryPane = new JPanel();
        inventoryPane.setLayout(new GridBagLayout());
        var constraints = new GridBagConstraints();
        constraints.insets = new Insets(2, 2, 2, 2);

        merlotAmount = new JLabel();
        merlotAmount.setName(WindowLabels.MERLOT_AMOUNT_LABEL_NAME);
        merlotAmount.setText(WindowLabels.MERLOT_AMOUNT_LABEL_TEXT);
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.CENTER;
        inventoryPane.add(merlotAmount, constraints);

        roseAmount = new JLabel();
        roseAmount.setName(WindowLabels.ROSE_AMOUNT_LABEL_NAME);
        roseAmount.setText(WindowLabels.ROSE_AMOUNT_LABEL_TEXT);
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.anchor = GridBagConstraints.CENTER;
        inventoryPane.add(roseAmount, constraints);

        sauvignonAmount = new JLabel();
        sauvignonAmount.setName(WindowLabels.SAUVIGNON_AMOUNT_LABEL_NAME);
        sauvignonAmount.setText(WindowLabels.SAUVIGNON_AMOUNT_LABEL_TEXT);
        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        inventoryPane.add(sauvignonAmount, constraints);

        totalAmount = new JLabel();
        totalAmount.setName(WindowLabels.TOTAL_AMOUNT_LABEL_NAME);
        totalAmount.setText(WindowLabels.TOTAL_AMOUNT_LABEL_TEXT);
        constraints.gridx = 1;
        constraints.gridy = 3;
        constraints.anchor = GridBagConstraints.CENTER;
        inventoryPane.add(totalAmount, constraints);

        var merlotLabel = new JLabel();
        merlotLabel.setText(WindowLabels.MERLOT_AMOUNT_TEXT);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.LINE_END;
        inventoryPane.add(merlotLabel, constraints);

        var roseLabel = new JLabel();
        roseLabel.setText(WindowLabels.ROSE_AMOUNT_TEXT);
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.anchor = GridBagConstraints.LINE_END;
        inventoryPane.add(roseLabel, constraints);

        var sauvignonLabel = new JLabel();
        sauvignonLabel.setText(WindowLabels.SAUVIGNON_AMOUNT_TEXT);
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.anchor = GridBagConstraints.LINE_END;
        inventoryPane.add(sauvignonLabel, constraints);

        var totalLabel = new JLabel();
        totalLabel.setText(WindowLabels.TOTAL_AMOUNT_TEXT);
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.anchor = GridBagConstraints.LINE_END;
        inventoryPane.add(totalLabel, constraints);

        return inventoryPane;
    }

    private JPanel customerOrderPanel() {
        var customerOrderPane = new JPanel();
        customerOrderPane.setLayout(new GridBagLayout());

        wineTypes = new Vector<>();
        wineTypes.add(WindowLabels.ROSE_BUTTON_TEXT);
        wineTypes.add(WindowLabels.MERLOT_BUTTON_TEXT);
        wineTypes.add(WindowLabels.SAUVIGNON_BUTTON_TEXT);

        var customerOrderPaneConstraints = new GridBagConstraints();
        customerOrderPaneConstraints.gridx = 0;
        customerOrderPaneConstraints.gridy = 0;
        customerOrderPaneConstraints.weighty = 0.30;
        customerOrderPane.add(buildPosTopPanel(), customerOrderPaneConstraints);

        customerOrderPaneConstraints.gridy = 1;
        customerOrderPaneConstraints.weighty = 0.40;
        customerOrderPane.add(buildSingleTypePanel(), customerOrderPaneConstraints);

        customerOrderPaneConstraints.gridy = 2;
        customerOrderPaneConstraints.weighty = 0.40;
        customerOrderPane.add(buildMixedTypePanel(), customerOrderPaneConstraints);

        customerOrderPaneConstraints.gridy = 3;
        customerOrderPaneConstraints.weighty = 0.30;
        customerOrderPane.add(buildPosBottomPanel(), customerOrderPaneConstraints);

        return customerOrderPane;
    }

    private JPanel buildPosTopPanel() {
        var posTopPanel = new JPanel();
        posTopPanel.setLayout(new GridBagLayout());
        posTopPanel.setName(WindowLabels.POS_TOP_PANEL_NAME);
        posTopPanel.setVisible(true);

        var posInstruction = new JLabel();
        posInstruction.setName(WindowLabels.POS_INSTRUCTION_LABEL_NAME);
        posInstruction.setText(WindowLabels.POS_INSTRUCTION_LABEL_TEXT);
        var posInstructionConstraints = new GridBagConstraints();
        posInstructionConstraints.gridx = 0;
        posInstructionConstraints.gridy = 0;
        posInstructionConstraints.gridwidth = 2;
        posInstructionConstraints.anchor = GridBagConstraints.CENTER;
        posTopPanel.add(posInstruction, posInstructionConstraints);

        var orderType       = new ButtonGroup();
        var singleTypeOrder = new JRadioButton();
        singleTypeOrder.setName(WindowLabels.SINGLE_TYPE_BUTTON_NAME);
        singleTypeOrder.setText(WindowLabels.SINGLE_TYPE_BUTTON_TEXT);
        singleTypeOrder.addActionListener(controller);
        var singleTypeConstraints = new GridBagConstraints();
        singleTypeConstraints.gridx = 0;
        singleTypeConstraints.gridy = 1;
        posTopPanel.add(singleTypeOrder, singleTypeConstraints);
        orderType.add(singleTypeOrder);

        var mixedTypeOrder = new JRadioButton();
        mixedTypeOrder.setName(WindowLabels.MIXED_TYPE_BUTTON_NAME);
        mixedTypeOrder.setText(WindowLabels.MIXED_TYPE_BUTTON_TEXT);
        mixedTypeOrder.addActionListener(controller);
        var mixedTypeConstraints = new GridBagConstraints();
        mixedTypeConstraints.gridx = 1;
        mixedTypeConstraints.gridy = 1;
        posTopPanel.add(mixedTypeOrder, mixedTypeConstraints);
        orderType.add(mixedTypeOrder);

        return posTopPanel;
    }

    private JPanel buildSingleTypePanel() {
        singleTypePanel = new JPanel();
        singleTypePanel.setLayout(new GridBagLayout());
        singleTypePanel.setName(WindowLabels.SINGLE_TYPE_PANEL_NAME);
        singleTypePanel.setVisible(false);

        var selectLabel = new JLabel();
        selectLabel.setText(WindowLabels.SINGLE_WINE_SELECT_TEXT);
        var selectConstraints = new GridBagConstraints();
        selectConstraints.gridx = 0;
        selectConstraints.gridy = 0;
        selectConstraints.anchor = GridBagConstraints.LINE_END;
        selectConstraints.insets = new Insets(5, 5, 5, 5);
        singleTypePanel.add(selectLabel, selectConstraints);

        singleComboBox = new JComboBox<>(wineTypes);
        singleComboBox.setName(WindowLabels.SINGLE_WINE_COMBO_BOX_NAME);
        singleComboBox.setEditable(false);
        var singleConstraints = new GridBagConstraints();
        singleConstraints.gridx = 1;
        singleConstraints.gridy = 0;
        singleConstraints.anchor = GridBagConstraints.LINE_START;
        singleConstraints.insets = new Insets(5, 5, 5, 5);
        singleTypePanel.add(singleComboBox, singleConstraints);

        var submitButton = new JButton();
        submitButton.setName(WindowLabels.SINGLE_WINE_CONFIRM_BUTTON_NAME);
        submitButton.setText(WindowLabels.SINGLE_WINE_CONFIRM_BUTTON_TEXT);
        submitButton.addActionListener(controller);
        var submitConstraints = new GridBagConstraints();
        submitConstraints.gridx = 0;
        submitConstraints.gridy = 1;
        submitConstraints.gridwidth = 2;
        submitConstraints.insets = new Insets(5, 5, 5, 5);
        singleTypePanel.add(submitButton, submitConstraints);

        return singleTypePanel;
    }

    private JPanel buildMixedTypePanel() {
        mixedTypePanel = new JPanel();
        mixedTypePanel.setLayout(new GridBagLayout());
        mixedTypePanel.setName(WindowLabels.MIXED_TYPE_PANEL_NAME);
        mixedTypePanel.setVisible(false);

        var selectLabel = new JLabel();
        selectLabel.setText(WindowLabels.MIXED_WINE_SELECT_TEXT);
        var selectConstraints = new GridBagConstraints();
        selectConstraints.gridx = 0;
        selectConstraints.gridy = 0;
        selectConstraints.gridwidth = 2;
        selectConstraints.insets = new Insets(5, 5, 5, 5);
        selectConstraints.anchor = GridBagConstraints.CENTER;
        mixedTypePanel.add(selectLabel, selectConstraints);

        mixedComboBox1 = new JComboBox<>(wineTypes);
        mixedComboBox1.setName(WindowLabels.MIXED_WINE_COMBO_BOX_ONE_NAME);
        mixedComboBox1.setEditable(false);
        var mixedConstraints1 = new GridBagConstraints();
        mixedConstraints1.gridx = 0;
        mixedConstraints1.gridy = 1;
        mixedConstraints1.anchor = GridBagConstraints.CENTER;
        mixedConstraints1.insets = new Insets(5, 5, 5, 5);
        mixedTypePanel.add(mixedComboBox1, mixedConstraints1);

        mixedComboBox2 = new JComboBox<>(wineTypes);
        mixedComboBox2.setName(WindowLabels.MIXED_WINE_COMBO_BOX_TWO_NAME);
        mixedComboBox2.setEditable(false);
        var mixedConstraints2 = new GridBagConstraints();
        mixedConstraints2.gridx = 1;
        mixedConstraints2.gridy = 1;
        mixedConstraints2.anchor = GridBagConstraints.CENTER;
        mixedConstraints2.insets = new Insets(5, 5, 5, 5);
        mixedTypePanel.add(mixedComboBox2, mixedConstraints2);

        var submitButton = new JButton();
        submitButton.setName(WindowLabels.MIXED_WINE_CONFIRM_BUTTON_NAME);
        submitButton.setText(WindowLabels.MIXED_WINE_CONFIRM_BUTTON_TEXT);
        submitButton.addActionListener(controller);
        var submitConstraints = new GridBagConstraints();
        submitConstraints.gridx = 0;
        submitConstraints.gridy = 2;
        submitConstraints.gridwidth = 2;
        submitConstraints.insets = new Insets(5, 5, 5, 5);
        mixedTypePanel.add(submitButton, submitConstraints);

        return mixedTypePanel;
    }

    private JPanel buildPosBottomPanel() {
        var posBottomPanel = new JPanel();
        posBottomPanel.setLayout(new GridBagLayout());
        posBottomPanel.setName(WindowLabels.POS_BOTTOM_PANEL_NAME);
        posBottomPanel.setVisible(true);

        var constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(2, 2, 2, 2);

        saleMessageLabel = new JLabel();
        saleMessageLabel.setName(WindowLabels.SALE_MESSAGE_LABEL_NAME);
        saleMessageLabel.setText("");
        posBottomPanel.add(saleMessageLabel, constraints);

        var customerNameLabel = new JLabel();
        customerNameLabel.setText(WindowLabels.CUSTOMER_NAME_LABEL_TEXT);
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        posBottomPanel.add(customerNameLabel, constraints);

        var amountLabel = new JLabel();
        amountLabel.setText(WindowLabels.SALE_AMOUNT_LABEL_TEXT);
        constraints.gridx = 1;
        posBottomPanel.add(amountLabel, constraints);

        customerNameField = new JTextField();
        customerNameField.setName(WindowLabels.CUSTOMER_NAME_TEXT_FIELD_NAME);
        customerNameField.setEnabled(false);
        customerNameField.setVisible(true);
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        posBottomPanel.add(customerNameField, constraints);

        saleAmountField = new JTextField();
        saleAmountField.setName(WindowLabels.SALE_AMOUNT_LABEL_FIELD_NAME);
        saleAmountField.setEnabled(false);
        saleAmountField.setVisible(true);
        constraints.gridx = 1;
        posBottomPanel.add(saleAmountField, constraints);

        loyaltyDiscountButton = new JRadioButton();
        loyaltyDiscountButton.setName(WindowLabels.LOYALTY_DISCOUNT_BUTTON_NAME);
        loyaltyDiscountButton.setText(WindowLabels.LOYALTY_DISCOUNT_BUTTON_TEXT);
        loyaltyDiscountButton.setEnabled(false);
        loyaltyDiscountButton.addActionListener(controller);
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.fill = GridBagConstraints.NONE;
        posBottomPanel.add(loyaltyDiscountButton, constraints);

        submitOrderButton = new JButton();
        submitOrderButton.setName(WindowLabels.SUBMIT_ORDER_BUTTON_NAME);
        submitOrderButton.setText(WindowLabels.SUBMIT_ORDER_BUTTON_TEXT);
        submitOrderButton.setEnabled(false);
        submitOrderButton.addActionListener(controller);
        constraints.gridx = 1;
        posBottomPanel.add(submitOrderButton, constraints);

        saleSuccessMessageLabel = new JLabel();
        saleSuccessMessageLabel.setName(WindowLabels.SALE_SUCCESS_LABEL_NAME);
        saleSuccessMessageLabel.setText(" ");
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 2;
        posBottomPanel.add(saleSuccessMessageLabel, constraints);

        return posBottomPanel;
    }

    // Methods to get specific components

    JPanel orderInputPanel() {return orderInputPanel;}

    JComboBox<String> supplierComboBox() {return supplierComboBox;}

    JTextField priceTextField() {return priceTextField;}

    JComboBox<Integer> amountComboBox() {return amountComboBox;}

    JButton submitButton() {return submitButton;}

    JLabel successLabel() {return successLabel;}

    JLabel messageLabel() {return messageLabel;}

    JComboBox<String> supplierNamesComboBox() {return supplierNames;}

    TableRowSorter<DefaultTableModel> ordersTableSorter() {return ordersTableSorter;}

    DefaultTableModel ordersTableModel() {return ordersTableModel;}

    JPanel singleTypePanel() {return singleTypePanel;}

    JPanel mixedTypePanel() {return mixedTypePanel;}

    JLabel saleMessageLabel() {return saleMessageLabel;}

    JLabel saleSuccessMessageLabel() {return saleSuccessMessageLabel;}

    JTextField customerNameField() {return customerNameField;}

    JTextField saleAmountField() {return saleAmountField;}

    JRadioButton loyaltyDiscountButton() {return loyaltyDiscountButton;}

    JButton submitOrderButton() {return submitOrderButton;}

    JComboBox<String> singleComboBox() {
        return singleComboBox;
    }

    JComboBox<String> mixedComboBox1() {
        return mixedComboBox1;
    }

    JComboBox<String> mixedComboBox2() {
        return mixedComboBox2;
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

    void updateInventoryCount(final int merlotCount, final int roseCount, final int sauvignonCount, final int totalCount) {
        merlotAmount.setText(String.valueOf(merlotCount));
        sauvignonAmount.setText(String.valueOf(sauvignonCount));
        roseAmount.setText(String.valueOf(roseCount));
        totalAmount.setText(String.valueOf(totalCount));
    }
}
