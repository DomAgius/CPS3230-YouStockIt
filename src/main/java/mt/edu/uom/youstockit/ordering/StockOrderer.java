package mt.edu.uom.youstockit.ordering;

import mt.edu.uom.youstockit.ItemOrder;
import mt.edu.uom.youstockit.StockItem;
import mt.edu.uom.youstockit.email.EmailSender;
import mt.edu.uom.youstockit.email.ServiceLocator;
import mt.edu.uom.youstockit.supplier.Supplier;
import mt.edu.uom.youstockit.supplier.SupplierResponse;

public class StockOrderer
{
    private final EmailSender emailSender;

    public StockOrderer()
    {
        // Find email sender service
        ServiceLocator serviceLocator = ServiceLocator.getInstance();
        this.emailSender = (EmailSender) serviceLocator.findService("EmailSender");
    }

    /**
     *  Function used to handle stock ordering automatically
     * @param item Item the order is being placed on
     * @param buyQuantity How much of the item is being bought in the order
     * @return True when the amount of requested stock is valid and available and false otherwise
     */
    public boolean processOrder(StockItem item, int buyQuantity)
    {
        int ownedQuantity = item.getQuantity();
        // If order quantity is 0 or less or if there are not enough items to fulfill the order, then abort the order
        if(buyQuantity < 0 || ownedQuantity < buyQuantity)
        {
            return false;
        }

        // Update item quantity and number of times sold after successful order
        item.setQuantity(ownedQuantity - buyQuantity);
        item.incrementNumTimesSold(buyQuantity);

        if(item.getQuantity() < item.getMinimumOrderQuantity())
        {
            orderMore(item);
        }

        return true;
    }

    // This helper function orders more of an object if the quantity goes below a certain number
    private void orderMore(StockItem item)
    {
        Supplier supplier = item.getSupplier();

        int tries = 0;
        boolean retry = true;

        // Try to order from the supplier's server 4 times at most
        while(retry && tries < 4)
        {
            ItemOrder[] orders = new ItemOrder[1];
            orders[0] = new ItemOrder(item.getId(), item.getOrderAmount());
            SupplierResponse[] response = supplier.supplierServer.orderItems(orders);
            // Update the number of attempts
            tries++;

            // Handle supplier response
            switch (response[0].errorCode)
            {
                case COMMUNICATION_ERROR:
                {
                    // If this is not the final try, wait for 5 seconds before trying again
                    if(tries < 4)
                    {
                        try
                        {
                            Thread.sleep(5000);
                        } catch (InterruptedException e)
                        {
                            // If waiting fails, send an email to the manager to notify them about failure and abort
                            emailSender.sendEmailToManager("YouStockIt failed to retry ordering of stock item \"" +
                                    item.getName() + "\" with id " + item.getId());
                            retry = false;
                        }
                    }
                    // If this was the final attempt, send an email to the supplier
                    else
                    {
                        emailSender.sendEmailToSupplier(supplier, "YouStockIt system was unable to connect " +
                                "to your stock server.");
                    }
                } break;

                case ITEM_NOT_FOUND:
                {
                    // If item is not found in supplier's stock, set minimum order quantity to zero
                    item.setMinimumOrderQuantity(0);
                    retry = false;
                } break;

                case OUT_OF_STOCK:
                {
                    // Send an email to the manager, and continue to run code in SUCCESS case
                    emailSender.sendEmailToManager("YouStockIt failed to restock item \"" + item.getName() +
                            "\" with id " + item.getId() + " since the supplier has run out of stock.");
                }
                case SUCCESS:
                {
                    // Add the items given to us by the supplier to the stock
                    int newQuantity = item.getQuantity() + response[0].actualQuantity;
                    item.setQuantity(newQuantity);
                    // Stop trying to communicate to the supplier
                    retry = false;
                } break;
            }
        }
    }
}
