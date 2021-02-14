package mt.edu.uom.youstockit.ordering;

import mt.edu.uom.youstockit.ItemOrder;
import mt.edu.uom.youstockit.StockItem;
import mt.edu.uom.youstockit.supplier.Supplier;
import mt.edu.uom.youstockit.supplier.SupplierErrorCode;
import mt.edu.uom.youstockit.supplier.SupplierResponse;

import java.util.concurrent.TimeUnit;

public class StockOrderer
{
    /**
     *  Function used to handle stock ordering automatically
     * @param item Item the order is being placed on
     * @param buyQuantity How much of the item is being bought in the order
     * @return False if stock item should be deleted and true if it shouldn't
     */
    public boolean processOrder(StockItem item, int buyQuantity)
    {
        int ownedQuantity = item.getQuantity();
        // If order quantity is negative or if there are not enough items to fulfill the order, then abort the order
        if(buyQuantity < 0 || ownedQuantity < buyQuantity)
        {
            return false;
        }

        // Update item quantity after successful order
        item.setQuantity(ownedQuantity - buyQuantity);

        if(item.getQuantity() < item.getMinimumOrderQuantity())
        {
            orderMore(item);
        }

        return true;
    }

    // This helper function orders more of an object if the quantity goes below a certain number
    protected void orderMore(StockItem item)
    {
        Supplier supplier = item.getSupplier();

        int tries = 0;
        boolean retry = true;

        while(retry && tries < 3)
        {
            // Try to order from the supplier's server
            ItemOrder[] orders = new ItemOrder[1];
            orders[0] = new ItemOrder(item.getId(), item.getOrderAmount());
            SupplierResponse[] response = supplier.supplierServer.orderItems(orders);

            // Handle supplier response
            switch (response[0].errorCode)
            {
                case COMMUNICATION_ERROR:
                {
                    // Wait for 5 seconds before trying again
                    try
                    {
                        Thread.sleep(5000);
                    } catch (InterruptedException e)
                    {
                        //TODO add send email to manager
                    }
                } break;
                case SUCCESS:
                {
                    // Add the items given to us by the supplier to the stock
                    int newQuantity = item.getQuantity() + response[0].actualQuantity;
                    item.setQuantity(newQuantity);
                    // Stop trying to communicate to the supplier
                    retry = false;
                } break;
            }

            // Update the number of attempts
            tries++;
        }
    }
}
