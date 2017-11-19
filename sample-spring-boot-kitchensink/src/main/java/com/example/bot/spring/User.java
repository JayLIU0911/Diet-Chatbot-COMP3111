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
 * A class used to do tasks directly related to the user
 * It will use many functions in userDB.class
 * @author Liu Shuyue, Sun Yusen
 * @since 2017-11-18
 */
@Slf4j
public class User{

    /**
     * This function check the userlist and usertable and the user follow time from database
     * it calls 3 functions from userDB.class in order to connect to the database
     * it insert new user into the userlist with the followtime and create a new user table for him/her
     * @param id input the user id in order to check and create table
     * @param date input the Coupon begin date in order to check whether the user can get coupon
     * @return result Return a boolean type, if true, insert success
     */
    public static boolean insert(String id, Date date){
        
        boolean result = false;
        PreparedStatement stmt = null;
        
        boolean x = UserDB.check_userlist(id);

        if(x==false)
        	delete(id);
         
        UserDB.create_usertable(id);
            
        UserDB.insert_user(id);

        check_followtime(id,date);

        result = true;

        return result;
    }

    /**
     * This function check the user's follow time depending on the coupon begin date
     * @param id Input the user id and change the user list at this user id with the follow time
     * @param date Input the coupon begin date to check whether the user follow the chatbot before the begin date
     * @return result Return a boolean in order to tell whether the user follow the chatbot before the begin date
     */
    public static boolean check_followtime(String id, Date date){
        Date time = new Date();
        boolean result = false;

        if(time.getTime()-date.getTime()>=0)
            {setUser(id,"follow","yes");
        	result = true;}
        else
            {setUser(id,"follow","no");
        	result = false;}

        return result;
    }

    /**
     * This function is going to delete the user from user list and also delete the user table and the user's coupon in coupon table
     * @param id Input user id in order to connect to the data base to delete the related things
     * @return result Return true if all there things are done
     */
    public static boolean delete(String id)
    {
        boolean result = false;

        UserDB.drop_table(id);

        UserDB.delete_user(id);

        CouponDB.delete_id(id);

        FoodDB.DBdeleteMenu(id);

        result = true;
        
        return result;
    }


    /**
     * This function is to update user table with the input text and the user's weight at this time
     * it get the user's weight at this time from the user list table
     * @param id Input the user id in order to search user's weight and insert record in to the user table
     * @param text Input the food name and get other data with the food name
     * @return result Return a boolean, if all the tasks are done, return true
     */
   public static boolean updateRecord(String id, String text)
    {
        
        boolean result = false;

        String weight = getUser(id,"weight");

        UserDB.insert_record(id,text,weight);

        result = true;
        
        return result;
    }

    /**
     * This function is just created for the test case
     * This function is going to delete the corresponding row in the user table
     * And it is not used in the controller
     * it just use one function in the userDB class to connection to the database
     * @param id Input the user id to find the user table
     * @param text Input the food name to find a row in the user table
     * @return result Return a boolean value to check whether the function finished
     */
    public static boolean delete_record(String id, String text){
    	boolean result = false;
    	if(UserDB.deleteRecord(id,text)==true)
    		result = true;
    	return result;
    }

    /**
     * This function is going to get user data from the user list
     * it calls the function select_userlist in the userDB.class to get the result
     * @param id Input the user id to find the row in the user list
     * @param column Input the column name to find the column in the user list
     * @return result Return a String with the data in the given row and column
     */
    public static String getUser(String id, String column){
        
        String result = null;

        result = UserDB.select_userlist(id,column); 
        
    	return result;
}

	/**
	 * This function is going to set the data in the given row and column in the user list
	 * It calls the function update_userlist in userDB.class
	 * @param id Input the user id to find the row in user list
	 * @param column Input the column name to find the column in the user list
	 * @param text Input the data you want to update
	 * @return result Return a boolean value to check whether all the tasks are done
	 */
    public static boolean setUser(String id, String column, String text){

        boolean result = false;

        if(!column.equals("days_for_target"))
            UserDB.update_userlist(id,column,text);
        else
            setDay(id,text);

        result = true;

        return result;
    }

