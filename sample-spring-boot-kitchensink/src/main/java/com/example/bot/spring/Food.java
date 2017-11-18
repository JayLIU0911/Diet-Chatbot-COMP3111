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

@Slf4j
//@Autowired
public class Food {
	FoodDB foodAdaptor = new FoodDB();

	public ArrayList<ArrayList<String>> searchFood(String food){

		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();

		String[] words = food.split(" ");

		//String[] temp = new String[words.length];

		for(int i=0;i<words.length;i++){
			try{
				 ArrayList<String> temp = new ArrayList<String>();
			//	if(words[i] == "and" || words[i]== "with"|| words[i] == "served") {
			//		continue;
				//}
				//else if(words[i+1] == "and" || words[i+1]== "with") {
				//	String combination = words[i]+" "+words[i+1]+" "+words[i+2];
				//	temp = foodAdaptor.DBSearchIngredient(temp,combination);
				//	if(temp!=null){ 
				//		result.add(temp);
				//		i +=2;
				//		continue;}
				//	}
				temp = foodAdaptor.DBSearchIngredient(words[i]);
				if(temp==null)
					{continue;}
				result.add(temp);

			}catch(Exception e){
		 		log.info("Exception while searching food: {}", e.toString());
			}
		}		
			return result;
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

    public boolean createMenuText(String id, String txt){
    	//only one word
    	//no price input
    	//multiple line

    	//if the user already has menu stored, delete them
    	foodAdaptor.DBdeleteMenu(id);

		boolean result = true;
        int items = 0;
        ArrayList<String> menu = new ArrayList<>();
        try (Scanner scanner = new Scanner(txt)){
            while (scanner.hasNextLine()){
                menu.add(scanner.nextLine());
                items ++;
            }
                //scanner.close();
        }catch(Exception e){
            log.info("in create Menu scanner:{}",e.toString());
        }
        
            for (int i=0; i < items; i++){
                String[] c = menu.get(i).split("\\s");
                String[] chart = new String [c.length-1];
                for (int j = 0; j<c.length-1;j++){
                    chart[j]=c[j];
                }
                log.info("create menu 1,{}");
                String p = c[c.length-1];
                String name = String.join(" ",chart);
                //food[i] = new Food(name,p);
                //food[i] = new Food(name,p,null);
                result = result && foodAdaptor.DBInsertMenu(id,name,p);     
            }
        return result;
    }
    public boolean createMenuJson(String id, String txt){

            //https://github.com/khwang0/2017F-COMP3111/blob/master/Project/topic%202/project%20guidelines.md#json 
            foodAdaptor.DBdeleteMenu(id);

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
                    name = obj.getString("nm");
                    //price = obj.getInt("price");
                    log.info("enter json for loop,{}",name);   
                    //String p = String.valueOf(price);
                    String p = obj.getString("cty");
                    result = result && foodAdaptor.DBInsertMenu(id,name,p);
                }
                reader.close();
            }catch(Exception e) {
                // if (reader != null)    
                log.info("create menu json,{}",e.toString());
            }    
        
