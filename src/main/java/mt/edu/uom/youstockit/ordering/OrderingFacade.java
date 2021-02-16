package mt.edu.uom.youstockit.ordering;

import mt.edu.uom.youstockit.services.email.EmailSender;
import mt.edu.uom.youstockit.services.ServiceLocator;

import java.util.List;

public class OrderingFacade
{
    private StockOrderer stockOrderer;
    private ProductCatalogue availableItems;
    private ProductCatalogue discontinuedItems;

    // Email sender is private since it is configured from the service locator
    private EmailSender emailSender;

    public OrderingFacade(StockOrderer stockOrderer, ProductCatalogue availableItems, ProductCatalogue discontinuedItems)
    {
        this.stockOrderer = stockOrderer;
        this.availableItems = availableItems;
        this.discontinuedItems = discontinuedItems;

        // Get email sender service
        ServiceLocator serviceLocator = ServiceLocator.getInstance();
        emailSender = (EmailSender) serviceLocator.findService("EmailSender");
    }

    // Order an item from the catalogue
    public FacadeResponse placeOrder(int id, int buyAmount)
    {
        // Search for stock item in product catalogue
        StockItem stockItem = availableItems.getById(id);
        // If stock item does not exist, return an error message
        if(stockItem == null)
        {
            return new FacadeResponse(false, "Stock item with ID " + id + " does not exist.");
        }

        // If order is placed successfully, return a success message
        if(stockOrderer.processOrder(stockItem, buyAmount))
        {
            String responseMessage = "Order placed successfully.";

            // If the item is out of stock should not be restocked,
            if(stockItem.getMinimumOrderQuantity() == 0 && stockItem.getQuantity() == 0)
            {
                // delete it from the catalogue of items being sold,
                responseMessage += "\nItem has gone out of stock, removing from catalogue...";
                availableItems.remove(stockItem.getId());
                // and add it to the catalogue of discontinued items
                discontinuedItems.add(stockItem);
            }
            return new FacadeResponse(true, responseMessage);
        }
        // If processOrder returns false then the requested quantity is either invalid, or not available in stock
        else
        {
            if(stockItem.getQuantity() == 0)
            {
                return new FacadeResponse(false, "Requested quantity is invalid. Item is out of " +
                        "stock.");
            }
            else
            {
                return new FacadeResponse(false, "Requested quantity is invalid. Must be between 1 " +
                        "and " + stockItem.getQuantity() + " (inclusive).");
            }
        }
    }

    // Add an item to the product catalogue of available items
    public void addItem(StockItem stockItem)
    {
        availableItems.add(stockItem);
    }

    // Delete an item from the product catalogue of available items
    public FacadeResponse deleteItem(int id)
    {
        // Find item to delete
        StockItem stockItem = availableItems.getById(1);

        // If item is not found, return failure message
        if(stockItem == null)
        {
            return new FacadeResponse(false, "Stock item with ID " + id + " does not exist.");
        }

        // If the stock item exists, delete it
        availableItems.remove(id);
        String responseMessage = "Deleted item from catalogue";

        // If there are items in stock, email the manager about the deletion
        if(stockItem.getQuantity() > 0)
        {
            emailSender.sendEmailToManager("Item with id " + id + " was deleted from the product catalogue.");
            responseMessage += " and notified manager via email.";
        }

        return new FacadeResponse(false, responseMessage);
    }

    // Calculate profits for current session
    public double calculateProfit()
    {
        // Get all stock items, both ones that are still available and those that are discontinued
        List<StockItem> items = availableItems.getAll();
        items.addAll(discontinuedItems.getAll());

        double totalProfit = 0.0;
        // Add up the profits for each item
        for (StockItem item : items)
        {
            double profitPerItem = item.getSellingPrice() - item.getBuyingPrice();
            totalProfit += profitPerItem * item.getNumTimesSold();
        }

        return totalProfit;
    }

    // Get all items available for order
    public List<StockItem> getAvailableItems()
    {
        return availableItems.getAll();
    }

    // Get all items available for order in a specific category
    public List<StockItem> getAvailableItems(String category)
    {
        return availableItems.getByCategory(category);
    }
}
