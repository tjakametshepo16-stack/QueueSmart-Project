package queuesmart.Database;

import queuesmart.DatabaseConnection;
import java.sql.*;

public class StaffDAO {
    
    public int getStaffIdByUserId(int userId){
        String s="SELECT staff_id FROM staff WHERE user_id=?";
        
        try(Connection c=DatabaseConnection.getConnection();
                PreparedStatement p=c.prepareStatement(s)){p.setInt(1,userId);
                
                try(ResultSet r=p.executeQuery()){
                    if(r.next())return r.getInt(1);}
        }catch(SQLException e){System.out.println(e.getMessage());}
        return 0;
    }
    
    public boolean staffNumberExists(String n){
        String s="SELECT 1 FROM staff WHERE staff_number=?";
        
        try(Connection c=DatabaseConnection.getConnection();
                PreparedStatement p=c.prepareStatement(s)){p.setString(1,n);
                
                try(ResultSet r=p.executeQuery()){
                    return r.next();
                }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return false;
    }
    
    public int insertStaff(int userId,String number){
        return insertStaff(userId,number,"IT Support Technician");
    }
    
    public int insertStaff(int userId,String number,String position){
        String s="INSERT INTO staff(user_id,staff_number,position) VALUES(?,?,?)";
        
        try(Connection c=DatabaseConnection.getConnection();
                PreparedStatement p=c.prepareStatement(s,Statement.RETURN_GENERATED_KEYS)){p.setInt(1,userId);
                p.setString(2,number);
                p.setString(3,position==null||position.isBlank()?"IT Support Technician":position);
                p.executeUpdate();
                
                try(ResultSet r=p.getGeneratedKeys()){
                    if(r.next())return r.getInt(1);
                }
                
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return 0;
    }
}