            return result;

    }

    public boolean createMenuImage(String id, URI uri){
        File image = new File("https://sliuao.herokuapp.com/"+uri.getPath());
        Ocr.setUp(); // one time setup
        Ocr ocr = new Ocr(); // create a new OCR engine
        ocr.startEngine("eng", Ocr.SPEED_FASTEST); // English
        String s = ocr.recognize(new File[]{image}, Ocr.RECOGNIZE_TYPE_ALL, Ocr.OUTPUT_FORMAT_PLAINTEXT);
        log.info("create from image,{}",s);
        //System.out.println("Result: " + s);
        // ocr more images here ...
        createMenuText(id, s);
        ocr.stopEngine();

        return true;
    }

    public String findOptimal(String id, ArrayList<String> foods){

 	   	User user = new User();
 	   	log.info("now in findOptimal");
	   	if(foods.size()<=1){
	   		return "\nSince you have only input one food, my recommendation can only be " + foods.get(0) + "\n";
	   	}

	   	log.info("now in findOptimal, {}",foods.size());

		int intake = user.getIdealDailyIntake(id);
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

	public String checkNutrition(String name){
    	String result = "The nutrition for " + name + " is \n";
    	float[] qua = getQuality(name);
    	String[] quas = new String[3];
    	for (int i = 0; i < qua.length; ++i) {
    		quas[i] = String.format("%.1f", qua[i]);
    	}
    	result+= "energy: "+ quas[0] + "kcal\n";
    	result+= "sodium: "+ quas[1] + "mg\n";
    	result+= "fatty acid: "+ quas[2] +"g";
    	return result;
    }

    public String getMenuInfo(String id){
		
		ArrayList<String>foods=new ArrayList<String>();
   	   	String result = " ";

        foods = foodAdaptor.DBgetMenu(foods, id);
		
		Iterator<String> itr = foods.iterator();
		while (itr.hasNext()) {
			result += checkNutrition(itr.next());
			result += "\n";
			log.info("iterator : {}", result);
		}

		result = result + "my recommendation is " + findOptimal(id,foods);
        //result = result + "\n and my recommendation size is "+ foodPortion(id,foods);
		log.info("printing result", result);
		
		return result;
    }
    
    public boolean FoodinMenu(String id, String food) {
      ArrayList<String>foods = new ArrayList<String>();
      foods = foodAdaptor.DBgetMenu(foods,id);
      if (foods.contains(food)) {
      return true;
     }
     else return false;
      }
    
    
    public String WarningforFoodSelection(String id, String food) {
        User user = new User();
        ArrayList<String>foods=new ArrayList<String>();
        foods = foodAdaptor.DBgetMenu(foods, id);
        String result = " ";
        float energy = getQuality(food)[0];
        float intake = user.getIdealDailyIntake(id);
        float optimal = getQuality(findOptimal(id,foods))[0];
        intake = intake/3;
        if(energy > intake) {
     result = "Your selection is greater than your recommended intake, we suggest you to change your selection.";
    }
    else if(energy < intake && energy < optimal) {
     result = "It's okay to have this food, but its energy is less than the the food we recommended.";
    }
    
    else
     result = "It's okay to have this food, but its energy is greater than the food we recommended.";
    return result;
    }
    
    public String foodPortion(String id, String food) {
        User user = new User();
        float energy = getQuality(food)[0];
        float intake = user.getIdealDailyIntake(id);
        intake = intake/3;
        float portion = intake/energy;
        String result;
        if(0<portion&&portion<0.4) {
         result = "a quarter";
        }
        else if(0.4<portion&&portion<0.7) {
         result = "a half";
        }
        else if(0.7<portion&&portion<0.8) {
         result = "three quarters";
        }
        else {
         result = "all";
        }
        return result;
     }



    public String getTip(){

    	Random rand = new Random();
		int  n = rand.nextInt(22) + 1;

		String result = "Tips:\n";

		result = result + foodAdaptor.DBgetTip(n);

		return result;
    }

    public String checkAppropriate(String id, String name){
    	User user = new User();
    	int ideal = user.getIdealDailyIntake(id);
    	float energy = getQuality(name)[0];
    	Date time = new Date();
    	SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
    	String search_date = dateformat.format(time);

    	//log.info("in appro: {}",search_date);

    	int x = (int)user.getDailyIntake(id,search_date);
    	if(x==0)
    		return "It is totally fine that you have this today.";

    	if((x+energy)>=ideal)
    		return "This food is not appropriate for you to have today, since it will exceed your ideal intake.";
    	else
    		return "It is totally fine that you have this today.";

    }
}

  

// JSONArray arr = obj.getJSONArray("ingredients");

                // String[] ingredients = new String[arr.length()];
    
                // for (int i = 0; i < arr.length(); i++)
                //     ingredients[i]=arr[i];


// JSONArray json;
//             json = loadJSONArray(txt);



//             // Get the first array of elements
//             JSONArray values = json.getJSONArray(0);
  
//             for (int i = 0; i < values.size(); i++) {
    
//                 JSONObject item = values.getJSONObject(i); 
//                 String name = item.getString("name");
//                 String p = item.getString("price");
//                 result = result && foodAdaptor.DBInsertMenu(id,name,p);
//             }        