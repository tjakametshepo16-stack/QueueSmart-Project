package queuesmart.Models;

public class Service {

    private int serviceId;
    private String serviceName;
    private String description;
    private boolean active;

    public Service() {
    }

    public Service(int serviceId, String serviceName, String description, boolean active) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.description = description;
        this.active = active;
    }

    public int getServiceId() { 
        return serviceId; 
    }
    
    public void setServiceId(int serviceId) { 
        this.serviceId = serviceId; 
    }

    public String getServiceName() {
        return serviceName; 
    }
    
    public void setServiceName(String serviceName) { 
        this.serviceName = serviceName; 
    }

    public String getDescription() {
        return description; 
    }
    
    public void setDescription(String description) { 
        this.description = description; 
    }

    public boolean isActive() { 
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active; 
    }

    @Override
    public String toString() {
        return serviceName;
    }
}
