package queuesmart.Database;

import queuesmart.DatabaseConnection;
import queuesmart.Models.Laptop;
import java.sql.*;import java.util.*;

public class LaptopDAO {
    
    public List<Laptop> getAvailableLaptops(){
        List<Laptop> out=new ArrayList<>();
        String s="SELECT laptop_id,asset_number,status FROM laptops WHERE status='AVAILABLE' ORDER BY model,asset_number";
        
        try(Connection c=DatabaseConnection.getConnection();
                PreparedStatement p=c.prepareStatement(s);ResultSet r=p.executeQuery()){
            
            while(r.next()){Laptop l=new Laptop();l.setLaptopId(r.getInt("laptop_id"));
            l.setAssetTag(r.getString("asset_number"));l.setStatus(r.getString("status"));out.add(l);
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }return out;
    }
    
    public boolean allocateLaptop(int laptopId,int studentId){
        String u="UPDATE laptops SET status='ALLOCATED' WHERE laptop_id=? AND status='AVAILABLE'";
        String i="INSERT INTO laptop_allocations(laptop_id,student_id,allocated_by) VALUES(?,?,NULL)";
        
        try(Connection c=DatabaseConnection.getConnection()){c.setAutoCommit(false);
        try(PreparedStatement p=c.prepareStatement(u)){
            p.setInt(1,laptopId);
            
            if(p.executeUpdate()!=1){c.rollback();
            return false;
            }
        }try(PreparedStatement p=c.prepareStatement(i)){
            p.setInt(1,laptopId);p.setInt(2,studentId);p.executeUpdate();
        }c.commit();
        return true;
        }catch(SQLException e){
            System.out.println(e.getMessage());
            return false;
        }
    }
}