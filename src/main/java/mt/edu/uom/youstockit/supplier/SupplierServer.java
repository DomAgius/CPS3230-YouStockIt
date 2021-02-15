package mt.edu.uom.youstockit.supplier;

public interface SupplierServer
{
    // Method used to order items
    SupplierResponse[] orderItems(ItemOrder[] orders);
}
