package com.example.bot.spring;
import java.io.IOException;
import java.util.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
//food class  member name:string price:float quality: float[*] foodDB: foodDBAdapeter
//Functions getName();getPrice();getQuality();
public class Food{
    
    FoodDBAdaptor foodDB = new FoodDBAdaptor();
    private String name;
    private float price;
    private float[] quality;
    //private String[] ingredients;
    //private foodDBAdapter foodDB;
    //ArrayList < String > components= new ArrayList < String >;
    ArrayList<Component> components = new ArrayList<Component>();
    
    public Food(String n, float p, String[] ingres){
        this.name = n;
        this.price = p;
        float[] dummy = new float[3];
        if (ingres == null){
            if(this.foodDB.searchFood(n) == null){
                this.quality = dummy;
            }
            else{
                components = this.foodDB.searchFood(n);
                
                Iterator<Component> itr = components.iterator();
                while (itr.hasNext()) {
                	 	float[] x = itr.next().getNutritionPerServing();
                	 	for(int j= 0;j<x.length; j++){
                            dummy[j]=dummy[j]+x[j];
                     }
                }                   
            }
        }
        
        else{
            
            for(int i =0; i < ingres.length;i++){
                if (this.foodDB.searchFood(ingres[i])== null){
                    continue;
                }
                else{
                    try {
                    		float[] c = this.foodDB.searchIngredient(ingres[i]).getNutritionPerServing();
                         for(int j=0; j< c.length;j++){
                        	 	dummy[j] = dummy[j] + c[j];
                         }
                    }catch(Exception e) {
                    		//System.out.println("searchIngredient");
                    }
                }
            }
        }
        
        this.quality = dummy;
    }
//    public Food(String name, float price){
//        String[] x = null;
//        this(name,price,x);
//    }
    
    	public Food(String text) {
    		Food x = new Food(text,0,null);
    		this.name = x.getName();
    		this.price = x.getPrice();
    		this.quality = x.getQuality();
    		this.components = x.getComponents();
    	}
    
    
    public float[] getQuality(){
        return quality;
    }
    
    public String getName(){
        return name;
    }
    
    public float getPrice(){
        return price;
    }
    
    public ArrayList<Component> getComponents(){
    		return components;
    }
    
}