package queuesmart.Database;

import queuesmart.DatabaseConnection;
import queuesmart.Models.User;
import java.sql.*;

public class UserDAO {
     
    public User findByUsername(String username){
        String sql="SELECT u.user_id,u.username,u.password_hash,r.role_name AS role,u.full_name,u.email FROM users u JOIN roles r ON r.role_id=u.role_id WHERE u.username=? AND u.is_active=TRUE";
    
        try(Connection c=DatabaseConnection.getConnection();
                PreparedStatement ps=c.prepareStatement(sql)){
            ps.setString(1,username);
            
            try(ResultSet rs=ps.executeQuery()){
                if(rs.next())return mapRow(rs);
            }
        }catch(SQLException e){
            System.out.println("User lookup failed: "+e.getMessage());
        }
        return null;
 }
 
    public boolean usernameExists(String username){
        return findByUsername(username)!=null;
    }
    
    public int insertUser(User u){
        String sql="INSERT INTO users(username,password_hash,full_name,email,role_id) VALUES(?,?,?,?,(SELECT role_id FROM roles WHERE role_name=?))";
        try(Connection c=DatabaseConnection.getConnection();
                PreparedStatement ps=c.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1,u.getUsername()); ps.setString(2,u.getPasswordHash());
            ps.setString(3,u.getFullName()); ps.setString(4,u.getEmail());
            ps.setString(5,u.getRole());     ps.executeUpdate();
            
            try(ResultSet rs=ps.getGeneratedKeys()){
                if(rs.next())return rs.getInt(1);
            }
        }catch(SQLException e){
            System.out.println("User insert failed: "+e.getMessage());
        }
        return 0;
 }
    private User mapRow(ResultSet rs)throws SQLException{
        return new User(
            rs.getInt("user_id"),
            rs.getString("username"),
            rs.getString("password_hash"),
            rs.getString("role"),
            rs.getString("full_name"),
            rs.getString("email")         
        );
    }
}