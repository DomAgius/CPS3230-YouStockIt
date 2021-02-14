package mt.edu.uom.youstockit.ordering;

import mt.edu.uom.youstockit.StockItem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProductCatalogue
{
    // List of stock items
    private List<StockItem> items;

    public ProductCatalogue()
    {
        items = new ArrayList<>();
    }

    // Add new stock item
    public boolean add(StockItem item)
    {
        // If item has a unique id, add it to the list and return true
        if(getById(item.getId()) == null)
        {
            items.add(item);
            return true;
        }
        // otherwise return false
        else
        {
            return false;
        }
    }

    // Add delete stock item with a given ID
    public void remove(int id)
    {
        items.removeIf(stockItem -> stockItem.getId() == id);
    }

    // Get all stock items
    public List<StockItem> getAll()
    {
        // Note: since the list is passed directly the user can make direct changes to the product catalogue
        return items;
    }

    // Get all items with a specific category
    public List<StockItem> getByCategory(String category)
    {
        return items.stream().filter(stockItem -> stockItem.getCategory().equals(category))
                .collect(Collectors.toList());
    }

    public StockItem getById(int id)
    {
        // Get the first item with the given id (or null if no item is found)
        return items.stream().filter(stockItem -> stockItem.getId() == id)
                .findFirst().orElse(null);
    }
}
