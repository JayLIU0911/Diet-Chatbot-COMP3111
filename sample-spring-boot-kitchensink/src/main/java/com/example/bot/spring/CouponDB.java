package com.example.bot.spring;

import java.util.ArrayList;
import java.util.HashMap;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.Date;
import java.text.*;
import java.util.Calendar;

import java.sql.*;
import java.net.URISyntaxException;
import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * This class is going to connect to the psql database and change/get the data in coupon table
 * @author Liu Shuyue
 * @since 2017-11-18
 */
@Slf4j
public class CouponDB{

    /**
     * This function is going to insert a coupon code into the coupon table with the user id
     * @param id The user id
     * @param code The coupon code
     * @return result Return a boolean value to tell wether the insert is succeed
     */
	public static boolean insert_code(String id, String code){
		Connection connection = null;
        PreparedStatement stmt = null;
        boolean result = false;

        try{
        	connection = getConnection();
        	String sql = "insert into coupon values('" + id + "', '" + code + "')";
        	stmt = connection.prepareStatement(sql);
        	stmt.executeUpdate();
            result = true;
        } catch (Exception e) {
            log.info("SQLExecption while inserting coupon: {}", e.toString());
        } finally{
            try{
                connection.close();
                
                    stmt.close();
            } catch (Exception ex) {
                log.info("SQLException while closing2-1: {}", ex.toString());
            }
        }
        return result;
	}

    /**
     * This function is going to check the coupon code in coupon table
     * If exist return the user id
     * If not exist return null
     * @param code The coupon code to check in the coupon table
     * @return id The user id found in the coupon table corresponding to the coupon code
     */
	public static String check_code(String code){
		Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String result = null;

        try{
        	connection = getConnection();
        	String sql = "select * from coupon where code='" + code + "'";
        	stmt = connection.prepareStatement(sql);
        	rs = stmt.executeQuery();
        	if(rs.next())
        		result = rs.getString(1);
        } catch (Exception e) {
            log.info("SQLExecption while checking coupon: {}", e.toString());
        } finally{
            try{
                connection.close();
                
                    stmt.close();
                rs.close();
            } catch (Exception ex) {
                log.info("SQLException while closing2-2: {}", ex.toString());
            }
        }
        return result;
	}

    /**
     * This function is going to check the user list whether a user can get the coupon or not
     * It depends on the message stored in the follow and coupon column in the user list table
     * @param id the user id to search the row in the user list table
     * @return result A boolean value, if succeed, return true, else return false
     */
	public static boolean check_available(String id){
		Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String follow = null;
        String coupon = null;

        try{
        	connection = getConnection();
        	String sql = "select follow,coupon from user_list where uid = '"+id+"'";
        	stmt = connection.prepareStatement(sql);
        	rs = stmt.executeQuery();
        	while(rs.next()){
        		follow = rs.getString(1);
        		coupon  = rs.getString(2);
        	}
        } catch (Exception e) {
            log.info("SQLExecption while check userlist: {}", e.toString());
        } finally{
            try{
                connection.close();
                
                    stmt.close();
                rs.close();
            } catch (Exception ex) {
                log.info("SQLException while closing2-3: {}", ex.toString());
            }
        }

        if(follow.equals("yes") && coupon.equals("yes"))
			return true;
		else
			return false;
	}

    /**
     * This function is going the delete the coupon code in the coupon table(the whole role)
     * @param code The coupon code in oder to search the row in the coupon table
     * @return result A boolean value to tell whether the delete is completed or not
     */
    public static boolean delete_code(String code){
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean result = false;

        try{
            connection = getConnection();
            String sql = "select from coupon where code = '"+code+"'";
            stmt = connection.prepareStatement(sql);
            rs = stmt.executeQuery();
            if(rs.next())
            {sql = "delete from coupon where code='" + code + "'";
            stmt = connection.prepareStatement(sql);
            stmt.executeUpdate();
            result = true;}
        } catch (Exception e) {
            log.info("SQLExecption while deleting coupon: {}", e.toString());
        } finally{
            try{
                connection.close();
                
                    stmt.close();
                rs.close();
            } catch (Exception ex) {
                log.info("SQLException while closing2-4: {}", ex.toString());
            }
        }
        return result;
    }

    /**
     * This function is going to delete the coupon table's row with the given user id
     * It will delete all the rows with the given user id
     * @param id The user id in order to search in the coupon table
     * @return result A boolean value whether the delete is completed or not
     */
    public static boolean delete_id(String id){
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean result = false;

        try{
            connection = getConnection();
            String sql = "select from coupon where id = '"+id+"'";
            stmt = connection.prepareStatement(sql);
            rs = stmt.executeQuery();
            if(!rs.next())
                result = false;
            else{
            sql = "delete from coupon where id='" + id + "'";
            stmt = connection.prepareStatement(sql);
            stmt.executeUpdate();
            result = true;}
        } catch (Exception e) {
            log.info("SQLExecption while deleting coupon--id: {}", e.toString());
        } finally{
            try{
                connection.close();
                
                    stmt.close();
                rs.close();
            } catch (Exception ex) {
                log.info("SQLException while closing2-5: {}", ex.toString());
            }
        }
        return result;
    }

    /**
     * This function is going to get connection to the psql in heroku
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