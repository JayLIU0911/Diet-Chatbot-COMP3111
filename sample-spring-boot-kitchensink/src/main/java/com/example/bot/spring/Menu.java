package com.example.bot.spring;

import java.util.*;
public class Menu{
	
    private Food[] food;
    
    public Menu(String txt){
        int items = 0;
        List<String> menu = new ArrayList<>();
        try (Scanner scanner = new Scanner(txt)){
            while (scanner.hasNextLine()){
                menu.add(scanner.nextLine());
                items ++;
            }
        }
        food = new Food[items];
        for (int i=0; i < items; i++){
            float p = 0;
            String[] c = menu.get(i).split("\\s");
            String[] chart = new String [c.length -1 ];
            for (int j = 0; j<c.length;j++){
                try{
                    p = Float.parseFloat(c[j]);
                }
                catch (NumberFormatException e){
                    chart[j]=c[j];
                    continue;
                }
            }
            String name = String.join(" ",chart);
            //food[i] = new Food(name,p);
            food[i] = new Food(name,p,null);
        }
    }
    
    public Food findMinPrice(){
        int index = 0;
        float minprice = this.food[0].getPrice();
        for (int i = 0; i < food.length;i++){
            if (food[i].getPrice()<minprice){
                minprice = food[i].getPrice();
                index = i;
            }
        }
        return food[index];
    }
    public Food findMinEnergy(){
        int index = 0;
        return food[index];
        
    }
    public Food findOptimal(User user){
        int index = 0;
        return food[index];
    }
}