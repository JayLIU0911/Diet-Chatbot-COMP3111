package com.example.bot.spring;

import java.util.ArrayList;
import java.util.HashMap;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.Date;
import java.text.*;

import java.sql.*;
import java.net.URISyntaxException;
import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * A class use to connect to the psql database
 * @author Liu Shuyue
 * @since 2017-11-18
 */
@Slf4j
public class UserDB{

	/**
	 * This function is going to check the database whether a user id is in the user list
	 * @param id Input a user id in order to check it in the database
	 * @return result return a boolean, if the user exist, return false, else, true
	 */
    public static boolean check_userlist(String id){
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try{
        connection = getConnection();
        stmt = connection.prepareStatement("SELECT * FROM user_list WHERE uid = ?");
        stmt.setString(1,id);
        rs = stmt.executeQuery();
        if(rs.next())
        		return false;
        } catch (Exception e) {
            log.info("SQLExecption while inserting user: {}", e.toString());
        } finally{
            try{
                connection.close();
                stmt.close();

            } catch (Exception ex) {
                log.info("SQLException while closing1: {}", ex.toString());
            }
        }
        
        return true;
    }

    /**
     * This function is going the create a new user table with the name of user id
     * @param id Input user id to be the name of the table
     */
    public static void create_usertable(String id){
        Connection connection = null;
        PreparedStatement stmt = null;
        try{
        	
        connection = getConnection();
        String sql = "CREATE TABLE IF NOT EXISTS " + id + " (date varchar(50), time varchar(50), food_intake varchar(200), price varchar(50), weight varchar(50), energy varchar(50), sodium varchar(50), fatty_acids_total_saturated varchar(50))";
        stmt = connection.prepareStatement(sql);
        stmt.executeUpdate();
        } catch (Exception e) {
            log.info("SQLExecption whle inserting user2: {}", e.toString());
        } finally{
            try{
                connection.close();
                stmt.close();

            } catch (Exception ex) {
                log.info("SQLException while closing2: {}", ex.toString());
            }
        }
        
    }

    /**
     * This function is going to insert a new row in the user list table with the user id
     * The new row is initiated with the user id and haven't get coupon
     * @param id Input the user id to create a new row
     */
    public static void insert_user(String id){
        Connection connection = null;
        PreparedStatement stmt = null;
        try{
        connection = getConnection();
        String sql = "INSERT INTO user_list VALUES('" + id + "', ' ', ' ', ' ', ' ', ' ', ' ', ' ', '0', ' ', 'yes')";
        stmt = connection.prepareStatement(sql);
        stmt.executeUpdate();
        } catch (Exception e) {
            log.info("SQLExecption while inserting user3: {}", e.toString());
        } finally{
            try{
                connection.close();
                stmt.close();

            } catch (Exception ex) {
                log.info("SQLException while closing3: {}", ex.toString());
            }
        }
        
    }

    /**
     * This function is going to drop a user table with the user id
     * @param id Input the user id to find the corresponding user table
     * @return result Return true if the table found and drop successfully, else return false
     */
    public static boolean drop_table(String id) {
        Connection connection = null;
        PreparedStatement stmt = null;
        boolean result = false;
        try{
        	
        connection = getConnection();
        String sql = "drop table if exists " + id;
        stmt = connection.prepareStatement(sql);
        stmt.executeUpdate();
        result = true;
        } catch (Exception e) {
            log.info("SQLExecption while deleting1: {}", e.toString());
        } finally{
            try{
                connection.close();
				stmt.close();

            } catch (Exception ex) {
                log.info("SQLException while closing4: {}", ex.toString());
            }
        }
        return result;
    }

    /**
     * This function is going to delete the user's row from the user list table
     * First to check whether the user's row exist
     * And then delete the corresponding user row
     * @param id Input the user id to check whether the row exist and delete the row
     * @return result Return a boolean. If the user id not exist, return false, else if the user id exist and deleted successfully, return true
     */
    public static boolean delete_user(String id) {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean result = false;
        try{
        connection = getConnection();
        stmt = connection.prepareStatement("select FROM user_list WHERE uid = ?");
        stmt.setString(1,id);
        rs = stmt.executeQuery();
        if(rs.next()){
        stmt = connection.prepareStatement("DELETE FROM user_list WHERE uid = ?");
        stmt.setString(1,id);
        stmt.executeUpdate();
        result = true;}
        else
        	result = false;
        } catch (Exception e) {
            log.info("SQLExecption while deleting2: {}", e.toString());
            result = false;
        } finally{
            try{
                connection.close();
                stmt.close();
                rs.close();

            } catch (Exception ex) {
                log.info("SQLException while closing5: {}", ex.toString());
            }
        }
        return result;
        
    }

