package com.example.bot.spring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

import com.google.common.io.ByteStreams;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.FollowEvent;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.MessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.spring.boot.annotation.LineBotMessages;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Calendar;
import java.util.*;

import com.example.bot.spring.User;
import com.example.bot.spring.UserDB;
import com.example.bot.spring.Food;
import com.example.bot.spring.FoodDB;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = { FoodDBTester.class, FoodDB.class })
public class FoodDBTester {
	@Autowired
	private FoodDB foodDB;
	
	// Search Ingredient from database
	// input String ingredient output outcome is found or notfound 
	@Test
	public void testSearchIngredient() throws Exception {
		boolean thrown = false;
		ArrayList<String> result = new ArrayList<String>();

		try{
			result = this.foodDB.DBSearchIngredient("pork");
		} catch (Exception e){
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(!result.isEmpty()).isEqualTo(true);
	}
    
    @Test
    public void testSearchIngredientNotFound()throws Exception{
    	boolean thrown = false;
    	boolean result1 = false;
    	ArrayList<String> result = new ArrayList<String>();
    	try{
    		result = this.foodDB.DBSearchIngredient("football");
    		if (result.get(1)==null) {
    			result1 = true;
    		}
    	} catch (Exception e) {
    		thrown = true;
    	}
    	assertThat(!thrown).isEqualTo(true);
    assertThat(result1).isEqualTo(true);
    }
    
    @Test
    public void testSearchIngredient3() throws Exception {
        boolean thrown = false;
        ArrayList<String> result = new ArrayList<String>();
        
        try{
            result = this.foodDB.DBSearchIngredient("Alcoholic");
        } catch (Exception e){
            thrown = true;
        }
        assertThat(!thrown).isEqualTo(true);
        assertThat(!result.isEmpty()).isEqualTo(true);
    }
    
    @Test
    public void testSearchIngredient4() throws Exception {
        boolean thrown = false;
        ArrayList<String> result = new ArrayList<String>();
        
        try{
            result = this.foodDB.DBSearchIngredient("powder");
        } catch (Exception e){
            thrown = true;
        }
        assertThat(!thrown).isEqualTo(true);
        assertThat(!result.isEmpty()).isEqualTo(true);
    }
    
    //DB delecteMenu delete with menu not found and delete with menu found
    @Test
    public void testDeleteMenuNotFound()throws Exception{
    	boolean thrown = false;
    	boolean result = false;
    	try{
    		result = this.foodDB.DBdeleteMenu("nobody");
    	} catch (Exception e){
    		thrown = true;
    	}
    	assertThat(!thrown).isEqualTo(true);
    	assertThat(result).isEqualTo(false);
    }
    
  @Test
  public void testDeleteMenuFound()throws Exception{
  	boolean thrown = false;
  	boolean result = false;
  	this.foodDB.DBInsertMenu("Menutest01","Food1","10");
  	try{
  		result = this.foodDB.DBdeleteMenu("Menutest01");
  	} catch (Exception e){
  		thrown = true;
  	}
  	assertThat(!thrown).isEqualTo(true);
  	assertThat(result).isEqualTo(true);
  }  
  
  // DBinsert menu; 
  @Test
  public void testInsertMenu()throws Exception{
  	boolean thrown = false;
  	boolean result = false;
  	try{
  		result = this.foodDB.DBInsertMenu("user1","food1","10");
  	} catch (Exception e){
  		thrown = true;
  	}
  	foodDB.DBdeleteMenu("user1");
  	assertThat(!thrown).isEqualTo(true);
  	assertThat(result).isEqualTo(true);
  }
    
  //DBgetMenu Menu found and not found;
  @Test
  public void testGetMenuFound()throws Exception{
  	boolean thrown = false;
  	ArrayList<String> result = new ArrayList<String>();
  	boolean result2 = false;
  	this.foodDB.DBInsertMenu("testmenu2","food2","20");
  	try{
  		result = this.foodDB.DBgetMenu(result,"testmenu2");
  		if(result.contains("food2")){
  			result2=true;
  		}
  	} catch (Exception e){
  		thrown = true;
  	}
  	foodDB.DBdeleteMenu("testmenu2");
  	assertThat(!thrown).isEqualTo(true);
  	assertThat(result2).isEqualTo(true);
  }

  @Test
  public void testGetMenuNotFound()throws Exception{
  	boolean thrown = false;
  	ArrayList<String> result = new ArrayList<String>();
  	boolean result2 = false;
  	try{
  		result = this.foodDB.DBgetMenu(result,"nothismenu");
  		if(result.isEmpty()){
  			result2= true;
  		}
  	} catch (Exception r){
  		thrown = true;
  	}
  	assertThat(!thrown).isEqualTo(true);
  	assertThat(result2).isEqualTo(true);
  }
  

  //getTips
	
  @Test
  public void testgetTipsFound()throws Exception{
  	boolean thrown = false;
  	String result = null;
  	boolean result2= false;
  	try{
  		result = this.foodDB.DBgetTip(1);
  		if(result.contains("Drink")) {
  			result2 = true;
  		}
  	}catch (Exception e){
  		thrown = true;
  	}
  	assertThat(!thrown).isEqualTo(true);
  	assertThat(result2).isEqualTo(true);
  }
    
  @Test
  public void testgetTipsNotFound()throws Exception{
  	boolean thrown = false;
  	String result = null;
  	try{
  		result = this.foodDB.DBgetTip(30);
  	}catch (Exception e){
  		thrown = true;
  	}
  	assertThat(!thrown).isEqualTo(true);
  	assertThat(result).isEqualTo("Keep healthy!");
  }

  @Test
  public void testDBexist()throws Exception{
    boolean thrown = false;
    boolean result = false;
    try{
      result = this.foodDB.DBexist("powder");
    }catch (Exception e){
      thrown = true;
    }
    assertThat(!thrown).isEqualTo(true);
    assertThat(result).isEqualTo(true);
  }
   
   @Test
  public void testDBNotexist()throws Exception{
    boolean thrown = false;
    boolean result = false;
    try{
      result = this.foodDB.DBexist("adshjkfhaj");
    }catch (Exception e){
      thrown = true;
    }
    assertThat(!thrown).isEqualTo(true);
    assertThat(result).isEqualTo(false);
  }
    
  
	
    
    
}
