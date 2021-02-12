package mt.edu.uom.youstockit;

public class StockItem
{
    // Id of stock item - set only once
    public int id;
    public String name;
    public String category;
    public String description;
    public int minimumOrderQty;
    public int quantity;
    public int orderAmount;
    public StockSupplier supplier;
    public double sellingPrice;
    public int numTimesSold;
    public boolean discontinued;

    public StockItem(int id)
    {
        this.id = id;
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
}
