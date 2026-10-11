package queuesmart.Logic;

import queuesmart.Database.*;
import queuesmart.Models.*;
import java.util.*;

public class LaptopLogic {
    private final LaptopDAO dao=new LaptopDAO(); 
    private final AuditLogDAO audit=new AuditLogDAO();
    
    public List<Laptop> getAvailableLaptops(){
        return dao.getAvailableLaptops();
    }
    
    public boolean allocateLaptop(int laptopId,int studentId,int staffUserId){
        if(laptopId<=0||studentId<=0)
            return false;
        
        int staffId=new StaffDAO().getStaffIdByUserId(staffUserId);
        boolean ok=dao.allocateLaptop(laptopId,studentId);
        
        if(ok)audit.insertLog(new AuditLog(staffUserId,"LAPTOP_ALLOCATE","Laptop "+laptopId+" allocated to student "+studentId+" by staff "+staffId));
        return ok;
    }
}
