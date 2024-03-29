package mt.edu.uom.youstockit.ordering;

import mt.edu.uom.youstockit.supplier.Supplier;

public class StockItem
{
    // Id of stock item - set only once
    private int id;
    private String name;
    private String category;
    private String description;
    private int minimumOrderQty;
    private int quantity;
    private int orderAmount;
    private Supplier supplier;
    private double buyingPrice;
    private double sellingPrice;
    private int numTimesSold;

    public StockItem(int id)
    {
        // Set id
        this.id = id;

        // Set numeric values to -1 to show that they have not been initialized
        minimumOrderQty = -1;
        quantity = -1;
        orderAmount = -1;
    }

    public int getId()
    {
        return id;
    }

    public boolean setName(String name)
    {
        // Name should be between 5 and 100 characters
        boolean validName = name.length() >= 5 && name.length() <= 100;

        if(validName)
        {
            this.name = name;
        }
        // Return true if name change was successful
        return validName;
    }

    public String getName()
    {
        return this.name;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public String getCategory()
    {
        return category;
    }

    public boolean setDescription(String desc)
    {
        // Description can be up to 500 characters long
        boolean validDesc = desc.length() <= 500;

        if(validDesc)
        {
            this.description = desc;
        }
        // Return true if description change was successful
        return validDesc;
    }

    public String getDescription()
    {
        return description;
    }

    public boolean setMinimumOrderQuantity(int quantity)
    {
        // Minimum order quantity must be at least 0
        boolean valid = quantity >= 0;

        if(valid)
        {
            this.minimumOrderQty = quantity;
        }
        // Return true if change was successful
        return valid;
    }

    public int getMinimumOrderQuantity()
    {
        return minimumOrderQty;
    }

    public boolean setQuantity(int quantity)
    {
        // Quantity must be at least 0 (it can be set to be less than the minimum order quantity)
        boolean valid = quantity >= 0;

        if(valid)
        {
            this.quantity = quantity;
        }
        // Return true if change was successful
        return valid;
    }

    public int getQuantity()
    {
        return quantity;
    }

    public boolean setOrderAmount(int amount)
    {
        // Order amount must be at least 1
        boolean valid = amount >= 1;

        if(valid)
        {
            this.orderAmount = amount;
        }
        // Return true if change was successful
        return valid;
    }

    public int getOrderAmount()
    {
        return orderAmount;
    }

    public void setSupplier(Supplier supplier)
    {
        this.supplier = supplier;
    }

    public Supplier getSupplier()
    {
        return supplier;
    }

    public boolean setBuySellPrices(double buyingPrice, double sellingPrice)
    {
        // Both buying price and selling prices must be larger than zero, and the selling price must be larger than or
        // equal to the buying price.
        boolean valid = buyingPrice > 0.0 && sellingPrice >= buyingPrice;

        if(valid)
        {
            this.buyingPrice = buyingPrice;
            this.sellingPrice = sellingPrice;
        }
        // Return true if change was successful
        return valid;
    }

    public double getBuyingPrice()
    {
        return buyingPrice;
    }

    public double getSellingPrice()
    {
        return sellingPrice;
    }

    public void incrementNumTimesSold(int numTimesSold)
    {
        this.numTimesSold += numTimesSold;
    }

    public int getNumTimesSold()
    {
        return numTimesSold;
    }
}
