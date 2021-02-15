package mt.edu.uom.youstockit.ordering;

import mt.edu.uom.youstockit.StockItem;

public class OrderingFacade
{
    public StockOrderer stockOrderer;
    public ProductCatalogue catalogue;

    public OrderingFacade(StockOrderer stockOrderer, ProductCatalogue catalogue)
    {
        this.stockOrderer = stockOrderer;
        this.catalogue = catalogue;
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
}
