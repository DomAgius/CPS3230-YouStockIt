package mt.edu.uom.youstockit.ordering;

// This class is returned by some methods the "OrderingFacade" class to report if an operation was successful or not
public class FacadeResponse
{
    // Has the operation succeeded?
    public boolean succeeded;
    // Description of what occurred (e.g reason of failure)
    public String message;

    public FacadeResponse(boolean succeeded, String message)
    {
        this.succeeded = succeeded;
        this.message = message;
    }
}
