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
    public void testSetNameWhenNameIs5CharactersLong()
    {
        // Exercise
        boolean result = stockItem.setName("12345");

        // Verify
        Assertions.assertTrue(result);
    }

    @Test
    public void testSetNameWhenNameIs100CharactersLong()
    {
        // Exercise
        // Create 49 character name
        StringBuilder name = new StringBuilder();
        for(int i = 0; i < 100; i++)
        {
            name.append("1");
        }
        boolean result = stockItem.setName(name.toString());

        // Verify
        Assertions.assertTrue(result);
    }

    @Test
    public void testSetNameWhenNameIs101CharactersLong()
    {
        // Exercise
        // Create 49 character name
        StringBuilder name = new StringBuilder();
        for(int i = 0; i < 101; i++)
        {
            name.append("1");
        }
        boolean result = stockItem.setName(name.toString());

        // Verify
        Assertions.assertFalse(result);
    }
}
