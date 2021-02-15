package mt.edu.uom.youstockit.ordering;

public class FacadeResponse
{
    // Has the operation succeeded?
    public boolean succeeded;
    // Message from facade
    public String message;

    public FacadeResponse(boolean succeeded, String message)
    {
        this.succeeded = succeeded;
        this.message = message;
    }
}
