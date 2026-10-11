package queuesmart.Database;

import queuesmart.DatabaseConnection;
import queuesmart.Models.Feedback;
import java.sql.*;

public class FeedbackDAO {
    
    public boolean insertFeedback(Feedback f){
        String s="INSERT INTO feedback(student_id,queue_id,rating,comment) VALUES(?,?,?,?)";
        
        try(Connection c=DatabaseConnection.getConnection();
                PreparedStatement p=c.prepareStatement(s)){p.setInt(1,f.getStudentId());
                
                if(f.getQueueId()>0)p.setInt(2,f.getQueueId());
                
                else p.setNull(2,Types.INTEGER);p.setInt(3,f.getRating());p.setString(4,f.getComment());
                
                return p.executeUpdate()>0;
                
        }catch(SQLException e){
            System.out.println(e.getMessage());
                return false;
        }
    }
    
    public double getAverageRating(){
        try(Connection c=DatabaseConnection.getConnection();
                PreparedStatement p=c.prepareStatement("SELECT COALESCE(AVG(rating),0) avg_rating FROM feedback");
                ResultSet r=p.executeQuery()){
            return r.next()?r.getDouble("avg_rating"):0;
        }catch(SQLException e){
            return 0;
        }
    }
}
