package com.example.bot.spring;

import lombok.extern.slf4j.Slf4j;
// import java.net.URISyntaxException;
// import java.net.URI;
import javax.annotation.PostConstruct;
//import javax.sql.DataSource;
import java.sql.*;
// import java.net.URISyntaxException;
// import java.net.URI;
import java.util.*;
//import java.util.Date;
import java.text.*;

import java.net.*;
import java.io.*;
import org.json.*;
import java.util.Date;
import com.asprise.ocr.*;

// import org.apache.commons.io.FileUtils;



@Slf4j
//@Autowired

/**
 * This is the Food class that act as a mediator between Controller and FoodDB class
 */
public class Food {


   /**
    * This method takes a food name and search it in the database, and return data
    * @param food a food name(can have more than one ingredients)
    * @return result a 2D arraylist(String) that stores the data of a given food
    */
	public static ArrayList<ArrayList<String>> searchFood(String food){

		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();

		String[] words = food.split(" ");

        for(int i=1;i<words.length;i++){
            try{
                String currentName = "";
                ArrayList<String> temp = new ArrayList<String>();
                StringJoiner joiner = new StringJoiner(" ");
                joiner.add(words[i-1]);
                joiner.add(words[i]);
                currentName = joiner.toString();
                log.info("now in search food, {}",currentName);

                if(FoodDB.DBexist(currentName)){
                    words[i]=currentName;
                }
                    
                else{
                    temp = FoodDB.DBSearchIngredient(words[i-1]);
                    if(temp.get(0)==null)
                        continue;
                    result.add(temp);
                    //temp.clear();
                }

             }catch(Exception e){
                log.info("Exception while searching food: {}", e.toString());
             }
        }

        ArrayList<String> temp = new ArrayList<String>();
        temp = FoodDB.DBSearchIngredient(words[words.length-1]);
        if(temp.get(0)!=null){result.add(temp);}
        
        if(result.isEmpty()){return null;}
        return result;
	}
    
    //         
    //     }
                    // for(int j=0;j<words.length;j++){
                    //     log.info("now print word,{}", words[j]);
                    // }

                // ArrayList<String> temp = new ArrayList<String>();

                // temp = FoodDB.DBSearchIngredient(words[i]);
                // if(temp.get(0)==null)
                //     {continue;}
                // result.add(temp);

    /**
    * This method takes a food name and return the energy, sodium, fatty data
    * @param name a food name(can have more than one ingredients)
    * @return quality float[3] that stores the data of a given food in this order: energy, sodium, fatty
    */
	public static float[] getQuality(String name){

    	float[] quality = new float[3];
        for(int i=0;i<3;i++){
            quality[i] = 0;
        }

        ArrayList<ArrayList<String>> food = searchFood(name);
        if(food==null){return quality;}
        float energy=0;
    	float sodium=0;
    	float fat=0;

    	for(int i=0;i<food.size();i++){
    		//sum energy&sodium&fat
    		for(int j=0;j<3;j++){
    			quality[j]+=Float.parseFloat(food.get(i).get(j+4));
    		}
    	}
    	return quality;
    }

    /**
    * This method insert row(s) into the database, with a user ID and the name of the foods in the menu
    * @param id a user ID
    * @param txt a food name(can have more than one ingredients)
    * @return boolean whether the insert is success or not
    */
    public static boolean createMenuText(String id, String txt){
    	//only one word
    	//no price input
    	//multiple line

    	//if the user already has menu stored, delete them
    	FoodDB.DBdeleteMenu(id);

		boolean result = true;
        int items = 0;
        ArrayList<String> menu = new ArrayList<>();
        try {
        	Scanner scanner = new Scanner(txt);
            while (scanner.hasNextLine()){
                menu.add(scanner.nextLine());
                items ++;
            }
                //scanner.close();
        }catch(Exception e){
            log.info("in create Menu scanner:{}",e.toString());
        }

            for (int i=0; i < items; i++){

                if(menu.get(i).contains("..")||menu.get(i).contains("--"))
                    continue;
                //menu.get(i).matches(".*\\d+.*")
                String[] c = menu.get(i).split("\\s");
                String[] chart = new String [c.length];
                chart[c.length-1] = "";
                for (int j = 0; j<c.length-1;j++){
                    chart[j]=c[j];
                }
                log.info("create menu 1,{}");

                String p = "0";

                try{
                    float price = Float.valueOf(c[c.length-1]);
                    p = Float.toString(price);
                    if(c.length-1==0){
                        continue;
                    }
                }catch(NumberFormatException e){
                    log.info("the menu contains no price");
                    chart[c.length-1]=c[c.length-1];
                }

                String name = String.join(" ",chart);
                if(searchFood(name)==null)
                    continue;
                //food[i] = new Food(name,p);
                //food[i] = new Food(name,p,null);
                result = result && FoodDB.DBInsertMenu(id,name,p);
            }
        return result;
    }

