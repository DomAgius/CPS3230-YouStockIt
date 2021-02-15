package mt.edu.uom.youstockit.ordering;

import mt.edu.uom.youstockit.StockItem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class ProductCatalogueTests
{
    ProductCatalogue productCatalogue;

    @BeforeEach
    public void setup()
    {
        // Create a new product catalogue before each test
        productCatalogue = new ProductCatalogue();
    }

    @AfterEach
    public void teardown()
    {
        // Remove old product catalogue after each test
        productCatalogue = null;
    }

    @Test
    public void testEmptyProductCatalogue()
    {
        // Exercise
        List<StockItem> result = productCatalogue.getAll();

        // Verify
        // The tour catalogue should be empty
        Assertions.assertEquals(0, result.size());
    }

    @Test
    public void testAddItemWithUniqueIdToProductCatalogue()
    {
        // Setup
        StockItem item = new StockItem(1);

        // Exercise
        boolean result = productCatalogue.add(item);
        List<StockItem> items = productCatalogue.getAll();

        // Verify
        // Add function should return true
        Assertions.assertTrue(result);
        // The tour catalogue should have a single item
        Assertions.assertEquals(1, items.size());
        Assertions.assertEquals(item, items.get(0));
    }

    @Test
    public void testAddItemWithDuplicateIdToProductCatalogue()
    {
        // Setup
        StockItem item1 = new StockItem(1);
        productCatalogue.add(item1);

        // Exercise
        StockItem item2 = new StockItem(1);
        boolean result = productCatalogue.add(item2);
        List<StockItem> items = productCatalogue.getAll();

        // Verify
        // Add function should return false
        Assertions.assertFalse(result);
        // The tour catalogue should only the original item
        Assertions.assertEquals(1, items.size());
        Assertions.assertEquals(item1, items.get(0));
    }

    @Test
    public void testRemoveItemFromProductCatalogueNotContainingItem()
    {
        // Setup
        // Initialize catalogue with a single item whish should not be deleted
        StockItem item = new StockItem(1);
        productCatalogue.add(item);

        // Exercise
        boolean result = productCatalogue.remove(2);
        List<StockItem> items = productCatalogue.getAll();

        // Verify
        // Since the item has not been deleted it should return false
        Assertions.assertFalse(result);
        // The tour catalogue should still have a single item
        Assertions.assertEquals(1, items.size());
        Assertions.assertEquals(item, items.get(0));
    }

    @Test
    public void testRemoveItemFromProductCatalogueWithItem()
    {
        // Setup
        StockItem item1 = new StockItem(1);
        StockItem item2 = new StockItem(2);
        productCatalogue.add(item1);
        productCatalogue.add(item2);

        // Exercise
        boolean result = productCatalogue.remove(1);
        List<StockItem> items = productCatalogue.getAll();

        // Verify
        // If the item has been deleted it should return true
        Assertions.assertTrue(result);
        // The tour catalogue should have a single item
        Assertions.assertEquals(1, items.size());
        Assertions.assertEquals(item2, items.get(0));
    }

    @Test
    public void testGetCategoryForEmptyCatalogue()
    {
        // Exercise
        List<StockItem> result = productCatalogue.getByCategory("Category1");

        // Verify
        // The results should be empty
        Assertions.assertEquals(0, result.size());
    }

    @Test
    public void testGetCategoryForCatalogueWithItems()
    {
        // Setup
        StockItem item1 = new StockItem(1);
        item1.setCategory("Category1");
        StockItem item2 = new StockItem(2);
        item2.setCategory("Category1");
        StockItem item3 = new StockItem(3);
        item3.setCategory("Category2");
        productCatalogue.add(item1);
        productCatalogue.add(item2);
        productCatalogue.add(item3);

        // Exercise
        List<StockItem> result = productCatalogue.getByCategory("Category1");

        // Verify
        // The results should have two results - order of results is irrelevant
        Assertions.assertEquals(2, result.size());
        Assertions.assertTrue(result.contains(item1));
        Assertions.assertTrue(result.contains(item2));
    }

    @Test
    public void testGetIdForCatalogueWithNoItems()
    {
        // Exercise
        StockItem result = productCatalogue.getById(1);

        // Verify
        // The catalogue should return null
        Assertions.assertNull(result);
    }

    @Test
    public void testGetIdForCatalogueWithItems()
    {
        // Setup
        StockItem item1 = new StockItem(1);
        item1.setCategory("Category1");
        StockItem item2 = new StockItem(2);
        item2.setCategory("Category1");
        StockItem item3 = new StockItem(3);
        item3.setCategory("Category2");
        productCatalogue.add(item1);
        productCatalogue.add(item2);
        productCatalogue.add(item3);

        // Exercise
        StockItem result = productCatalogue.getById(2);

        // Verify
        // The catalogue should return "item2"
        Assertions.assertEquals(item2, result);
    }
}