    /**
     * This function is going to change an integer the user input for the days to target into a date format
     * And then change the date into the format dd/mm/yyyy and insert it into the user list table
     * it will be more precise for the further checking
     * @param id Input the user id to find the corresponding row in the user list
     * @param text Input the target days(an integer but with type String)
     * @return result Return a boolean value. If update succeed, return true. else, return false.
     */
    public static boolean setDay(String id, String text){
    	Date day = new Date();
    	Calendar c = Calendar.getInstance();
    	c.setTime(day);
    	c.add(Calendar.DATE, Integer.parseInt(text));
    	day = c.getTime();

        SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
        String target_date = dateformat.format(day);

        UserDB.update_userlist(id,"days_for_target",target_date);

        return true;
    	
    }

    /**
     * This function is going to check the days from now to the target date
     * if get the target date from the user list and change it into the Date type
     * And then we calculate the days by the two date
     * @param id Input the user id to find the days_for_target column in the user list
     * @return result Return an integer which is from now to the target date
     */
    public static int check_day(String id){
        String target = getUser(id,"days_for_target");
        SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
        int result = 0;

        try{
            Date target_date = dateformat.parse(target);
            Date day = new Date();
            result = (int)((target_date.getTime() - day.getTime()) / (1000 * 60 * 60 * 24));
        } catch(Exception e){
            log.info("check_day error!!!");
        }

        return result;
    }

    /**
     * This function is going to check whether the user achieve the goal or meet the target date
     * It has four different conditions which can be used in the controller to push message to the user
     * @param id Input user id to get the user's weight and target weight and also the target date
     * @return result Return an integer(1,2,3,4) to tell which condition the user is in
     */
    public static int check_goal(String id){
        if(check_day(id)==0 && getUser(id,"weight").equals(getUser(id,"target_weight")))
            return 1;
        else if(check_day(id)!=0 && getUser(id,"weight").equals(getUser(id,"target_weight")))
            return 2;
        else if(check_day(id)==0 && (!getUser(id,"weight").equals(getUser(id,"target_weight"))))
            return 3;
        else
            return 4;

    }

    /**
     * This function is going to get the energy the user has consumed at certain day
     * It use the function DBsearch_day in the userDB.class to get the record at certain day
     * @param id Input the user id to get the user table
     * @param date Input the date and search the user table with the date
     * @return result Return the energy consumed at that date. If not found, return 0
     */
    public static float getDailyIntake(String id, String date){
        ArrayList<String> x = UserDB.DBsearch_day(id,date);
        log.info("search daily success: {}");

        if (x.get(3).equals("0.0"))
        	return 0;

        float result = Float.parseFloat(x.get(2));
        log.info("the result of daily summary is : {}");
        return result;
    }


    /**
     * This function is going to calculate the ideal daily intake of the user depend on the information in the user list
     * It will get the data from user list and divide them into daily intake
     * @param id Input the user id to get the user's information from the user list
     * @return IdealDailyIntake Return an integer which is the calculate result of the daily intake
     */
    public static int getIdealDailyIntake (String id){

        float weight = Float.parseFloat(getUser(id,"weight"));
        String status = new String(getUser(id,"status"));
        float height = Float.parseFloat(getUser(id,"height"));
        int age = Integer.parseInt(getUser(id,"age"));

        float BMR=0;
        if (status.equalsIgnoreCase("male")){
            BMR = (float)88.362 + ( (float)13.397*weight) + ((float)4.799*height) - ((float)5.677*age);
        }
        else{
            BMR = (float)447.593 + ( (float)9.247*weight) + ( (float)3.098*height) - ((float)4.33*age);
        }


        String targetWeightString = new String(getUser(id,"target_weight"));

        float dayForTarget = (float)check_day(id);
        float targetWeight = Float.parseFloat(targetWeightString);
        float lose_weight = weight * 30;
        float maintain = weight*40;
        float gain_weight = weight*50;
        float daily_loss = 3500*3/7;
        float change_per_day = ( targetWeight - weight)/ dayForTarget;
        float intake_calories = change_per_day*daily_loss;
        return (int)(BMR*(float)1.375 + intake_calories);
        

    }

