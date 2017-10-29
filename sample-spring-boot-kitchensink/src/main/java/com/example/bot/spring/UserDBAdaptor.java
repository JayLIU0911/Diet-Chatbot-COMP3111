package test;

import java.util.ArrayList;
import java.util.HashMap;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import java.sql.*;
import java.net.URISyntaxException;
import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class UserDBAdaptor extends SQLDatabaseEngine{
	
//    @Autowired
//    private SQLDatabaseEngine databaseEngine;
    
	public boolean insert(User user)
    {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs= null;
        boolean result = false;
        int x = 0;
        
        int uid = user.getUserId();
        String name = user.getUserName();
        String status = user.getUserStatus();
        int age = user.getUseAge();
        float weight = user.getUserWeight();
        String purpose = user.getUserGoal().getPurpose();
        float target_weight = user.getUserGoal().getGoalTarget();
        float height = user.getUserHeight();
        int target_day = user.getUserGoal().getGoalDay();
        
        
        try {
            connection = this.getConnection();
            stmt = connection.prepareStatement("SELECT * FROM user_list WHERE uid=?");
            stmt.setInt(1,uid);
            rs = stmt.executeQuery();
            if(!rs.next()){
                return false;
            }
            
                stmt = null;
                rs = null;
                String tableName = "user_" + uid;
                connection.prepareStatement("CREATE TABLE ? (date int, time int, food_intake varchar(200), weight float, energy float, sodium float, fatty_acids_total_saturated float, vegetable float, fruit float, grain float, meat float)");
                stmt.setString(1,tableName);
                rs = stmt.executeQuery();
                
                if(rs.next())
                    x++;
                                             
            
            stmt = null;
            rs = null;
            stmt = connection.prepareStatement("INSERT INTO user_list VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)");
            stmt.setInt(1,uid);
            stmt.setString(2,name);
            stmt.setString(3,status);
            stmt.setInt(4,age);
            stmt.setFloat(5,weight);
            stmt.setString(6,purpose);
            stmt.setFloat(7,target_weight);
            stmt.setFloat(8,height);
            stmt.setInt(9,target_day);
            rs = stmt.executeQuery();
            if(rs.next()){
                x++;}
        } catch (Exception e) {
            log.info("SQLExecption while inserting: {}", e.toString());
        } finally{
            try{
                if(rs.next())
                    rs.close();
                if(stmt!=null)
                    stmt.close();
                if(connection!=null)
                    connection.close();
                
            } catch (Exception ex) {
                log.info("SQLException while closing: {}", ex.toString());
            }
        }
        if(x==2)
            result = true;
        return result;
    }
    
    
    public boolean delete(User user)
    {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean result = false;
        int id = user.getUserId();
        String tableName = "user_"+id;
        int x = 0;
        
        try{
            connection = this.getConnection();
            stmt = connection.prepareStatement("drop table ?");
            stmt.setString(1,tableName);
            rs = stmt.executeQuery();
            if(rs.next())
                x++;
            stmt = null;
            rs = null;
            connection = this.getConnection();
            stmt = connection.prepareStatement("DELETE FROM user_list WHERE uid=?");
            stmt.setInt(1,id);
            rs = stmt.executeQuery();
            if(rs.next())
                x++;
        } catch (Exception e) {
            log.info("SQLExecption while deleting: {}", e.toString());
        } finally{
            try{
                if(rs.next())
                    rs.close();
                if(stmt!=null)
                    stmt.close();
                if(connection!=null)
                    connection.close();
                
            } catch (Exception ex) {
                log.info("SQLException while closing: {}", ex.toString());
            }
        }
        if(x==2)
            result = true;
        return result;
    }
    
    
    public boolean updateRecord(int id, Food food)
    {
        String tableName = "user_" + id;
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean result = false;
        //int date =
        //int time =
        String intake = food.getUserName();
        //float weight = food.getWeight();
        float energy = food.getEnergy();
        float sodium = food.getSodium();
        float fatty = food.getFatty();
        
        User a = this.searchUser(id);
        float weight = a.getUserWeight();
        
        try{
            connection = this.getConnection();
            stmt = connection.prepareStatement("INSERT INTO ? (date, time, food_intake, weight, energy, sodium, fatty_acids_total_saturated) VALUES(?,?,?,?,?,?,?)");
            stmt.setString(1,tableName);
            stmt.setInt(2,date);
            stmt.setInt(3,time);
            stmt.setString(4,intake);
            stmt.setFloat(5,weight);
            stmt.setFloat(6,energy);
            stmt.setFloat(7,sodium);
            stmt.setFloat(8,fatty);
            rs = stmt.executeQuery();
            if(rs.next())
                result = true;
        } catch (Exception e){
            log.info("SQLException while updating: {}", e.toString());
        } finally{
            try{
                if(rs.next())
                    rs.close();
                if(stmt!=null)
                    stmt.close();
                if(connection!=null)
                    connection.close();
            }catch (Exception ex) {
                log.info("SQLException while closing: {}", ex.toString());
            }
        }
        
        return result;
    }
    
    
    public boolean updateWeight(int id, float weight)
    {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean result = false;
        
        try{
            connection = this.getConnection();
            stmt = connection.prepareStatement("update user_list set weight=? where uid=?");
            stmt.setFloat(1,weight);
            stmt.setInt(2,id);
            rs = stmt.executeQuery();
            if(rs.next()){
                result = true;
            }
        } catch (Exception e){
            log.info("SQLException while updating weight: {}", e.toString());
        } finally{
            try{
                if(rs.next())
                    rs.close();
                if(stmt!=null)
                    stmt.close();
                if(connection!=null)
                    connection.close();
            }catch (Exception ex) {
                log.info("SQLException while closing: {}", ex.toString());
            }
        }
        
        return result;
    }
    
    
    public boolean updateGoal(int id, Goal goal)
    {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean result = false;
        float weight = goal.getGoalTarget();
        float day = goal.getGoalDay();
        
        try{
            connection = this.getConnection();
            stmt = connection.prepareStatement("update user_list set target_weight=?,days_for_target=? where uid=?");
            stmt.setInt(1,id);
            stmt.setFloat(2,weight);
            stmt.setFloat(3,day);
            rs = stmt.executeQuery();
            if(rs.next())
                result = true;
        } catch (Exception e){
            log.info("SQLException while updating target: {}", e.toString());
        } finally{
            try{
                if(rs.next())
                    rs.close();
                if(stmt!=null)
                    stmt.close();
                if(connection!=null)
                    connection.close();
            }catch (Exception ex) {
                log.info("SQLException while closing: {}", ex.toString());
            }
        }
        
        return result;
    }
    
    
    public User searchUser(int id)
    {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        User result = null;
        
        
        try{
            connection = this.getConnection();
            stmt = connection.prepareStatement("select * from user_list where uid=?");
            stmt.setInt(1,id);
            rs = stmt.executeQuery();
            if(rs.next()){
                //int id = rs.getInt(1);
                String name = rs.getString(2);
                String status = rs.getString(3);
                int age = rs.getInt(4);
                float weight = rs.getFloat(5);
                String purpose = rs.getString(6);
                float target_weight = rs.getFloat(7);
                float height = rs.getFloat(8);
                int target_day = rs.getInt(9);
                
                result = new User(name,status,age,weight,height,target_day,target_weight,purpose);
            }
        } catch (Exception e){
            log.info("SQLException while searching user: {}", e.toString());
        } finally{
            try{
                if(rs.next())
                    rs.close();
                if(stmt!=null)
                    stmt.close();
                if(connection!=null)
                    connection.close();
            }catch (Exception ex) {
                log.info("SQLException while closing: {}", ex.toString());
            }
        }
        
        return result;
        
    }
    
    
    public ArrayList<ArrayList<String>> searchRecord(int id)
    {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        ArrayList<String> a = new ArrayList<String>();
        
        
        float x = 0;
        float y = 0;
        float z = 0;
        float weight = 0;
        
        String tableName = "user_" + id;
        
        try{
            connection = this.getConnection();
            stmt = connection.prepareStatement("SELECT date, weight, energy, sodium, fatty_acids_total_saturated from ?");
            stmt.setString(1,tableName);
            rs = stmt.executeQuery();
            
            
            
            int date2 = 0;
            //int i = -1;
            
            while(rs.next())
            {
                int date = rs.getInt(1);
                float energy = rs.getFloat(3);
                float sodium = rs.getFloat(4);
                float fatty = rs.getFloat(5);
                weight = rs.getFloat(2);
                
                if(date!=date2)
                {
                    //i++;
                    
                    a = null;
                    a.add(Integer.toString(date));
                    a.add(Float.toString(weight));
                    a.add(Float.toString(x));
                    a.add(Float.toString(y));
                    a.add(Float.toString(z));
                    
                    result.add(a);
                    
                    date2 = date;
                }
                else{
                	x+=energy;
                	y+=sodium;
                	z+=fatty;
                }
            }
            
        } catch (Exception e){
            log.info("SQLException while computing record: {}", e.toString());
        } finally{
            try{
                if(rs.next())
                    rs.close();
                if(stmt!=null)
                    stmt.close();
                if(connection!=null)
                    connection.close();
            }catch (Exception ex) {
                log.info("SQLException while closing: {}", ex.toString());
            }
        }
        
        return result;
        
    }
    
    
    public ArrayList<String> searchRecord2(int id, int date)
    {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        ArrayList<String> result = new ArrayList<String>();
        //String[] result = null;
        //int i = 1;
        float x = 0;
        float y = 0;
        float z = 0;
        float weight = 0;
        
        String tableName = "user_" + id;
        
        try{
            connection = this.getConnection();
            stmt = connection.prepareStatement("SELECT date, weight, energy, sodium, fatty_acids_total_saturated from ? where date=?");
            stmt.setString(1,tableName);
            stmt.setInt(2,date);
            rs = stmt.executeQuery();
            
            result.add(Integer.toString(date));
            
            while(rs.next())
            {
                int date2 = rs.getInt(1);
                weight = rs.getFloat(2);
                float energy = rs.getFloat(3);
                float sodium = rs.getFloat(4);
                float fatty = rs.getFloat(5);
                
                
                
                if(date2==date){
                    x+=energy;
                    y+=sodium;
                    z+=fatty;
                }
                else
                    break;
            }
            
            result.add(Float.toString(weight));
            result.add(Float.toString(x));
            result.add(Float.toString(y));
            result.add(Float.toString(z));
            
        } catch (Exception e){
            log.info("SQLException while computing record: {}", e.toString());
        } finally{
            try{
                if(rs.next())
                    rs.close();
                if(stmt!=null)
                    stmt.close();
                if(connection!=null)
                    connection.close();
            }catch (Exception ex) {
                log.info("SQLException while closing: {}", ex.toString());
            }
        }
        
        return result;
    }
    
    @Override
    public Connection getConnection() throws URISyntaxException, SQLException {
        Connection connection;
        URI dbUri = new URI(System.getenv("DATABASE_URL"));
        
        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() +  "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
        
        log.info("Username: {} Password: {}", username, password);
        log.info ("dbUrl: {}", dbUrl);
        
        connection = DriverManager.getConnection(dbUrl, username, password);
        
        return connection;
    }
    
    
}
