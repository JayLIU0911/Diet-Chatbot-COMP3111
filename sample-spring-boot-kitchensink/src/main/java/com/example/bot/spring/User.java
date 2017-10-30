



package com.example.bot.spring;


//import org.jfree.chart.JFreeChart;
//import org.jfree.chart.ChartUtilities;
//import org.jfree.chart.ChartFactory;
//import org.jfree.data.general.DefaultPieDataset;
import java.io.File;

import java.util.Arrays;
import java.util.Date;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class User {
    

    static UserDBAdaptor userAdapter = new UserDBAdaptor();
    static FoodDBAdaptor foodAdapter = new FoodDBAdaptor();
    
    private int id;
    private String name;
    private String status;
    private int age;
    private float weight;
    private float height;
    private Goal goal;
    
    public User (ArrayList<String> list){
        
        id = Integer.parseInt(list.get(0));
        name=new String(list.get(1));
        status=new String(list.get(2));
        age=Integer.parseInt(list.get(3));
        height = Float.parseFloat(list.get(4));
        weight=Float.parseFloat(list.get(5));
        goal = new Goal(Integer.parseInt(list.get(6)),Float.parseFloat(list.get(7)),list.get(8));
    }
    
    
    public int getUserId () { return id; }
    public String getUserName (){ return name;}
    public String getUserStatus () { return status;}
    public int getUseAge () { return age; }
    public float getUserWeight (){ return weight;}
    public float getUserHeight () { return height;}
    public Goal getUserGoal () { return goal;}
    public void setWeight (String newWeight){
        
        try {
            
            if (userAdapter.updateWeight(id, weight)){
                weight =Float.parseFloat(newWeight);
                return;
            }
        }catch (Exception e){
            log.info("SQLException while updating weight: {}", e.toString());}
//        }finally{
//            return;
//        }
    }
    
    
    public float getDailyIntake () {
        return Float.parseFloat(userAdapter.searchRecord(id).get(userAdapter.searchRecord(id).size()-1).get(3));
    }
    
    public float getIdealDailyIntake (){
        float lose_weight = weight * 30;
        float maintain = weight*40;
        float gain_weight = weight*50;
        float daily_loss = 3500*3/7;
        float change_per_day = (goal.getGoalTarget() - weight)/goal.getGoalDay();
        float intake_calories = change_per_day*daily_loss;
        float BMR=0;
        
        if (status.equalsIgnoreCase("man")){
            BMR = (float)88.362 + ( (float)13.397*weight) + ((float)4.799*height) - ((float)5.677*age);
    
            
        }
        else if (status.equalsIgnoreCase("woman")){
            BMR = (float)447.593 + ( (float)9.247*weight) + ( (float)3.098*height) - ((float)4.33*age);
        }
        
        
        if (goal.getGoalTarget()==0){
            return BMR;
        }
        else
            return BMR + intake_calories;
    }
    
    
    
    
    
    public String generateSummary() {
        
        ArrayList<ArrayList<String>> Record = new ArrayList<ArrayList<String>>();
        
        int number_of_date = userAdapter.searchRecord(id).size();
        
        for (int i = 0; i < number_of_date; i++){
            ArrayList<String> Line = new ArrayList<String>();
            
            for (String obj: userAdapter.searchRecord(id).get(i)){
                Line.add(obj);
            }
            Record.add(Line);
        }
        
        
        float[][] float_Record = new float[number_of_date][3];  // calories, sodium, fat
        
        for (int i=0;i<number_of_date;i++){
            for(int j=1; j<3; j++){
                float_Record[i][j] = Float.parseFloat(Record.get(i).get(j+2));
            }
        }
        
        float[] overall = {0,0,0} ; // total calories, sodium, fat
        for (int i=0; i<number_of_date; i++){
            for (int j=0; j<3; j++){
                overall[j] += float_Record[i][j];
            }
        }
        
        String output = "So far, you have consumed " + overall[0] + " calories, " + overall[1] + " sodium and " + overall[2] + "fatty acid. ";
        
        return output;
        
    }
    
    public File generateWeeklySummary () {return null;}
//        ArrayList<ArrayList<String>> Record = new ArrayList<ArrayList<String>>();
//        
//        int number_of_date = userAdapter.searchRecord(id).size();
//        
//        for (int i = 0; i < number_of_date; i++){
//            ArrayList<String> Line = new ArrayList<String>();
//            
//            for (String obj: userAdapter.searchUser(id).get(i)){
//                Line.add(obj);
//            }
//            Record.add(Line);
//        }
//        
//        
//       String[][] pass7Record = new String[adapter.searchRecord(id).size()][5];// Record[date][date, weight, calories, sodium, fat]
//        
//        int number_of_date = Record.size();
//        
//        for (int i = 0; i < 7; i++){
//            for (int j=0; j<4; j++){
//                pass7Record[i][j] = Record.get(number_of_date-7+i).get(j);
//            }
//        
//        }//pass 7 days record
//        
//        float[][] pass7summary = new float[7][4] ;  // weight, calories, sodium, fat
//        
//        for (int i=0;i<7;i++){
//            pass7summary[i][0] = Float.parseFloat(pass7Record[i][1]);
//            for(int j=1; j<4; j++){
//                pass7summary[i][j] = Float.parseFloat(Record[i][j+1]);
//            }
//        }
//        
//        String[] date = new String[7];
//        for (int i=0; i<7; i++){
//            data[i] = pass7Record[i][0];
//        }
//    
//    
//    
//            // Create a simple XY chart
//        XYSeries series_weight = new XYSeries("Weight");
//        XYSeries series_energy = new XYSeries("Energy");
//        XYSeries series_sodium = new XYSeries("Sodium");
//        XYSeries series_fattyacid = new XYSeries("Fatty Acid");
//         XYSeriesCollection dataset = new XYSeriesCollection();
//        for (int i=0; i<4; i++){
//            series_weight.add(date[i], pass7summary[i][0]);
//        }
//        for (int i=0; i<4; i++){
//            series_energy.add(date[i], pass7summary[i][0]);
//        }
//        for (int i=0; i<4; i++){
//            series_sodium.add(date[i], pass7summary[i][0]);
//        }
//        for (int i=0; i<4; i++){
//            series_fattyacid.add(date[i], pass7summary[i][0]);
//        }
//        dataset.addSeries(series_weight);
//        dataset.addSeries(series_energy);
//        dataset.addSeries(series_sodium);
//        dataset.addSeries(series_fattyacid );
//    
//        
//        
//            // Generate the graph
//            JFreeChart chart = ChartFactory.createXYLineChart(
//                                                              "Weekly Summary",
//                                                              "Date",
//                                                              "change",
//                                                              dataset,
//                                                              PlotOrientation.VERTICAL,  // Plot Orientation
//                                                              true,                      // Show Legend
//                                                              true,                      // Use tooltips
//                                                              false                      // Configure chart to generate URLs?
//                                                              );
//            try {
//                File weeksummary = new File("weekly.jpg");
//                ChartUtilities.saveChartAsJPEG(weeksummary, chart, 500, 300);
//                return weeksummary;
//            } catch (IOException e) {
//                System.err.println("Problem occurred creating chart.");
//            }
//   }
    
    
        
        
        
    
        
        
}


