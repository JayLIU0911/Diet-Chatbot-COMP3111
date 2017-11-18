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



@RunWith(SpringRunner.class)
@SpringBootTest(classes = { UserDBTester.class, UserDB.class, Food.class  })
public class UserDBTester {
	@Autowired
	private UserDB userDB;
	
	@Test
	public void testCheckUserlist() throws Exception {
		boolean thrown = false;
		boolean result = false;
		
		try {
			result = this.userDB.check_userlist("test-12345");
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo(true);
	}

	@Test
	public void testNotFound_userlist() throws Exception {
		boolean thrown = false;
		boolean result = true;

		try{
			result = this.userDB.check_userlist("test");
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo(false);
	}

	@Test
	public void testCreateUsertable() throws Exception {
		boolean thrown = false;

		try{
			this.userDB.create_usertable("test-54321");
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
	}

	@Test
	public void testInsertUser() throws Exception {
		boolean thrown = false;
		
		try{
			this.userDB.insert_user("test-54321");
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
	}

	@Test
	public void testDroptable() throws Exception {
		boolean thrown = false;
		
		try{
			this.userDB.drop_table("test-54321");
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
	}

	@Test
	public void testDroptableNotFound() throws Exception {
		boolean thrown = false;
		boolean result = false;
		
		try{
			result = this.userDB.drop_table("test-666666");
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo(false);
	}

	@Test
	public void testDeleteUser() throws Exception {
		boolean thrown = false;
		
		try{
			this.userDB.delete_user("test-54321");
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
	}

	@Test
	public void testDeleteUserNotFound() throws Exception {
		boolean thrown = false;
		boolean result = false;
		
		try{
			result = this.userDB.delete_user("test-6666666");
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo(false);
	}

	@Test
	public void testInsertRecord() throws Exception {
		boolean thrown = false;
		
		try{
			this.userDB.insert_record("test-54321","Pork","50");
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
	}


	@Test
	public void testInsertRecordNotFound() throws Exception {
		boolean thrown = false;
		boolean result = false;
		
		try{
			result = this.userDB.insert_record("test-666666666","Pork","50");
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo(false);
	}

	@Test
	public void testDeleteRecord() throws Exception {
		boolean thrown = false;
		boolean result = false;
		
		try{
			result = this.userDB.deleteRecord("test-54321","Pork");
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo(false);
	}

	@Test
	public void testDeleteRecordNotFound() throws Exception {
		boolean thrown = false;
		boolean result = false;
		
		try{
			result = this.userDB.deleteRecord("test-88888","Pork");
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo(false);
	}


	@Test
	public void testDBSearchRecord() throws Exception {
		boolean thrown = false;
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		ArrayList<String> x = new ArrayList<String>();
		ArrayList<String> y = new ArrayList<String>();
		ArrayList<String> z = new ArrayList<String>();
		boolean d = false;
		boolean a = false;
		boolean b = false;
		boolean c = false;

		x.add("10/11/2017");
		x.add("60");
		x.add("250.0");
		x.add("250.0");
		x.add("250.0");
		
		y.add("11/11/2017");
		y.add("60");
		y.add("200.0");
		y.add("200.0");
		y.add("200.0");
	
		z.add("12/11/2017");
		z.add("65");
		z.add("300.0");
		z.add("300.0");
		z.add("300.0");

		try{
			result = this.userDB.DBsearch_record("test");
			if(result.size()==3)
				a=true;
			if(result.contains(x))
				b=true;
			if(result.contains(y))
				c=true;
			if(result.contains(z))
				d=true;
			
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(a).isEqualTo(true);
		assertThat(b).isEqualTo(true);
		assertThat(c).isEqualTo(true);
		assertThat(d).isEqualTo(true);
	}

	@Test
	public void testDBSearchRecordNotFound() throws Exception{
		boolean thrown = false;
		boolean result = false;
		ArrayList<ArrayList<String>> x = new ArrayList<ArrayList<String>>();

		try{
			x = this.userDB.DBsearch_record("test2");
			if(x.isEmpty())
				result = true;
		} catch(Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo(true);
	}


	@Test
	public void testDBSearchDay() throws Exception {
		boolean thrown = false;
		ArrayList<String> result = new ArrayList<String>();
		ArrayList<String> x = new ArrayList<String>();
		boolean z = false;
		boolean a = false;
		boolean b = false;
		boolean c = false;

		try{
			result = this.userDB.DBsearch_day("test","10/11/2017");
			if(result.contains("10/11/2017"))
				a = true;
			if(result.contains("60"))
				b = true;
			if(result.contains("250.0"))
				c = true;
			if(result.size()==5)
				z=true;
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(a).isEqualTo(true);
		assertThat(b).isEqualTo(true);
		assertThat(c).isEqualTo(true);
		assertThat(z).isEqualTo(true);
	}

	@Test
	public void testDBSearchDayNotFoundDate() throws Exception{
		boolean thrown = false;
		boolean result = false;
		ArrayList<String> x = new ArrayList<String>();

		try{
			x = this.userDB.DBsearch_day("test","00/00/0000");
			if((x.get(0)).equals("00/00/0000") && (x.get(1))==null && (x.get(2)).equals("0.0"))
				result = true;
		} catch(Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo(true);
	}

	@Test
	public void testDBSearchDayNotFoundUser() throws Exception{
		boolean thrown = false;
		boolean result = false;
		ArrayList<String> x = new ArrayList<String>();

		try{
			x = this.userDB.DBsearch_day("test-66666666","00/00/0000");
			if(x.isEmpty())
				result = true;
		} catch(Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo(true);
	}

	@Test
	public void testSelectUserlist() throws Exception {
		boolean thrown = false;
		String result = null;
		
		try{
			result = this.userDB.select_userlist("test","target_weight");
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo("65");
	}


	@Test
	public void testSelectUserlistNotFound() throws Exception {
		boolean thrown = false;
		boolean result = false;
		
		try{
			String x = this.userDB.select_userlist("test-66666","target_weight");
			if(x==null)
				result = true;
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo(true);
	}

	@Test
	public void testUpdateUserlist() throws Exception {
		boolean thrown = false;
		
		try{
			this.userDB.insert_record("test-111","weight","50");
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
	}

	@Test
	public void testUpdateUserlistNotFound() throws Exception {
		boolean thrown = false;
		boolean result = false;
		
		try{
			result = this.userDB.insert_record("test-666666666","weight","50");
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo(false);
	}

}

