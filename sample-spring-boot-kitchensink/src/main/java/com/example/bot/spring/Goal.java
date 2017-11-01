package com.example.bot.spring;

import java.util.Arrays;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Goal {
    
    private int day;
    //private String purpose;
    private float target;
    
    public Goal (String Day, String Target){
        day = Integer.parseInt(Day);
        target = Float.parseFloat(Target);
        //purpose =loat new String(Purpose);
    }
    
    public int getGoalDay () {
        return day;
    }
    
    public float getGoalTarget (){
        return target;
    }
    
//    public String getPurpose (){
//        return purpose;
//    }
}