    /**
     * This function is going to insert the record into the user table with food name, user's weight, food's qualities
     * First we will get the current time
     * And we will call food.class to the corresponding nutrition depending on the food name
     * Finally we use all these information to insert a new row in the user table
     * @param id Input the user id to get corresponding user's weight and select the user table
     * @param text Input the food name to get information about the food
     * @param weight Input the user's weight at the time to insert into the table
     * @return Return a boolean. If all these things done successfully(can find the user table and get the information about the food), it will return true. Else, return false
     */
    public static boolean insert_record(String id, String text, String weight){
        Connection connection = null;
        PreparedStatement stmt = null;
        Food foodAdapter = new Food();
        Date current_time = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh/mm/ss");

        boolean result = false;

        String date = dateFormat.format(current_time);
        String time = timeFormat.format(current_time);

        String energy = Float.toString(Food.getQuality(text)[0]);
        String sodium = Float.toString(Food.getQuality(text)[1]);
        String fatty = Float.toString(Food.getQuality(text)[2]);

        try{
        connection = getConnection();
        String sql = "insert into " + id + " (date, time, food_intake, price, weight, energy, sodium, fatty_acids_total_saturated) values('" + date +"','"+time+"','"+text+"','0','"+weight+"','"+energy+"','"+sodium+"','"+fatty+"')";
        stmt = connection.prepareStatement(sql);
        stmt.executeUpdate();
        result = true;
        } catch (Exception e) {
            log.info("SQLExecption while update record: {}", e.toString());
        } finally{
            try{
                connection.close();
                
                    stmt.close();

            } catch (Exception ex) {
                log.info("SQLException while closing6: {}", ex.toString());
            }
        }
        return result;
    }

    /**
     * This function is just created for the test case in order to delete the corresponding record we insert through the test case
     * It is not used in the controller
     * @param id Input the user id to find the user table
     * @param text Input the food name to find the corresponding rows in the user table
     * @return result Return a boolean value if all the things done successfully
     */
    public static boolean deleteRecord(String id, String text){
    	Connection connection = null;
        PreparedStatement stmt = null;
        boolean result = false;
        ResultSet rs = null;
        try{
        connection = getConnection();
        String sql = "select * from "+id+" where food_intake='"+text+"'";
        stmt = connection.prepareStatement(sql);
        rs = stmt.executeQuery();
        if(rs.next()){
        sql = "delete from "+id+" where food_intake='"+text+"'";
        stmt = connection.prepareStatement(sql);
        stmt.executeUpdate();
        result = true;}
    	} catch (Exception e) {
            log.info("SQLExecption while delete record: {}", e.toString());
        } finally{
            try{
                connection.close();
                rs.close();
                    stmt.close();

            } catch (Exception ex) {
                log.info("SQLException while closing11: {}", ex.toString());
            }
        }
        return result;

    }

    /**
     * This function is going to search the whole user table
     * It will search the table depending on the date and add the information on the same date together
     * It will return the data in a 2D arraylist with one date added together
     * @param id Input the user id to find the user table
     * @return result Return a 2D arraylist(String) with one date of a row and there are five columns(date,weight,energy,sodium,fatty)
     */
    public static ArrayList<ArrayList<String>> DBsearch_record(String id){
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
        //ArrayList<String> a = new ArrayList<String>(5);
        
        float x = 0;
        float y = 0;
        float z = 0;
        String weight = "0";
        String date2 = "0";
        String date = "0";
        String energy = "0";
        String sodium = "0";
        String fatty = "0";
        String weight2 = "0";

        int i = 0;

        try{
            connection = getConnection();
        	String sql = "SELECT date, weight, energy, sodium, fatty_acids_total_saturated from " + id;
        	stmt = connection.prepareStatement(sql);
        	rs = stmt.executeQuery();

            while(rs.next())
            {
                date = rs.getString(1);
                energy = rs.getString(3);
                sodium = rs.getString(4);
                fatty = rs.getString(5);
                weight = rs.getString(2);

                if(date2.equals("0")){
                    x = Float.parseFloat(energy);
                    y = Float.parseFloat(sodium);
                    z = Float.parseFloat(fatty);
                    date2 = date;
                    weight2 = weight;
                    log.info("12345-6, {}", x);
                    log.info("12345-6, {}", y);
                    log.info("12345-6, {}", z);
                }

                else if(!date.equals(date2))
                {

                    ArrayList<String> a = new ArrayList<String>(5);
        
                    a.add(date2);
                    a.add(weight2);
                    a.add(Float.toString(x));
                    a.add(Float.toString(y));
                    a.add(Float.toString(z));

                    log.info("12345-5, {}", x);
                    log.info("12345-5, {}", y);
                    log.info("12345-5, {}", z);

                    result.add(a);

                    x = Float.parseFloat(energy);
                    y = Float.parseFloat(sodium);
                    z = Float.parseFloat(fatty);

                    date2 = date;
                    weight2 = weight;
                    i = i + 1;
                    log.info("123456-2");
                }
                else{
                    x = x + Float.parseFloat(energy);
                    y = y + Float.parseFloat(sodium);
                    z = z + Float.parseFloat(fatty);
                    log.info("12345-7, {}", x);
                    log.info("12345-7, {}", y);
                    log.info("12345-7, {}", z);
                    weight2 = weight;
                }
            }
            if(!date2.equals("0")){
            ArrayList<String> b = new ArrayList<String>(5);
           
            b.add(date);
            b.add(weight);
            b.add(Float.toString(x));
            b.add(Float.toString(y));
            b.add(Float.toString(z));
            result.add(b);}

        }catch(Exception e){
            log.info("In searchRecord,{}",e.toString());
        } finally{
        	try{
                connection.close();
                
                    stmt.close();
                rs.close();

            } catch (Exception ex) {
                log.info("SQLException while closing7: {}", ex.toString());
            }
        }
        
        return result;
    }

