package com.example.bot.spring;

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
	
   @Autowired
   private SQLDatabaseEngine databaseEngine;

   private FoodDBAdapter foodAdapter = new FoodDBAdapter();
    
    public boolean insert(String id){
    	Connection connection = null;
    	PreparedStatement stmt = null;
    	ResultSet rs = null;

    	boolean result = false;

    	try{
    		connection = this.getConnection();
    		stmt = connection.prepareStatement("SELECT * FROM user_list WHERE uid like '%'||?||'%'");
            stmt.setString(1,id);
            rs = stmt.executeQuery();
            if(rs.next())
                return false;

            stmt = null;
            rs = null;
            String tableName = "user_" + id;
            connection.prepareStatement("CREATE TABLE ? (date int, time int, food_intake varchar(200), weight float, energy float, sodium float, fatty_acids_total_saturated float, vegetable float, fruit float, grain float, meat float)");
            stmt.setString(1,tableName);
            rs = stmt.executeQuery();

            stmt = null;
            rs = null;
            stmt = connection.prepareStatement("INSERT INTO user_list VALUES(?, ' ', ' ', 0, 0, 0, 0, 0, '0')");
            stmt.setString(1,id);
            rs = stmt.executeQuery();

            result = true;
    	} catch (Exception e) {
            log.info("SQLExecption while inserting user: {}", e.toString());
        } finally{
            try{
                if(rs.next())
                    rs.close();
                if(stmt!=null)
                    stmt.close();
                if(connection!=null)
                    connection.close();
                
            } catch (Exception ex) {
                log.info("SQLException while closing1: {}", ex.toString());
            }
        }
        return result;
    }


// 	public boolean insert(ArrayList<String> user)
//     {
//         Connection connection = null;
//         PreparedStatement stmt = null;
//         ResultSet rs= null;
//         boolean result = false;
//         int x = 0;
        
//         String uid = user.get(0);
//         String name = user.get(1);
//         String status = user.get(2);
//         int age = Integer.parseInt(user.get(3));
//         float weight = Float.parseFloat(user.get(4));
//         //String purpose = user.getUserGoal().getPurpose();
//         float target_weight = Float.parseFloat(user.get(5));
//         float height = Float.parseFloat(user.get(6));
//         int target_day = Integer.parseInt(user.get(7));
//         String state = "0";
        
        
//         try {
//             connection = this.getConnection();
//             stmt = connection.prepareStatement("SELECT * FROM user_list WHERE uid like '%'||?||'%'");
//             stmt.setString(1,uid);
//             rs = stmt.executeQuery();
//             if(rs.next()){
//                 return false;
//             }
            
//                 stmt = null;
//                 rs = null;
//                 String tableName = "user_" + uid;
//                 connection.prepareStatement("CREATE TABLE ? (date int, time int, food_intake varchar(200), weight float, energy float, sodium float, fatty_acids_total_saturated float, vegetable float, fruit float, grain float, meat float)");
//                 stmt.setString(1,tableName);
//                 rs = stmt.executeQuery();
                
// //                if(rs.next())
// //                    x++;
            
            
//             stmt = null;
//             rs = null;
//             stmt = connection.prepareStatement("INSERT INTO user_list VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)");
//             stmt.setString(1,uid);
//             stmt.setString(2,name);
//             stmt.setString(3,status);
//             stmt.setInt(4,age);
//             stmt.setFloat(5,weight);
//             //stmt.setString(6,purpose);
//             stmt.setFloat(6,target_weight);
//             stmt.setFloat(7,height);
//             stmt.setInt(8,target_day);
//             stmt.setString(9,state);
//             rs = stmt.executeQuery();
// //            if(rs.next()){
// //                x++;}
//         } catch (Exception e) {
//             log.info("SQLExecption while inserting: {}", e.toString());
//         } finally{
//             try{
//                 if(rs.next())
//                     rs.close();
//                 if(stmt!=null)
//                     stmt.close();
//                 if(connection!=null)
//                     connection.close();
                
