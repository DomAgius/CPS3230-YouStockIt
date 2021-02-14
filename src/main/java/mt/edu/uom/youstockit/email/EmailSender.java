package mt.edu.uom.youstockit.email;

import mt.edu.uom.youstockit.supplier.Supplier;

public interface EmailSender
{
    // Send an email to a supplier
    void sendEmailToSupplier(Supplier supplier, String message);
    // Send an email to the manager
    void sendEmailToManager(String message);
}
