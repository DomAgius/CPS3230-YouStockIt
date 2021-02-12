package mt.edu.uom.youstockit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StockTests {
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
        // Exercise
        String name = "12345";
        boolean result = stockItem.setName(name);

        // Verify (this name should be valid)
        Assertions.assertTrue(result);
        // Name should be set
        Assertions.assertEquals(name, stockItem.getName());
    }

    @Test
    public void testSetNameWhenNameIs100CharactersLong()
    {
        // Exercise
        // Create 100 character name
        StringBuilder name = new StringBuilder();
        for(int i = 0; i < 100; i++)
        {
            name.append("1");
        }
        boolean result = stockItem.setName(name.toString());

        // Verify (this name should be valid)
        Assertions.assertTrue(result);
        // Name should be set
        Assertions.assertEquals(name.toString(), stockItem.getName());
    }

    @Test
    public void testSetNameWhenNameIs101CharactersLong()
    {
        // Exercise
        // Create 101 character name
        StringBuilder name = new StringBuilder();
        for(int i = 0; i < 101; i++)
        {
            name.append("1");
        }
        boolean result = stockItem.setName(name.toString());

        // Verify (this name should be invalid)
        Assertions.assertFalse(result);
        // Name should not be set
        Assertions.assertNull(stockItem.getName());
    }
}
