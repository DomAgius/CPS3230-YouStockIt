package mt.edu.uom.youstockit.ordering;

import mt.edu.uom.youstockit.*;
import mt.edu.uom.youstockit.email.EmailSender;
import mt.edu.uom.youstockit.supplier.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class StockOrdererTests
{
    EmailSender emailServer;
    StockOrderer orderer;
    StockItem stockItem;

    @BeforeEach
    public void setup()
    {
        // Setup a new automated stock orderer and stock item before each test
        emailServer = Mockito.mock(EmailSender.class);
        orderer = new StockOrderer(emailServer);
        stockItem = new StockItem(1);
    }

    @AfterEach
    public void teardown()
    {
        // Remove reference to old stock orderer and stock item after each test
        orderer = null;
        stockItem = null;
    }

    @Test
    public void testProcessOrderWithSufficientQuantity()
    {
        // Setup
        stockItem.setQuantity(50);
        stockItem.setMinimumOrderQuantity(20);

        // Exercise
        boolean result = orderer.processOrder(stockItem, 30);

        // Verify
        // The order should be accepted in this case
        Assertions.assertTrue(result);
        // And the quantity of the stock should decrease
        Assertions.assertEquals(20, stockItem.getQuantity());
    }

    @Test
    public void testProcessOrderWithInsufficientQuantity()
    {
        // Setup
        stockItem.setQuantity(50);
        stockItem.setMinimumOrderQuantity(20);

        // Exercise
        boolean result = orderer.processOrder(stockItem, 51);

        // Verify
        // The order should be rejected in this case
        Assertions.assertFalse(result);
        // And the quantity should remain the same
        Assertions.assertEquals(50, stockItem.getQuantity());
    }

    @Test
    public void testProcessOrderWhenOrderQuantityIsNegative()
    {
        // Setup
        stockItem.setQuantity(50);
        stockItem.setMinimumOrderQuantity(20);

        // Exercise
        boolean result = orderer.processOrder(stockItem, -1);

        // Verify
        // The order should be rejected in this case
        Assertions.assertFalse(result);
        // The number of items should not change
        Assertions.assertEquals(50, stockItem.getQuantity());
    }

    @Test
    public void testProcessOrderWhenQuantityGoesBelowMinimumOrderQuantity()
    {
        // Setup
        stockItem.setQuantity(50);
        stockItem.setMinimumOrderQuantity(20);
        stockItem.setOrderAmount(30);
        // Mock supplier server to return requested number of items
        SupplierResponse response = new SupplierResponse(30, 30, SupplierErrorCode.SUCCESS);
        SupplierResponse[] responses = {response};
        SupplierServer server = Mockito.mock(SupplierServer.class);
        when(server.orderItems(Matchers.<ItemOrder[]>anyObject())).thenReturn(responses);
        Supplier supplier = new Supplier();
        supplier.supplierServer = server;
        stockItem.setSupplier(supplier);

        // Exercise
        boolean result = orderer.processOrder(stockItem, 31);

        // Verify
        Assertions.assertTrue(result);
        // After ordering 31 items the ordering system should restock 30 more, resulting in 19 + 30 = 49 items
        Assertions.assertEquals(49, stockItem.getQuantity());
    }

//    @Test
//    public void testProcessOrderWhenSupplierServerReturnsCommunicationErrorOneTime()
//    {
//        // Setup
//        stockItem.setQuantity(50);
//        stockItem.setMinimumOrderQuantity(20);
//        stockItem.setOrderAmount(30);
//        // Mock supplier server to first return a communication error, and then return a successful response
//        SupplierServerMock serverMock = new SupplierServerMock();
//        serverMock.addResponse(30, 0, SupplierErrorCode.COMMUNICATION_ERROR);
//        serverMock.addResponse(30, 30, SupplierErrorCode.SUCCESS);
//        Supplier supplier = new Supplier();
//        supplier.supplierServer = serverMock;
//        stockItem.setSupplier(supplier);
//
//        // Exercise
//        boolean result = orderer.processOrder(stockItem, 31);
//
//        // Verify
//        Assertions.assertTrue(result);
//        // Check that the method tried to connect the supplier twice
//        Assertions.assertEquals(2, serverMock.getNumTimesOrderItems());
//        // It should wait 5 seconds before ordering from the supplier again
//        assertAllLargerOrEqualTo(serverMock.getTimesBetweenCalls(), 5000);
//        // After ordering 31 items the ordering system should restock 30 more, resulting in 19 + 30 = 49 items
//        Assertions.assertEquals(49, stockItem.getQuantity());
//    }
//
//    @Test
//    public void testProcessOrderWhenSupplierServerReturnsCommunicationErrorThreeTimes()
//    {
//        // Setup
//        stockItem.setQuantity(50);
//        stockItem.setMinimumOrderQuantity(20);
//        stockItem.setOrderAmount(30);
//        // Mock supplier server to return a communication error three times, and then return a successful response
//        SupplierServerMock serverMock = new SupplierServerMock();
//        serverMock.addResponse(30, 0, SupplierErrorCode.COMMUNICATION_ERROR);
//        serverMock.addResponse(30, 0, SupplierErrorCode.COMMUNICATION_ERROR);
//        serverMock.addResponse(30, 0, SupplierErrorCode.COMMUNICATION_ERROR);
//        serverMock.addResponse(30, 30, SupplierErrorCode.SUCCESS);
//        Supplier supplier = new Supplier();
//        supplier.supplierServer = serverMock;
//        stockItem.setSupplier(supplier);
//
//        // Exercise
//        boolean result = orderer.processOrder(stockItem, 31);
//
//        // Verify
//        Assertions.assertTrue(result);
//        // Check that the method tried to connect the supplier four times
//        Assertions.assertEquals(4, serverMock.getNumTimesOrderItems());
//        // It should wait 5 seconds before ordering from the supplier again
//        assertAllLargerOrEqualTo(serverMock.getTimesBetweenCalls(), 5000);
//        // After ordering 31 items the ordering system should restock 30 more, resulting in 19 + 30 = 49 items
//        Assertions.assertEquals(49, stockItem.getQuantity());
//    }
//
//    @Test
//    public void testProcessOrderWhenSupplierServerReturnsCommunicationErrorFourTimes()
//    {
//        // Setup
//        stockItem.setQuantity(50);
//        stockItem.setMinimumOrderQuantity(20);
//        stockItem.setOrderAmount(30);
//        // Mock supplier server to return a communication error four times
//        SupplierServerMock serverMock = new SupplierServerMock();
//        serverMock.addResponse(30, 0, SupplierErrorCode.COMMUNICATION_ERROR);
//        serverMock.addResponse(30, 0, SupplierErrorCode.COMMUNICATION_ERROR);
//        serverMock.addResponse(30, 0, SupplierErrorCode.COMMUNICATION_ERROR);
//        serverMock.addResponse(30, 0, SupplierErrorCode.COMMUNICATION_ERROR);
//        Supplier supplier = new Supplier();
//        supplier.supplierServer = serverMock;
//        stockItem.setSupplier(supplier);
//
//        // Exercise
//        boolean result = orderer.processOrder(stockItem, 31);
//
//        // Verify
//        Assertions.assertTrue(result);
//        // Check that the method tried to connect the supplier four times
//        Assertions.assertEquals(4, serverMock.getNumTimesOrderItems());
//        // It should wait 5 seconds before ordering from the supplier again
//        assertAllLargerOrEqualTo(serverMock.getTimesBetweenCalls(), 5000);
//        // It should notify the supplier about the connection failure
//        verify(emailServer, times(1)).sendEmailToSupplier(eq(supplier), anyString());
//        // After ordering 31 items the ordering system should not restock
//        Assertions.assertEquals(19, stockItem.getQuantity());
//    }

    @Test
    public void testProcessOrderWhenSupplierServerReturnsItemNotFound()
    {
        // Setup
        stockItem.setQuantity(50);
        stockItem.setMinimumOrderQuantity(20);
        stockItem.setOrderAmount(30);
        // Mock supplier server to return a communication error four times
        SupplierServerMock serverMock = new SupplierServerMock();
        serverMock.addResponse(30, 0, SupplierErrorCode.ITEM_NOT_FOUND);
        Supplier supplier = new Supplier();
        supplier.supplierServer = serverMock;
        stockItem.setSupplier(supplier);

        // Exercise
        boolean result = orderer.processOrder(stockItem, 31);

        // Verify
        Assertions.assertTrue(result);
        // The minimum order quantity should now be set to zero
        Assertions.assertEquals(0, stockItem.getMinimumOrderQuantity());
        // The item should also be marked as discontinued
        Assertions.assertTrue(stockItem.isDiscontinued());
        // After ordering 31 items the ordering system should not restock
        Assertions.assertEquals(19, stockItem.getQuantity());
    }

    // Helper function used to assert if all values in a list are larger or equal to a minimum value
    private void assertAllLargerOrEqualTo(List<Long> values, long minimum)
    {
        for (long value: values)
        {
            Assertions.assertTrue(value >= minimum);
        }
    }
}
