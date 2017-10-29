



package com.example.bot.spring;


import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.ChartFactory;
import org.jfree.data.general.DefaultPieDataset;
import java.io.File;

import java.util.Arrays;
import java.util.Date;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class User {
    
    
    static int userCount = 0;
    static UserDBAdaptor userAdapter = new UserDBAdaptor();
    static FoodDBAdaptor foodAdapter = new FoodDBAdaptor();
    
    private int id;
    private String name;
    private String gender;
    private int age;
    private float weight;
    private float height;
    private Goal goal;
    
    public User (String NAME, String GENDER, int AGE, float WEIGHT, float HEIGHT, int DAY, float TARGET, String Puerpose ) {
        
        id = userCount;
        userCount ++;
        
        name=new String(NAME);
        gender=new String(GENDER);
        age=AGE;
        weight=WEIGHT;
        goal = new Goal(DAY,TARGET,Puerpose);
    }
    
    
    public int getUserId () { return id; }
    public String getUserName (){ return name;}
    public String getUserStatus () { return status;}
    public int getUseAge () { return age; }
    public float getUserWeight (){ return weight;}
    public float getUserHeight () { return height;}
    public Goal getUserGoal () { return goal;}
 
    
    public String generateSummary() {
        
        String[][] Record = new String[userAdapter.searchUser(id).length][5];  // Record[numer_of_date][date, weight, calories, sodium, fat]
        
        int number_of_date = userAdapter.searchUser(id).length;
        
        for (int i = 0; i < number_of_date; i++){
            System.arraycopy(Record[i],0,adapter.searchUser(id)[i],0,5);
        }
        
        float[][] float_Record = new float[number_of_date][3];  // calories, sodium, fat
        
        for (int i=0;i<number_of_date;i++){
            for(int j=1; j<3; j++){
                float_Record[i][j] = Float.parseFloat(Record[i][j+2]);
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
       String[][] pass7Record = new String[adapter.searchUser(id).length][5];// Record[date][date, weight, calories, sodium, fat]
        
        int number_of_date = userAdapter.searchUser(id).length;
        
        for (int i = 0; i < 7; i++){
            System.arraycopy(pass7Record[i],0,adapter.searchUser(id)[number_of_date-7+i],0,length);
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
    
    
    
            // Create a simple XY chart
        XYSeries series_weight = new XYSeries("Weight");
        XYSeries series_energy = new XYSeries("Energy");
        XYSeries series_sodium = new XYSeries("Sodium");
        XYSeries series_fattyacid = new XYSeries("Fatty Acid");
         XYSeriesCollection dataset = new XYSeriesCollection();
        for (int i=0; i<4; i++){
            series_weight.add(date[i], pass7summary[i][0])
        }
        for (int i=0; i<4; i++){
            series_energy.add(date[i], pass7summary[i][0])
        }
        for (int i=0; i<4; i++){
            series_sodium.add(date[i], pass7summary[i][0])
        }
        for (int i=0; i<4; i++){
            series_fattyacid.add(date[i], pass7summary[i][0])
        }
        dataset.addSeries(series_weight);
        dataset.addSeries(series_energy);
        dataset.addSeries(series_sodium);
        dataset.addSeries(series_fattyacid );
    
        
        
            // Generate the graph
            JFreeChart chart = ChartFactory.createXYLineChart(
                                                              "Weekly Summary",
                                                              "Date",
                                                              "change",
                                                              dataset,
                                                              PlotOrientation.VERTICAL,  // Plot Orientation
                                                              true,                      // Show Legend
                                                              true,                      // Use tooltips
                                                              false                      // Configure chart to generate URLs?
                                                              );
            try {
                File weeksummary = new File("weekly.jpg");
                ChartUtilities.saveChartAsJPEG(weeksummary, chart, 500, 300);
                return weeksummary;
            } catch (IOException e) {
                System.err.println("Problem occurred creating chart.");
            }
    }
    
    
        
        
        
    
        
        
}


