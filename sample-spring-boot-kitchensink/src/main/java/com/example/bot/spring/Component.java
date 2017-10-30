package com.example.bot.spring;

import lombok.extern.slf4j.Slf4j;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.*;
import java.net.URISyntaxException;
import java.net.URI;
import java.util.*;

public class Component {
	private int id;
	private String name;
	private float weight;
	private String amount;
	private float[] nutrition;
	public Component(int id, String name, float weight, String amount, float[] nutrition){
		this.id = id;
		this.name = name;
		this.weight = weight;
		this.amount = amount;
		this.nutrition = nutrition;
	}
	public int getID(){
		return id;
	}
	public String getName(){
		return name;
	}
	public float getWeight(){
		return weight;
	}

	public float getAmount(){
		float amountNum = 0;
		String[] words = this.amount.split("\\s");
		for (String w:words){
			try{
				amountNum = Float.parseFloat(w);
				break;
			}
			catch (NumberFormatException e){
				continue;
			}
		}
		return amountNum;
	}
	
	public float[] getNutrition(){
		return nutrition;
	}

	public float[] getNutritionPerServing(){
		float[] NPS = this.nutrition;
		float amount = this.getAmount();
		for(int i = 0; i < NPS.length; i++){
			NPS[i] = NPS[i] * amount;
		}
		return NPS;
	}

}