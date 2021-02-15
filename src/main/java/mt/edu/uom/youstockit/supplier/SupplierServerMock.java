package mt.edu.uom.youstockit.supplier;

import java.util.ArrayList;
import java.util.List;

public class SupplierServerMock implements SupplierServer
{
    // Integer storing the numbers times the "orderItems" method was called
    private int numTimesOrderItems;
    // Array storing the times between each call (in milliseconds)
    private List<Long> timesBetweenCalls;
    // Stores the system time at which the "orderItems" method was last called
    private long timeOfLastOrder;

    // Store a list of pre-canned responses and an index to keep track of the last response that was given
    private List<SupplierResponse[]> serverResponses;
    private int currentResponseIndex;

    public SupplierServerMock()
    {
        numTimesOrderItems = 0;
        timesBetweenCalls = new ArrayList<>();
        timeOfLastOrder = 0;

        // Initialise responses
        serverResponses = new ArrayList<>();
        currentResponseIndex = 0;
    }

    public SupplierResponse[] orderItems(ItemOrder[] orders)
    {
        // Store the time at which the method got called
        long now = System.currentTimeMillis();
        // If this is not the first time the method was called, calculate the time interval between calls
        if(numTimesOrderItems > 0)
        {
            timesBetweenCalls.add(now - timeOfLastOrder);
        }
        timeOfLastOrder = now;
        // Increment the number of times this method was called
        numTimesOrderItems++;

        // By default return null
        SupplierResponse[] response = null;
        // If the response list is not empty, return the next pre-canned response in the list
        // Note: If the end of list is reached, this keeps outputting last response
        if(!serverResponses.isEmpty())
        {
            response = serverResponses.get(currentResponseIndex);
            if(currentResponseIndex < serverResponses.size()-1)
            {
                currentResponseIndex++;
            }
        }
        return response;
    }

    // Add a pre-canned response to the mock
    public void addResponse(int requestedQuantity, int actualQuantity, SupplierErrorCode errorCode)
    {
        SupplierResponse[] response = new SupplierResponse[1];
        response[0] = new SupplierResponse(requestedQuantity, actualQuantity, errorCode);
        serverResponses.add(response);
    }

    public int getNumTimesOrderItems()
    {
        return numTimesOrderItems;
    }

    public List<Long> getTimesBetweenCalls()
    {
        return timesBetweenCalls;
    }
}
