package mt.edu.uom.youstockit.email;

import java.util.HashMap;
import java.util.Map;

public class ServiceLocator
{
    // Singleton instance of service provider
    private static ServiceLocator instance;
    // Directory of services available to all classes
    private Map<String, Object> services;

    private ServiceLocator()
    {
        services = new HashMap<String, Object>();
    }

    // Lazily instantiates the singleton instance
    public static ServiceLocator getInstance()
    {
        if(instance == null)
        {
            instance = new ServiceLocator();
        }
        return instance;
    }

    // Add a service to the global list of available services
    public void registerService(String name, Object serviceProvider)
    {
        services.put(name, serviceProvider);
    }

    // Return a service object
    public Object findService(String name)
    {
        return services.get(name);
    }

    // Clears all services (useful during testing)
    public void clear()
    {
        services.clear();
    }
}