    /**
     * This function is going to generate a summary from the user table
     * It will calculate all the data since the user first user the chatbot and add them together
     * And then it will output a simple summary of the user's intake overall
     * @param id Input the user id to get the data from user table
     * @return output Return a String with the energy, sodium, fatty consumed from the first day the user used to now
     */
    public static String generateSummary(String id) {

        ArrayList<ArrayList<String>> Record = new ArrayList<ArrayList<String>>();

        Record = UserDB.DBsearch_record(id);
        log.info("2333331");
        int number_of_date = Record.size();
        if(Record.isEmpty())
        	number_of_date = 0;
        log.info("record size = , {}", number_of_date);

        if (number_of_date == 0)
        	return "You have not input any data yet!";

       
        float[][] float_Record = new float[number_of_date][3];  // calories, sodium, fat

        for (int i=0;i<number_of_date;i++){
            for(int j=0; j<3; j++){
                float_Record[i][j] = Float.parseFloat(Record.get(i).get(j+2));
                log.info("232,{}", j);
            }
        }

        float[] overall = {0,0,0} ; // total calories, sodium, fat
        for (int i=0; i<number_of_date; i++){
            for (int j=0; j<3; j++){
                overall[j] += float_Record[i][j];
            }
        }

        String first_day = Record.get(0).get(0);
        String output = "Starting from " + first_day + " , you have totally consumed: \n"+"Energy: "+ (int)overall[0]+"kcal, \n" + "Sodium: "+(int)overall[1]+"mg, \n"+"Saturated Fatty Acids: "+(int)overall[2]+"g.";     

        return output;

    }

    /**
     * This function is going to generate at most 7 days summary from the user table record
     * It will calculate the average intake and weight of the user
     * And also output everyday's record
     * @param id Input the user id to get the user table
     * @return output Return a String with average data and everyday's data
     */
    public static String generateWeeklySummary (String id) {
        ArrayList<ArrayList<String>> Record = new ArrayList<ArrayList<String>>(UserDB.DBsearch_record(id));
        log.info("54321");
     
        int number_of_date = Record.size();
        log.info("65432");

        if(Record.isEmpty())
        	number_of_date = 0;

        if (number_of_date == 0)
        	return "You have not input any data yet!";
        
        int date_for_summary = number_of_date;

        if (date_for_summary>7){
            date_for_summary=7;
        }


        float[] sum= {0,0,0,0}; // calories, sodium, fat

        for (int i=0;i<date_for_summary;i++){

            for (int j=0; j<4; j++){
                sum[j] += Float.parseFloat(Record.get(number_of_date-i-1).get(j+1));
            }
        }
        
        String output = "In the past " + date_for_summary + " days, your daily energy intake and average weight are: \n" + "Energy: " +(int)(sum[1]/date_for_summary) +
        "kcal/day,\n"+ "the standard daily calorie consumption: "+ getIdealDailyIntake(id) + "kcal/day\n" + "Average Weight: " + (int)(sum[0]/date_for_summary) + "kg. \n\n" + "The detailed daily record of weight and energy intake: \n";
       
        for ( int i=0; i<date_for_summary; i++){
            output =output + Record.get(number_of_date-i-1).get(0) +": \n"+"Energy: "+Record.get(number_of_date-i-1).get(2)+"kcal \n"+"Weight: "+Record.get(number_of_date-i-1).get(1)+"kg\n";
        } 
        
        return output;

    }


}
