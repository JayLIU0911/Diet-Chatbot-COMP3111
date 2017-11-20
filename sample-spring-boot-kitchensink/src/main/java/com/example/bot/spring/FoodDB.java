package com.example.bot.spring;

import lombok.extern.slf4j.Slf4j;
import java.net.URISyntaxException;
import java.net.URI;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.*;
import java.net.URISyntaxException;
import java.net.URI;
import java.util.*;
import java.util.Date;
import java.text.*;


/**
 * This class is going to connect the psql datebase menu and nutrition
 * @author Gao Huaxuan
 * @since 2017-11-18
 */
@Slf4j
public class FoodDB{

	/**
	 * This function is going to search a ingredient of the food in the database
	 * It search for the given food name and get the average of all those data found
	 * @param ingredient An ingredient in a food in order to search the corresponding food in the database
	 * @return An arraylist with all the information(average) of that food
	 */
	public static ArrayList<String> DBSearchIngredient(String ingredient){
		Connection connection=null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ArrayList<String> result = new ArrayList<String>();
		float x = 0;
		float y = 0;
		float z = 0;
		String energy = null;
		String sodium = null;
		String fatty = null;
		String no = null;
		String description = null;
		String measure = null;
		String weight = null;
		int i = 0;
		try{
			connection=getConnection();
			stmt = connection.prepareStatement("SELECT ndb_no, description, measure, weight, energy_per_measure, sodium_na_per_measure, fatty_acids_total_saturated_per_measure FROM nutrition1 where description like '%'||?||'%'");

			stmt.setString(1,ingredient); 

			rs = stmt.executeQuery();

			while(rs.next()){
				no = rs.getString(1);
				description = rs.getString(2);
				measure = rs.getString(3);
				weight = rs.getString(4);
				energy = rs.getString(5);
				sodium = rs.getString(6);
				fatty = rs.getString(7);
				x = x + Float.parseFloat(energy);
				if(!sodium.equals("--"))
					y = y + Float.parseFloat(sodium);
				if(!fatty.equals("--"))
					z = z + Float.parseFloat(fatty);
				i=i+1;
			}
			if(i!=0){
				x = x/i;
				y = y/i;
				z = z/i;
			}
			// x = x/i;
			// y = y/i;
			// z = z/i;
			result.add(no);
			result.add(description);
			result.add(measure);
			result.add(weight);
			result.add(Float.toString(x));
			result.add(Float.toString(y));
			result.add(Float.toString(z));

			return result;
		
		}catch (Exception ex) {
			log.info("SQLException while searching ingredient: {}", ex.toString());
		}finally{
			try{
				connection.close();
				rs.close();
				stmt.close();
			}catch (Exception ex) {
				log.info("SQLException while closing: {}", ex.toString());
			}
		}
		return null;
	}

	/**
	 * This function is going to check whether a food name can be found in the database
	 * @param name The food name to search in the database
	 * @return result Return a boolean value to tell whether the food is exist in the database. If exist, return true, else false
	 */
	public static boolean DBexist(String name){
		Connection connection=null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		boolean result = false;
		try{
            connection = getConnection();        
            stmt = connection.prepareStatement("SELECT * FROM nutrition1 WHERE description like '%'||?||'%'");
            stmt.setString(1,name);
            rs = stmt.executeQuery();
            log.info("in create menu text delete 1 {}");
            if(rs.next())
            	result = true;
        }catch(Exception e){
            	log.info("in DBexist() deleting rows {}",e.toString());
        }finally{
			try{
				connection.close();
				rs.close();
				stmt.close();
			}catch (Exception ex) {
				log.info("SQLException while closing: {}", ex.toString());
			}
		}
        return result;
	}

