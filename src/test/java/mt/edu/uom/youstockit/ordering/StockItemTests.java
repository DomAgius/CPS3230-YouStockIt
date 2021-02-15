package mt.edu.uom.youstockit.ordering;

import mt.edu.uom.youstockit.ordering.StockItem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StockItemTests
{
    StockItem stockItem;

    @BeforeEach
    public void setup()
    {
        // Create new stock item before each test
        stockItem = new StockItem(0);
    }

    @AfterEach
    public void teardown()
    {
        // Remove reference to old stock item between tests
        stockItem = null;
    }

    @Test
    public void testSetNameWhenNameIs4CharactersLong()
    {
        // Exercise
        String name = "1234";
        boolean valid = stockItem.setName(name);

        // Verify (this name should be invalid)
        Assertions.assertFalse(valid);
        // Name should not be set
        Assertions.assertNull(stockItem.getName());
    }

    @Test
    public void testSetNameWhenNameIs5CharactersLong()
    {
        // Setup
        String name = "12345";

        // Exercise
        boolean valid = stockItem.setName(name);

        // Verify (this name should be valid)
        Assertions.assertTrue(valid);
        // Name should be set
        Assertions.assertEquals(name, stockItem.getName());
    }

    @Test
    public void testSetNameWhenNameIs100CharactersLong()
    {
        // Setup
        // Create 100 character name
        StringBuilder name = new StringBuilder();
        for(int i = 0; i < 100; i++)
        {
            name.append("1");
        }

        // Exercise
        boolean valid = stockItem.setName(name.toString());

        // Verify (this name should be valid)
        Assertions.assertTrue(valid);
        // Name should be set
        Assertions.assertEquals(name.toString(), stockItem.getName());
    }

    @Test
    public void testSetNameWhenNameIs101CharactersLong()
    {
        // Setup
        // Create 101 character name
        StringBuilder name = new StringBuilder();
        for(int i = 0; i < 101; i++)
        {
            name.append("1");
        }

        // Exercise
        boolean valid = stockItem.setName(name.toString());

        // Verify (this name should be invalid)
        Assertions.assertFalse(valid);
        // Name should not be set
        Assertions.assertNull(stockItem.getName());
    }

    @Test
    public void testSetDescriptionWhenDescriptionIs500CharactersLong()
    {
        // Setup
        StringBuilder desc = new StringBuilder();
        for(int i = 0; i < 500; i++)
        {
            desc.append("1");
        }

        // Exercise
        // Create 500 character description
        boolean valid = stockItem.setDescription(desc.toString());

        // Verify (this description should be valid)
        Assertions.assertTrue(valid);
        // Description should be set
        Assertions.assertEquals(desc.toString(), stockItem.getDescription());
    }

    @Test
    public void testSetDescriptionWhenDescriptionIs501CharactersLong()
    {
        // Setup
        // Create 501 character description
        StringBuilder desc = new StringBuilder();
        for(int i = 0; i < 501; i++)
        {
            desc.append("1");
        }

        // Exercise
        boolean valid = stockItem.setDescription(desc.toString());

        // Verify (this description should be invalid)
        Assertions.assertFalse(valid);
        // Description should not be set
        Assertions.assertNull(stockItem.getDescription());
    }

    @Test
    public void testSetMinimumOrderQuantityWhenQuantityIsMinus1()
    {
        // Exercise
        boolean valid = stockItem.setMinimumOrderQuantity(-1);

        // Verify (this quantity should be invalid)
        Assertions.assertFalse(valid);
        // Minimum order quantity should not be set (it should remain as -1)
        Assertions.assertEquals(-1, stockItem.getMinimumOrderQuantity());
    }

    @Test
    public void testSetMinimumOrderQuantityWhenQuantityIs0()
    {
        // Exercise
        boolean valid = stockItem.setMinimumOrderQuantity(0);

        // Verify (this quantity should be valid)
        Assertions.assertTrue(valid);
        // Minimum order quantity should be set
        Assertions.assertEquals(0, stockItem.getMinimumOrderQuantity());
    }

    @Test
    public void testSetQuantityWhenQuantityIsMinus1()
    {
        // Exercise
        boolean valid = stockItem.setQuantity(-1);

        // Verify (this quantity should be invalid)
        Assertions.assertFalse(valid);
        // Minimum order quantity should not be set
        Assertions.assertEquals(-1, stockItem.getQuantity());
    }

    @Test
    public void testSetQuantityWhenQuantityIs0()
    {
        // Exercise
        boolean valid = stockItem.setQuantity(0);

        // Verify (this quantity should be valid)
        Assertions.assertTrue(valid);
        // Minimum order quantity should be set
        Assertions.assertEquals(0, stockItem.getQuantity());
    }

    @Test
    public void testSetOrderAmountWhenAmountIs0()
    {
        // Exercise
        boolean valid = stockItem.setOrderAmount(0);

        // Verify (this quantity should be invalid)
        Assertions.assertFalse(valid);
        // Order amount should not be set
        Assertions.assertEquals(-1, stockItem.getOrderAmount());
    }

    @Test
    public void testSetQuantityWhenQuantityIs1()
    {
        // Exercise
        boolean valid = stockItem.setOrderAmount(1);

        // Verify (this quantity should be valid)
        Assertions.assertTrue(valid);
        // Order amount quantity should be set
        Assertions.assertEquals(1, stockItem.getOrderAmount());
    }

    @Test
    public void testSetBuySellPricesWhenSellPriceIsLargerThanBuyPrice()
    {
        // Exercise
        boolean valid = stockItem.setBuySellPrices(1.25, 1.50);

        // Verify (these parameters should be valid)
        Assertions.assertTrue(valid);
        // Buy and sell prices should not be set
        Assertions.assertEquals(1.25, stockItem.getBuyingPrice());
        Assertions.assertEquals(1.50, stockItem.getSellingPrice());
    }

    @Test
    public void testSetBuySellPricesWhenSellPriceIsEqualToBuyPrice()
    {
        // Exercise
        boolean valid = stockItem.setBuySellPrices(1.50, 1.50);

        // Verify (these parameters should be valid)
        Assertions.assertTrue(valid);
        // Buy and sell prices should not be set
        Assertions.assertEquals(1.50, stockItem.getBuyingPrice());
        Assertions.assertEquals(1.50, stockItem.getSellingPrice());
    }

    @Test
    public void testSetBuySellPricesWhenSellPriceIsLessThanBuyPrice()
    {
        // Exercise
        boolean valid = stockItem.setBuySellPrices(1.50, 1.25);

        // Verify (these parameters should be invalid)
        Assertions.assertFalse(valid);
        // Buy and sell prices should not be set
        Assertions.assertEquals(0.00, stockItem.getBuyingPrice());
        Assertions.assertEquals(0.00, stockItem.getSellingPrice());
    }

    @Test
    public void testSetBuySellPricesWhenBuyPriceIsLessThanZero()
    {
        // Exercise
        boolean valid = stockItem.setBuySellPrices(-0.01, 0.01);

        // Verify (these parameters should be invalid)
        Assertions.assertFalse(valid);
        // Buy and sell prices should not be set
        Assertions.assertEquals(0.00, stockItem.getBuyingPrice());
        Assertions.assertEquals(0.00, stockItem.getSellingPrice());
    }

    @Test
    public void testSetBuySellPricesWhenSellPriceIsLessThanZero()
    {
        // Exercise
        boolean valid = stockItem.setBuySellPrices(0.01, -0.01);

        // Verify (these parameters should be invalid)
        Assertions.assertFalse(valid);
        // Buy and sell prices should not be set
        Assertions.assertEquals(0.00, stockItem.getBuyingPrice());
        Assertions.assertEquals(0.00, stockItem.getSellingPrice());
    }
}
