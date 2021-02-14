package mt.edu.uom.youstockit.supplier;

public class SupplierResponse
{
    public int requestedQuantity;
    public int actualQuantity;
    public SupplierErrorCode errorCode;

    public SupplierResponse(int requestedQuantity, int actualQuantity, SupplierErrorCode errorCode)
    {
        this.requestedQuantity = requestedQuantity;
        this.actualQuantity = actualQuantity;
        this.errorCode = errorCode;
    }
}