    /**
    * This method insert menu of json type into the database, with a user ID and the name of the foods in the menu
    * @param id a user ID
    * @param txt a url string that refers to the Json file
    * @return boolean whether the insert is success or not
    */
    public static boolean createMenuJson(String id, String txt){

            //https://github.com/khwang0/2017F-COMP3111/blob/master/Project/topic%202/project%20guidelines.md#json
            FoodDB.DBdeleteMenu(id);

            boolean result = true;
            log.info("enter json branch");
            BufferedReader reader = null;
            try {
                URL url = new URL(txt);

                reader = new BufferedReader(new InputStreamReader(url.openStream()));
                StringBuffer buffer = new StringBuffer();
                int read;
                char[] chars = new char[1024];
                while ((read = reader.read(chars)) != -1)
                    buffer.append(chars, 0, read);

                log.info("enter json branch see buffer,{}",buffer.toString());
                JSONArray json = new JSONArray(buffer.toString());

                String name;
                //int price;

                for(int i=0;i<json.length();i++){
                    JSONObject obj = json.getJSONObject(i);
                    name = obj.getString("name");
                    //price = obj.getInt("price");
                    log.info("enter json for loop,{}",name);
                    //String p = String.valueOf(price);
                    String p = obj.getString("price");
                    result = result && FoodDB.DBInsertMenu(id,name,p);
                }
                reader.close();
            }catch(Exception e) {
                // if (reader != null)
                log.info("create menu json,{}",e.toString());
            }

            return result;

    }

    /**
    * This method insert menu of image type into the database, with a user ID and the name of the foods in the menu
    * @param id a user ID
    * @param uri a URI object that refers to the Image file
    * @return boolean whether the insert is success or not
    */
    public static boolean createMenuImage(String id, URI uri){

            //throw new Exception("create menu failed"); //
        try{
            //File image = new File(URLEncoder.encode(uri.getPath(), "UTF-8"));
            //URI uri = image.toURI();
            Properties propSpec = new Properties();


            Ocr.setUp(); // one time setup
            Ocr ocr = new Ocr(); // create a new OCR engine

            ocr.startEngine("eng", Ocr.SPEED_FAST, "START_PROP_DICT_CUSTOM_DICT_FILE=dict.txt"); // English

            String s = ocr.recognize(new URL[]{new URL(uri.toString())}, Ocr.RECOGNIZE_TYPE_ALL, Ocr.OUTPUT_FORMAT_PLAINTEXT, "PROP_IMG_PREPROCESS_TYPE=custom|PROP_IMG_PREPROCESS_CUSTOM_CMDS=scale(2);gayscale();sharpen()");
            

            log.info("create from image,{}",s);
            //System.out.println("Result: " + s);  
            // ocr more images here ...
            if(s==null){
                return false;
            }
            createMenuText(id, s);
            ocr.stopEngine();
            return true;

        }catch(Exception e){
            log.info("create menu image fail");
            return false;
        }
}


    /**
    * This method find the most suitable food from the menu
    * @param id a user ID
    * @param foods a arraylist of food name
    * @return String the name of the best food
    */
    public static String findOptimal(String id, ArrayList<String> foods){

 	   	log.info("now in findOptimal");
	   	if(foods.size()<=1){
	   		return "\nSince you have only input one food... my recommendation can only be " + foods.get(0) + "\n";
	   	}

	   	log.info("now in findOptimal, the input size is : {}",foods.size());

		int intake = User.getIdealDailyIntake(id);
		log.info("now in findOptimal get ideal intake: {}", intake);
    	intake = (intake/3);
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
    	return foods.get(index);
}

    /**
    * This method check for the nutrition of a given food
    * @param name name of the food
    * @return String that contain information of the food
    */

