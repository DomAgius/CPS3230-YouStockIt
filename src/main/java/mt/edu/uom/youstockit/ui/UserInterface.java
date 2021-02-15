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
        // Represents the action chosen by the user from the menu
        int menuChoice;
        // Counter used to assign unique ids to stock items
        int stockIdCounter = 1;


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
            System.out.println("7. Exit");

            // Get option choice from user
            boolean valid;
            do
            {
                // Ask user for input, and validate it
                menuChoice = getIntInput("\nPlease select an option");
                valid = menuChoice >= 1 && menuChoice <= 7;

                // If choice is invalid, output an error message
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
                            System.out.println("----------------Items----------------");
                            System.out.println("ID: " + item.getId() + " Name: " + item.getName() +
                                    " Quantity: " + item.getQuantity());
                        }
                    }
                } break;

                // Add item to catalogue
                case 3:
                {
                    // Create a new stock item and update stock id counter
                    StockItem item = new StockItem(stockIdCounter++);

                    // Ask user for name
                    valid = false;
                    while(!valid)
                    {
                        String name = getStringInput("Input item name");
                        valid = item.setName(name);
                        if(!valid)
                        {
                            System.out.println("Name must be between 5 and 100 characters long (inclusive).");
                        }
                    }

                    // Ask user for category
                    String category = getStringInput("Input item category");
                    item.setCategory(category);

                    // Ask user for description
                    valid = false;
                    while(!valid)
                    {
                        String description = getStringInput("Input item description");
                        valid = item.setDescription(description);
                        if(!valid)
                        {
                            System.out.println("Description must be between 0 and 500 characters long (inclusive).");
                        }
                    }

                    // Ask user for quantity
                    valid = false;
                    while(!valid)
                    {
                        int quantity = getIntInput("Input item quantity");
                        valid = item.setQuantity(quantity);
                        if(!valid)
                        {
                            System.out.println("Item quantity must be at least 0.");
                        }
                    }

                    // Ask user for minimum order quantity
                    valid = false;
                    while(!valid)
                    {
                        int minOrderQuantity = getIntInput("Input minimum order quantity");
                        valid = item.setMinimumOrderQuantity(minOrderQuantity);
                        if(!valid)
                        {
                            System.out.println("Minimum order quantity must be at least 0.");
                        }
                    }

                    // Ask user for order amount
                    valid = false;
                    while(!valid)
                    {
                        int orderAmount = getIntInput("Input order amount");
                        valid = item.setOrderAmount(orderAmount);
                        if(!valid)
                        {
                            System.out.println("Order amount must be at least 0.");
                        }
                    }

                    // Todo add supplier choice

                    // Ask user for buying and selling prices
                    valid = false;
                    while(!valid)
                    {
                        double buyingPrice = getPriceInput("Input buying price");
                        double sellingPrice = getPriceInput("Input selling price");
                        valid = item.setBuySellPrices(buyingPrice, sellingPrice);
                        if(!valid)
                        {
                            System.out.println("Selling price must be larger than buying price.");
                        }
                    }

                    // Add item to product catalogue
                    orderingFacade.addItem(item);
                }
                break;
            }

        } while (menuChoice != 7);

    }

    // Gets an integer input from the user
    public static int getIntInput(String prompt)
    {
        // Create scanner to get user input
        Scanner sc = new Scanner(System.in);
        // User input
        int input = 0;
        // Initially assume input is invalid
        boolean valid = false;
        // Keep asking user for input until a valid integer is parsed
        do
        {
            try
            {
                System.out.print(prompt + ": ");
                input = sc.nextInt();
                valid = true;
            }
            catch (InputMismatchException e)
            {
                // If input is not a valid integer, clear standard input and display an error message
                sc.nextLine();
                System.out.println("Input must be an integer.");
            }
        } while (!valid);

        return input;
    }

    // Gets a price input from the user
    public static double getPriceInput(String prompt)
    {
        // Create scanner to get user input
        Scanner sc = new Scanner(System.in);
        // User input
        double input = 0;
        // Initially assume input is invalid
        boolean valid = false;
        // Keep asking user for input until a valid integer is parsed
        do
        {
            try
            {
                System.out.print(prompt + ": ");
                input = sc.nextDouble();
                // Price must be positive
                valid = input > 0;
            }
            catch (InputMismatchException e)
            {
                // If input is not a valid integer, clear standard input and display an error message
                sc.nextLine();
            }

            // If input is not valid
            if(!valid)
            {
                System.out.println("Input must be a positive real number.");
            }
        } while (!valid);

        // Truncate input to 2 decimal places
        return Math.floor(input * 100.0) / 100.0;
    }

    public static String getStringInput(String prompt)
    {
        // Create scanner to get user input
        Scanner sc = new Scanner(System.in);
        // Read next line of user input
        System.out.print(prompt + ": ");
        return sc.nextLine();
    }
}
