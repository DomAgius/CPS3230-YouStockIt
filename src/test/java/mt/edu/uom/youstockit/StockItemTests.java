package mt.edu.uom.youstockit;

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
        boolean result = stockItem.setName(name);

        // Verify (this name should be invalid)
        Assertions.assertFalse(result);
        // Name should not be set
        Assertions.assertNull(stockItem.getName());
    }

    @Test
    public void testSetNameWhenNameIs5CharactersLong()
    {
        // Setup
        String name = "12345";

        // Exercise
        boolean result = stockItem.setName(name);

        // Verify (this name should be valid)
        Assertions.assertTrue(result);
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
        boolean result = stockItem.setName(name.toString());

        // Verify (this name should be valid)
        Assertions.assertTrue(result);
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
        boolean result = stockItem.setName(name.toString());

        // Verify (this name should be invalid)
        Assertions.assertFalse(result);
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
        boolean result = stockItem.setDescription(desc.toString());

        // Verify (this description should be valid)
        Assertions.assertTrue(result);
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
        boolean result = stockItem.setDescription(desc.toString());

        // Verify (this description should be invalid)
        Assertions.assertFalse(result);
        // Description should not be set
        Assertions.assertNull(stockItem.getDescription());
    }

    @Test
    public void testSetMinimumOrderQuantityWhenQuantityIs0()
    {
        // Exercise
        boolean result = stockItem.setMinimumOrderQuantity(0);

        // Verify (this quantity should be invalid)
        Assertions.assertFalse(result);
        // Minimum order quantity should not be set
        Assertions.assertEquals(-1, stockItem.getMinimumOrderQuantity());
    }

    @Test
    public void testSetMinimumOrderQuantityWhenQuantityIs1()
    {
        // Exercise
        boolean result = stockItem.setMinimumOrderQuantity(1);

        // Verify (this quantity should be valid)
        Assertions.assertTrue(result);
        // Minimum order quantity should be set
        Assertions.assertEquals(1, stockItem.getMinimumOrderQuantity());
    }

    @Test
    public void testSetQuantityWhenQuantityIsMinus1()
    {
        // Exercise
        boolean result = stockItem.setQuantity(-1);

        // Verify (this quantity should be invalid)
        Assertions.assertFalse(result);
        // Minimum order quantity should not be set
        Assertions.assertEquals(-1, stockItem.getQuantity());
    }

    @Test
    public void testSetQuantityWhenQuantityIs0()
    {
        // Exercise
        boolean result = stockItem.setQuantity(0);

        // Verify (this quantity should be valid)
        Assertions.assertTrue(result);
        // Minimum order quantity should be set
        Assertions.assertEquals(0, stockItem.getQuantity());
    }

    @Test
    public void testSetOrderAmountWhenAmountIs0()
    {
        // Exercise
        boolean result = stockItem.setOrderAmount(0);

        // Verify (this quantity should be invalid)
        Assertions.assertFalse(result);
        // Minimum order quantity should not be set
        Assertions.assertEquals(-1, stockItem.getOrderAmount());
    }

    @Test
    public void testSetQuantityWhenQuantityIs1()
    {
        // Exercise
        boolean result = stockItem.setOrderAmount(1);

        // Verify (this quantity should be valid)
        Assertions.assertTrue(result);
        // Minimum order quantity should be set
        Assertions.assertEquals(1, stockItem.getOrderAmount());
    }
}
