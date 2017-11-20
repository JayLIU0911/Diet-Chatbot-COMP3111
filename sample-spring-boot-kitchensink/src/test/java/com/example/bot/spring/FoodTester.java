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
import java.net.*;

import com.example.bot.spring.User;
import com.example.bot.spring.UserDB;



@RunWith(SpringRunner.class)
@SpringBootTest(classes = { FoodTester.class, Food.class, FoodDB.class })
public class FoodTester {
	@Autowired
	private Food food;
	
	@Test
	public void testSearchFoodFound1()throws Exception{
		boolean thrown = false;
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		try{
			result = food.searchFood("kakuboucaabrea Chili chicken on rice kakuboucaabrea");
		} catch (Exception e){
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result.get(0).get(0)==null).isEqualTo(false);
	}

		@Test
	public void testSearchFoodFound2()throws Exception{
		boolean thrown = false;
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		try{
			result = food.searchFood("Chili chicken on rice kakuboucaabrea rice");
		} catch (Exception e){
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result.get(0).get(0)==null).isEqualTo(false);
	}

	@Test
	public void testSearchFoodNotFound()throws Exception{
		boolean thrown = false;
	//	boolean result = false;
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		try{
			result = food.searchFood("thisissomethingmakenosense thisissomethingmakenosense");
			
		} catch (Exception e){
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result==null).isEqualTo(true);
	}

	@Test
	public void testGetQualityFound()throws Exception{
		boolean thrown = false;
		float[]quality = new float[3];
		try{
			quality = food.getQuality("Chili chicken on rice");
		} catch (Exception e){
			thrown = true;
		}
		assertThat(quality == null).isEqualTo(false);
		assertThat(!thrown).isEqualTo(true);
	}

	@Test
	public void testGetQualityNotFound()throws Exception{
		boolean thrown = false;
		float[]quality = new float[3];
		try{
			quality = food.getQuality("Thisissomethingmakenosense");
		} catch (Exception e){
			thrown = true;
		}
		assertThat(quality == null).isEqualTo(false);
		assertThat(!thrown).isEqualTo(true);
		assertThat(quality[0] == 0).isEqualTo(true);
	}

	@Test
	public void testCreateMenuinTXT()throws Exception{
		boolean thrown = false;
		boolean result = false;
		try{
			result = food.createMenuText("MenutxtTest","This is my menu 20");
		} catch (Exception e){
			thrown = true;
		}
		assertThat(result).isEqualTo(true);
		assertThat(!thrown).isEqualTo(true);
	}

	@Test
	public void testCreateMenuinTXTContainGang()throws Exception{
		boolean thrown = false;
		boolean result = false;
		try{
			result = food.createMenuText("MenutxtTest","This -- menu");
		} catch (Exception e){
			thrown = true;
		}
		assertThat(result).isEqualTo(true);
		assertThat(!thrown).isEqualTo(true);
	}

	@Test
	public void testCreateMenuinTXTContainDian()throws Exception{
		boolean thrown = false;
		boolean result = false;
		try{
			result = food.createMenuText("MenutxtTest","This .. menu");
		} catch (Exception e){
			thrown = true;
		}
		assertThat(result).isEqualTo(true);
		assertThat(!thrown).isEqualTo(true);
	}
	
    @Test
    public void testCreateMenuinTXTContainOnlyOne()throws Exception{
        boolean thrown = false;
        boolean result = false;
        try{
            result = food.createMenuText("MenutxttTest","This 20");
        } catch (Exception e){
            thrown = true;
        }
        assertThat(result).isEqualTo(true);
        assertThat(!thrown).isEqualTo(true);
    }
	//@Test
	//public void testCreateMenuinJSON

	//@Test
	//public void testCreateMenuinJPEG
	
	@Test
	public void testfindOptimal1food() throws Exception{
		boolean thrown = false;
		String result = null;
		ArrayList<String> food = new ArrayList<String>();
		food.add("Apple");
		try{
			result = this.food.findOptimal("idealintaketest",food);
		} catch (Exception e){
			thrown = true;
		}
		assertThat(result.contains("Apple")).isEqualTo(true);
		assertThat(!thrown).isEqualTo(true);
	}

