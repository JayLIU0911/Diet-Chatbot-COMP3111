package com.example.bot.spring;

import lombok.extern.slf4j.Slf4j;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.*;
import java.net.URISyntaxException;
import java.net.URI;
import java.util.*;

@Slf4j
public class foodDBAdaptor{

	public Component searchIngredient(String ingredient) throws Exception{

		Connection connection=null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Component result = null;

		try{
			connection=this.getConnection();
			stmt = connection.prepareStatement("SELECT ndb_no, description, measure, weight, energy_per_measure, sodium_na_per_measure, fatty_acid_total_saturated_per_measure FROM nutrition1 where description like '%'||?||'%' ");

			stmt.setString(1,ingredient); //the input
			rs = stmt.executeQuery();
			if(!rs.next())
				return null;
			else
			{
				int id = Integer.parseInt(rs.getString(1));  					
				String description = rs.getString(2);
				String amount = rs.getString(3);
				float weight = Float.parseFloat(rs.getString(4));
				
				float[] nutrition = new float[3];
				
				for (int i=0;i<3;i++)
				{
					if(rs.getString(i+5).equals("--")){nutrition[i]=0;}
					else nutrition[i] = Float.parseFloat(rs.getString(i+5));
				}

				result = new Component(id, description, weight, amount, nutrition);
				return result;

			}

		} catch (Exception ex) {
			log.info("SQLException while closing: {}", ex.toString());
		}finally{
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
		return null;
	}

	public ArrayList<Component> searchFood(String food){
		
		ArrayList<Component> result = new ArrayList<Component>();

		String[] words = food.split(" ");

		String[] temp = new String[words.length];

		for(int i=0;i<words.length;i++)
		{
			Component a = searchIngredient(words[i]);
			if(a!=null)
			{
				if(i==0){
					temp[0]=words[0];
				}
				else{
					temp[i]=temp[i-1]+" "+words[i];
					if(i==words.length-1){
						Component b = searchIngredient(temp[i]);
						result.add(b);
					}
				}
			} 
			else{
				if(i==0||temp[i-1]==null){
					continue;
				}

				Component b = searchIngredient(temp[i-1]);
				result.add(b);
			}
		}
		return result;
	}

	// int fre=rs.getInt(2);
	// fre++;
	// stmt = connection.prepareStatement("UPDATE resH SET frequency = ? WHERE response = ?");
	// stmt.setInt(1,fre);
	// stmt.setString(2, result);
	// stmt.executeUpdate();
	//result +=  "The weight of "+descrip+" is "+weight+"and the measure is "+measure; 
	private Connection getConnection() throws URISyntaxException, SQLException {
		Connection connection;
		URI dbUri = new URI(System.getenv("DATABASE_URL"));

		String username = dbUri.getUserInfo().split(":")[0];
		String password = dbUri.getUserInfo().split(":")[1];
		String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort()  +dbUri.getPath() +  "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";

		log.info("Username: {} Password: {}", username, password);
		log.info ("dbUrl: {}", dbUrl);
		
		connection = DriverManager.getConnection(dbUrl, username, password);

		return connection;
	}

}