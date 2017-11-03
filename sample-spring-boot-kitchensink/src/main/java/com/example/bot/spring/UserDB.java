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

@Slf4j
public class UserDB extends SQLDatabaseEngine{

   @Autowired
   private SQLDatabaseEngine databaseEngine;

   private FoodDB foodAdapter = new FoodDB();

    public boolean insert(String id){
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        boolean result = false;

        try{
            connection = this.getConnection();
            stmt = connection.prepareStatement("SELECT * FROM user_list WHERE uid = ?");
            stmt.setString(1,id);
            rs = stmt.executeQuery();
            if(rs.next()){
                if(connection!=null)
                    connection.close();
                return false;
            }

            stmt = null;
            rs = null;
            String tableName = "user_" + id;
            stmt = connection.prepareStatement("CREATE TABLE " + id + " (date varchar(50), time varchar(50), food_intake varchar(200), price varchar(50), weight varchar(50), energy varchar(50), sodium varchar(50), fatty_acids_total_saturated varchar(50))");
            // stmt.setString(1,tableName);
                        log.info("create table stmt: {}", stmt.toString());
            stmt.executeUpdate();

                        log.info("create table stmt succeed: {}", stmt.toString());
            stmt = null;
            rs = null;
                        String sql = "INSERT INTO user_list VALUES('" + id + "', ' ', ' ', ' ', ' ', ' ', ' ', ' ', '0')";
                        log.info("insert table stmt: {}", sql);
                        // sql = "INSERT INTO user_list VALUES(" + id + ", ' ', ' ', ' ', ' ', ' ', ' ', ' ', '0')";
                        // log.info("insert table stmt0: {}", sql);

            stmt = connection.prepareStatement(sql);
            // stmt.setString(1,id);

                        // log.info("insert table stmt1: {}", stmt.toString());

                                                // stmt = connection.prepareStatement("INSERT INTO user_list VALUES(?, ' ', ' ', ' ', ' ', ' ', ' ', ' ', '0')");
                                    // stmt.setString(1,id);
                                                // log.info("insert table stmt2: {}", stmt.toString());
                        stmt.executeUpdate();

            result = true;
        } catch (Exception e) {
            log.info("SQLExecption while inserting user: {}", e.toString());
        } finally{
            try{
                if(connection!=null)
                    connection.close();
                //rs.close();
                if(stmt!=null)
                    stmt.close();

            } catch (Exception ex) {
                log.info("SQLException while closing1: {}", ex.toString());
            }
        }
        
        return result;
    }


