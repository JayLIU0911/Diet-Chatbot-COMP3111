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

//executeUpdate  rs=   ?   create a string stmt.exe(string )

@Slf4j
//@Autowired
public class FoodDB{

	public ArrayList<String> DBSearchIngredient(String ingredient){
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
		int i = 1;
		try{
			connection=this.getConnection();
			//String pre = "SELECT ndb_no, description, measure, weight, energy_per_measure, sodium_na_per_measure, fatty_acid_total_saturated_per_measure FROM nutrition1 where description like concat('%', ?, '%')";
			stmt = connection.prepareStatement("SELECT ndb_no, description, measure, weight, energy_per_measure, sodium_na_per_measure, fatty_acids_total_saturated_per_measure FROM nutrition1 where description like '%'||?||'%'");

			stmt.setString(1,ingredient); //the input
			//log.info("SQLException while searching ingredient: {}", stmt.toString());
			rs = stmt.executeQuery();
			//log.info("SQLException while searching ingredient: {}", rs.toString());

			while(rs.next()){
				no = rs.getString(1);
				description = rs.getString(2);
				measure = rs.getString(3);
				weight = rs.getString(4);
				energy = rs.getString(5);
				sodium = rs.getString(6);
				fatty = rs.getString(7);
				if(!energy.equals("--"))
					x = x + Float.parseFloat(energy);
				if(!sodium.equals("--"))
					y = y + Float.parseFloat(sodium);
				if(!fatty.equals("--"))
					z = z + Float.parseFloat(fatty);
				i=i+1;
			}
			x = x/i;
			y = y/i;
			z = z/i;
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
				if(rs.next())
					rs.close();
				if(stmt!=null)
					stmt.close();
			}catch (Exception ex) {
				log.info("SQLException while closing: {}", ex.toString());
			}
		}
		return null;
	}

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

	public void DBdeleteMenu(String id){
		Connection connection=null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		//flag = false;
		try{
            connection = this.getConnection();
            String pre = "SELECT * FROM menu WHERE userid = '"+ id +"'";
            stmt = connection.prepareStatement(pre);
            rs = stmt.executeQuery();
            log.info("in create menu text delete 1 {}");
            if(rs==null){
            	return;
            }
            if(rs.next()){
            	pre = "DELETE FROM menu WHERE userid = '"+ id +"'";
            	stmt = connection.prepareStatement(pre);
            	stmt.executeUpdate();
                rs.close();
                stmt.close();
                log.info("in create menu text delete2");
                //flag = true;
            }
        }catch(Exception e){
            	log.info("in create menu text deleting rows {}",e.toString());
        }
        //return flag;
	}

	public boolean DBInsertMenu(String id, String name, String p){
		Connection connection=null;
		PreparedStatement stmt = null;
		boolean result = false;
		try{
				connection = this.getConnection();
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

    public ArrayList<String> DBgetMenu(ArrayList<String> foods, String id){
    	Connection connection=null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try{
			connection=this.getConnection();
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



    public String DBgetTip(int n){
    	Connection connection=null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
            connection = this.getConnection();
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

	// int fre=rs.getInt(2);
	// fre++;
	// stmt = connection.prepareStatement("UPDATE resH SET frequency = ? WHERE response = ?");
	// stmt.setInt(1,fre);
	// stmt.setString(2, result);
	// stmt.executeUpdate();
	//result +=  "The weight of "+descrip+" is "+weight+"and the measure is "+measure;
	//@Override


// ArrayList<Component> components = new ArrayList<Component>();
//         float[] dummy = new float[3];
//         if (ingres == null){
//             if(this.foodDB.searchFood(name) == null){
//                 this.quality = dummy;
//             }
//             else{
//                 components = this.foodDB.searchFood(n);

//                 Iterator<Component> itr = components.iterator();
//                 while (itr.hasNext()) {
//                 	 	float[] x = itr.next().getNutritionPerServing();
//                 	 	for(int j= 0;j<x.length; j++){
//                             dummy[j]=dummy[j]+x[j];
//                      }
//                 }
//             }
//         }

//         else{

//             for(int i =0; i < ingres.length;i++){
//                 if (this.foodDB.searchFood(ingres[i])== null){
//                     continue;
//                 }
//                 else{
//                     try {
//                     		float[] c = this.foodDB.searchIngredient(ingres[i]).getNutritionPerServing();
//                          for(int j=0; j< c.length;j++){
//                         	 	dummy[j] = dummy[j] + c[j];
//                          }
//                     }catch(Exception e) {
//                     		//System.out.println("searchIngredient");
//                     }
//                 }
//             }
//         }

//         this.quality = dummy;
//     }

// for(int i=0;i<words.length;i++)
		// {
		// 	try{
		// 		Component a = searchIngredient(words[i]);
		// 		if(a!=null)
		// 		{
		// 			if(i==0){
		// 				temp[0]=words[0];
		// 			}
		// 			else{
		// 				temp[i]=temp[i-1]+" "+words[i];
		// 				if(i==words.length-1){

		// 						Component b = searchIngredient(temp[i]);
		// 						result.add(b);

		// 				}
		// 			}
		// 		}
		// 		else{
		// 			if(i==0||temp[i-1]==null){
		// 				continue;
		// 			}

		// 			Component b = searchIngredient(temp[i-1]);
		// 			result.add(b);

		// 		}
		// 	}
		// 	catch(Exception e)catch (Exception ex) {
		// 		log.info("SQLException while searching food: {}", ex.toString());
		// 	}

		// }

//   public String getPrice(String name){
		// Connection connection=null;
		// PreparedStatement stmt = null;
		// ResultSet rs = null;

		// try{
		// 	connection=this.getConnection();
		// 	String pre = "SELECT price FROM menu where foodname = '"+ name+"'";
  //   		stmt = connection.prepareStatement(pre);
		// 	//stmt.setString(1,name); //the input
		// 	rs = stmt.executeQuery();
		// 	if(!rs.next()){
		// 		return "0";
		// 	}
		// 	return rs.getString(1);
		// }catch (Exception ex) {
		// 	log.info("SQLException while getPrice: {}", ex.toString());
		// }finally{
		// 		try{
		// 			connection.close();
		// 			stmt.close();
		// 			rs.close();
		// 		}catch(Exception e){
		// 			log.info("SQL get price close fail: {}", e.toString());
		// 		}
		// 	}
		// return "0";

  //   }
