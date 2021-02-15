package mt.edu.uom.youstockit.ordering;

import mt.edu.uom.youstockit.StockItem;
import mt.edu.uom.youstockit.email.EmailSender;
import mt.edu.uom.youstockit.email.ServiceLocator;

public class OrderingFacade
{
    public StockOrderer stockOrderer;
    public ProductCatalogue catalogue;

    // Email sender is private since it is configured from the service locator
    private EmailSender emailSender;

    public OrderingFacade(StockOrderer stockOrderer, ProductCatalogue catalogue)
    {
        this.stockOrderer = stockOrderer;
        this.catalogue = catalogue;

        // Get email sender service
        ServiceLocator serviceLocator = ServiceLocator.getInstance();
        emailSender = (EmailSender) serviceLocator.findService("EmailSender");
    }

    public FacadeResponse placeOrder(int id, int buyAmount)
    {
        // Search for stock item in product catalogue
        StockItem stockItem = catalogue.getById(id);
        // If stock item does not exist, return an error message
        if(stockItem == null)
        {
            return new FacadeResponse(false, "Stock item with ID " + id + " does not exist.");
        }

        // If order is placed successfully, return a success message
        if(stockOrderer.processOrder(stockItem, buyAmount))
        {
            String responseMessage = "Order placed successfully.";

            // If the item is out of stock should not be restocked, delete it from the catalogue
            if(stockItem.getMinimumOrderQuantity() == 0 && stockItem.getQuantity() == 0)
            {
                responseMessage += "\nItem has gone out of stock, removing from catalogue...";
                catalogue.remove(stockItem.getId());
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
        if(catalogue.remove(id))
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
}
