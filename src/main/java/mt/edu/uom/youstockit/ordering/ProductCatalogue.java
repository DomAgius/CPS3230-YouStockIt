package mt.edu.uom.youstockit.ordering;

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
    // Returns true if item with given ID was found and deleted and false otherwise
    public boolean remove(int id)
    {
        // Search for item with given id
        StockItem item = getById(id);
        boolean isFound = item != null;

        // If item is found, delete it
        if(isFound)
        {
            items.removeIf(stockItem -> stockItem.getId() == id);
        }

        return isFound;
    }

    // Get all stock items
    public List<StockItem> getAll()
    {
        // Note: the arraylist is cloned to prevent accidental changes to the original list in the catalogue
        return new ArrayList<>(items);
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