	public static String checkNutrition(String name){
    	String result = "The nutrition for " + name + " is \n";
    	float[] qua = getQuality(name);
        if(qua[0]==0){
            result += "Not found... QAQ";
            return result;
        }

    	String[] quas = new String[3];
    	for (int i = 0; i < qua.length; ++i) {
    		quas[i] = String.format("%.1f", qua[i]);
    	}
    	result+= "energy: "+ quas[0] + "kcal\n";
    	result+= "sodium: "+ quas[1] + "mg\n";
    	result+= "fatty acid: "+ quas[2] +"g";
    	return result;
    }

    /**
    * This method print out the menu information of a user
    * @param id a user ID
    * @return String containing the nutrition info of each food, followed by a recommendation
    */
    public static String getMenuInfo(String id){

		ArrayList<String>foods=new ArrayList<String>();
   	   	String result = "";

        foods = FoodDB.DBgetMenu(foods, id);
        if(foods.isEmpty()){
            return "You haven't input a menu or your menu is not recognized, please input again\n";
        }

		Iterator<String> itr = foods.iterator();
		while (itr.hasNext()) {
			result += checkNutrition(itr.next());
			result += "\n";
			log.info("iterator : {}", result);
		}

		result = result + "my recommendation is: \n" + findOptimal(id,foods);
        result = result + "\n and my recommendation size is "+ foodPortion(id,findOptimal(id,foods));
		log.info("printing result", result);

		return result;
    }

    /**
    * This method check whether a food is in the menu of a user
    * @param id a user ID
    * @param food name of the food
    * @return boolean indicating the food is in or not
    */
    public static boolean FoodinMenu(String id, String food) {
      ArrayList<String>foods = new ArrayList<String>();
      foods = FoodDB.DBgetMenu(foods,id);
      if (foods.contains(food)) {
      return true;
      }
      else return false;
    }

    /**
    * This method return a string telling the user whether the his/her choice is healthy or not
    * @param id a user ID
    * @param food name of the food
    * @return String a sentence
    */
    public static String WarningforFoodSelection(String id, String food) {
        ArrayList<String>foods=new ArrayList<String>();
        foods = FoodDB.DBgetMenu(foods, id);
        String result = " ";
        float energy = getQuality(food)[0];
        float intake = User.getIdealDailyIntake(id);
        float optimal = getQuality(findOptimal(id,foods))[0];
        if(energy==0)
            return "We cannot find the food in our database";
        intake = intake/3;
        if(energy > intake) {
     result = "Your selection is greater than your recommended intake, we suggest you to change your selection.";
    }
    else if(energy < optimal) {
     result = "It's okay to have this food, but its energy is less than the the food we recommended.";
    }

    else
     result = "It's okay to have this food, but its energy is greater than the food we recommended.";
    return result;
    }

    /**
    * This method return a string telling the user how much portion to have regarding to a food
    * @param id a user ID
    * @param food name of the food
    * @return String a quarter/a half/three quarters/all
    */
    public static String foodPortion(String id, String food) {

        float energy = getQuality(food)[0];
        float intake = User.getIdealDailyIntake(id);
        intake = intake/3;
        float portion = intake/energy;
        String result;
        if(portion<=0.25) {
         result = "a quarter";
        }
        else if(portion<0.5) {
         result = "a half";
        }
        else if(portion<=0.75) {
         result = "three quarters";
        }
        else {
         result = "all";
        }
        return result;
     }

    /**
    * This method go to the database storing tips to retrieve a health tip
    * @return String a healthy tip
    */
    public static String getTip(){

    	Random rand = new Random();
		int  n = rand.nextInt(22) + 1;

		String result = "Tips:\n";

		result = result + FoodDB.DBgetTip(n);

		return result;
    }

    /**
    * This method check whether a food is appropriate for a user to have
    * @param id a user ID
    * @param name name of the food
    * @return String a sentence
    */
    public static String checkAppropriate(String id, String name){

    	int ideal = User.getIdealDailyIntake(id);
    	float energy = getQuality(name)[0];
    	Date time = new Date();
    	SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
    	String search_date = dateformat.format(time);

    	//log.info("in appro: {}",search_date);

    	int x = (int)User.getDailyIntake(id,search_date);
    	// if(x==0)
    	// 	return "It is totally fine that you have this today.";

    	if((x+energy)>=ideal)
    		return "This food is not appropriate for you to have today, since it will exceed your ideal intake.";
    	else
    		return "It is totally fine that you have this today.";

    }
}