    public boolean delete(String id)
    {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean result = false;
        //String id = user.getUserId();
        //String tableName = "user_"+id;
        int x = 0;

        try{
            connection = this.getConnection();
            String sql = "drop table " + id;
            stmt = connection.prepareStatement(sql);
            //stmt.setString(1,tableName);
            stmt.executeUpdate();
//            if(rs.next())
//                x++;
            connection.close();
            stmt = null;
            rs = null;
            connection = this.getConnection();
            stmt = connection.prepareStatement("DELETE FROM user_list WHERE uid = ?");
            stmt.setString(1,id);
            stmt.executeUpdate();
//            if(rs.next())
//                x++;
            result = true;
        } catch (Exception e) {
            log.info("SQLExecption while deleting: {}", e.toString());
        } finally{
            try{
                if(connection!=null)
                    connection.close();
                // if(rs.next())
                //     rs.close();
                if(stmt!=null)
                    stmt.close();

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
        //String tableName = "user_" + id;
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean result = false;

        //foodAdapter = new Food(text);
        Date current_time = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh/mm/ss");

        String date = dateFormat.format(current_time);
        String time = timeFormat.format(current_time);


        //String intake = foodAdapter.getName(text);
        //float weight = food.getWeight();
        String energy = Float.toString(foodAdapter.getQuality(text)[0]);
        String sodium = Float.toString(foodAdapter.getQuality(text)[1]);
        String fatty = Float.toString(foodAdapter.getQuality(text)[2]);

        String weight = getUser(id,"weight");
        //String price = Float.toString(foodAdapter.getPrice(text));
        //float weight = a.getUserWeight();

        try{
            connection = this.getConnection();
            String sql = "insert into " + id + " (date, time, food_intake, price, weight, energy, sodium, fatty_acids_total_saturated) values('" + date +"','"+time+"','"+text+"','0','"+weight+"','"+energy+"','"+sodium+"','"+fatty+"')";
            //stmt = connection.prepareStatement("INSERT INTO ? (date, time, food_intake, price, weight, energy, sodium, fatty_acids_total_saturated) VALUES(?,?,?,?,?,?,?,?)");
            // stmt.setString(1,tableName);
            // stmt.setString(2,date);
            // stmt.setString(3,time);
            // stmt.setString(4,text);
            // stmt.setString(5,"0");
            // stmt.setString(6,weight);
            // stmt.setString(7,energy);
            // stmt.setString(8,sodium);
            // stmt.setString(9,fatty);
            stmt = connection.prepareStatement(sql);
            stmt.executeUpdate();
            //if(rs.next())
                result = true;
        } catch (Exception e){
            log.info("SQLException while updating: {}", e.toString());
        } finally{
            try{
                if(connection!=null)
                    connection.close();
                // if(rs.next())
                //     rs.close();
                if(stmt!=null)
                    stmt.close();
                
            }catch (Exception ex) {
                log.info("SQLException while closing3: {}", ex.toString());
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
        String weight = Float.toString(goal.getGoalTarget());
        String day = Float.toString(goal.getGoalDay());

        try{
            connection = this.getConnection();
            String sql = "update user_list set target_weight='" + weight + "',days_for_target='" + day + "' where uid = '" + id +"'"; 
            stmt = connection.prepareStatement(sql);
            //stmt = connection.prepareStatement("update user_list set target_weight=?,days_for_target=? where uid=?");
            //stmt.setString(1,id);
            stmt.setString(1,weight);
            stmt.setString(2,day);
            stmt.setString(3,id);
            stmt.executeUpdate();
            //if(rs.next())
                result = true;
        } catch (Exception e){
            log.info("SQLException while updating target: {}", e.toString());
        } finally{
            try{
                if(connection!=null)
                    connection.close();
                // if(rs.next())
                //     rs.close();
                if(stmt!=null)
                    stmt.close();
                
            }catch (Exception ex) {
                log.info("SQLException while closing5: {}", ex.toString());
            }
        }
        
        return result;
    }


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
        String weight = "0";

        //String tableName = "user_" + id;

        try{
            connection = this.getConnection();
            String sql = "SELECT date, weight, energy, sodium, fatty_acids_total_saturated from " + id;
            //stmt = connection.prepareStatement("SELECT date, weight, energy, sodium, fatty_acids_total_saturated from ?");
            //stmt.setString(1,tableName);
            stmt = connection.prepareStatement(sql);
            rs = stmt.executeQuery();
            //log.info("")



            String date2 = "0";
            String date = null;
            String energy = null;
            String sodium = null;
            String fatty = null;
            //int i = -1;

            while(rs.next())
            {
                date = rs.getString(1);
                log.info("in 280, {}",date);
                //date2 = date;
                energy = rs.getString(3);
                log.info("in 280, {}",energy);
                sodium = rs.getString(4);
                fatty = rs.getString(5);
                weight = rs.getString(2);

                if(date2.equals("0")){
                    x = Float.parseFloat(energy);
                    y = Float.parseFloat(sodium);
                    z = Float.parseFloat(fatty);
                    date2 = date;
                    log.info("12345");
                }

                else if(!date.equals(date2))
                {
                    //i++;

                    a.clear();
                    a.add(date);
                    a.add(weight);
                    a.add(Float.toString(x));
                    a.add(Float.toString(y));
                    a.add(Float.toString(z));

                    result.add(a);

                    x = Float.parseFloat(energy);
                    y = Float.parseFloat(sodium);
                    z = Float.parseFloat(fatty);


                    date2 = date;
                    log.info("123456");
                }
                else{
                    x = x + Float.parseFloat(energy);
                    y = y + Float.parseFloat(sodium);
                    z = z + Float.parseFloat(fatty);
                    log.info("123457");
                }
            }

            a.clear();
            a.add(date);
            a.add(weight);
            a.add(Float.toString(x));
            a.add(Float.toString(y));
            a.add(Float.toString(z));
            result.add(a);

        } catch (Exception e){
            log.info("SQLException while computing record: {}", e.toString());
        } finally{
            try{
                if(connection!=null)
                    connection.close();
                if(rs.next())
                    rs.close();
                if(stmt!=null)
                    stmt.close();
                
            }catch (Exception ex) {
                log.info("SQLException while closing7: {}", ex.toString());
            }
        }
        
        return result;

    }


    public ArrayList<String> searchRecord2(String id, String date)
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
        //float weight = 0;

        String tableName = "user_" + id;

        try{
            connection = this.getConnection();
            String sql = "SELECT date, weight, energy, sodium, fatty_acids_total_saturated from " + id + " where date = '" + date +"'";
            //stmt = connection.prepareStatement("SELECT date, weight, energy, sodium, fatty_acids_total_saturated from ? where date=?");
            //stmt.setString(1,tableName);
            //stmt.setString(2,date);

            stmt = connection.prepareStatement(sql);
            rs = stmt.executeQuery();

            log.info("search daily data success in database: {}", stmt.toString());

            result.add(date);

            String weight=null;

            while(rs.next())
            {
                String date2 = rs.getString(1);
                weight = rs.getString(2);
                String energy = rs.getString(3);
                String sodium = rs.getString(4);
                String fatty = rs.getString(5);



                if(date2.equals(date)){
                    x = x + Float.parseFloat(energy);
                    y = y + Float.parseFloat(sodium);
                    z = z + Float.parseFloat(fatty);
                }
                else
                    break;
            }

            result.add(weight);
            result.add(Float.toString(x));
            result.add(Float.toString(y));
            result.add(Float.toString(z));
            if ( x==0 && y==0 && z==0){
                result = null;
            }

        } catch (Exception e){
            log.info("SQLException while computing record: {}", e.toString());
        } finally{
            try{
                if(connection!=null)
                    connection.close();
                if(rs.next())
                    rs.close();
                if(stmt!=null)
                    stmt.close();
                
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


    public String getUser(String id, String column){
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        //String id = user.getUserId();
        String result = null;

        try{
            connection = this.getConnection();
            String sql = "select " + column + " from user_list where uid = '" + id + "'";
            //stmt = connection.prepareStatement("select ? from user_list where uid="+ id);
            //stmt.setString(1,column);
            //stmt.setString(2,id);
            stmt = connection.prepareStatement(sql);
            rs = stmt.executeQuery();

            if(rs.next())
                result = rs.getString(1);

    } catch (Exception e){
        log.info("SQLException while getting state: {}", e.toString());
    } finally{
        try{
            connection.close();
            if(rs.next())
                rs.close();
            if(stmt!=null)
                stmt.close();
            //if(connection!=null)
            
            }catch (Exception ex) {
                log.info("SQLException while closing9: {}", ex.toString());
            }
    }
    
    return result;
}


    public boolean setUser(String id, String column, String text){
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        boolean result = false;
        //String id = user.getUserId();

        try{
            connection = this.getConnection();
            String sql = "update user_list set " + column + "='" + text + "' where uid = '" + id + "'";
            stmt = connection.prepareStatement(sql);
            //stmt = connection.prepareStatement("update user_list set ?=? where uid="+id);
            //stmt.setString(1,column);
            //stmt.setString(2,text);
            //stmt.setString(3,id);
            stmt.executeUpdate();

            result = true;

        } catch (Exception e){
            log.info("SQLException while setting state: {}", e.toString());
        } finally{
            try{
                connection.close();
                // if(rs.next())
                //     rs.close();
                if(stmt!=null)
                    stmt.close();
                //if(connection!=null)
                
            }catch (Exception ex) {
                log.info("SQLException while closing10: {}", ex.toString());
            }
        }
        
        return result;
    }


    public float getDailyIntake(String id, Date date){
        SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
        //Date current_time = new Date();
        //String date = dateFormat.format(current_time);
        String search_date = dateformat.format(date);
        log.info("date success: {}",date.toString());
        ArrayList<String> x = this.searchRecord2(id,search_date);
        log.info("search daily success: {}");

        if (x == null) return 0;

        float result = Float.parseFloat(x.get(2));
        log.info("the result of daily summary is : {}");
        return result;
    }


    public int getIdealDailyIntake (String id){



        float weight = Float.parseFloat(this.getUser(id,"weight"));
        String status = new String(this.getUser(id,"status"));
        float height = Float.parseFloat(this.getUser(id,"height"));
        int age = Integer.parseInt(this.getUser(id,"age"));

        float BMR=0;
        if (status.equalsIgnoreCase("male")){
            BMR = (float)88.362 + ( (float)13.397*weight) + ((float)4.799*height) - ((float)5.677*age);
        }
        else if (status.equalsIgnoreCase("female")){
            BMR = (float)447.593 + ( (float)9.247*weight) + ( (float)3.098*height) - ((float)4.33*age);
        }


        String targetWeightString = new String(this.getUser(id,"target_weight"));
        if (targetWeightString.equalsIgnoreCase("no")) return (int)(BMR*(float)1.375);

            else{

        float dayForTarget = Float.parseFloat(this.getUser(id,"days_for_target"));
        float targetWeight = Float.parseFloat(targetWeightString);
        float lose_weight = weight * 30;
        float maintain = weight*40;
        float gain_weight = weight*50;
        float daily_loss = 3500*3/7;
        float change_per_day = ( targetWeight - weight)/ dayForTarget;
        float intake_calories = change_per_day*daily_loss;
        return (int)(BMR*(float)1.375 + intake_calories);
    }








    }


    public String generateSummary(String id) {

        ArrayList<ArrayList<String>> Record = new ArrayList<ArrayList<String>>();

        Record = this.searchRecord(id);
        log.info("2333331");
        int number_of_date = Record.size();

        // for (int i = 0; i < number_of_date; i++){
        //     ArrayList<String> Line = new ArrayList<String>();

        //     //for (String obj: this.searchRecord(id).get(i)){
        //         Line.add(this.searchRecord(id).get(i));
        //     //}
        //     Record.add(Line);
        // }

        


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
        
        if (number_of_date == 0) output = "You have not input any data yet!";

        return output;

    }


    public String generateWeeklySummary (String id) {
        ArrayList<ArrayList<String>> Record = new ArrayList<ArrayList<String>>(this.searchRecord(id));
        log.info("54321");
        //Record = this.searchRecord(id);
        int number_of_date = Record.size();
        log.info("65432");
        int date_for_summary = number_of_date;
        // in case there are less than 7 days' reord

        if (date_for_summary>7){
            date_for_summary=7;
        }

        


        float[] sum= {0,0,0,0}; // calories, sodium, fat

        for (int i=0;i<date_for_summary;i++){

            for (int j=0; j<4; j++){
                sum[j] += Float.parseFloat(Record.get(number_of_date-i-1).get(j+1));
            }
        }
       
        
        
        String output = "In the past " + date_for_summary + " days, your daily energy intake and average weight are: \n" + "Energy: " +(int)(sum[0]/date_for_summary) +
        "kcal/day,\n"+ "the standard daily calorie consumption: "+ getIdealDailyIntake(id) + "kcal/day\n" + "Average Weight: " + (int)(sum[1]/date_for_summary) + "kg. \n\n" + "The detailed daily record of weight and energy intake: \n";
        
        
        
        
        
        
        for ( int i=0; i<date_for_summary; i++){
            output =output + Record.get(number_of_date-i-1).get(0) +": \n"+"Energy: "+Record.get(number_of_date-i-1).get(2)+"kcal \n"+"Weight: "+Record.get(number_of_date-i-1).get(1)+"kg\n";
        }
        
        if (date_for_summary == 0) output = "You have not input any data yet!";
        
        return output;



    }


}
