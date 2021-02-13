package mt.edu.uom.youstockit.email;

import mt.edu.uom.youstockit.Supplier;

public interface EmailSender
{
    // Send an email to a supplier
    boolean SendEmailToSupplier(Supplier supplier, String message);
    // Send an email to the manager
    boolean SendEmailToManager(String message);
}
