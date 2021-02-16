package mt.edu.uom.youstockit.ordering;

import mt.edu.uom.youstockit.services.email.EmailSender;
import mt.edu.uom.youstockit.services.ServiceLocator;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class OrderingFacadeTests
{
    static ServiceLocator serviceLocator;
    EmailSender emailSender;
    StockOrderer stockOrderer;
    ProductCatalogue availableItems;
    ProductCatalogue discontinuedItems;
    OrderingFacade orderingFacade;

    @BeforeAll
    public static void setupBeforeAll()
    {
        // Get reference to service locator singleton
        serviceLocator = ServiceLocator.getInstance();
    }

    @BeforeEach
    public void setupBeforeEach()
    {
        // Mock email service before each test
        emailSender = Mockito.mock(EmailSender.class);
        serviceLocator.registerService("EmailSender", emailSender);

        // Create new mock dependencies before each test
        stockOrderer = Mockito.mock(StockOrderer.class);
        availableItems = Mockito.mock(ProductCatalogue.class);
        discontinuedItems = Mockito.mock(ProductCatalogue.class);
        // Then create new instance of the SUT
        orderingFacade = new OrderingFacade(stockOrderer, availableItems, discontinuedItems);
    }

    @AfterEach
    public void teardown()
    {
        // Remove references to old objects before each test
        emailSender = null;
        stockOrderer = null;
        availableItems = null;
        orderingFacade = null;

        // Clear service locator state after each test
        serviceLocator.clear();
    }

    @Test
    public void testPlaceOrderWhenObjectDoesNotExist()
    {
        // Setup
        // Set catalogue to fail to return an item
        when(availableItems.getById(anyInt())).thenReturn(null);

        // Exercise
        FacadeResponse response = orderingFacade.placeOrder(1, 20);

        // Verify
        // Order should fail since stock item with id is not found
        Assertions.assertFalse(response.succeeded);
        String expectedMessage = "Stock item with ID 1 does not exist.";
        Assertions.assertEquals(response.message, expectedMessage);
    }

    @Test
    public void testPlaceOrderWhenThereIsNotEnoughStock()
    {
        // Setup
        // Set catalogue to return an item with a set quantity (so it can be displayed in an error)
        StockItem stockItem = new StockItem(1);
        stockItem.setQuantity(20);
        when(availableItems.getById(anyInt())).thenReturn(stockItem);
        // Set stock orderer to return that the order was not successful (due to insufficient quantity)
        when(stockOrderer.processOrder(eq(stockItem), anyInt())).thenReturn(false);

        // Exercise
        FacadeResponse response = orderingFacade.placeOrder(1, 21);

        // Verify
        Assertions.assertFalse(response.succeeded);
        String expectedMessage = "Requested quantity is invalid. Must be between 1 and 20 (inclusive).";
        Assertions.assertEquals(response.message, expectedMessage);
    }

    @Test
    public void testPlaceOrderWhenOrderSucceeds()
    {
        // Setup
        // Set catalogue to return an item
        StockItem stockItem = new StockItem(1);
        when(availableItems.getById(anyInt())).thenReturn(stockItem);
        // Set stock orderer to return that the order was successful
        when(stockOrderer.processOrder(eq(stockItem), anyInt())).thenReturn(true);

        // Exercise
        FacadeResponse response = orderingFacade.placeOrder(1, 20);

        // Verify
        Assertions.assertTrue(response.succeeded);
        String expectedMessage = "Order placed successfully.";
        Assertions.assertEquals(response.message, expectedMessage);
    }

    @Test
    public void testPlaceOrderWhenOrderSucceedsAndItemGoesOutOfStock()
    {
        // Setup
        // Set catalogue to return a item which should not be restocked (0 minimum order quantity)
        StockItem stockItem = new StockItem(1);
        stockItem.setQuantity(20);
        stockItem.setMinimumOrderQuantity(0);
        when(availableItems.getById(anyInt())).thenReturn(stockItem);
        // Set stock orderer to return that the order was successful, and remove all items in stock
        doAnswer(invocationOnMock -> {
            StockItem item = invocationOnMock.getArgumentAt(0, StockItem.class);
            item.setQuantity(0);
            return true;
        }).when(stockOrderer).processOrder(eq(stockItem), anyInt());

        // Exercise (order all items in stock)
        FacadeResponse response = orderingFacade.placeOrder(1, 20);

        // Verify
        Assertions.assertTrue(response.succeeded);
        String expectedMessage = "Order placed successfully.\nItem has gone out of stock, removing from catalogue...";
        Assertions.assertEquals(response.message, expectedMessage);
        // Since all items in stock are bought, the system should try to delete the item
        verify(availableItems, times(1)).remove(eq(1));
        // It should also try to add it to the catalogue of discontinued items
        verify(discontinuedItems, times(1)).add(stockItem);
    }

    @Test
    public void testDeleteItemWhenItemDoesNotExist()
    {
        // Setup
        // Set product catalogue to return that product was not deleted
        when(availableItems.remove(eq(1))).thenReturn(false);

        // Exercise
        FacadeResponse response = orderingFacade.deleteItem(1);

        // Verify
        Assertions.assertFalse(response.succeeded);
        String expectedMessage = "Stock item with ID 1 does not exist.";
        Assertions.assertEquals(response.message, expectedMessage);
    }

    @Test
    public void testDeleteItemWhenItemIsInStock()
    {
        // Setup
        // Set product catalogue to return an item which is in stock
        StockItem stockItem = new StockItem(1);
        stockItem.setQuantity(20);
        when(availableItems.getById(eq(1))).thenReturn(stockItem);
        // Set product catalogue to return that product was deleted successfully
        when(availableItems.remove(eq(1))).thenReturn(true);

        // Exercise
        FacadeResponse response = orderingFacade.deleteItem(1);

        // Verify
        Assertions.assertFalse(response.succeeded);
        String expectedMessage = "Deleted item from catalogue and notified manager via email.";
        Assertions.assertEquals(response.message, expectedMessage);
        // Check that the method tried to notify the manager via email
        verify(emailSender, times(1)).sendEmailToManager(anyString());
    }

    @Test
    public void testDeleteItemWhenItemIsOutOfStock()
    {
        // Setup
        // Set product catalogue to return an item which is out of stock
        StockItem stockItem = new StockItem(1);
        stockItem.setQuantity(0);
        when(availableItems.getById(eq(1))).thenReturn(stockItem);
        // Set product catalogue to return that product was deleted successfully
        when(availableItems.remove(eq(1))).thenReturn(true);

        // Exercise
        FacadeResponse response = orderingFacade.deleteItem(1);

        // Verify
        Assertions.assertFalse(response.succeeded);
        String expectedMessage = "Deleted item from catalogue";
        Assertions.assertEquals(response.message, expectedMessage);
        // Check that the method did not try to notify the manager via email
        verify(emailSender, times(0)).sendEmailToManager(anyString());
    }

    @Test
    public void testCalculateProfitWithEmptyProductCatalogues()
    {
        // Setup
        // Set product catalogue to return an empty list
        List<StockItem> items = new ArrayList<>();
        when(availableItems.getAll()).thenReturn(items);

        // Exercise
        double profits = orderingFacade.calculateProfit();

        // Verify
        // Profits should be zero, since there are no stock items
        Assertions.assertEquals(0,profits);
    }

    @Test
    public void testCalculateProfitWithOneAvailableItem()
    {
        // Setup
        // This stock item should have a profit of (0.5-0.25) * 20 = 5
        StockItem item = new StockItem(1);
        item.setBuySellPrices(0.25,0.5);
        item.incrementNumTimesSold(20);
        // Set product catalogue of available items to return a list with one item
        List<StockItem> items = new ArrayList<>();
        items.add(item);
        when(availableItems.getAll()).thenReturn(items);

        // Exercise
        double profits = orderingFacade.calculateProfit();

        // Verify
        Assertions.assertEquals(5, profits, 0.001);
    }

    @Test
    public void testCalculateProfitWithTwoAvailableItems()
    {
        // Setup
        // This stock item should have a profit of (0.5-0.25) * 20 = 5
        StockItem item1 = new StockItem(1);
        item1.setBuySellPrices(0.25,0.5);
        item1.incrementNumTimesSold(20);
        // This stock item should have a profit of (19.99 - 9.99) * 5 = 50
        StockItem item2 = new StockItem(2);
        item2.setBuySellPrices(9.99, 19.99);
        item2.incrementNumTimesSold(5);
        // Set product catalogue of available items to return a list with two items
        List<StockItem> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        when(availableItems.getAll()).thenReturn(items);

        // Exercise
        double profits = orderingFacade.calculateProfit();

        // Verify
        // Total profit should be 5 + 50 = 55
        Assertions.assertEquals(55, profits, 0.001);
    }

    @Test
    public void testCalculateProfitWithOneDiscontinuedItem()
    {
        // Setup
        // This stock item should have a profit of (1.5-0.8) * 15 = 10.5
        StockItem item = new StockItem(1);
        item.setBuySellPrices(0.80,1.50);
        item.incrementNumTimesSold(15);
        // Set product catalogue of discontinued items to return a list with one item
        List<StockItem> items = new ArrayList<>();
        items.add(item);
        when(discontinuedItems.getAll()).thenReturn(items);

        // Exercise
        double profits = orderingFacade.calculateProfit();

        // Verify
        Assertions.assertEquals(10.5, profits, 0.001);
    }

    @Test
    public void testCalculateProfitWithOneAvailableAndOneDiscontinuedItem()
    {
        // Setup
        // This stock item should have a profit of (0.5-0.25) * 20 = 5
        StockItem item1 = new StockItem(1);
        item1.setBuySellPrices(0.25,0.5);
        item1.incrementNumTimesSold(20);
        // This stock item should have a profit of (1.5-0.8) * 15 = 10.5
        StockItem item2 = new StockItem(1);
        item2.setBuySellPrices(0.80,1.50);
        item2.incrementNumTimesSold(15);

        // Set product catalogue of available items to return a list with one item
        List<StockItem> items = new ArrayList<>();
        items.add(item1);
        when(availableItems.getAll()).thenReturn(items);
        // Set product catalogue of discontinued items to return a list with one item
        items = new ArrayList<>();
        items.add(item2);
        when(discontinuedItems.getAll()).thenReturn(items);

        // Exercise
        double profits = orderingFacade.calculateProfit();

        // Verify
        // Total profits should be 5 + 10.5 = 15.5
        Assertions.assertEquals(15.5, profits, 0.001);
    }

    /* The following tests are added for coverage purposes */

    @Test
    public void testAddItem()
    {
        // Setup
        StockItem stockItem = new StockItem(1);

        // Exercise
        orderingFacade.addItem(stockItem);

        // Verify
        // The facade should try to add the item to the catalogue of available items
        verify(availableItems, times(1)).add(eq(stockItem));
    }

    @Test
    public void getAvailableItems()
    {
        // Setup
        // Set catalogue available items to return a list of dummy stock items
        List<StockItem> items = new ArrayList<>();
        items.add(new StockItem(1));
        items.add(new StockItem(2));
        when(availableItems.getAll()).thenReturn(items);

        // Exercise
        List<StockItem> returnedItems = orderingFacade.getAvailableItems();

        // Verify
        // The facade should return a list with the same items
        Assertions.assertEquals(items, returnedItems);
    }

    @Test
    public void getAvailableItemsByCategory()
    {
        // Setup
        // Set catalogue available items to return a list of dummy stock items for the category "Category1"
        List<StockItem> items = new ArrayList<>();
        items.add(new StockItem(1));
        items.add(new StockItem(2));
        when(availableItems.getByCategory(eq("Category1"))).thenReturn(items);

        // Exercise
        List<StockItem> returnedItems = orderingFacade.getAvailableItems("Category1");

        // Verify
        // The facade should return the list of items created while setting up the test
        Assertions.assertEquals(items, returnedItems);
    }
}
