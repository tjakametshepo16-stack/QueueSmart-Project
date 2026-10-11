package queuesmart.Database;

import queuesmart.DatabaseConnection;
import queuesmart.Models.QueueEntry;
import java.sql.*;import java.util.*;

public class QueueDAO {
    
    public int getNextQueueNumber(int serviceId){
        String s="SELECT COALESCE(MAX(queue_number),0)+1 n FROM queue_entries WHERE service_id=? AND queue_date=CURDATE()";
        
        try(Connection c=DatabaseConnection.getConnection();
                PreparedStatement p=c.prepareStatement(s)){p.setInt(1,serviceId);
                
                try(ResultSet r=p.executeQuery()){
                    if(r.next())return r.getInt(1);
                }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }return 0;
    }
    
    public boolean insertQueueEntry(QueueEntry e){
        String s="INSERT INTO queue_entries(student_id,service_id,queue_number,queue_date,status,joined_at) VALUES(?,?,?,?,?,?)";
        
        try(Connection c=DatabaseConnection.getConnection();
                PreparedStatement p=c.prepareStatement(s)){p.setInt(1,e.getStudentId());
                p.setInt(2,e.getServiceId());p.setInt(3,e.getQueueNumber());p.setObject(4,e.getQueueDate());
                p.setString(5,e.getStatus());p.setTimestamp(6,Timestamp.valueOf(e.getJoinedAt()));
                return p.executeUpdate()>0;
        }catch(SQLException x){
            System.out.println(x.getMessage());
            return false;
        }
    }
    
    public List<QueueEntry> getTodaysActiveQueue(){
        List<QueueEntry> out=new ArrayList<>();
        String s="SELECT q.*,u.full_name student_name,s.service_name FROM queue_entries q JOIN students st ON st.student_id=q.student_id JOIN users u ON u.user_id=st.user_id JOIN services s ON s.service_id=q.service_id WHERE q.queue_date=CURDATE() AND q.status IN('WAITING','SERVING') ORDER BY CASE q.status WHEN 'SERVING' THEN 0 ELSE 1 END,q.queue_number";
        
        try(Connection c=DatabaseConnection.getConnection();
                PreparedStatement p=c.prepareStatement(s);ResultSet r=p.executeQuery()){
            while(r.next())out.add(map(r));
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }return out;
    }
    
    public QueueEntry getActiveEntryForStudent(int studentId){
        String s="SELECT q.*,s.service_name FROM queue_entries q JOIN services s ON s.service_id=q.service_id WHERE q.student_id=? AND q.queue_date=CURDATE() AND q.status IN('WAITING','SERVING') ORDER BY q.queue_id DESC LIMIT 1";
        
        try(Connection c=DatabaseConnection.getConnection();
                PreparedStatement p=c.prepareStatement(s)){p.setInt(1,studentId);
                
                try(ResultSet r=p.executeQuery()){
                    if(r.next())return map(r);
                }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }return null;
    }
    
    public int countAhead(QueueEntry e){
        String s="SELECT COUNT(*) n FROM queue_entries WHERE service_id=? AND queue_date=? AND status='WAITING' AND queue_number<?";
        try(Connection c=DatabaseConnection.getConnection();
                PreparedStatement p=c.prepareStatement(s)){p.setInt(1,e.getServiceId());p.setObject(2,e.getQueueDate());
                p.setInt(3,e.getQueueNumber());
                try(ResultSet r=p.executeQuery()){
                    if(r.next())return r.getInt(1);
                }
        }catch(SQLException x){
            System.out.println(x.getMessage());
        }return 0;
    }
    
    public boolean markInService(int queueId,int staffId){
        String s="UPDATE queue_entries SET status='SERVING',staff_id=?,called_at=NOW() WHERE queue_id=? AND status='WAITING'";
        
        try(Connection c=DatabaseConnection.getConnection();
                PreparedStatement p=c.prepareStatement(s)){p.setInt(1,staffId);p.setInt(2,queueId);
                return p.executeUpdate()>0;
        }catch(SQLException e){
            System.out.println(e.getMessage());
            return false;
        }
    }
    
    public boolean markCompleted(int queueId){
        String s="UPDATE queue_entries SET status='COMPLETED',completed_at=NOW() WHERE queue_id=? AND status='SERVING'";
        
        try(Connection c=DatabaseConnection.getConnection();
                PreparedStatement p=c.prepareStatement(s)){p.setInt(1,queueId);
                return p.executeUpdate()>0;
        }catch(SQLException e){
            System.out.println(e.getMessage());
            return false;
        }
    }
    
    private QueueEntry map(ResultSet r)throws SQLException{
        
        QueueEntry e=new QueueEntry();e.setQueueId(r.getInt("queue_id"));
        e.setStudentId(r.getInt("student_id"));e.setServiceId(r.getInt("service_id"));
        e.setQueueNumber(r.getInt("queue_number"));e.setQueueDate(r.getObject("queue_date",java.time.LocalDate.class));
        e.setStatus(r.getString("status"));Timestamp t=r.getTimestamp("joined_at");
        
        if(t!=null)e.setJoinedAt(t.toLocalDateTime());t=r.getTimestamp("called_at");
        
        if(t!=null)e.setCalledAt(t.toLocalDateTime());t=r.getTimestamp("completed_at");
        
        if(t!=null)e.setCompletedAt(t.toLocalDateTime());e.setServiceName(r.getString("service_name"));
        
        try{e.setStudentName(r.getString("student_name"));
        
        }catch(SQLException ignored){}return e;
    }
}
