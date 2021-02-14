package mt.edu.uom.youstockit.supplier;

import mt.edu.uom.youstockit.ItemOrder;

public interface SupplierServer
{
    // Method used to order items
    SupplierResponse[] orderItems(ItemOrder[] orders);
}
