package com.github.ksouthwood.possystem;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class Controller extends AbstractAction implements ChangeListener {
    private final RootWindow rootWindow;
    private final DBHandler  dbHandler;

    private final HashMap<String, Integer> wineInventory = new HashMap<>();

    private final String TOTAL_BOTTLES_KEY = "Total";


    public Controller(final RootWindow rootWindow, final String databaseName) {
        this.rootWindow = rootWindow;
        this.dbHandler = new DBHandler(databaseName);
        initBottlesMap();
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        var source = (JComponent) actionEvent.getSource();

        switch (source.getName()) {
            case WindowLabels.MERLOT_BUTTON,
                 WindowLabels.ROSE_BUTTON,
                 WindowLabels.SAUVIGNON_BUTTON -> wineButtonSelected();

            case WindowLabels.SUBMIT_BUTTON_NAME -> submitButtonPressed();
            case WindowLabels.FILTER_BUTTON_NAME -> filterSuppliers();

            case WindowLabels.SINGLE_TYPE_BUTTON_NAME,
                 WindowLabels.MIXED_TYPE_BUTTON_NAME -> orderTypeButtonSelected(source.getName());

            case WindowLabels.SINGLE_WINE_CONFIRM_BUTTON_NAME -> singleWineOrder();
            case WindowLabels.MIXED_WINE_CONFIRM_BUTTON_NAME -> mixedWineOrder();

            case WindowLabels.SUBMIT_ORDER_BUTTON_NAME -> customerOrderPlaced();
            default -> {
            }
        }
    }

    private void initBottlesMap() {
        wineInventory.put(WindowLabels.MERLOT_BUTTON_TEXT, 0);
        wineInventory.put(WindowLabels.ROSE_BUTTON_TEXT, 0);
        wineInventory.put(WindowLabels.SAUVIGNON_BUTTON_TEXT, 0);
        wineInventory.put(TOTAL_BOTTLES_KEY, 0);
    }

    private void wineButtonSelected() {
        rootWindow.orderInputPanel()
                  .setVisible(true);
        rootWindow.submitButton()
                  .setEnabled(true);
        rootWindow.successLabel()
                  .setVisible(false);
        rootWindow.messageLabel()
                  .setText("");
    }

    private void submitButtonPressed() {
        var price = getPrice();
        if (price.isEmpty()) {
            return;
        }

        var amount = getAmount();
        if (amount.isEmpty()) {
            return;
        }

        var supplier = getSupplier();
        if (supplier.isEmpty()) {
            return;
        }

        var wine = rootWindow.getSelectedWine();

        inputOrderValid(supplier.get(), wine, amount.get(), price.get());
    }

    private void inputOrderValid(final String supplier, final String wine, final int cases, final double price) {
        int     bottles = cases * 12;
        boolean isPaid  = false; // new order so it isn't paid yet

        rootWindow.messageLabel()
                  .setText(String.format("Added %d cases of %s from %s (%d bottles).",
                                         cases, wine, supplier, bottles));
        rootWindow.ordersTableModel()
                  .addRow(
                          new Object[]{supplier, wine, bottles, price, isPaid}
                  );
        dbHandler.addSupplierOrder(supplier, wine, bottles, price, isPaid);

        updateInventoryCount(wine, bottles);

        rootWindow.successLabel()
                  .setVisible(true);
        rootWindow.submitButton()
                  .setEnabled(false);
        rootWindow.orderInputPanel()
                  .setVisible(false);
        rootWindow.amountComboBox()
                  .setSelectedIndex(-1);
        rootWindow.priceTextField()
                  .setText("");

        if (rootWindow.supplierComboBox()
                      .getSelectedIndex() == -1) {
            rootWindow.supplierComboBox()
                      .addItem(supplier);
            rootWindow.supplierNamesComboBox()
                      .addItem(supplier);
        }

        rootWindow.supplierComboBox()
                  .setSelectedIndex(-1);
    }

    private void updateInventoryCount(final String wine, final int bottles) {
        wineInventory.put(wine, wineInventory.get(wine) + bottles);
        wineInventory.put(TOTAL_BOTTLES_KEY, wineInventory.get(TOTAL_BOTTLES_KEY) + bottles);
    }

    private Optional<String> getSupplier() {
        var supplier      = rootWindow.supplierComboBox();
        var supplierName  = (String) supplier.getSelectedItem();
        var supplierIndex = supplier.getSelectedIndex();

        if (supplierName == null) {
            return Optional.empty();
        }

        if (supplierIndex == -1) {
            if (!supplierName.matches("[\\w ]+")) {
                rootWindow.messageLabel()
                          .setText("Error: supplier name contains invalid characters");
                resetOrderInputPanel();
                return Optional.empty();
            }
        }

        return Optional.of(supplierName);
    }

    private Optional<Double> getPrice() {
        var price = rootWindow.priceTextField()
                              .getText();
        try {
            return Optional.of(Double.parseDouble(price));
        } catch (NumberFormatException nfe) {
            rootWindow.messageLabel()
                      .setText("Error: price must be a number");
            resetOrderInputPanel();
            return Optional.empty();
        }
    }

    private Optional<Integer> getAmount() {
        var index = rootWindow.amountComboBox()
                              .getSelectedIndex();
        if (index == -1) {
            rootWindow.messageLabel()
                      .setText("Error: Please choose an amount");
            return Optional.empty();
        }
        return Optional.of(rootWindow.amountComboBox()
                                     .getItemAt(index));
    }

    private void resetOrderInputPanel() {
        rootWindow.supplierComboBox()
                  .setSelectedIndex(-1);
        rootWindow.priceTextField()
                  .setText("");
    }

    private void filterSuppliers() {
        var selected = rootWindow.supplierNamesComboBox()
                                 .getSelectedItem();
        if (selected == null) {
            return;
        }

        String supplier = selected.toString();
        if (supplier.equals(WindowLabels.ORDERS_COMBO_ALL)) {
            rootWindow.ordersTableSorter()
                      .setRowFilter(null);
        } else {
            rootWindow.ordersTableSorter()
                      .setRowFilter(getRowFilter(supplier));
        }
    }

    private RowFilter<DefaultTableModel, Object> getRowFilter(final String supplier) {
        return new RowFilter<>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ?> entry) {
                return entry.getStringValue(0)
                            .equals(supplier);
            }
        };
    }

    void loadFromDatabase() {
        var orders = dbHandler.getAllRows();

        if (orders != null) {
            Set<String> suppliers = new HashSet<>();
            for (var order : orders) {
                rootWindow.ordersTableModel()
                          .addRow(order);
                suppliers.add(order[0].toString());
                updateInventoryCount(order[1].toString(), (int) order[2]);
            }

            suppliers.forEach(supplier -> {
                rootWindow.supplierComboBox()
                          .addItem(supplier);
                rootWindow.supplierNamesComboBox()
                          .addItem(supplier);
            });

            rootWindow.supplierComboBox()
                      .setSelectedIndex(-1);

        }
    }

    private void orderTypeButtonSelected(final String buttonName) {
        rootWindow.saleSuccessMessageLabel()
                  .setText(" ");

        switch (buttonName) {
            case WindowLabels.SINGLE_TYPE_BUTTON_NAME -> {
                rootWindow.singleTypePanel()
                          .setVisible(true);
                rootWindow.mixedTypePanel()
                          .setVisible(false);
            }
            case WindowLabels.MIXED_TYPE_BUTTON_NAME -> {
                rootWindow.mixedTypePanel()
                          .setVisible(true);
                rootWindow.singleTypePanel()
                          .setVisible(false);
            }
            default -> {
            }
        }
    }

    private void singleWineOrder() {
        var wineType = (String) rootWindow.singleComboBox()
                                          .getSelectedItem();
        rootWindow.saleMessageLabel()
                  .setText(String.format("Selected wine: %s", wineType));
        enablePosBottomPanel();
    }

    private void mixedWineOrder() {
        var wineType1 = (String) rootWindow.mixedComboBox1()
                                           .getSelectedItem();
        var wineType2 = (String) rootWindow.mixedComboBox2()
                                           .getSelectedItem();
        if (wineType1.equals(wineType2)) {
            rootWindow.saleMessageLabel()
                      .setText("Error: Wine types must be different");
            return;
        }

        rootWindow.saleMessageLabel()
                  .setText(String.format("Selected wines: %s and %s", wineType1, wineType2));
        enablePosBottomPanel();
    }

    private void customerOrderPlaced() {
        var customerName = rootWindow.customerNameField()
                                     .getText();
        var amount = rootWindow.saleAmountField()
                               .getText();
        if (isCustomerOrderValid(customerName, amount)) {
            processCustomerOrder(customerName, amount);
        }
    }

    boolean isCustomerOrderValid(final String customerName, final String amount) {
        if (!customerName.matches("[A-Z][a-z]*")) {
            rootWindow.saleSuccessMessageLabel()
                      .setText("Error: Customer name must start with a capital letter and contain only letters");
            return false;
        }
        try {
            Double.parseDouble(amount);
        } catch (NumberFormatException nfe) {
            rootWindow.saleSuccessMessageLabel()
                      .setText("Error: Amount must be a number");
            return false;
        }
        return true;
    }

    private void processCustomerOrder(final String customerName, final String amount) {
        var discount = rootWindow.loyaltyDiscountButton()
                                 .isSelected()
                       ? 0.85
                       : 1.00;
        var price = Double.parseDouble(amount) * discount;

        boolean inventoryUpdated;

        if (rootWindow.singleTypePanel()
                      .isVisible()) {
            inventoryUpdated = removeFromInventory(rootWindow.singleComboBox()
                                                             .getSelectedItem()
                                                             .toString(), 12);
        } else {
            inventoryUpdated = removeFromInventory(rootWindow.mixedComboBox1()
                                                             .getSelectedItem()
                                                             .toString(), 6) &&
                    removeFromInventory(rootWindow.mixedComboBox2()
                                                  .getSelectedItem()
                                                  .toString(), 6);
        }

        if (inventoryUpdated) {
            rootWindow.saleSuccessMessageLabel()
                      .setText(String.format("Order sold to %s for $%f", customerName, price));
            disablePosBottomPanel();
        } else {
            rootWindow.saleSuccessMessageLabel()
                      .setText("Error: Not enough inventory to fulfill order");
        }
    }

    private boolean removeFromInventory(final String wine, final int bottles) {
        if (wineInventory.get(wine) < bottles) {
            return false;
        }
        wineInventory.put(wine, wineInventory.get(wine) - bottles);
        wineInventory.put(TOTAL_BOTTLES_KEY, wineInventory.get(TOTAL_BOTTLES_KEY) - bottles);
        return true;
    }

    private void enablePosBottomPanel() {
        rootWindow.customerNameField()
                  .setEnabled(true);
        rootWindow.saleAmountField()
                  .setEnabled(true);
        rootWindow.loyaltyDiscountButton()
                  .setEnabled(true);
        rootWindow.submitOrderButton()
                  .setEnabled(true);
        rootWindow.saleSuccessMessageLabel()
                  .setText(" ");
    }

    private void disablePosBottomPanel() {
        rootWindow.customerNameField()
                  .setEnabled(false);
        rootWindow.customerNameField()
                  .setText("");

        rootWindow.saleAmountField()
                  .setEnabled(false);
        rootWindow.saleAmountField()
                  .setText("");

        rootWindow.loyaltyDiscountButton()
                  .setEnabled(false);
        rootWindow.loyaltyDiscountButton()
                  .setSelected(false);

        rootWindow.submitOrderButton()
                  .setEnabled(false);

        rootWindow.singleTypePanel()
                  .setVisible(false);
        rootWindow.mixedTypePanel()
                  .setVisible(false);

        rootWindow.saleMessageLabel()
                  .setText(" ");
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        var tabbedPane = (JTabbedPane) e.getSource();
        var tabIndex   = tabbedPane.indexOfTab(WindowLabels.INVENTORY_TAB_NAME);
        if (tabbedPane.getSelectedIndex() == tabIndex) {
            rootWindow.updateInventoryCount(wineInventory.get(WindowLabels.MERLOT_BUTTON_TEXT),
                                            wineInventory.get(WindowLabels.ROSE_BUTTON_TEXT),
                                            wineInventory.get(WindowLabels.SAUVIGNON_BUTTON_TEXT),
                                            wineInventory.get(TOTAL_BOTTLES_KEY));
        }
    }
}
