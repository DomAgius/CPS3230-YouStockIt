package mt.edu.uom.youstockit.ordering;

import mt.edu.uom.youstockit.ItemOrder;
import mt.edu.uom.youstockit.StockItem;

public class AutomatedStockOrderer
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
        // If there are not enought items to fullfill the order, then abort the order
        if(ownedQuantity < buyQuantity)
        {
            return false;
        }

        item.setQuantity(ownedQuantity - buyQuantity);
        return true;
    }
}
