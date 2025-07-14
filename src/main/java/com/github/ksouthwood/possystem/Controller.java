package com.github.ksouthwood.possystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class Controller extends AbstractAction {
    final private RootWindow rootWindow;
    final private DBHandler  dbHandler;

    private int bottlesRose = 0;
    private int bottlesMerlot = 0;
    private int bottlesSauvignon = 0;
    private int bottlesTotal = 0;

    public Controller(final RootWindow rootWindow, final String databaseName) {
        this.rootWindow = rootWindow;
        this.dbHandler = new DBHandler(databaseName);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        var source = (JComponent) actionEvent.getSource();

        switch (source) {
            case JRadioButton ignored -> wineButtonSelected();
            case JButton button -> {
                switch (button.getName()) {
                    case WindowLabels.SUBMIT_BUTTON_NAME -> submitButtonPressed();
                    case WindowLabels.FILTER_BUTTON_NAME -> filterSuppliers();
                }
            }
            default -> {
            }
        }
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
        switch(wine) {
            case WindowLabels.MERLOT_BUTTON_TEXT -> bottlesMerlot += bottles;
            case WindowLabels.ROSE_BUTTON_TEXT -> bottlesRose += bottles;
            case WindowLabels.SAUVIGNON_BUTTON_TEXT -> bottlesSauvignon += bottles;
        }
        bottlesTotal += bottles;
        rootWindow.updateInventoryCount(bottlesMerlot, bottlesRose, bottlesSauvignon, bottlesTotal);
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
        }
    }
}