    /**
     * This function is going the search the data in the user table on a exact given date
     * It will return the data on the given day
     * @param id Input the user id to find the user table
     * @param date Input the date you want to check in the user table
     * @return result Return an Arraylist which include the information on the given date by five values -- date, weight, energy, sodium, and fatty
     */
    public static ArrayList<String> DBsearch_day(String id, String date){
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ArrayList<String> result = new ArrayList<String>();

        float x = 0;
        float y = 0;
        float z = 0;
        String weight=null;
        try{
        connection = getConnection();
        String sql = "SELECT date, weight, energy, sodium, fatty_acids_total_saturated from " + id + " where date = '" + date +"'";
        stmt = connection.prepareStatement(sql);
        rs = stmt.executeQuery();
        

        while(rs.next())
            {
                String date2 = rs.getString(1);
                weight = rs.getString(2);
                String energy = rs.getString(3);
                String sodium = rs.getString(4);
                String fatty = rs.getString(5);

                x = x + Float.parseFloat(energy);
                y = y + Float.parseFloat(sodium);
                z = z + Float.parseFloat(fatty);
            }

            result.add(date);
            result.add(weight);
            result.add(Float.toString(x));
            result.add(Float.toString(y));
            result.add(Float.toString(z));

        } catch (Exception e) {
            log.info("SQLExecption while search record2: {}", e.toString());
        } finally{
            try{
                connection.close();
                
                    stmt.close();
                rs.close();

            } catch (Exception ex) {
                log.info("SQLException while closing8: {}", ex.toString());
            }
        }
        return result;
    }

    /**
     * This function the going to get the data on a given column and a given row in the user list table
     * @param id Input the user id to find the corresponding row in the user list
     * @param column Input the column name to find the column
     * @return result Return a String which is the data found in the given row and column
     */
    public static String select_userlist(String id, String column){
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String result = null;
        try{
            connection = getConnection();
            String sql = "select " + column + " from user_list where uid = '" + id + "'";
            stmt = connection.prepareStatement(sql);
            rs = stmt.executeQuery();
            if(rs.next())
            	result = rs.getString(1);
        } catch (Exception e){
        log.info("SQLException while get user: {}", e.toString());
    } finally{
        try{
            connection.close();
            
                stmt.close();
            rs.close();         
            }catch (Exception ex) {
                log.info("SQLException while closing9: {}", ex.toString());
            }
    }
    return result;
    }

    /**
     * This function is going to update the information in the user list table
     * It will update the data in the given row and given column
     * @param id Input the user id to find the corresponding row in the user list
     * @param column Input the column name to find the column
     * @param text Input the text you want to update at the given row and column in the user list
     * @return result Return a boolean value to tell whether update succeeds
     */
    public static boolean update_userlist(String id, String column, String text){
        Connection connection = null;
        PreparedStatement stmt = null;
        boolean result = false;

        try{
            connection = getConnection();
            String sql = "update user_list set " + column + "='" + text + "' where uid = '" + id + "'";
            stmt = connection.prepareStatement(sql);
            stmt.executeUpdate();
            result = true;
        } catch (Exception e){
            log.info("SQLException while setting state: {}", e.toString());
        } finally{
            try{
                connection.close();
                
                    stmt.close();
                
            }catch (Exception ex) {
                log.info("SQLException while closing10: {}", ex.toString());
            }
        }
        return result;
    }

    /**
     * This function is going to connect the data base in heroku
     * @return connection Return the connection
     */
    private static Connection getConnection() throws URISyntaxException, SQLException {
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