	@Test
	public void testfindOptimalMorefood()throws Exception{
		boolean thrown = false;
		String result = null;
		ArrayList<String> foods = new ArrayList<String>();
		foods.add("water");
		foods.add("Banana");
		foods.add("Apple");
		foods.add("Beef pork beef beef beef");
		try{
			result = this.food.findOptimal("idealintaketest",foods);
		} catch (Exception e){
			thrown = true;
		}
		assertThat(!result.contains("you have only input one food")).isEqualTo(true);
		assertThat(!thrown).isEqualTo(true);
	}

	@Test
	public void testCheckNutrition() throws Exception{
		boolean thrown = false;
		String result = null;
		try{
			result = this.food.checkNutrition("Banana");
		} catch (Exception e){
			thrown = true;
		}
		assertThat(result==null).isEqualTo(false);
		assertThat(!thrown).isEqualTo(true);
	}
    
    @Test
    public void testCheckNutritionNotFound() throws Exception{
        boolean thrown = false;
        String result = null;
        try{
            result = this.food.checkNutrition("Blablaxiaomoxian");
        } catch (Exception e){
            thrown = true;
        }
        boolean flag = (result==null);
        assertThat(flag).isEqualTo(false);
        assertThat(!thrown).isEqualTo(true);
    }

	@Test
	public void testGetMenuInfo() throws Exception{
		String result = null;
		boolean thrown = false;
		boolean a = false;
		try{
			result = this.food.getMenuInfo("foodtester");
			if(result.contains("is"))
				a = true;
		} catch (Exception e){
			thrown = true;
		}
		assertThat(a).isEqualTo(true);
		assertThat(!thrown).isEqualTo(true);
	}
    
    @Test
    public void testGetMenuInfoNotFound() throws Exception{
        String result = null;
        boolean thrown = false;
        try{
            result = this.food.getMenuInfo("iamaprogrammerlaim");
        } catch (Exception e){
            thrown = true;
        }
        assertThat(result.contains("Food")).isEqualTo(false);
        assertThat(!thrown).isEqualTo(true);
    }
	
	@Test
	public void testFoodinMenuFound() throws Exception{
		boolean thrown = false;
		boolean result = false;
		try{
			result = this.food.FoodinMenu("foodtester","Awesome Food");
		} catch (Exception e){
			thrown = true;
		}
		assertThat(result).isEqualTo(true);
		assertThat(!thrown).isEqualTo(true);
	}

	@Test
	public void testFoodinMenuNotFound() throws Exception{
		boolean thrown = false;
		boolean result = false;
		try{
			result = this.food.FoodinMenu("foodtester","Banana");
		} catch (Exception e){
			thrown = true;
		}
		assertThat(result).isEqualTo(false);
		assertThat(!thrown).isEqualTo(true);
	}
		
	@Test
	public void testWarningforSelectionNotRecommending() throws Exception{
		boolean thrown = false;
		String result = null;
		try{
			result = this.food.WarningforFoodSelection("missskinny","Chicken Steak Pork");
		} catch (Exception e){
			thrown = true;
		}
		assertThat(result.contains("Your selection is greater than your recommended intake, we suggest you to change your selection")).isEqualTo(true);
		assertThat(!thrown).isEqualTo(true);
	}
	
	@Test
	public void testWarningforSelectionGreater() throws Exception{
		boolean thrown = false;
		String result = null;
		try{
			result = this.food.WarningforFoodSelection("idealintaketest","beef beef");
		} catch (Exception e){
			thrown = true;
		}
		assertThat(result.contains("It's okay to have this food")).isEqualTo(true);
		assertThat(!thrown).isEqualTo(true);
	}
	
	@Test
	public void testWarningforSelectionLess() throws Exception{
		boolean thrown = false;
		String result = null;
		try{
			result = this.food.WarningforFoodSelection("mrmuscle","sugar");
		} catch (Exception e){
			thrown = true;
		}
		assertThat(result.contains("but its energy is less")).isEqualTo(true);
		assertThat(!thrown).isEqualTo(true);
	}

