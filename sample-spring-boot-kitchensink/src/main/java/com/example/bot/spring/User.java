



package com.example.bot.spring;

/*
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.imageio.ImageIO;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

import java.io.File;
*/

import java.util.Arrays;
import java.util.Date;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class User {
    

    static UserDBAdaptor userAdapter = new UserDBAdaptor();
    static FoodDBAdaptor foodAdapter = new FoodDBAdaptor();
    
    private String id;
    private String name;
    private String status;
    private int age;
    private float weight;
    private float height;
    private Goal goal;
    
    public User (ArrayList<String> list){
        
        id = new String(list.get(0));
        name=new String(list.get(1));
        status=new String(list.get(2));
        age=Integer.parseInt(list.get(3));
        height = Float.parseFloat(list.(4));
        weight=Float.parseFloat(list.get(5));
        goal = new Goal(list.get(6),list.get(7));
    }
    
    
    public String getUserId () { return id; }
    public String getUserName (){ return name;}
    public String getUserStatus () { return status;}
    public int getUseAge () { return age; }
    public float getUserWeight (){ return weight;}
    public float getUserHeight () { return height;}
    public Goal getUserGoal () { return goal;}
    public void userSetWeight (String newWeight){
        
        try {
            
            if (userAdapter.updateWeight(id, weight)){
                weight =Float.parseFloat(newWeight)
                return;
            }
        }catch (Exception e){
            log.info("SQLException while updating weight: {}", e.toString());
        }fnally{
            return;
        }
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
        
        if (gender.equalsIgnoreCase("man")){
            BMR = 88.362 + ( 13.397*weight) + (4.799*height) - (5.677*age);
    
            
        }
        else if (gender.equalsIgnoreCase("woman")){
            BMR = 447.593 + ( 9.247*weight) + ( 3.098*height) - (4.33*age);
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
    
    public File generateWeeklySummary () {
        ArrayList<ArrayList<String>> Record = new ArrayList<ArrayList<String>>();
        
        int number_of_date = userAdapter.searchRecord(id).size();
        
        for (int i = 0; i < number_of_date; i++){
            ArrayList<String> Line = new ArrayList<String>();
            
            for (String obj: userAdapter.searchUser(id).get(i)){
                Line.add(obj);
            }
            Record.add(Line);
        }
        
        
       String[][] pass7Record = new String[adapter.searchRecord(id).size()][5];// Record[date][date, weight, calories, sodium, fat]
        
        int number_of_date = Record.size();
        
        for (int i = 0; i < 7; i++){
            for (int j=0; j<4; j++){
                pass7Record[i][j] = Record.get(number_of_date-7+i).get(j);
            }
        
        }//pass 7 days record
        
        float[][] pass7summary = new float[7][4] ;  // weight, calories, sodium, fat
        
        for (int i=0;i<7;i++){
            pass7summary[i][0] = Float.parseFloat(pass7Record[i][1]);
            for(int j=1; j<4; j++){
                pass7summary[i][j] = Float.parseFloat(Record[i][j+1]);
            }
        }
        
        String[] date = new String[7];
        for (int i=0; i<7; i++){
            data[i] = pass7Record[i][0];
        }
        File weeksummary = new File("Weekly.jpg");
                             return weeksummary;
    }
    
     
        
    
    
}
/*
class LineChartSample extends Application {
    
    @Override public void start(Stage stage) {
                             Stage stage = new Stage;
        stage.setTitle("Line Chart Sample");
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Date");
        final LineChart<String,Number> lineChart =
        new LineChart<String,Number>(xAxis,yAxis);
        
        lineChart.setTitle("Weekly Summary");
        
        XYChart.Series series1 = new XYChart.Series();
        series1.setName("Weight");
        XYChart.Series series2 = new XYChart.Series();
        series2.setName("Energy");
        
        for (int i=0; i<7; i++){
            series1.getData().add(new XYChart.Data(Date[i], pass7Record[i][0]));
            series1.getData().add(new XYChart.Data(Date[i], pass7Record[i][1]));

        }
        
        

        Scene scene  = new Scene(lineChart,800,600);
        lineChart.getData().addAll(series1, series2);
        
        saveAsJpg(scene, "weeklySummary.jpg");
        stage.setScene(scene);
        //stage.show();
        
        WritableImage image = scene.snapshot(null);
        File weeksummary = new File("Weekly.jpg");
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "jpg",
                          weeksummary);
            
            
            
        } catch (Exception e) {
            log.info("ImageWriteException while writing weeklysummary: {}", e.toString());
        }
    
    }
    
    
    
    public static void main(String[] args) {
        launch(args);

 }
 
}
*/
