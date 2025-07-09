package com.github.ksouthwood.possystem;

import org.assertj.swing.core.ComponentLookupScope;
import org.assertj.swing.data.Index;
import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.exception.ComponentLookupException;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JPanelFixture;
import org.junit.jupiter.api.*;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RootWindowTest {
    private static FrameFixture window;

    @BeforeAll
    public static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
        RootWindow frame = GuiActionRunner.execute(RootWindow::new);
        Assertions.assertNotNull(frame);
        window = new FrameFixture(frame);
        window.robot()
              .settings()
              .componentLookupScope(ComponentLookupScope.ALL);
        window.robot()
              .settings()
              .delayBetweenEvents(50);
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    void tearDown() {

    }

    @AfterAll
    static void finish() {
        window.cleanUp();
    }

    @Test
    @Order(1)
    public void testWindowInitialState() {
        // check JFrame
        assertEquals(WindowLabels.WINDOW_NAME, window.target().getName(),
                     "The window title should be " + WindowLabels.WINDOW_TITLE);
        try {
            window.tabbedPane(WindowLabels.TABBED_PANE_NAME)
                  .requireVisible()
                  .requireSelectedTab(Index.atIndex(0));
        } catch (ComponentLookupException e) {
            fail("The window should have a tabbed pane named " + WindowLabels.TABBED_PANE_NAME);
        } catch (AssertionError error) {
            fail("The tabbed pane should be visible and set to " + WindowLabels.SUPPLIER_PANE_TITLE);
        }

        // get panels
        JPanelFixture top    = window.panel(WindowLabels.TOP_PANEL_NAME);
        JPanelFixture middle = window.panel(WindowLabels.MIDDLE_PANEL_NAME);
        JPanelFixture bottom = window.panel(WindowLabels.BOTTOM_PANEL_NAME);

        // check panels visibility
        try {
            top.requireVisible();
            middle.requireNotVisible();
            bottom.requireVisible();
        } catch (AssertionError e) {
            fail("Make sure the initial panel visibilities are correct");
        }

        // check the top panel
        try {
            top.label(WindowLabels.INSTRUCTION_LABEL_NAME)
               .requireText(WindowLabels.INSTRUCTION_LABEL_TEXT)
               .requireVisible();
            top.radioButton(WindowLabels.MERLOT_BUTTON)
               .requireText(WindowLabels.MERLOT_BUTTON_TEXT)
               .requireEnabled();
            top.radioButton(WindowLabels.ROSE_BUTTON)
               .requireText(WindowLabels.ROSE_BUTTON_TEXT)
               .requireEnabled();
            top.radioButton(WindowLabels.SAUVIGNON_BUTTON)
               .requireText(WindowLabels.SAUVIGNON_BUTTON_TEXT)
               .requireEnabled();
        } catch (ComponentLookupException e) {
            fail(String.format("The top panel should contain %s, %s, %s, & %s.",
                               WindowLabels.INSTRUCTION_LABEL_NAME, WindowLabels.MERLOT_BUTTON,
                               WindowLabels.ROSE_BUTTON, WindowLabels.SAUVIGNON_BUTTON));
        } catch (AssertionError e) {
            fail(String.format("%s, %s, %s, & %s should have their respective texts.",
                               WindowLabels.INSTRUCTION_LABEL_NAME, WindowLabels.MERLOT_BUTTON,
                               WindowLabels.ROSE_BUTTON, WindowLabels.SAUVIGNON_BUTTON));
        }

        // check the bottom panel
        try {
            bottom.label(WindowLabels.SUCCESS_LABEL_NAME)
                  .requireNotVisible();
            bottom.button(WindowLabels.SUBMIT_BUTTON_NAME)
                  .requireDisabled();
            bottom.label(WindowLabels.MESSAGE_LABEL_NAME)
                  .requireVisible()
                  .requireText("");
        } catch (ComponentLookupException e) {
            fail("Bottom panel does not contain all required components.");
        } catch (AssertionError error) {
            fail("Bottom panel components are not in their correct initial state.");
        }
    }

    @Test
    @Order(2)
    void testWineButtonClick() {
        var merlot      = window.radioButton(WindowLabels.MERLOT_BUTTON);
        var sauvignon   = window.radioButton(WindowLabels.SAUVIGNON_BUTTON);
        var rose        = window.radioButton(WindowLabels.ROSE_BUTTON);
        var middlePanel = window.panel(WindowLabels.MIDDLE_PANEL_NAME);

        // Click on a wine button
        middlePanel.requireNotVisible();
        merlot.click();

        // Check state after clicking on a wine button
        merlot.requireSelected();
        middlePanel.requireVisible();
        middlePanel.comboBox(WindowLabels.AMOUNT_COMBO_BOX)
                   .requireVisible()
                   .requireEnabled();
        middlePanel.comboBox(WindowLabels.SUPPLIER_COMBO_BOX)
                   .requireVisible()
                   .requireEnabled()
                   .requireEditable();
        middlePanel.textBox(WindowLabels.PURCHASED_PRICE_TEXT_FIELD)
                   .requireVisible()
                   .requireEnabled()
                   .requireEditable();
        window.label(WindowLabels.SUCCESS_LABEL_NAME)
              .requireNotVisible();
        window.button(WindowLabels.SUBMIT_BUTTON_NAME)
              .requireEnabled();

        // Check that only button is selected at a time
        sauvignon.click();
        rose.click();
        try {
            sauvignon.requireNotSelected();
            merlot.requireNotSelected();
        } catch (AssertionError e) {
            fail("Only one wine selection button should be selected at once");
        }
    }

    @Test
    @Order(3)
    void testInvalidSupplierAndPrices() {
        Pattern errorMsg = Pattern.compile(".*[E|e]rror.*");
        var     amount   = window.comboBox(WindowLabels.AMOUNT_COMBO_BOX);
        var     supplier = window.comboBox(WindowLabels.SUPPLIER_COMBO_BOX);
        var     price    = window.textBox(WindowLabels.PURCHASED_PRICE_TEXT_FIELD);
        var     submit   = window.button(WindowLabels.SUBMIT_BUTTON_NAME);
        var     message  = window.label(WindowLabels.MESSAGE_LABEL_NAME);

        // Test invalid supplier name
        window.radioButton(WindowLabels.SAUVIGNON_BUTTON)
              .click();
        amount.selectItem(3);
        supplier.enterText("Ernest & Julio");
        price.enterText("23.50");
        submit.click();

        try {
            message.requireText(errorMsg);
        } catch (AssertionError e) {
            fail("""
                 Message in MessageLabel should contain the word 'error' when the supplier name contains invalid characters
                 """);
        }

        // test invalid price
        amount.selectItem(5);
        supplier.enterText("Gallo");
        price.enterText("43b");
        submit.click();

        try {
            message.requireText(errorMsg);
        } catch (AssertionError error) {
            fail("""
                 Message in MessageLabel should contain the word 'error' when the price contains invalid characters
                 """);
        }
    }

    @Test
    @Order(4)
    void testSuccessfulOrder() {
        window.radioButton(WindowLabels.ROSE_BUTTON)
              .click();

        var supplierComboBox  = window.comboBox(WindowLabels.SUPPLIER_COMBO_BOX);
        var purchaseTextField = window.textBox(WindowLabels.PURCHASED_PRICE_TEXT_FIELD);

        supplierComboBox.enterText("Ernest");
        window.comboBox(WindowLabels.AMOUNT_COMBO_BOX)
              .selectItem(0);
        purchaseTextField.enterText("123");
        window.button(WindowLabels.SUBMIT_BUTTON_NAME)
              .click();

        window.label(WindowLabels.SUCCESS_LABEL_NAME)
              .requireVisible()
              .requireText(WindowLabels.SUCCESS_LABEL_TEXT);

        assertEquals(1, supplierComboBox.contents().length,
                     "Supplier names should be added to SupplierComboBox " +
                     "after a successful submission.");
        assertEquals("Ernest", supplierComboBox.contents()[0],
                     "Supplier names should be added to SupplierComboBox " +
                     "after a successful submission.");
    }

    @Test
    @Order(5)
    void testMultipleOrders() {
        var amountComboBox = window.comboBox(WindowLabels.AMOUNT_COMBO_BOX);
        var supplierComboBox = window.comboBox(WindowLabels.SUPPLIER_COMBO_BOX);
        var purchaseTextField = window.textBox(WindowLabels.PURCHASED_PRICE_TEXT_FIELD);
        var submitButton = window.button(WindowLabels.SUBMIT_BUTTON_NAME);

        window.radioButton(WindowLabels.ROSE_BUTTON).click();
        amountComboBox.selectItem(1);
        supplierComboBox.enterText("Julio");
        purchaseTextField.enterText("14.50");
        submitButton.click();

        window.radioButton(WindowLabels.SAUVIGNON_BUTTON).click();
        amountComboBox.selectItem(3);
        supplierComboBox.enterText("Ernest");
        purchaseTextField.enterText("25.78");
        submitButton.click();

        try {
            supplierComboBox.requireItemCount(2);
            assertEquals("Ernest", supplierComboBox.contents()[0]);
            assertEquals("Julio", supplierComboBox.contents()[1]);
        } catch (AssertionError ignored) {
            fail("Supplier names should be added to SupplierComboBox after a successful submission.");
        }
    }

    @Test
    @Order(6)
    void testTableContents() {
        try {
            window.tabbedPane()
                  .selectTab(1);
        } catch (IndexOutOfBoundsException ignored) {
            fail("Tabbed pane should have two tabs.");
        }

        var ordersTable = window.table(WindowLabels.ORDERS_TABLE_NAME);
        ordersTable.requireRowCount(3);
    }
}
