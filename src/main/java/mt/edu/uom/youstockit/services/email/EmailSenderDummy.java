package mt.edu.uom.youstockit.services.email;

import mt.edu.uom.youstockit.supplier.Supplier;

// Dummy used to mock an instance of an "Email Sender" class in the user interface
public class EmailSenderDummy implements EmailSender
{
    @Override
    public void sendEmailToSupplier(Supplier supplier, String message)
    {
        // Do nothing
    }

    @Override
    public void sendEmailToManager(String message)
    {
        // Do nothing
    }
}
