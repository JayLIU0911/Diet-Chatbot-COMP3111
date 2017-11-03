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
public class FoodDB extends SQLDatabaseEngine{
	
	public ArrayList<String> searchIngredient(String ingredient) throws Exception{

		Connection connection=null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ArrayList<String> result = new ArrayList<String>();

		try{
			connection=this.getConnection();
			//String pre = "SELECT ndb_no, description, measure, weight, energy_per_measure, sodium_na_per_measure, fatty_acid_total_saturated_per_measure FROM nutrition1 where description like concat('%', ?, '%')";
			stmt = connection.prepareStatement("SELECT ndb_no, description, measure, weight, energy_per_measure, sodium_na_per_measure, fatty_acids_total_saturated_per_measure FROM nutrition1 where description like '%'||?||'%'");

			stmt.setString(1,ingredient); //the input
			//log.info("SQLException while searching ingredient: {}", stmt.toString());
			rs = stmt.executeQuery();
			//log.info("SQLException while searching ingredient: {}", rs.toString());
			if(!rs.next()){
				if(connection!=null)
					connection.close();
				if(rs!=null)
					rs.close();
				return null;
			}
			else
			{
				for(int i=1;i<=7;i++){
					result.add(rs.getString(i));
				}
			return result;
			}

		} catch (Exception ex) {
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

	public ArrayList<ArrayList<String>> searchFood(String food){
		
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();

		String[] words = food.split(" ");

		//String[] temp = new String[words.length];

		for(int i=0;i<words.length;i++){
			try{
				
				ArrayList<String> temp = searchIngredient(words[i]);
				if(temp==null)
					{continue;}
				result.add(temp);

			}catch(Exception e){
		 		log.info("SQLException while searching food: {}", e.toString());
			}
		}

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
			return result;
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
    
    public float[] getQuality(String name){
    	ArrayList<ArrayList<String>> food = searchFood(name);
    	float[] quality = new float[3];
    	float energy=0;
    	float sodium=0;
    	float fat=0;

    	for(int i=0;i<food.size();i++){

    		//sum energy&sodium&fat
    		for(int j=0;j<3;j++){
    			if(food.get(i).get(j+4).equals("--")){
    				quality[j]+=0;
    				continue;
    			}
    			quality[j]+=Float.parseFloat(food.get(i).get(j+4));
    		}
    	}
    	return quality;
    } 
    
    public String getPrice(String name){
		Connection connection=null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			connection=this.getConnection();
			String pre = "SELECT price FROM menu where foodname = '"+ name+"'";
    		stmt = connection.prepareStatement(pre);
			//stmt.setString(1,name); //the input
			rs = stmt.executeQuery();				
			if(!rs.next()){
				return "0";
			}
			return rs.getString(1);
		}catch (Exception ex) {
			log.info("SQLException while getPrice: {}", ex.toString());
		}finally{
				try{
					connection.close();
					stmt.close();
					rs.close();
				}catch(Exception e){
					log.info("SQL get price close fail: {}", e.toString());
				}
			}
		return "0";
		
    }

 	public float findMinPrice(){
        // int index = 0;
        // float minprice = this.food[0].getPrice();
        // for (int i = 0; i < food.length;i++){
        //     if (food[i].getPrice()<minprice){
        //         minprice = food[i].getPrice();
        //         index = i;
        //     }
        // }
        // return food[index];
 		return 0;
    }
   

    public boolean createMenuText(String id, String txt){
    	//only one word 
    	//no price input
    	//multiple line
    	
      	Connection connection=null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		boolean result = false;


    	int items = 0;
        ArrayList<String> menu = new ArrayList<>();
        try (Scanner scanner = new Scanner(txt)){
            while (scanner.hasNextLine()){
                menu.add(scanner.nextLine());
                items ++;
            }
        }

        //if the user already has menu stored, delete them
        //"DELETE FROM user_list WHERE uid = ?"
        try{
            connection = this.getConnection();
            String pre = "SELECT * FROM menu WHERE userid = '"+ id +"'";
            stmt = connection.prepareStatement(pre);
            rs = stmt.executeQuery();
            if(rs.next()){
            	pre = "DELETE FROM menu WHERE userid = '"+ id +"'";
            	stmt = connection.prepareStatement(pre);
            	stmt.executeUpdate();
                rs.close();
                stmt.close();
            }
        }catch(Exception e){
            	log.info("in create menu text deleting rows {}",e.toString());
        }
        
       
    	//insert menu
        for (int i=0; i < items; i++){
            String[] c = menu.get(i).split("\\s");
            String[] chart = new String [c.length-1];
            for (int j = 0; j<c.length-1;j++){
          	    chart[j]=c[j];
            }
        	String p = c[c.length-1];
            String name = String.join(" ",chart);
            //food[i] = new Food(name,p);
            //food[i] = new Food(name,p,null);

			try{
				
				String pre = "INSERT INTO menu VALUES('" +id+ "','" + name+"','" + p+"')";
    			stmt = connection.prepareStatement(pre);
    			log.info("nowin create Menu prepare stmt: {}", stmt.toString());
				// stmt.setString(1,name);
				// stmt.setFloat(2, p);
				// stmt.setString(3, id);
				stmt.executeUpdate();
			}catch(Exception e){
				log.info("SQLException while updating menu: {}", e.toString());
				return false;
			}
			
        }

		try{
			connection.close();
			stmt.close();
		}catch(Exception e){
			log.info("SQL create menu close fail: {}", e.toString());
		}
			
        return true;
    }

    public float findMinEnergy(String id){

  	//       int index = 0;
		// float[] data = getQuality(name);

  	//       float minEnergy = data[0];
  	//       for(int i = 0; i < data.length; i++){
  	//           if(food[i].getQuality()[0]<minEnergy){
  	//               minEnergy = data[0];
  	//               index = i;
  	//           }
  	//       }
  	//       return food[index];
    	return 0;
    }
    
    public String findOptimal(String id, ArrayList<String> foods){

     	   	UserDB user = new UserDB();
     	   	log.info("now in findOptimal");
    	   	if(foods.size()==1){
    	   		return "\nSince you have only input one food, my recommendation can only be " + foods.get(0) + "\n";
    	   	}

    	   	log.info("now in findOptimal, {}",foods.size());

			int intake = user.getIdealDailyIntake(id);
			log.info("now in findOptimal get ideal intake: {}", intake);
        	//intake = intake/3;
        	float min = getQuality(foods.get(0))[0]- intake;
        	log.info("now in findOptimal get min: {}", min);
        	int index = 0;
        	for (int i = 1; i < foods.size()-1; i++){
            	float diff = getQuality(foods.get(i))[0] - intake;
            	log.info("now in findOptimal get QUA: {}", i);
            	if (diff < min){
                	min = diff;
                	index = i;

            	}
        	}
        	log.info("now in findOptimal get index: {}", index);
        	return "\n My reccommendation is: "+foods.get(index);
	}
			

    public String checkNutrition(String name){
    	String result = "The nutrition for " + name + " is \n";
    	float[] qua = getQuality(name);
    	String[] quas = new String(3);
    	for (int i = 0; i < qua.length; ++i) {
    		quas[i] = String.format("%.1f", qua[i]);
    	}
    	result+= "energy: "+ quas[0] + "Kcal\n";
    	result+= "sodium: "+ quas[1] + "mg\n";
    	result+= "fatty acid: "+ quas[2] +"g";
    	return result;
    }


    public String getMenuInfo(String id){
		Connection connection=null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
   	   	ArrayList<String>foods=new ArrayList<String>();
   	   	String result = " ";
		try{
			

			connection=this.getConnection();
			String pre = "SELECT foodname FROM menu where userid = '" + id+"'";
    	   	stmt = connection.prepareStatement(pre);
    	   	log.info("in getMenu info : {}", stmt.toString());
			rs = stmt.executeQuery();

			if(rs==null){
				log.info("rs has no next : {}", rs.toString());					
				connection.close();
				rs.close();
				return null;
			}

			while(rs.next()){
				foods.add(rs.getString(1));
				log.info("rs in menuinfo has next : {}", foods.get(0));
			}
	
			Iterator<String> itr = foods.iterator();
			while (itr.hasNext()) {
				result += checkNutrition(itr.next());
				result += "\n";
				log.info("iterator : {}", result);
			}

			result += findOptimal(id,foods);
			log.info("printing result", result);
			
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
		return result; 	
    }
   

    public String getTip(){
    	
    	Random rand = new Random();

		int  n = rand.nextInt(22) + 1;

		Connection connection=null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String result = "Tips:\n";

		try{
            connection = this.getConnection();
            String pre = "SELECT tips FROM tips WHERE id = '"+ n +"'";
            //log.info("23333333: {}",stmt.toString());
            stmt = connection.prepareStatement(pre);
            log.info("23333333: {}",stmt.toString());
            rs = stmt.executeQuery();
            if(rs.next()){
            	result += rs.getString(1);
            	log.info("result= {}",result);
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
		return result; 	
    }

    public String checkAppropriate(String id, String name){
    	UserDB user = new UserDB();
    	int ideal = user.getIdealDailyIntake(id);
    	float energy = getQuality(name)[0];
    	Date time = new Date();
    	SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
    	String search_date = dateformat.format(time);

    	log.info("in appro: {}",search_date);
    	
    	ArrayList<String> x = user.searchRecord2(id,search_date);
    	if(x==null)
    		return "It is totally fine that you have this today.";

    	float y = Float.parseFloat(x.get(2));

    	log.info("in appro2: {}",y);

    	if((y+energy)>=ideal)
    		return "This food is not appropriate for you to have today, since it will exceed your ideal intake.";
    	else
    		return "It is totally fine that you have this today.";

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
