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
import com.example.bot.spring.User;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = { CouponTester.class, Coupon.class, CouponDB.class, User.class })
public class CouponTester{
	

	@Autowired
	private User user;

	@Autowired
	private CouponDB couponDB;

	@Test
	public void testFriendCall() throws Exception {
		boolean thrown = false;
		boolean result = false;
		String x = null;
		
		try {
			x = Coupon.getCoupon().friend_call("test");
			if(Integer.parseInt(x)<1000000 && Integer.parseInt(x)>100000)
				result = true;
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo(true);
	}

	@Test
	public void testCodeCall() throws Exception {
		boolean thrown = false;
		String result = null;

		user.setUser("test-222","coupon","yes");
		user.setUser("test-222","follow","yes");
		
		try {
			result = Coupon.getCoupon().code_call("test-222","123456");
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo("2");
	}

	@Test
	public void testCodeCall2() throws Exception {
		boolean thrown = false;
		String result = null;
		
		try {
			result = Coupon.getCoupon().code_call("test-333","test");
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo("1");
	}

	@Test
	public void testCodeCall3() throws Exception {
		boolean thrown = false;
		
		try {
			Coupon.getCoupon().code_call("test","000000");
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		
	}

	@Test
	public void testCodeCall4() throws Exception {
		boolean thrown = false;
		String result = null;
		user.setUser("test","coupon","yes");
		
		try {
			result = Coupon.getCoupon().code_call("test","123456");
			this.couponDB.insert_code("test-222","123456");
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo("test-222");
			
	}
}