	@Test
	public void testWarningforSelectionNotFound() throws Exception{
		boolean thrown = false;
		String result = null;
		try{
			result = this.food.WarningforFoodSelection("mrmuscle","sadhjkhkad");
		} catch (Exception e){
			thrown = true;
		}
		assertThat(result.contains("database")).isEqualTo(true);
		assertThat(!thrown).isEqualTo(true);
	}
	
	
	@Test
	public void testFoodPortionAquarter() throws Exception{
		boolean thrown = false;
		String result = null;
		try{
			result = this.food.foodPortion("idealintaketest","pizza pizza pizza pizza pizza pizza pizza pizza pizza pizza pizza pizza pizza");
		} catch (Exception e){
			thrown = true;
		}
		assertThat(result.contains("quarter")).isEqualTo(true);
		assertThat(!thrown).isEqualTo(true);
	}
	@Test
	public void testFoodPortionAhalf() throws Exception{
		boolean thrown = false;
		String result = null;
		try{
			result = this.food.foodPortion("idealintaketest","pizza pizza pizza pizza pizza pizza pizza pizza");
		} catch (Exception e){
			thrown = true;
		}
		assertThat(result.contains("half")).isEqualTo(true);
		assertThat(!thrown).isEqualTo(true);
	}
	
	
	@Test
	public void testFoodPortionThreequarter() throws Exception{
		boolean thrown = false;
		String result = null;
		try{
			result = this.food.foodPortion("idealintaketest","pizza pizza pizza pizza");
		} catch (Exception e){
			thrown = true;
		}
		assertThat(result.contains("quarters")).isEqualTo(true);
		assertThat(!thrown).isEqualTo(true);
	}
	
	@Test
	public void testFoodPortionAll() throws Exception{
		boolean thrown = false;
		String result = null;
		try{
			result = this.food.foodPortion("idealintaketest","beef beef beef");
		} catch (Exception e){
			thrown = true;
		}
		assertThat(result.contains("all")).isEqualTo(true);
		assertThat(!thrown).isEqualTo(true);
	}
	
	
	@Test
	public void testGetTip() throws Exception{
		boolean thrown = false;
		String result = null;
		try {
			result = this.food.getTip();
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(result == null).isEqualTo(false);
		assertThat(!thrown).isEqualTo(true);
	}
	
	/*
	@Test
	public void testCheckAppropriate() throws Exception{
		
	}
	*/

	@Test
 	public void testCheckAppropriateFine1() throws Exception{
  	boolean thrown = false;
  	String result = null;
  	try{
   		result = this.food.checkAppropriate("idealintaketest","beef");
  	} catch (Exception e){
   		thrown = true;
  	}
  	assertThat(result.contains("fine")).isEqualTo(true);
  	assertThat(!thrown).isEqualTo(true);
  
 	}

 	@Test
 	public void testCheckAppropriateNotFine() throws Exception{
  	boolean thrown = false;
  	String result = null;
  	try{
   		result = this.food.checkAppropriate("idealintaketest","pizza pizza pizza pizza pizza pizza pizza pizza pizza pizza");
  	} catch (Exception e){
   		thrown = true;
  	}
  	assertThat(result.contains("not appropriate")).isEqualTo(true);
  	assertThat(!thrown).isEqualTo(true);
 	}

     @Test
     public void testCreateMenuJsonBad() throws Exception{
     boolean thrown = false;
     try{
         food.createMenuJson("Hero", "NotAValidUrl");
     }catch(Exception e){
         thrown = true;
     }
     assertThat(!thrown).isEqualTo(true);
   }
   @Test
   public void testCreateMenuJsonGood() throws Exception{
     boolean thrown = false;
       String test = "http://hgaoab.student.ust.hk/materials/test.json";
     try{
         food.createMenuJson("Hero", test);
     }catch(Exception e){
         thrown = true;
     }
     assertThat(!thrown).isEqualTo(true);
   }

  @Test
  public void testCreateMenuImageBad() throws Exception{
    boolean thrown = false;
    try{
        URL link = new URL ("https://www.wikihow.com/i-Version-tures-Step-2-Version-4.jpg");
        food.createMenuImage("Hero",link.toURI());
    }catch(Exception e){
        thrown = true;
    }
    assertThat(!thrown).isEqualTo(true);
  }

  @Test
  public void testCreateMenuImageGood() throws Exception{
    boolean thrown = false;
    try{
        URL link = new URL ("https://forum.bubble.is/uploads/default/original/3X/3/2/3203781e3319501fd56867f99daa8c6ed0461903.png");
        food.createMenuImage("Hero", link.toURI());
    }catch(Exception e){
        thrown = true;
    }
    assertThat(!thrown).isEqualTo(true);
  }
}

