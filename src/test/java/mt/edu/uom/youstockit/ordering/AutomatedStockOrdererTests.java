package mt.edu.uom.youstockit.ordering;

import mt.edu.uom.youstockit.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class AutomatedStockOrdererTests
{
    AutomatedStockOrderer orderer;
    StockItem stockItem;

    @BeforeEach
    public void setup()
    {
        // Create a new automated stock orderer and stock item before each test
        orderer = new AutomatedStockOrderer();
        stockItem = new StockItem(1);
    }

    @AfterEach
    public void teardown()
    {
        // Remove reference to old stock orderer and stock item after each test
        orderer = null;
        stockItem = null;
    }

    @Test
    public void testProcessOrderWithSufficientQuantity()
    {
        // Setup
        stockItem.setQuantity(50);
        stockItem.setMinimumOrderQuantity(20);

        // Exercise
        boolean result = orderer.processOrder(stockItem, 30);

        // Verify
        // The order should be accepted in this case
        Assertions.assertTrue(result);
        // And the quantity of the stock should decrease
        Assertions.assertEquals(20, stockItem.getQuantity());
    }

    @Test
    public void testProcessOrderWithInsufficientQuantity()
    {
        // Setup
        stockItem.setQuantity(50);
        stockItem.setMinimumOrderQuantity(20);

        // Exercise
        boolean result = orderer.processOrder(stockItem, 51);

        // Verify
        // The order should be rejected in this case
        Assertions.assertFalse(result);
        // And the quantity should remain the same
        Assertions.assertEquals(50, stockItem.getQuantity());
    }

    @Test
    public void testProcessOrderWhenOrderQuantityIsNegative()
    {
        // Setup
        stockItem.setQuantity(50);
        stockItem.setMinimumOrderQuantity(20);

        // Exercise
        boolean result = orderer.processOrder(stockItem, -1);

        // Verify
        // The order should be rejected in this case
        Assertions.assertFalse(result);
        // The number of items should not change
        Assertions.assertEquals(50, stockItem.getQuantity());
    }

    @Test
    public void testProcessOrderWhenQuantityGoesBelowMinimumOrderQuantity()
    {
        // Setup
        stockItem.setQuantity(50);
        stockItem.setMinimumOrderQuantity(20);
        stockItem.setOrderAmount(30);
        // Mock supplier server to return requested number of items
        SupplierResponse response = new SupplierResponse(30, 30, SupplierErrorCode.SUCCESS);
        SupplierResponse[] responses = {response};
        SupplierServer server = Mockito.mock(SupplierServer.class);
        when(server.orderItems(Matchers.<ItemOrder[]>anyObject())).thenReturn(responses);
        Supplier supplier = new Supplier();
        supplier.supplierServer = server;
        stockItem.setSupplier(supplier);

        // Exercise
        boolean result = orderer.processOrder(stockItem, 31);

        // Verify
        Assertions.assertTrue(result);
        // After ordering 30 items the ordering system should restock 30 more, resulting in 19 + 30 = 49 items
        Assertions.assertEquals(49, stockItem.getQuantity());
    }
}
