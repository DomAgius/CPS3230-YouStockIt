package mt.edu.uom.youstockit.ordering;

import mt.edu.uom.youstockit.StockItem;
import mt.edu.uom.youstockit.email.EmailSender;
import mt.edu.uom.youstockit.email.ServiceLocator;

public class OrderingFacade
{
    public StockOrderer stockOrderer;
    public ProductCatalogue availableItems;
    public ProductCatalogue discontinuedItems;

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
            return new FacadeResponse(false, "Requested quantity is invalid. Must be between 1 and " +
                    stockItem.getQuantity() + " (inclusive).");
        }
    }

    public FacadeResponse deleteItem(int id)
    {
        if(availableItems.remove(id))
        {
            // Notify manager via email about deletion
            emailSender.sendEmailToManager("Item with id " + id + " was deleted from the product catalogue.");
            return new FacadeResponse(false, "Deleted item from catalogue and notified manager via " +
                    "email.");
        }
        else
        {
            return new FacadeResponse(false, "Stock item with ID " + id + " does not exist.");
        }
    }

    public double calculateProfits()
    {
        return 0;
    }
}
