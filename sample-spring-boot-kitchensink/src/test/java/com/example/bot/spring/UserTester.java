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


@RunWith(SpringRunner.class)
@SpringBootTest(classes = { UserTester.class, User.class, UserDB.class })
public class UserTester {
	@Autowired
	private User user;
	
	@Test
	public void testInsert() throws Exception {
		boolean thrown = false;
		boolean result = false;
		Calendar calendar = new GregorianCalendar(2017,10,03);
		Date coupon_date =  calendar.getTime();
		try {
			result = this.user.insert("test-111",coupon_date);
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo(true);
	}
	
	@Test
	public void testCheckFollowTimeYes() throws Exception {
		boolean thrown = false;
		boolean result = false;
		Calendar calendar = new GregorianCalendar(2017,10,03);
		Date coupon_date =  calendar.getTime();
		try {
			result = this.user.check_followtime("test-222",coupon_date);
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo(true);
	}

	@Test
	public void testCheckFollowTimeNo() throws Exception {
		boolean thrown = false;
		boolean result = false;
		Calendar calendar = new GregorianCalendar(2017,11,30);
		Date coupon_date = calendar.getTime();
		try {
			result = this.user.check_followtime("test-222",coupon_date);
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo(false);
	}

	@Test
	public void testDelete() throws Exception {
		boolean thrown = false;
		boolean result = false;

		try {
			result = this.user.delete("test-111");
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo(true);
	}

	@Test
	public void testUpdateRecord() throws Exception {
		boolean thrown = false;
		boolean result = false;
		
		try {
			result = this.user.updateRecord("test","fish");
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo(true);
	}

	@Test
	public void testDelete_Record() throws Exception {
		boolean thrown = false;
		boolean result = false;
		
		try {
			result = this.user.delete_record("test","fish");
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo(true);
	}

	@Test
	public void testdeleteRecordNotFound() throws Exception {
		boolean thrown = false;
		boolean result = false;
		
		try {
			result = this.user.delete_record("sdasddas","dasjhjkds");
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo(false);
	}

	
	@Test
	public void testGetUser() throws Exception {
		boolean thrown = false;
		String result = null;
		
		try {
			result = this.user.getUser("test","weight");
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo("70");
	}

	@Test
	public void testGetUserNotFound() throws Exception {
		boolean thrown = false;
		String result = null;
		boolean x = false;
		
		try {
			result = this.user.getUser("test-77777","weight");
			if(result==null)
				x=true;
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(x).isEqualTo(true);
	}
	
	@Test
	public void testSetUser() throws Exception {
		boolean thrown = false;
		boolean result = false;
		
		try {
			result = this.user.setUser("test-222","weight","75");
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo(true);
	}


	@Test
	public void testSetUserTargetDay() throws Exception {
		boolean thrown = false;
		boolean result = false;
		
		try {
			result = this.user.setUser("test-222","days_for_target","40");
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo(true);
	}

	@Test
	public void testSetDay() throws Exception {
		boolean thrown = false;
		boolean result = false;
		
		try {
			result = this.user.setDay("test-222","45");
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo(true);
	}

	@Test
	public void testCheckDay() throws Exception {
		boolean thrown = false;
		int result = 0;
		
		try {
			result = this.user.check_day("test-333");
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo(16);//update everyday!!!!
	}

	@Test
	public void testCheckGoal() throws Exception {
		boolean thrown = false;
		int result = 0;
		
		try {
			result = this.user.check_goal("test-222");
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo(4);
	}

	@Test
	public void testCheckGoal2() throws Exception {
		boolean thrown = false;
		int result = 0;
		
		try {
			result = this.user.check_goal("test-444");
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo(2);
	}

	@Test
	public void testGetDailyIntake() throws Exception {
		boolean thrown = false;
		float result = 0;
		
		try {
			result = this.user.getDailyIntake("test","10/11/2017");
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo(250);
	}

	@Test
	public void testGetIdealDailyIntake() throws Exception {
		boolean thrown = false;
		int result = 0;
		
		try {
			result = this.user.getIdealDailyIntake("test-333");
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo(1684);
	}

	@Test
	public void testGenerateSummary() throws Exception {
		boolean thrown = false;
		String result = null;
		String output = "Starting from 10/11/2017 , you have totally consumed: \n"+"Energy: 750kcal, \n" + "Sodium: 750mg, \n"+"Saturated Fatty Acids: 750g.";
		
		try {
			result = this.user.generateSummary("test");
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo(output);
	}


	@Test
	public void testGenerateSummaryNotFound() throws Exception {
		boolean thrown = false;
		String result = null;
		String output = "You have not input any data yet!";
		
		try {
			result = this.user.generateSummary("test2");
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo(output);
	}

	@Test
	public void testGenerateWeeklySummaryNotFound() throws Exception {
		boolean thrown = false;
		String result = null;
		String output = "You have not input any data yet!";
		
		try {
			result = this.user.generateWeeklySummary("test2");
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo(output);
	}

	@Test
	public void testGenerateWeeklySummary() throws Exception {
		boolean thrown = false;
		String result = null;
		boolean a = false;
		String output = null;
       
       	output = "12/11/2017: \n"+"Energy: 300.0kcal \n"+"Weight: 65kg\n";
        
		
		try {
			result = this.user.generateWeeklySummary("test");
			if(result.contains(output))
				a = true;
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(a).isEqualTo(true);
	}


}

