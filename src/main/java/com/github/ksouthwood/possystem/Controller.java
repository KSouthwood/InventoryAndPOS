package com.github.ksouthwood.possystem;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Optional;

public class Controller extends AbstractAction {
    final private RootWindow rootWindow;

    public Controller(RootWindow rootWindow) {
        this.rootWindow = rootWindow;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        var source = (JComponent) actionEvent.getSource();

        switch (source) {
            case JRadioButton ignored -> wineButtonSelected();
            case JButton ignored -> submitButtonPressed();
            default -> {}
        }
    }

    private void wineButtonSelected() {
        rootWindow.getOrderPanel().setVisible(true);
        rootWindow.getSubmitButton().setEnabled(true);
        rootWindow.getSuccessLabel().setVisible(false);
        rootWindow.setMessageLabelText("");
    }

    private void submitButtonPressed() {
        var price = rootWindow.getPrice();
        if (price.isEmpty()) { return; }

        var amount = rootWindow.getAmount();
        if (amount.isEmpty()) { return; }

        var supplier = getSupplier();
        if (supplier.isEmpty()) { return; }

        var wine = rootWindow.getSelectedWine();

        rootWindow.getMessageLabel().setText(String.format("Added %d cases of %s from %s (%d bottles).",
                                                     amount.get(), wine, supplier.get(), amount.get() * 12));
        rootWindow.orderSuccessful();
    }

    private Optional<String> getSupplier() {
        var supplier = rootWindow.getSupplier();
        if (!supplier.get("supplier").matches("[\\w ]+")) {
            rootWindow.setMessageLabelText("Error: supplier name contains invalid characters");
            rootWindow.resetOrderPanel();
            return Optional.empty();
        }

        if (supplier.get("index").equals("-1")) {
            rootWindow.updateSupplierComboBox(supplier.get("supplier"));
        }

        return Optional.of(supplier.get("supplier"));
    }
}
