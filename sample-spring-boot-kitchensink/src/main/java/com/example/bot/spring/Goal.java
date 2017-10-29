package com.example.bot.spring;

import java.util.Arrays;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Goal {
    
    private int day;
    private float target;
    
    
    public Goal (String Target_weight, String Target_day ){
        day = Integer.parseInt(Target_day);
        target = Float.parseFloat(Target_weight);
    }
    
    public int getGoalDay () {
        return day;
    }
    
    public float getGoalTarget (){
        return target;
    }
    
}
