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

import com.example.bot.spring.Coupon;
import com.example.bot.spring.CouponDB;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = { CouponDBTester.class, Coupon.class, CouponDB.class, User.class })
public class CouponDBTester{
	@Autowired
	private CouponDB couponDB;

	@Autowired
	private User user;

	@Test
	public void testInsertCode() throws Exception {
		boolean thrown = false;
		boolean result = false;
		
		try {
			result = this.couponDB.insert_code("test","666666");
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo(true);
	}

	@Test
	public void testInsertCode2() throws Exception {
		boolean thrown = false;
		boolean result = false;
		
		try {
			result = this.couponDB.insert_code("test","777777");
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo(true);
	}

	
	@Test
	public void testDeleteCodeNotFound() throws Exception {
		boolean thrown = false;
		boolean result = false;
		
		try {
			result = this.couponDB.delete_code("99999999");
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo(false);
	}

	@Test
	public void testCheckCode() throws Exception {
		boolean thrown = false;
		String result = null;
		
		try {
			result = this.couponDB.check_code("123456");
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo("test-222");
	}

	@Test
	public void testCheckCodeNotFound() throws Exception {
		boolean thrown = false;
		String result = null;
		boolean x = false;
		
		try {
			result = this.couponDB.check_code("sjdhjhak");
			if(result==null)
				x=true;
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(x).isEqualTo(true);
	}

	@Test
	public void testCheckAvailable1() throws Exception {
		boolean thrown = false;
		boolean result = false;
		user.setUser("test","coupon","yes");
		
		try {
			result = this.couponDB.check_available("test");
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo(true);
	}

	@Test
	public void testCheckAvailable2() throws Exception {
		boolean thrown = false;
		boolean result = false;
		
		try {
			result = this.couponDB.check_available("test-333");
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo(false);
	}

	@Test
	public void testCheckAvailable3() throws Exception {
		boolean thrown = false;
		boolean result = false;
		
		try {
			result = this.couponDB.check_available("test-444");
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo(false);
	}

	@Test
	public void testCheckAvailable4() throws Exception {
		boolean thrown = false;
		boolean result = false;
		
		try {
			result = this.couponDB.check_available("test-555");
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo(false);
	}

	@Test
	public void testDeleteCode() throws Exception {
		boolean thrown = false;
		boolean result = false;
		
		try {
			result = this.couponDB.delete_code("666666");
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo(true);
	}

	@Test
	public void testDeleteID() throws Exception {
		boolean thrown = false;
		boolean result = false;
		
		try {
			result = this.couponDB.delete_id("test");
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo(true);
	}

	@Test
	public void testDeleteIDNotFound() throws Exception {
		boolean thrown = false;
		boolean result = false;
		
		try {
			result = this.couponDB.delete_id("dasjg");
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo(false);
	}

}


