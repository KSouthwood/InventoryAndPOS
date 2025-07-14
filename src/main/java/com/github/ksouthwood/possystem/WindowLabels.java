package com.github.ksouthwood.possystem;

/**
 * A class to hold the Strings for the names of all the window components. Useful to have one source to worry about and
 * easier to write tests (since we won't have to write the names over and over.)
 */
public class WindowLabels {
    // Window labels
    public static final String WINDOW_TITLE = "Wine Merchant";
    public static final String WINDOW_NAME  = "RootWindow";

    // Tabbed Pane strings
    public static final String TABBED_PANE_NAME    = "TabbedPane";
    public static final String SUPPLIER_PANE_NAME  = "InputOrderPane";
    public static final String SUPPLIER_PANE_TITLE = "Input Supplier Order";
    public static final String ORDERS_PANE_NAME    = "SupplierOrdersPane";
    public static final String ORDERS_PANE_TITLE   = "Supplier Orders List";

    // Top Panel strings
    public static final String TOP_PANEL_NAME = "TopPanel";

    public static final String INSTRUCTION_LABEL_NAME = "InstructionLabel";
    public static final String INSTRUCTION_LABEL_TEXT = "Select the type of wine:";

    public static final String MERLOT_BUTTON         = "MerlotButton";
    public static final String MERLOT_BUTTON_TEXT    = "Merlot";
    public static final String ROSE_BUTTON           = "RoseButton";
    public static final String ROSE_BUTTON_TEXT      = "Rose";
    public static final String SAUVIGNON_BUTTON      = "SauvignonButton";
    public static final String SAUVIGNON_BUTTON_TEXT = "Sauvignon";

    // Middle Panel strings
    public static final String MIDDLE_PANEL_NAME          = "MiddlePanel";
    public static final String AMOUNT_COMBO_BOX           = "AmountComboBox";
    public static final String SUPPLIER_COMBO_BOX         = "SupplierComboBox";
    public static final String PURCHASED_PRICE_TEXT_FIELD = "PurchasedPriceTextField";

    // Bottom Panel strings
    public static final String BOTTOM_PANEL_NAME  = "BottomPanel";
    public static final String SUCCESS_LABEL_NAME = "SuccessLabel";
    public static final String SUCCESS_LABEL_TEXT = "Success!";
    public static final String SUBMIT_BUTTON_NAME = "SubmitButton";
    public static final String SUBMIT_BUTTON_TEXT = "Submit";
    public static final String MESSAGE_LABEL_NAME = "MessageLabel";

    // Supplier Order Pane strings
    public static final String   ORDERS_COMBO_BOX     = "SupplierOrderComboBox";
    public static final String   ORDERS_COMBO_ALL     = "All Orders";
    public static final String   FILTER_BUTTON_NAME   = "FilterButton";
    public static final String   FILTER_BUTTON_TEXT   = "Filter";
    public static final String   ORDERS_TABLE_NAME    = "OrdersTable";
    public static final String[] ORDERS_TABLE_COLUMNS = {"Supplier", "Wine", "Amount Purchased", "Purchased Price",
                                                         "Paid"};

    // Inventory Pane strings
    public static final String INVENTORY_TAB_NAME = "Inventory";
    public static final String MERLOT_AMOUNT_TEXT = "Merlot:";
    public static final String MERLOT_AMOUNT_LABEL_NAME = "MerlotAmountLabel";
    public static final String MERLOT_AMOUNT_LABEL_TEXT = "0";
    public static final String ROSE_AMOUNT_TEXT = "Rose:";
    public static final String ROSE_AMOUNT_LABEL_NAME = "RoseAmountLabel";
    public static final String ROSE_AMOUNT_LABEL_TEXT = "0";
    public static final String SAUVIGNON_AMOUNT_TEXT = "Sauvignon:";
    public static final String SAUVIGNON_AMOUNT_LABEL_NAME = "SauvignonAmountLabel";
    public static final String SAUVIGNON_AMOUNT_LABEL_TEXT = "0";
    public static final String TOTAL_AMOUNT_TEXT = "Total:";
    public static final String TOTAL_AMOUNT_LABEL_NAME = "TotalAmountLabel";
    public static final String TOTAL_AMOUNT_LABEL_TEXT = "0";
}
