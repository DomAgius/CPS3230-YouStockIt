package mt.edu.uom.youstockit.ordering;

import mt.edu.uom.youstockit.StockItem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        Assertions.assertTrue(result);
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
        Assertions.assertFalse(result);
        Assertions.assertEquals(50, stockItem.getQuantity());
    }
}
