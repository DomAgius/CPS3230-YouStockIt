package mt.edu.uom.youstockit.ordering;

import mt.edu.uom.youstockit.services.email.EmailSender;
import mt.edu.uom.youstockit.services.ServiceLocator;
import mt.edu.uom.youstockit.supplier.*;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class StockOrdererTests
{
    static ServiceLocator serviceLocator;
    EmailSender emailServer;
    StockOrderer orderer;
    StockItem stockItem;

    @BeforeAll
    public static void setupBeforeAll()
    {
        // Get an instance of the service locator singleton
        serviceLocator = ServiceLocator.getInstance();
    }

    @BeforeEach
    public void setupBeforeEach()
    {
        // Set up a mocked email sender service before each test
        emailServer = Mockito.mock(EmailSender.class);
        serviceLocator.registerService("EmailSender", emailServer);

        // Create a new automated stock orderer and stock item
        orderer = new StockOrderer();
        stockItem = new StockItem(1);
    }

    @AfterEach
    public void teardown()
    {
        // Remove references to old object after each test
        emailServer = null;
        orderer = null;
        stockItem = null;

        // Clear state of ServiceLocator singleton
        serviceLocator.clear();
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
        // Check that the number of times sold increases
        Assertions.assertEquals(30, stockItem.getNumTimesSold());
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
        // Check that the number of times sold stays the same
        Assertions.assertEquals(0, stockItem.getNumTimesSold());
    }

    @Test
    public void testProcessOrderWhenOrderQuantityIsZero()
    {
        // Setup
        stockItem.setQuantity(50);
        stockItem.setMinimumOrderQuantity(20);

        // Exercise
        boolean result = orderer.processOrder(stockItem, 0);

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
        SupplierServerMock serverMock = new SupplierServerMock();
        serverMock.alwaysReturnSuccessfulResponse();
        Supplier supplier = new Supplier();
        supplier.supplierServer = serverMock;
        stockItem.setSupplier(supplier);

        // Exercise
        boolean result = orderer.processOrder(stockItem, 31);

        // Verify
        Assertions.assertTrue(result);
        // Check that the method tried to connect the supplier only once
        Assertions.assertEquals(1, serverMock.getNumTimesOrderItems());
        // After ordering 31 items the ordering system should restock 30 more, resulting in 19 + 30 = 49 items
        Assertions.assertEquals(49, stockItem.getQuantity());
    }

    @Test
    public void testProcessOrderWhenSupplierServerReturnsCommunicationErrorOneTime()
    {
        // Setup
        stockItem.setQuantity(50);
        stockItem.setMinimumOrderQuantity(20);
        stockItem.setOrderAmount(30);
        // Mock supplier server to first return a communication error, and then return a successful response
        SupplierServerMock serverMock = new SupplierServerMock();
        serverMock.addResponse(0, SupplierErrorCode.COMMUNICATION_ERROR);
        serverMock.addResponse(30, SupplierErrorCode.SUCCESS);
        Supplier supplier = new Supplier();
        supplier.supplierServer = serverMock;
        stockItem.setSupplier(supplier);

        // Exercise
        boolean result = orderer.processOrder(stockItem, 31);

        // Verify
        Assertions.assertTrue(result);
        // Check that the method tried to connect the supplier twice
        Assertions.assertEquals(2, serverMock.getNumTimesOrderItems());
        // It should wait 5 seconds before ordering from the supplier again
        assertAllLargerOrEqualTo(serverMock.getTimesBetweenCalls(), 5000);
        // After ordering 31 items the ordering system should restock 30 more, resulting in 19 + 30 = 49 items
        Assertions.assertEquals(49, stockItem.getQuantity());
    }

    @Test
    public void testProcessOrderWhenSupplierServerReturnsCommunicationErrorThreeTimes()
    {
        // Setup
        stockItem.setQuantity(50);
        stockItem.setMinimumOrderQuantity(20);
        stockItem.setOrderAmount(30);
        // Mock supplier server to return a communication error three times, and then return a successful response
        SupplierServerMock serverMock = new SupplierServerMock();
        serverMock.addResponse(0, SupplierErrorCode.COMMUNICATION_ERROR);
        serverMock.addResponse(0, SupplierErrorCode.COMMUNICATION_ERROR);
        serverMock.addResponse(0, SupplierErrorCode.COMMUNICATION_ERROR);
        serverMock.addResponse(30, SupplierErrorCode.SUCCESS);
        Supplier supplier = new Supplier();
        supplier.supplierServer = serverMock;
        stockItem.setSupplier(supplier);

        // Exercise
        boolean result = orderer.processOrder(stockItem, 31);

        // Verify
        Assertions.assertTrue(result);
        // Check that the method tried to connect the supplier four times
        Assertions.assertEquals(4, serverMock.getNumTimesOrderItems());
        // It should wait 5 seconds before ordering from the supplier again
        assertAllLargerOrEqualTo(serverMock.getTimesBetweenCalls(), 5000);
        // After ordering 31 items the ordering system should restock 30 more, resulting in 19 + 30 = 49 items
        Assertions.assertEquals(49, stockItem.getQuantity());
    }

    @Test
    public void testProcessOrderWhenSupplierServerReturnsCommunicationErrorFourTimes()
    {
        // Setup
        stockItem.setQuantity(50);
        stockItem.setMinimumOrderQuantity(20);
        stockItem.setOrderAmount(30);
        // Mock supplier server to return a communication error four times
        SupplierServerMock serverMock = new SupplierServerMock();
        serverMock.addResponse(0, SupplierErrorCode.COMMUNICATION_ERROR);
        serverMock.addResponse(0, SupplierErrorCode.COMMUNICATION_ERROR);
        serverMock.addResponse(0, SupplierErrorCode.COMMUNICATION_ERROR);
        serverMock.addResponse(0, SupplierErrorCode.COMMUNICATION_ERROR);
        Supplier supplier = new Supplier();
        supplier.supplierServer = serverMock;
        stockItem.setSupplier(supplier);

        // Exercise
        boolean result = orderer.processOrder(stockItem, 31);

        // Verify
        Assertions.assertTrue(result);
        // Check that the method tried to connect the supplier four times
        Assertions.assertEquals(4, serverMock.getNumTimesOrderItems());
        // It should wait 5 seconds before ordering from the supplier again
        assertAllLargerOrEqualTo(serverMock.getTimesBetweenCalls(), 5000);
        // It should notify the supplier about the connection failure
        verify(emailServer, times(1)).sendEmailToSupplier(eq(supplier), anyString());
        // After ordering 31 items the ordering system should not restock
        Assertions.assertEquals(19, stockItem.getQuantity());
    }

    @Test
    public void testProcessOrderWhenSupplierServerReturnsItemNotFound()
    {
        // Setup
        stockItem.setQuantity(50);
        stockItem.setMinimumOrderQuantity(20);
        stockItem.setOrderAmount(30);
        // Mock supplier server to return a communication error four times
        SupplierServerMock serverMock = new SupplierServerMock();
        serverMock.addResponse(0, SupplierErrorCode.ITEM_NOT_FOUND);
        Supplier supplier = new Supplier();
        supplier.supplierServer = serverMock;
        stockItem.setSupplier(supplier);

        // Exercise
        boolean result = orderer.processOrder(stockItem, 31);

        // Verify
        Assertions.assertTrue(result);
        // Check that the method tried to connect the supplier only once
        Assertions.assertEquals(1, serverMock.getNumTimesOrderItems());
        // The minimum order quantity should now be set to zero
        Assertions.assertEquals(0, stockItem.getMinimumOrderQuantity());
        // After ordering 31 items the ordering system should not restock
        Assertions.assertEquals(19, stockItem.getQuantity());
    }

    @Test
    public void testProcessOrderWhenMinimumOrderQuantityIsZero()
    {
        // Setup
        stockItem.setQuantity(50);
        stockItem.setMinimumOrderQuantity(0);
        // Create a supplier server with a response ready in case the orderer tries to connect to it
        SupplierServerMock serverMock = new SupplierServerMock();
        serverMock.addResponse(10, SupplierErrorCode.OUT_OF_STOCK);
        Supplier supplier = new Supplier();
        supplier.supplierServer = serverMock;
        stockItem.setSupplier(supplier);

        // Exercise
        boolean result = orderer.processOrder(stockItem, 31);

        // Verify
        Assertions.assertTrue(result);
        // Check that the method did not try to connect
        Assertions.assertEquals(0, serverMock.getNumTimesOrderItems());
        // After ordering 31 items the ordering system should not restock
        Assertions.assertEquals(19, stockItem.getQuantity());
    }

    @Test
    public void testProcessOrderWhenSupplierServerReturnsOutOfStock()
    {
        // Setup
        stockItem.setQuantity(50);
        stockItem.setMinimumOrderQuantity(20);
        stockItem.setOrderAmount(30);
        // Mock supplier server to return a communication error four times
        SupplierServerMock serverMock = new SupplierServerMock();
        serverMock.addResponse(10, SupplierErrorCode.OUT_OF_STOCK);
        Supplier supplier = new Supplier();
        supplier.supplierServer = serverMock;
        stockItem.setSupplier(supplier);

        // Exercise
        boolean result = orderer.processOrder(stockItem, 31);

        // Verify
        Assertions.assertTrue(result);
        // Check that the method tried to connect the supplier only once
        Assertions.assertEquals(1, serverMock.getNumTimesOrderItems());
        // The system should email the manager to notify them about the issue
        verify(emailServer, times(1)).sendEmailToManager(anyString());
        // After ordering 31 items the ordering system should restock with only 10 items 10 + 19 = 29
        Assertions.assertEquals(29, stockItem.getQuantity());
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