//             } catch (Exception ex) {
//                 log.info("SQLException while closing1: {}", ex.toString());
//             }
//         }
// //        if(x==2)
// //            result = true;
//         return true;
//     }
    
    
    public boolean delete(String id)
    {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean result = false;
        //String id = user.getUserId();
        String tableName = "user_"+id;
        int x = 0;
        
        try{
            connection = this.getConnection();
            stmt = connection.prepareStatement("drop table ?");
            stmt.setString(1,tableName);
            rs = stmt.executeQuery();
//            if(rs.next())
//                x++;
            stmt = null;
            rs = null;
            connection = this.getConnection();
            stmt = connection.prepareStatement("DELETE FROM user_list WHERE uid like '%'||?||'%'");
            stmt.setString(1,id);
            rs = stmt.executeQuery();
//            if(rs.next())
//                x++;
            result = true;
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
                log.info("SQLException while closing2: {}", ex.toString());
            }
        }
//        if(x==2)
//            result = true;
        return result;
    }
    


   public boolean updateRecord(String id, String text)
    {
        String tableName = "user_" + id;
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean result = false;
        
        //foodAdapter = new Food(text);
        
        //int date =
        //int time =
        String intake = foodAdapter.getName(text);
        //float weight = food.getWeight();
        float energy = foodAdapter.getQuality(text)[0];
        float sodium = foodAdapter.getQuality(text)[1];
        float fatty = foodAdapter.getQuality(text)[2];
        
        float weight = this.getWeight(id);
        //float weight = a.getUserWeight();
        
        try{
            connection = this.getConnection();
            stmt = connection.prepareStatement("INSERT INTO ? (date, time, food_intake, weight, energy, sodium, fatty_acids_total_saturated) VALUES(?,?,?,?,?,?,?)");
            stmt.setString(1,tableName);
            //stmt.setInt(2,date);
            //stmt.setInt(3,time);
            stmt.setString(4,intake);
            stmt.setFloat(5,weight);
            stmt.setFloat(6,energy);
            stmt.setFloat(7,sodium);
            stmt.setFloat(8,fatty);
            rs = stmt.executeQuery();
            //if(rs.next())
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
                log.info("SQLException while closing3: {}", ex.toString());
            }
        }
        
        return result;
    }

    
    public boolean updateWeight(String id, float weight)
    {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean result = false;
        
        try{
            connection = this.getConnection();
            stmt = connection.prepareStatement("update user_list set weight=? where uid like '%'||?||'%'");
            stmt.setFloat(1,weight);
            stmt.setString(2,id);
            rs = stmt.executeQuery();
//            if(rs.next()){
                result = true;
            //}
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
                log.info("SQLException while closing4: {}", ex.toString());
            }
        }
        
        return result;
    }
    
    
    public boolean updateGoal(String id, Goal goal)
    {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean result = false;
        float weight = goal.getGoalTarget();
        float day = goal.getGoalDay();
        
        try{
            connection = this.getConnection();
            stmt = connection.prepareStatement("update user_list set target_weight=?,days_for_target=? where uid like '%'||?||'%'");
            stmt.setString(1,id);
            stmt.setFloat(2,weight);
            stmt.setFloat(3,day);
            rs = stmt.executeQuery();
            //if(rs.next())
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
                log.info("SQLException while closing5: {}", ex.toString());
            }
        }
        
        return result;
    }
    
    
    // public float searchUserWeight(String id)
    // {
    //     Connection connection = null;
    //     PreparedStatement stmt = null;
    //     ResultSet rs = null;
        
    //     float result = 0;
        
    //     ArrayList<String> x = new ArrayList<String>();
        
        
    //     try{
    //         connection = this.getConnection();
    //         stmt = connection.prepareStatement("select weight from user_list where uid like '%'||?||'%'");
    //         stmt.setString(1,id);
    //         rs = stmt.executeQuery();
    //         if(rs.next()){
    //             //int id = rs.getInt(1);
    //             //String name = rs.getString(2);
    //             //String status = rs.getString(3);
    //             //int age = rs.getInt(4);
    //             //float weight = rs.getFloat(5);
    //             //String purpose = rs.getString(6);
    //             //float target_weight = rs.getFloat(6);
    //             //float height = rs.getFloat(7);
    //             //int target_day = rs.getInt(8);
    //             //String state = rs.getString(9);
                
    //             // x.add(name);
    //             // x.add(status);
    //             // x.add(Integer.toString(age));
    //             // x.add(Float.toString(weight));
    //             // x.add(Float.toString(height));
    //             // x.add(Integer.toString(target_day));
    //             // x.add(Float.toString(target_weight));
    //             //x.add(purpose);
    //             //x.add(state);
                
                
    //             result = rs.getFloat(1);
    //         }
    //     } catch (Exception e){
    //         log.info("SQLException while searching user: {}", e.toString());
    //     } finally{
    //         try{
    //             if(rs.next())
    //                 rs.close();
    //             if(stmt!=null)
    //                 stmt.close();
    //             if(connection!=null)
    //                 connection.close();
    //         }catch (Exception ex) {
    //             log.info("SQLException while closing6: {}", ex.toString());
    //         }
    //     }
        
    //     return result;
        
    // }
    
    
    public ArrayList<ArrayList<String>> searchRecord(String id)
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
                log.info("SQLException while closing7: {}", ex.toString());
            }
        }
        
        return result;
        
    }
    
    
    public ArrayList<String> searchRecord2(String id, int date)
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
                log.info("SQLException while closing8: {}", ex.toString());
            }
        }
        
        return result;
    }
    
    //@Override
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
    
    
    public String getState(String id){
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        //String id = user.getUserId();
        String result = null;
        
        try{
            connection = this.getConnection();
            stmt = connection.prepareStatement("select state from user_list where uid like '%'||?||'%'");
            stmt.setString(1,id);
            rs = stmt.executeQuery();
            
            if(rs.next())
                result = rs.getString(1);
        
    } catch (Exception e){
        log.info("SQLException while getting state: {}", e.toString());
    } finally{
        try{
            if(rs.next())
                rs.close();
                if(stmt!=null)
                    stmt.close();
                    if(connection!=null)
                        connection.close();
                        }catch (Exception ex) {
                            log.info("SQLException while closing9: {}", ex.toString());
                        }
    }
    
    return result;
}
    
    
    public boolean setState(String id, String text){
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        boolean result = false;
        //String id = user.getUserId();
        
        try{
            connection = this.getConnection();
            stmt = connection.prepareStatement("update user_list set state=? where uid like '%'||?||'%'");
            stmt.setString(1,text);
            stmt.setString(2,id);
            rs = stmt.executeQuery();
            
            result = true;
            
        } catch (Exception e){
            log.info("SQLException while setting state: {}", e.toString());
        } finally{
            try{
                if(rs.next())
                    rs.close();
                if(stmt!=null)
                    stmt.close();
                if(connection!=null)
                    connection.close();
            }catch (Exception ex) {
                log.info("SQLException while closing10: {}", ex.toString());
            }
        }
        return result;
    }


    public boolean setName(String id, String text){
    	Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        boolean result = false;
        //String id = user.getUserId();
        
        try{
            connection = this.getConnection();
            stmt = connection.prepareStatement("update user_list set name=? where uid like '%'||?||'%'");
            stmt.setString(1,text);
            stmt.setString(2,id);
            rs = stmt.executeQuery();
            
            result = true;
            
        } catch (Exception e){
            log.info("SQLException while setting name: {}", e.toString());
        } finally{
            try{
                if(rs.next())
                    rs.close();
                if(stmt!=null)
                    stmt.close();
                if(connection!=null)
                    connection.close();
            }catch (Exception ex) {
                log.info("SQLException while closing11: {}", ex.toString());
            }
        }
        return result;
    }


    public boolean setStatus(String id, String text){
    	Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        boolean result = false;
        //String id = user.getUserId();
        
        try{
            connection = this.getConnection();
            stmt = connection.prepareStatement("update user_list set status=? where uid like '%'||?||'%'");
            stmt.setString(1,text);
            stmt.setString(2,id);
            rs = stmt.executeQuery();
            
            result = true;
            
        } catch (Exception e){
            log.info("SQLException while setting status: {}", e.toString());
        } finally{
            try{
                if(rs.next())
                    rs.close();
                if(stmt!=null)
                    stmt.close();
                if(connection!=null)
                    connection.close();
            }catch (Exception ex) {
                log.info("SQLException while closing12: {}", ex.toString());
            }
        }
        return result;
    }


    public boolean setAge(String id, int age){
    	Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        boolean result = false;
        //String id = user.getUserId();
        
        try{
            connection = this.getConnection();
            stmt = connection.prepareStatement("update user_list set age=? where uid like '%'||?||'%'");
            stmt.setInt(1,age);
            stmt.setString(2,id);
            rs = stmt.executeQuery();
            
            result = true;
            
        } catch (Exception e){
            log.info("SQLException while setting age: {}", e.toString());
        } finally{
            try{
                if(rs.next())
                    rs.close();
                if(stmt!=null)
                    stmt.close();
                if(connection!=null)
                    connection.close();
            }catch (Exception ex) {
                log.info("SQLException while closing13: {}", ex.toString());
            }
        }
        return result;
    }


    public boolean setWeight(String id, float weight){
    	Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        boolean result = false;
        //String id = user.getUserId();
        
        try{
            connection = this.getConnection();
            stmt = connection.prepareStatement("update user_list set weight=? where uid like '%'||?||'%'");
            stmt.setFloat(1,weight);
            stmt.setString(2,id);
            rs = stmt.executeQuery();
            
            result = true;
            
        } catch (Exception e){
            log.info("SQLException while setting weight: {}", e.toString());
        } finally{
            try{
                if(rs.next())
                    rs.close();
                if(stmt!=null)
                    stmt.close();
                if(connection!=null)
                    connection.close();
            }catch (Exception ex) {
                log.info("SQLException while closing14: {}", ex.toString());
            }
        }
        return result;
    }


    public boolean setTargetWeight(String id, float target){
    	Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        boolean result = false;
        //String id = user.getUserId();
        
        try{
            connection = this.getConnection();
            stmt = connection.prepareStatement("update user_list set target_weight=? where uid like '%'||?||'%'");
            stmt.setFloat(1,target);
            stmt.setString(2,id);
            rs = stmt.executeQuery();
            
            result = true;
            
        } catch (Exception e){
            log.info("SQLException while setting target weight: {}", e.toString());
        } finally{
            try{
                if(rs.next())
                    rs.close();
                if(stmt!=null)
                    stmt.close();
                if(connection!=null)
                    connection.close();
            }catch (Exception ex) {
                log.info("SQLException while closing15: {}", ex.toString());
            }
        }
        return result;
    }


    public boolean setHeight(String id, float height){
    	Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        boolean result = false;
        //String id = user.getUserId();
        
        try{
            connection = this.getConnection();
            stmt = connection.prepareStatement("update user_list set height=? where uid like '%'||?||'%'");
            stmt.setFloat(1,height);
            stmt.setString(2,id);
            rs = stmt.executeQuery();
            
            result = true;
            
        } catch (Exception e){
            log.info("SQLException while setting height: {}", e.toString());
        } finally{
            try{
                if(rs.next())
                    rs.close();
                if(stmt!=null)
                    stmt.close();
                if(connection!=null)
                    connection.close();
            }catch (Exception ex) {
                log.info("SQLException while closing16: {}", ex.toString());
            }
        }
        return result;
    }


    public boolean setTargetDay(String id, int day){
    	Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        boolean result = false;
        //String id = user.getUserId();
        
        try{
            connection = this.getConnection();
            stmt = connection.prepareStatement("update user_list set days_for_target=? where uid like '%'||?||'%'");
            stmt.setInt(1,day);
            stmt.setString(2,id);
            rs = stmt.executeQuery();
            
            result = true;
            
        } catch (Exception e){
            log.info("SQLException while setting target day: {}", e.toString());
        } finally{
            try{
                if(rs.next())
                    rs.close();
                if(stmt!=null)
                    stmt.close();
                if(connection!=null)
                    connection.close();
            }catch (Exception ex) {
                log.info("SQLException while closing17: {}", ex.toString());
            }
        }
        return result;
    }


    public float getWeight(String id){
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        //String id = user.getUserId();
        float result = 0;
        
        try{
            connection = this.getConnection();
            stmt = connection.prepareStatement("select weight from user_list where uid like '%'||?||'%'");
            stmt.setString(1,id);
            rs = stmt.executeQuery();
            
            if(rs.next())
                result = rs.getFloat(1);
        
    } catch (Exception e){
        log.info("SQLException while getting weight: {}", e.toString());
    } finally{
        try{
            if(rs.next())
                rs.close();
            if(stmt!=null)
                stmt.close();
            if(connection!=null)
                connection.close();
            }catch (Exception ex) {
                log.info("SQLException while closing18: {}", ex.toString());
            }
    }
    
    	return result;
	}


	public String getStatus(String id){
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        //String id = user.getUserId();
        String result = null;
        
        try{
            connection = this.getConnection();
            stmt = connection.prepareStatement("select status from user_list where uid like '%'||?||'%'");
            stmt.setString(1,id);
            rs = stmt.executeQuery();
            
            if(rs.next())
                result = rs.getString(1);
        
    } catch (Exception e){
        log.info("SQLException while getting status: {}", e.toString());
    } finally{
        try{
            if(rs.next())
                rs.close();
            if(stmt!=null)
                stmt.close();
            if(connection!=null)
                connection.close();
            }catch (Exception ex) {
                log.info("SQLException while closing19: {}", ex.toString());
            }
    }
    
    	return result;
	}


	public int getAge(String id){
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        //String id = user.getUserId();
        int result = 0;
        
        try{
            connection = this.getConnection();
            stmt = connection.prepareStatement("select age from user_list where uid like '%'||?||'%'");
            stmt.setString(1,id);
            rs = stmt.executeQuery();
            
            if(rs.next())
                result = rs.getInt(1);
        
    } catch (Exception e){
        log.info("SQLException while getting age: {}", e.toString());
    } finally{
        try{
            if(rs.next())
                rs.close();
            if(stmt!=null)
                stmt.close();
            if(connection!=null)
                connection.close();
            }catch (Exception ex) {
                log.info("SQLException while closing20: {}", ex.toString());
            }
    }
    
    	return result;
	}


	public float getTargetWeight(String id){
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        //String id = user.getUserId();
        float result = 0;
        
        try{
            connection = this.getConnection();
            stmt = connection.prepareStatement("select target_weight from user_list where uid like '%'||?||'%'");
            stmt.setString(1,id);
            rs = stmt.executeQuery();
            
            if(rs.next())
                result = rs.getFloat(1);
        
    } catch (Exception e){
        log.info("SQLException while getting target weight: {}", e.toString());
    } finally{
        try{
            if(rs.next())
                rs.close();
            if(stmt!=null)
                stmt.close();
            if(connection!=null)
                connection.close();
            }catch (Exception ex) {
                log.info("SQLException while closing22: {}", ex.toString());
            }
    }
    
    	return result;
	}


	public float getHeight(String id){
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        //String id = user.getUserId();
        float result = 0;
        
        try{
            connection = this.getConnection();
            stmt = connection.prepareStatement("select height from user_list where uid like '%'||?||'%'");
            stmt.setString(1,id);
            rs = stmt.executeQuery();
            
            if(rs.next())
                result = rs.getFloat(1);
        
    } catch (Exception e){
        log.info("SQLException while getting height: {}", e.toString());
    } finally{
        try{
            if(rs.next())
                rs.close();
            if(stmt!=null)
                stmt.close();
            if(connection!=null)
                connection.close();
            }catch (Exception ex) {
                log.info("SQLException while closing23: {}", ex.toString());
            }
    }
    
    	return result;
	}


	public int getTargetDay(String id){
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        //String id = user.getUserId();
        int result = null;
        
        try{
            connection = this.getConnection();
            stmt = connection.prepareStatement("select days_for_target from user_list where uid like '%'||?||'%'");
            stmt.setString(1,id);
            rs = stmt.executeQuery();
            
            if(rs.next())
                result = rs.getInt(1);
        
    } catch (Exception e){
        log.info("SQLException while getting target day: {}", e.toString());
    } finally{
        try{
            if(rs.next())
                rs.close();
            if(stmt!=null)
                stmt.close();
            if(connection!=null)
                connection.close();
            }catch (Exception ex) {
                log.info("SQLException while closing24: {}", ex.toString());
            }
    }
    
    	return result;
	}


    public float getDailyIntake(String id, int date){
    	ArrayList<String> x = this.searchRecord2(id,date);
    	float result = Float.parseFloat(x.get(2));
    	return result;
    }


    public float getIdealDailyIntake (String id){
    	float weight = this.getWeight(id);
        float lose_weight = weight * 30;
        float maintain = weight*40;
        float gain_weight = weight*50;
        float daily_loss = 3500*3/7;
        float change_per_day = (this.getTargetWeight(id) - weight)/this.getTargetDay(id);
        float intake_calories = change_per_day*daily_loss;
        float BMR=0;

        String status = this.getStatus(id);
        float height = this.getHeight(id);
        int age = this.getAge(id);
        
        if (status.equalsIgnoreCase("male")){
        	BMR = (float)88.362 + ( (float)13.397*weight) + ((float)4.799*height) - ((float)5.677*age);
    
            
        }
        else if (status.equalsIgnoreCase("female")){
        	BMR = (float)447.593 + ( (float)9.247*weight) + ( (float)3.098*height) - ((float)4.33*age);
        }
        
        
        if (this.getTargetWeight(id)==0){
            return BMR;
        }
        else
            return BMR + intake_calories;
    }


	public String generateSummary(String id) {
        
        ArrayList<ArrayList<String>> Record = new ArrayList<ArrayList<String>>();
        
        int number_of_date = this.searchRecord(id).size();
        
        // for (int i = 0; i < number_of_date; i++){
        //     ArrayList<String> Line = new ArrayList<String>();
            
        //     //for (String obj: this.searchRecord(id).get(i)){
        //         Line.add(this.searchRecord(id).get(i));
        //     //}
        //     Record.add(Line);
        // }

        Record = this.searchRecord(id);
        
        
        float[][] float_Record = new float[number_of_date][3];  // calories, sodium, fat
        
        for (int i=0;i<number_of_date;i++){
            for(int j=1; j<3; j++){
                float_Record[i][j] = Float.parseFloat(Record.get(i).get(j+2));
            }
        }
        
        float[] overall = {0,0,0} ; // total calories, sodium, fat
        for (int i=0; i<number_of_date; i++){
            for (int j=0; j<3; j++){
                overall[j] += float_Record[i][j];
            }
        }
        
        String output = "So far, you have consumed " + overall[0] + " calories, " + overall[1] + " sodium and " + overall[2] + "fatty acid. ";
        
        return output;
        
    }


    public File generateWeeklySummary (String id) {
        ArrayList<ArrayList<String>> Record = new ArrayList<ArrayList<String>>();
        Record = this.searchRecord(id);

        String[][] result = New String[7][3];

    }

    
}
