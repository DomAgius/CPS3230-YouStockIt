package mt.edu.uom.youstockit.ui;

import mt.edu.uom.youstockit.ordering.OrderingFacade;
import mt.edu.uom.youstockit.ordering.ProductCatalogue;
import mt.edu.uom.youstockit.ordering.StockItem;
import mt.edu.uom.youstockit.ordering.StockOrderer;
import mt.edu.uom.youstockit.services.ServiceLocator;
import mt.edu.uom.youstockit.services.email.EmailSender;
import mt.edu.uom.youstockit.services.email.EmailSenderDummy;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class UserInterface
{
    public static void main(String[] args)
    {
        // Create scanner to get user input
        Scanner sc = new Scanner(System.in);
        // Represents the action chosen by the user from the menu
        int menuChoice = 0;

        // Setup email server dummy
        EmailSender emailSender = new EmailSenderDummy();
        ServiceLocator serviceLocator = ServiceLocator.getInstance();
        serviceLocator.registerService("EmailSender", emailSender);

        // Setup ordering system
        StockOrderer stockOrderer = new StockOrderer();
        ProductCatalogue availableItems = new ProductCatalogue();
        ProductCatalogue discontinuedItems = new ProductCatalogue();
        OrderingFacade orderingFacade = new OrderingFacade(stockOrderer, availableItems, discontinuedItems);

        do
        {
            // Display menu
            System.out.println("\nYouStockIt");
            System.out.println("----------");
            System.out.println("1. View Catalogue");
            System.out.println("2. View items in a category");
            System.out.println("3. Add item to Catalogue");
            System.out.println("4. Remove item from Catalogue");
            System.out.println("5. Place customer order");
            System.out.println("6. Calculate profit from sales");
            System.out.println("7. Calculate profit from sales");

            // Get option choice from user
            boolean valid = false;
            do
            {
                try
                {
                    System.out.print("\nPlease select an option: ");
                    menuChoice = sc.nextInt();
                    // Check if choice is valid
                    valid = menuChoice >= 1 && menuChoice <= 7;
                }
                catch (InputMismatchException e)
                {
                    sc.nextLine();
                }

                if(!valid)
                {
                    System.out.println("Invalid choice. Input must be an integer from 1 to 7");
                }
            } while (!valid);

            // Perform requested action
            switch (menuChoice)
            {
                // View Catalogue
                case 1:
                {
                    List<StockItem> items = orderingFacade.getAvailableItems();

                    // If product catalogue is empty, inform the user
                    if(items.isEmpty())
                    {
                        System.out.println("No items available in product catalogue.");
                    }
                    // Otherwise show all items in the catalogue
                    else
                    {
                        for(StockItem item : items)
                        {
                            System.out.println("ID: " + item.getId() + " Name: " + item.getName() +
                                    "Quantity: " + item.getQuantity());
                        }
                    }
                } break;
            }

        } while (menuChoice != 7);

    }

    // Gets an integer input from the user
    public void getIntInput(String prompt, String errorMessage, int min, int max)
    {

    }
}
