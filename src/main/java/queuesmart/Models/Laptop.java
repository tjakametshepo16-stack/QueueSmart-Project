package queuesmart.Models;

public class Laptop {

    private int laptopId;
    private String assetTag;
    private String status; // AVAILABLE, ALLOCATED, RETURNED, MAINTENANCE
    private Integer allocatedToStudentId;

    public Laptop() {
    }

    public int getLaptopId() {
        return laptopId; 
    }
    
    public void setLaptopId(int laptopId) { 
        this.laptopId = laptopId; 
    }

    public String getAssetTag() { 
        return assetTag; 
    }
    
    public void setAssetTag(String assetTag) { 
        this.assetTag = assetTag; 
    }

    public String getStatus() { 
        return status; 
    }
    
    public void setStatus(String status) { 
        this.status = status; 
    }

    public Integer getAllocatedToStudentId() {
        return allocatedToStudentId; 
    }
    
    public void setAllocatedToStudentId(Integer allocatedToStudentId) { 
        this.allocatedToStudentId = allocatedToStudentId; 
    }
}
