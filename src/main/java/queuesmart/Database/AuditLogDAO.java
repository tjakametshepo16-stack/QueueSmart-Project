package queuesmart.Database;

import queuesmart.DatabaseConnection;
import queuesmart.Models.AuditLog;
import java.sql.*;import java.util.*;

public class AuditLogDAO {
    
    public boolean insertLog(AuditLog a){
        String s="INSERT INTO audit_logs(user_id,action,details) VALUES(?,?,?)";
        
        try(Connection c=DatabaseConnection.getConnection();
                PreparedStatement p=c.prepareStatement(s)){
            if(a.getUserId()==null)p.setNull(1,Types.INTEGER);
            else p.setInt(1,a.getUserId());
            p.setString(2,a.getAction());p.setString(3,a.getDetails());
            return p.executeUpdate()>0;
        
        }catch(SQLException e){
            System.out.println(e.getMessage());
            return false;
        }
    }
    
    public List<AuditLog> getRecentLogs(int limit){
        List<AuditLog> out=new ArrayList<>();
        String s="SELECT log_id,user_id,action,details,logged_at FROM audit_logs ORDER BY logged_at DESC LIMIT ?";
        try(Connection c=DatabaseConnection.getConnection();
                PreparedStatement p=c.prepareStatement(s)){p.setInt(1,Math.max(1,Math.min(limit,200)));
                try(ResultSet r=p.executeQuery()){
                    while(r.next()){AuditLog a=new AuditLog();a.setAuditId(r.getInt("log_id"));
                    int id=r.getInt("user_id");a.setUserId(r.wasNull()?null:id);
                    a.setAction(r.getString("action"));a.setDetails(r.getString("details"));
                    Timestamp t=r.getTimestamp("logged_at");
                    
                    if(t!=null)a.setCreatedAt(t.toLocalDateTime());out.add(a);
                    }
                }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return out;
    }
}