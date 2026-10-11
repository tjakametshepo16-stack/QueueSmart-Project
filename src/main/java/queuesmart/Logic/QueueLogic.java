package queuesmart.Logic;

import queuesmart.Database.*;
import queuesmart.Models.*;
import java.time.*;
import java.util.*;

public class QueueLogic {
    
    private final QueueDAO dao=new QueueDAO(); 
    private final AuditLogDAO audit=new AuditLogDAO();
 
    public QueueEntry getActiveEntryForStudent(int id){
        return dao.getActiveEntryForStudent(id);
    }
 
    public int getPositionInLine(QueueEntry e){
        return e==null||!"WAITING".equals(e.getStatus())?0:dao.countAhead(e)+1;
    }
 
    public int joinQueue(int studentId,int serviceId){
        
        if(studentId<=0||serviceId<=0||dao.getActiveEntryForStudent(studentId)!=null)
            return 0;
        
        int n=dao.getNextQueueNumber(serviceId);
        
        if(n<=0)return 0;
        
        QueueEntry e=new QueueEntry();
        e.setStudentId(studentId);e.setServiceId(serviceId);e.setQueueNumber(n);e.setQueueDate(LocalDate.now());
        e.setStatus("WAITING");e.setJoinedAt(LocalDateTime.now());
        
        if(!dao.insertQueueEntry(e))return 0;
        audit.insertLog(new AuditLog(null,"QUEUE_JOIN","Student "+studentId+" joined service "+serviceId+" as #"+n));
        return n;
    }
 
    public List<QueueEntry> getTodaysActiveQueue(){
        return dao.getTodaysActiveQueue();
    }
 
    public boolean callNext(QueueEntry e,int staffUserId){
        
        if(e==null)return false;
        int staffId=new StaffDAO().getStaffIdByUserId(staffUserId);
        
        if(staffId<=0)return false;
        boolean ok=dao.markInService(e.getQueueId(),staffId);
        
        if(ok)audit.insertLog(new AuditLog(staffUserId,"QUEUE_CALL","Called #"+e.getQueueNumber()+" ("+e.getServiceName()+")"));
        return ok;
    }
 
    public boolean completeService(QueueEntry e,int staffUserId){
        if(e==null)return false;
        boolean ok=dao.markCompleted(e.getQueueId());
        
        if(ok)audit.insertLog(new AuditLog(staffUserId,"QUEUE_COMPLETE","Completed #"+e.getQueueNumber()+" ("+e.getServiceName()+")"));
        return ok;
    }
}