	/**
	 * This function is going to connect the psql in heroku
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

	/**
	 * This function is going to delete all the rows in menu table of the given user id
	 * @param id The user id to search and delete in the menu table
	 * @return result Return a boolean value whether the rows exist and been deleted from the menu table. If all these done, return true. Else, false.
	 */
	public static boolean DBdeleteMenu(String id){
		Connection connection=null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		boolean result = false;
		try{
            connection = getConnection();
            String pre = "SELECT * FROM menu WHERE userid = '"+ id +"'";
            stmt = connection.prepareStatement(pre);
            rs = stmt.executeQuery();
            log.info("in create menu text delete 1 {}");
            if(rs.next()){
            	pre = "DELETE FROM menu WHERE userid = '"+ id +"'";
            	stmt = connection.prepareStatement(pre);
            	stmt.executeUpdate();
                rs.close();
                stmt.close();
                log.info("in create menu text delete2");
                result = true;
            }
        }catch(Exception e){
            	log.info("in create menu text deleting rows {}",e.toString());
        }
        return result;
	}

	/**
	 * This function is going to insert a row into the menu table with the user id and the food name and price
	 * @param id The user id which will be inserted in the menu table
	 * @param name The food name which will be inserted in the menu table
	 * @param p The food price which will be inserted in the menu table
	 * @return result Return a boolean value to check whether the task is completed
	 */
	public static boolean DBInsertMenu(String id, String name, String p){
		Connection connection=null;
		PreparedStatement stmt = null;
		boolean result = false;
		try{
				connection = getConnection();
				String pre = "INSERT INTO menu VALUES('" +id+ "','" + name+"','" + p+"')";
    			stmt = connection.prepareStatement(pre);
    			log.info("nowin create Menu prepare stmt: {}", stmt.toString());
				stmt.executeUpdate();
				result = true;
		}catch(Exception e){
			log.info("SQLException while updating menu: {}", e.toString());
			result = false;
		}finally{
			try{
				connection.close();
				stmt.close();
			}catch(Exception e){
				log.info("SQL create menu close fail: {}", e.toString());
				result = false;
			}
		}
		return result;
    }

    /**
     * This function is going to search in the menu table
     * And get a arraylist with food name
     * @param foods An arraylist with food name inside
     * @param id The user id to search in the menu table
     * @return foods An arraylist added some food name selected from the database
     */
    public static ArrayList<String> DBgetMenu(ArrayList<String> foods, String id){
    	Connection connection=null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try{
			connection=getConnection();
			String pre = "SELECT foodname FROM menu where userid = '" + id+"'";
    	   	stmt = connection.prepareStatement(pre);
    	   	log.info("in getMenu info : {}", stmt.toString());
			rs = stmt.executeQuery();

            while(rs.next()){
            	foods.add(rs.getString(1));
            	log.info("rs in menuinfo has next : {}", foods.get(0));
        	}
        	return foods;

    	}catch (Exception ex) {
				log.info("SQLException in menuinfo execute: {}", ex.toString());
		}finally{
			try{
				connection.close();
				stmt.close();
				rs.close();
			}catch(Exception e){
				log.info("SQL find optimal close fail: {}", e.toString());
			}
		}
		return null;
	}


	/**
	 * This function is going to get tips from database
	 * @param n An index number in order to find the corresponding tips in the tips table
	 * @return tips Return a String(tips)
	 */
    public static String DBgetTip(int n){
    	Connection connection=null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
            connection = getConnection();
            String pre = "SELECT tips FROM tips WHERE id = '"+ n +"'";
            //log.info("23333333: {}",stmt.toString());
            stmt = connection.prepareStatement(pre);
            log.info("23333333: {}",stmt.toString());
            rs = stmt.executeQuery();
            if(rs.next()){
            	log.info("result= {}",rs.getString(1));
            	return rs.getString(1);
            }
        }catch(Exception e){
            	log.info("SQL Exception in tips {}",e.toString());
        }finally{
			try{
				connection.close();
				stmt.close();
				rs.close();
			}catch(Exception e){
				log.info("SQL tips close fail: {}", e.toString());
			}
		}
		return "Keep healthy!";
    }

}

