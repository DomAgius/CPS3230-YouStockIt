package mt.edu.uom.youstockit.ordering;

import mt.edu.uom.youstockit.StockItem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class OrderingFacadeTests
{
    StockOrderer stockOrderer;
    ProductCatalogue catalogue;
    OrderingFacade orderingFacade;

    @BeforeEach
    public void setup()
    {
        // Create new mock dependencies before each test
        stockOrderer = Mockito.mock(StockOrderer.class);
        catalogue = Mockito.mock(ProductCatalogue.class);
        // Then create new instance of the SUT
        orderingFacade = new OrderingFacade(stockOrderer, catalogue);
    }

    @AfterEach
    public void teardown()
    {
        // Remove references to old objects before each test
        stockOrderer = null;
        catalogue = null;
        orderingFacade = new OrderingFacade(stockOrderer, catalogue);
    }

    @Test
    public void testPlaceOrderWhenObjectDoesNotExist()
    {
        // Setup
        // Set catalogue to fail to return an item
        when(catalogue.getById(anyInt())).thenReturn(null);

        // Exercise
        FacadeResponse response = orderingFacade.placeOrder(1, 20);

        // Verify
        // Order should fail since stock item with id is not found
        Assertions.assertFalse(response.succeeded);
        String expectedMessage = "Stock item with ID 1 does not exist.";
        Assertions.assertTrue(response.message.contains(expectedMessage));
    }

    @Test
    public void testPlaceOrderWhenThereIsNotEnoughStock()
    {
        // Setup
        // Set catalogue to return an item with a set quantity (so it can be displayed in an error)
        StockItem stockItem = new StockItem(1);
        stockItem.setQuantity(20);
        when(catalogue.getById(anyInt())).thenReturn(stockItem);
        // Set stock orderer to return that the order was not successful (due to insufficient quantity)
        when(stockOrderer.processOrder(eq(stockItem), anyInt())).thenReturn(false);

        // Exercise
        FacadeResponse response = orderingFacade.placeOrder(1, 21);

        // Verify
        // Order should fail since the user tried to buy an invalid amount
        Assertions.assertFalse(response.succeeded);
        String expectedMessage = "Requested quantity is invalid. Must be between 1 and 20 (inclusive).";
        Assertions.assertTrue(response.message.contains(expectedMessage));
    }

    @Test
    public void testPlaceOrderWhenOrderSucceeds()
    {
        // Setup
        // Set catalogue to return an item
        StockItem stockItem = new StockItem(1);
        when(catalogue.getById(anyInt())).thenReturn(stockItem);
        // Set stock orderer to return that the order was successful
        when(stockOrderer.processOrder(eq(stockItem), anyInt())).thenReturn(true);

        // Exercise
        FacadeResponse response = orderingFacade.placeOrder(1, 20);

        // Verify
        Assertions.assertTrue(response.succeeded);
        String expectedMessage = "Order placed successfully.";
        Assertions.assertTrue(response.message.contains(expectedMessage));
    }

    @Test
    public void testPlaceOrderWhenOrderSucceedsAndItemGoesOutOfStock()
    {
        // Setup
        // Set catalogue to return a item which should not be restocked (0 minimum order quantity)
        StockItem stockItem = new StockItem(1);
        stockItem.setQuantity(20);
        stockItem.setMinimumOrderQuantity(0);
        when(catalogue.getById(anyInt())).thenReturn(stockItem);
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
        verify(catalogue, times(1)).remove(eq(1));
    }
}
