package com.example.bot.spring;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.event.source.GroupSource;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.response.BotApiResponse;

//
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.postback.PostbackContent;
import java.lang.System;
import com.linecorp.bot.model.event.source.UserSource;
import java.net.URI;

import java.util.*;

// import com.example.bot.spring.SQLDatabaseEngine;
import com.example.bot.spring.User;
import com.example.bot.spring.UserDB;
import com.example.bot.spring.Food;
import com.example.bot.spring.FoodDB;
import com.example.bot.spring.Coupon;
import com.example.bot.spring.CouponDB;
import com.example.bot.spring.KitchenSinkController;

import com.linecorp.bot.model.Multicast;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.profile.UserProfileResponse;
import com.linecorp.bot.model.response.BotApiResponse;
import com.linecorp.bot.model.event.FollowEvent;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.NonNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;

import java.util.*;
import java.util.Calendar;

import java.util.Date;
import com.linecorp.bot.model.PushMessage;
import java.util.*;
import java.text.*;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import static org.mockito.Mockito.atLeast;


// @RunWith(MockitoJUnitRunner.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest({User.class, Food.class, Coupon.class, Date.class, SimpleDateFormat.class})
public class KitchenSinkControllerTest {
    @Mock
    private LineMessagingClient lineMessagingClient;

  	private String[] setOrder = {"name", "status", "age", "weight", "target_weight", "height", "days_for_target"};
    @Mock
  	private Calendar calendar;
    @Mock
  	private Date coupon_date;
    @InjectMocks
    private KitchenSinkController underTest;

    private void setup() {
      PowerMockito.mockStatic(User.class);
      PowerMockito.mockStatic(Food.class);
      PowerMockito.mockStatic(Coupon.class);

      calendar = new GregorianCalendar(2017,10,11);
      coupon_date = calendar.getTime();
    }

    @Test
    public void handleTextMessageEventTest() throws Exception {
      setup();
      handleTextMessageEventTestNullState();

      handleTextMessageEventTestValidAge();
      handleTextMessageEventTestValidStatus();
      handleTextMessageEventTestValidUpdate();

      handleTextMessageEventTestSetName();
      handleTextMessageEventTestSetStatus();
      handleTextMessageEventTestSetAge();
      handleTextMessageEventTestSetWeight();
      handleTextMessageEventTestSetTargetWeight();
      handleTextMessageEventTestSetHeight();
      handleTextMessageEventTestSetDaysForTarget();
      // handleTextMessageEventTestSetDefault();
      handleTextMessageEventTestSetWrong();

      handleTextMessageEventTestUpdateWeightT();
      handleTextMessageEventTestUpdateWeightF();
      handleTextMessageEventTestUpdateFoodT();
      handleTextMessageEventTestUpdateFoodF();
      handleTextMessageEventTestUpdateTargetWeightT();
      handleTextMessageEventTestUpdateTargetWeightF();
      handleTextMessageEventTestUpdateDaysForTargetT();
      handleTextMessageEventTestUpdateDaysForTargetF();
      handleTextMessageEventTestUpdateDefault();

      handleTextMessageEventTestMenuTextT();
      handleTextMessageEventTestMenuTextF();
      handleTextMessageEventTestMenuJsonT();
      handleTextMessageEventTestMenuJsonF();
      handleTextMessageEventTestMenuDefault();
      handleTextMessageEventTestMenuTextTF();
      handleTextMessageEventTestMenuJsonTF();

      handleTextMessageEventTestWarning();

      handleTextMessageEventTestCheckNutrition();
      handleTextMessageEventTestCheckAppropriate();
      handleTextMessageEventTestCheckDefault();

      handleTextMessageEventTestDefault();
    }
    private void handleTextMessageEventTestNullState() throws Exception {
      MessageEvent event = new MessageEvent<TextMessageContent>(
              "replyToken",
              new UserSource("test"),
              new TextMessageContent("test", "name"),
              Instant.now()
      );
      when(lineMessagingClient.replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("Get user state failed: test"))
      ))).thenReturn(CompletableFuture.completedFuture(
              new BotApiResponse("ok", Collections.emptyList())
      ));
      when(User.getUser("test", "state")).thenReturn(null);
      underTest.handleTextMessageEvent(event);
      verify(lineMessagingClient).replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("Get user state failed: test"))
      ));
    }
    private void handleTextMessageEventTestValidAge() throws Exception {
      MessageEvent event = new MessageEvent<TextMessageContent>(
              "replyToken",
              new UserSource("testUserSetAge"),
              new TextMessageContent("testUserSetAge", "string"),
              Instant.now()
      );
      when(lineMessagingClient.replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("Please follow the input instruction (•ૢ⚈͒⌄⚈͒•ૢ)"))
      ))).thenReturn(CompletableFuture.completedFuture(
              new BotApiResponse("ok", Collections.emptyList())
      ));
      when(User.getUser("testUserSetAge", "state")).thenReturn("set age");
      underTest.handleTextMessageEvent(event);
      verify(lineMessagingClient).replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("Please follow the input instruction (•ૢ⚈͒⌄⚈͒•ૢ)"))
      ));
    }
    private void handleTextMessageEventTestValidUpdate() throws Exception {
      MessageEvent event = new MessageEvent<TextMessageContent>(
              "replyToken",
              new UserSource("testUserUpdateWeight"),
              new TextMessageContent("testUserUpdateWeight", "string"),
              Instant.now()
      );
      when(lineMessagingClient.replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("Please input a number (๑•́₃•̀๑)"))
      ))).thenReturn(CompletableFuture.completedFuture(
              new BotApiResponse("ok", Collections.emptyList())
      ));
      when(User.getUser("testUserUpdateWeight", "state")).thenReturn("update weight");
      underTest.handleTextMessageEvent(event);
      verify(lineMessagingClient).replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("Please input a number (๑•́₃•̀๑)"))
      ));
    }
    private void handleTextMessageEventTestValidStatus() throws Exception {
      MessageEvent event = new MessageEvent<TextMessageContent>(
              "replyToken",
              new UserSource("testUserSetStatus"),
              new TextMessageContent("testUserSetStatus", "neutral"),
              Instant.now()
      );
      when(lineMessagingClient.replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("Please follow the input instruction (•ૢ⚈͒⌄⚈͒•ૢ)"))
      ))).thenReturn(CompletableFuture.completedFuture(
              new BotApiResponse("ok", Collections.emptyList())
      ));
      when(User.getUser("testUserSetStatus", "state")).thenReturn("set status");
      underTest.handleTextMessageEvent(event);
      verify(lineMessagingClient, atLeast(2)).replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("Please follow the input instruction (•ૢ⚈͒⌄⚈͒•ૢ)"))
      ));
    }
    private void handleTextMessageEventTestSetName() throws Exception {
      MessageEvent event = new MessageEvent<TextMessageContent>(
              "replyToken",
              new UserSource("testUserSetName"),
              new TextMessageContent("testUserSetName", "name"),
              Instant.now()
      );
      when(lineMessagingClient.replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("What is your gender(male/female)?"))
      ))).thenReturn(CompletableFuture.completedFuture(
              new BotApiResponse("ok", Collections.emptyList())
      ));
      when(User.getUser("testUserSetName", "state")).thenReturn("set name");
      when(User.setUser("testUserSetName", "name", "name")).thenReturn(true);
      underTest.handleTextMessageEvent(event);
      verify(lineMessagingClient).replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("What is your gender(male/female)?"))
      ));
    }
    private void handleTextMessageEventTestSetStatus() throws Exception {
      MessageEvent event = new MessageEvent<TextMessageContent>(
              "replyToken",
              new UserSource("testUserSetStatus"),
              new TextMessageContent("testUserSetStatus", "male"),
              Instant.now()
      );
      when(lineMessagingClient.replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("How old are you? (Please input a number)"))
      ))).thenReturn(CompletableFuture.completedFuture(
              new BotApiResponse("ok", Collections.emptyList())
      ));
      when(User.getUser("testUserSetStatus", "state")).thenReturn("set status");
      when(User.setUser("testUserSetStatus", "status", "male")).thenReturn(true);
      underTest.handleTextMessageEvent(event);
      verify(lineMessagingClient).replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("How old are you? (Please input a number)"))
      ));
    }
    private void handleTextMessageEventTestSetAge() throws Exception {
      MessageEvent event = new MessageEvent<TextMessageContent>(
              "replyToken",
              new UserSource("testUserSetAge"),
              new TextMessageContent("testUserSetAge", "20"),
              Instant.now()
      );
      when(lineMessagingClient.replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("What is your weight(kg)? (Please input a number)"))
      ))).thenReturn(CompletableFuture.completedFuture(
              new BotApiResponse("ok", Collections.emptyList())
      ));
      when(User.getUser("testUserSetAge", "state")).thenReturn("set age");
      when(User.setUser("testUserSetAge", "age", "20")).thenReturn(true);
      underTest.handleTextMessageEvent(event);
      verify(lineMessagingClient).replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("What is your weight(kg)? (Please input a number)"))
      ));
    }
    private void handleTextMessageEventTestSetWeight() throws Exception {
      MessageEvent event = new MessageEvent<TextMessageContent>(
              "replyToken",
              new UserSource("testUserSetWeight"),
              new TextMessageContent("testUserSetWeight", "70"),
              Instant.now()
      );
      when(lineMessagingClient.replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("What is your target weight(kg)? (Please input a number)"))
      ))).thenReturn(CompletableFuture.completedFuture(
              new BotApiResponse("ok", Collections.emptyList())
      ));
      when(User.getUser("testUserSetWeight", "state")).thenReturn("set weight");
      when(User.setUser("testUserSetWeight", "weight", "70")).thenReturn(true);
      underTest.handleTextMessageEvent(event);
      verify(lineMessagingClient).replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("What is your target weight(kg)? (Please input a number)"))
      ));
    }
    private void handleTextMessageEventTestSetTargetWeight() throws Exception {
      MessageEvent event = new MessageEvent<TextMessageContent>(
              "replyToken",
              new UserSource("testUserSetTargetWeight"),
              new TextMessageContent("testUserSetTargetWeight", "50"),
              Instant.now()
      );
      when(lineMessagingClient.replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("What is your height(cm)? (Please input a number)"))
      ))).thenReturn(CompletableFuture.completedFuture(
              new BotApiResponse("ok", Collections.emptyList())
      ));
      when(User.getUser("testUserSetTargetWeight", "state")).thenReturn("set target_weight");
      when(User.setUser("testUserSetTargetWeight", "target_weight", "50")).thenReturn(true);
      underTest.handleTextMessageEvent(event);
      verify(lineMessagingClient).replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("What is your height(cm)? (Please input a number)"))
      ));
    }
    private void handleTextMessageEventTestSetHeight() throws Exception {
      MessageEvent event = new MessageEvent<TextMessageContent>(
              "replyToken",
              new UserSource("testUserSetHeight"),
              new TextMessageContent("testUserSetHeight", "180"),
              Instant.now()
      );
      when(lineMessagingClient.replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("How long do you plan to achieve your goal? (Please input a day number)"))
      ))).thenReturn(CompletableFuture.completedFuture(
              new BotApiResponse("ok", Collections.emptyList())
      ));
      when(User.getUser("testUserSetHeight", "state")).thenReturn("set height");
      when(User.setUser("testUserSetHeight", "height", "180")).thenReturn(true);
      underTest.handleTextMessageEvent(event);
      verify(lineMessagingClient).replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("How long do you plan to achieve your goal? (Please input a day number)"))
      ));
    }
    private void handleTextMessageEventTestSetDaysForTarget() throws Exception {
      MessageEvent event = new MessageEvent<TextMessageContent>(
              "replyToken",
              new UserSource("testUserSetDaysForTarget"),
              new TextMessageContent("testUserSetDaysForTarget", "30"),
              Instant.now()
      );
      when(lineMessagingClient.replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("Great! Nice to meet you! You can input anything at any time to wake me up ✧⁺⸜(●˙▾˙●)⸝⁺✧"))
      ))).thenReturn(CompletableFuture.completedFuture(
              new BotApiResponse("ok", Collections.emptyList())
      ));
      when(User.getUser("testUserSetDaysForTarget", "state")).thenReturn("set days_for_target");
      when(User.setUser("testUserSetDaysForTarget", "days_for_target", "30")).thenReturn(true);
      underTest.handleTextMessageEvent(event);
      verify(lineMessagingClient).replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("Great! Nice to meet you! You can input anything at any time to wake me up ✧⁺⸜(●˙▾˙●)⸝⁺✧"))
      ));
    }
    private void handleTextMessageEventTestSetDefault() throws Exception {
      MessageEvent event = new MessageEvent<TextMessageContent>(
              "replyToken",
              new UserSource("testUserSetDefault"),
              new TextMessageContent("testUserSetDefault", "string"),
              Instant.now()
      );
      when(lineMessagingClient.replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("Wrong state in handleSetState(): set default"))
      ))).thenReturn(CompletableFuture.completedFuture(
              new BotApiResponse("ok", Collections.emptyList())
      ));
      when(User.getUser("testUserSetDefault", "state")).thenReturn("set default");
      when(User.setUser("testUserSetDefault", "default", "string")).thenReturn(true);
      underTest.handleTextMessageEvent(event);
      verify(lineMessagingClient).replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("Wrong state in handleSetState(): set default"))
      ));
    }
    private void handleTextMessageEventTestSetWrong() throws Exception {
      MessageEvent event = new MessageEvent<TextMessageContent>(
              "replyToken",
              new UserSource("testUserSetWrong"),
              new TextMessageContent("testUserSetWrong", "string"),
              Instant.now()
      );
      when(lineMessagingClient.replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("Set wrong failed. Please try to input again :("))
      ))).thenReturn(CompletableFuture.completedFuture(
              new BotApiResponse("ok", Collections.emptyList())
      ));
      when(User.getUser("testUserSetWrong", "state")).thenReturn("set wrong");
      when(User.setUser("testUserSetWrong", "wrong", "string")).thenReturn(false);
      underTest.handleTextMessageEvent(event);
      verify(lineMessagingClient, atLeast(1)).replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("Set wrong failed. Please try to input again :("))
      ));
    }

    private void handleTextMessageEventTestUpdateWeightT() throws Exception {
      MessageEvent event = new MessageEvent<TextMessageContent>(
              "replyToken",
              new UserSource("testUserUpdateWeight"),
              new TextMessageContent("testUserUpdateWeight", "70"),
              Instant.now()
      );
      when(lineMessagingClient.replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("Update weight succeeded"))
      ))).thenReturn(CompletableFuture.completedFuture(
              new BotApiResponse("ok", Collections.emptyList())
      ));
      when(User.getUser("testUserUpdateWeight", "state")).thenReturn("update weight");
      when(User.setUser("testUserUpdateWeight", "weight", "70")).thenReturn(true);
      // when(User.setUser("testUserUpdateWeight", "state", "0")).thenReturn(true);
      // when(User.check_goal("testUserUpdateWeight")).thenReturn(1);
      underTest.handleTextMessageEvent(event);
      verify(lineMessagingClient).replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("Update weight succeeded"))
      ));
    }
    private void handleTextMessageEventTestUpdateWeightF() throws Exception {
      MessageEvent event = new MessageEvent<TextMessageContent>(
              "replyToken",
              new UserSource("testUserUpdateWeight"),
              new TextMessageContent("testUserUpdateWeight", "70"),
              Instant.now()
      );
      when(lineMessagingClient.replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("Update weight failed"))
      ))).thenReturn(CompletableFuture.completedFuture(
              new BotApiResponse("ok", Collections.emptyList())
      ));
      when(User.getUser("testUserUpdateWeight", "state")).thenReturn("update weight");
      when(User.setUser("testUserUpdateWeight", "weight", "70")).thenReturn(false);
      // when(User.setUser("testUserUpdateWeight", "state", "0")).thenReturn(true);
      // when(User.check_goal("testUserUpdateWeight")).thenReturn(2);
      underTest.handleTextMessageEvent(event);
      verify(lineMessagingClient).replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("Update weight failed"))
      ));
    }
    private void handleTextMessageEventTestUpdateFoodT() throws Exception {
      MessageEvent event = new MessageEvent<TextMessageContent>(
              "replyToken",
              new UserSource("testUserUpdateFood"),
              new TextMessageContent("testUserUpdateFood", "food"),
              Instant.now()
      );
      when(lineMessagingClient.replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("Update food succeeded"))
      ))).thenReturn(CompletableFuture.completedFuture(
              new BotApiResponse("ok", Collections.emptyList())
      ));
      when(User.getUser("testUserUpdateFood", "state")).thenReturn("update food");
      when(User.updateRecord("testUserUpdateFood", "food")).thenReturn(true);
      underTest.handleTextMessageEvent(event);
      verify(lineMessagingClient).replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("Update food succeeded"))
      ));
    }
    private void handleTextMessageEventTestUpdateFoodF() throws Exception {
      MessageEvent event = new MessageEvent<TextMessageContent>(
              "replyToken",
              new UserSource("testUserUpdateFood"),
              new TextMessageContent("testUserUpdateFood", "food"),
              Instant.now()
      );
      when(lineMessagingClient.replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("Update food failed"))
      ))).thenReturn(CompletableFuture.completedFuture(
              new BotApiResponse("ok", Collections.emptyList())
      ));
      when(User.getUser("testUserUpdateFood", "state")).thenReturn("update food");
      when(User.updateRecord("testUserUpdateFood", "food")).thenReturn(false);
      underTest.handleTextMessageEvent(event);
      verify(lineMessagingClient).replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("Update food failed"))
      ));
    }
    private void handleTextMessageEventTestUpdateTargetWeightT() throws Exception {
      MessageEvent event = new MessageEvent<TextMessageContent>(
              "replyToken",
              new UserSource("testUserUpdateTargetWeight"),
              new TextMessageContent("testUserUpdateTargetWeight", "50"),
              Instant.now()
      );
      when(lineMessagingClient.replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("How long do you plan to achieve your new goal? (Please input a day number)"))
      ))).thenReturn(CompletableFuture.completedFuture(
              new BotApiResponse("ok", Collections.emptyList())
      ));
      when(User.getUser("testUserUpdateTargetWeight", "state")).thenReturn("update target_weight");
      when(User.setUser("testUserUpdateTargetWeight", "target_weight", "50")).thenReturn(true);
      underTest.handleTextMessageEvent(event);
      verify(lineMessagingClient).replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("How long do you plan to achieve your new goal? (Please input a day number)"))
      ));
    }
    private void handleTextMessageEventTestUpdateTargetWeightF() throws Exception {
      MessageEvent event = new MessageEvent<TextMessageContent>(
              "replyToken",
              new UserSource("testUserUpdateTargetWeight"),
              new TextMessageContent("testUserUpdateTargetWeight", "50"),
              Instant.now()
      );
      when(lineMessagingClient.replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("Update target_weight failed"))
      ))).thenReturn(CompletableFuture.completedFuture(
              new BotApiResponse("ok", Collections.emptyList())
      ));
      when(User.getUser("testUserUpdateTargetWeight", "state")).thenReturn("update target_weight");
      when(User.setUser("testUserUpdateTargetWeight", "target_weight", "50")).thenReturn(false);
      underTest.handleTextMessageEvent(event);
      verify(lineMessagingClient).replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("Update target_weight failed"))
      ));
    }
    private void handleTextMessageEventTestUpdateDaysForTargetT() throws Exception {
      MessageEvent event = new MessageEvent<TextMessageContent>(
              "replyToken",
              new UserSource("testUserUpdateDaysForTarget"),
              new TextMessageContent("testUserUpdateDaysForTarget", "30"),
              Instant.now()
      );
      when(lineMessagingClient.replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("Update goal succeeded"))
      ))).thenReturn(CompletableFuture.completedFuture(
              new BotApiResponse("ok", Collections.emptyList())
      ));
      when(User.getUser("testUserUpdateDaysForTarget", "state")).thenReturn("update days_for_target");
      when(User.setUser("testUserUpdateDaysForTarget", "days_for_target", "30")).thenReturn(true);
      underTest.handleTextMessageEvent(event);
      verify(lineMessagingClient).replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("Update goal succeeded"))
      ));
    }
    private void handleTextMessageEventTestUpdateDaysForTargetF() throws Exception {
      MessageEvent event = new MessageEvent<TextMessageContent>(
              "replyToken",
              new UserSource("testUserUpdateDaysForTarget"),
              new TextMessageContent("testUserUpdateDaysForTarget", "30"),
              Instant.now()
      );
      when(lineMessagingClient.replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("Update days_for_target failed"))
      ))).thenReturn(CompletableFuture.completedFuture(
              new BotApiResponse("ok", Collections.emptyList())
      ));
      when(User.getUser("testUserUpdateDaysForTarget", "state")).thenReturn("update days_for_target");
      when(User.setUser("testUserUpdateDaysForTarget", "days_for_target", "30")).thenReturn(false);
      underTest.handleTextMessageEvent(event);
      verify(lineMessagingClient).replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("Update days_for_target failed"))
      ));
    }
    private void handleTextMessageEventTestUpdateDefault() throws Exception {
      MessageEvent event = new MessageEvent<TextMessageContent>(
              "replyToken",
              new UserSource("testUserUpdateDefault"),
              new TextMessageContent("testUserUpdateDefault", "string"),
              Instant.now()
      );
      when(lineMessagingClient.replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("Wrong state in handleUpdateState(): update default"))
      ))).thenReturn(CompletableFuture.completedFuture(
              new BotApiResponse("ok", Collections.emptyList())
      ));
      when(User.getUser("testUserUpdateDefault", "state")).thenReturn("update default");
      underTest.handleTextMessageEvent(event);
      verify(lineMessagingClient).replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("Wrong state in handleUpdateState(): update default"))
      ));
    }

    private void handleTextMessageEventTestMenuTextT() throws Exception {
      MessageEvent event = new MessageEvent<TextMessageContent>(
              "replyToken",
              new UserSource("testUserMenuText"),
              new TextMessageContent("testUserMenuText", "food"),
              Instant.now()
      );
      when(lineMessagingClient.replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("recommendation" + "\nWhat do you want to choose?"))
      ))).thenReturn(CompletableFuture.completedFuture(
              new BotApiResponse("ok", Collections.emptyList())
      ));
      when(User.getUser("testUserMenuText", "state")).thenReturn("menu text");
      when(Food.createMenuText("testUserMenuText", "food")).thenReturn(true);
      when(Food.getMenuInfo("testUserMenuText")).thenReturn("recommendation");
      underTest.handleTextMessageEvent(event);
      verify(lineMessagingClient).replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("recommendation" + "\nWhat do you want to choose?"))
      ));
    }
    private void handleTextMessageEventTestMenuTextF() throws Exception {
      MessageEvent event = new MessageEvent<TextMessageContent>(
              "replyToken",
              new UserSource("testUserMenuText"),
              new TextMessageContent("testUserMenuText", "food"),
              Instant.now()
      );
      when(lineMessagingClient.replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("createMenuText() failed with userID: testUserMenuText and menu: food"))
      ))).thenReturn(CompletableFuture.completedFuture(
              new BotApiResponse("ok", Collections.emptyList())
      ));
      when(User.getUser("testUserMenuText", "state")).thenReturn("menu text");
      when(Food.createMenuText("testUserMenuText", "food")).thenReturn(false);
      underTest.handleTextMessageEvent(event);
      verify(lineMessagingClient).replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("createMenuText() failed with userID: testUserMenuText and menu: food"))
      ));
    }
    private void handleTextMessageEventTestMenuJsonT() throws Exception {
      MessageEvent event = new MessageEvent<TextMessageContent>(
              "replyToken",
              new UserSource("testUserMenuJson"),
              new TextMessageContent("testUserMenuJson", "food"),
              Instant.now()
      );
      when(lineMessagingClient.replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("recommendation" + "\nWhat do you want to choose?"))
      ))).thenReturn(CompletableFuture.completedFuture(
              new BotApiResponse("ok", Collections.emptyList())
      ));
      when(User.getUser("testUserMenuJson", "state")).thenReturn("menu json");
      when(Food.createMenuJson("testUserMenuJson", "food")).thenReturn(true);
      when(Food.getMenuInfo("testUserMenuJson")).thenReturn("recommendation");
      underTest.handleTextMessageEvent(event);
      verify(lineMessagingClient, atLeast(1)).replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("recommendation" + "\nWhat do you want to choose?"))
      ));
    }
    private void handleTextMessageEventTestMenuJsonF() throws Exception {
      MessageEvent event = new MessageEvent<TextMessageContent>(
              "replyToken",
              new UserSource("testUserMenuJson"),
              new TextMessageContent("testUserMenuJson", "food"),
              Instant.now()
      );
      when(lineMessagingClient.replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("createMenuJson() failed with userID: testUserMenuJson and menu: food"))
      ))).thenReturn(CompletableFuture.completedFuture(
              new BotApiResponse("ok", Collections.emptyList())
      ));
      when(User.getUser("testUserMenuJson", "state")).thenReturn("menu json");
      when(Food.createMenuJson("testUserMenuJson", "food")).thenReturn(false);
      underTest.handleTextMessageEvent(event);
      verify(lineMessagingClient).replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("createMenuJson() failed with userID: testUserMenuJson and menu: food"))
      ));
    }
    private void handleTextMessageEventTestMenuDefault() throws Exception {
      MessageEvent event = new MessageEvent<TextMessageContent>(
              "replyToken",
              new UserSource("testUserMenuDefault"),
              new TextMessageContent("testUserMenuDefault", "food"),
              Instant.now()
      );
      when(lineMessagingClient.replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("Wrong state in handleTextContent(): menu default"))
      ))).thenReturn(CompletableFuture.completedFuture(
              new BotApiResponse("ok", Collections.emptyList())
      ));
      when(User.getUser("testUserMenuDefault", "state")).thenReturn("menu default");
      // when(Food.createMenuText("testUserMenuDefault", "food")).thenReturn(true);
      // when(Food.getMenuInfo("testUserMenuDefault")).thenReturn("recommendation");
      underTest.handleTextMessageEvent(event);
      verify(lineMessagingClient).replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("Wrong state in handleTextContent(): menu default"))
      ));
    }
    private void handleTextMessageEventTestMenuTextTF() throws Exception {
      MessageEvent event = new MessageEvent<TextMessageContent>(
              "replyToken",
              new UserSource("testUserMenuText"),
              new TextMessageContent("testUserMenuText", "food"),
              Instant.now()
      );
      when(lineMessagingClient.replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("You haven't input a menu"))
      ))).thenReturn(CompletableFuture.completedFuture(
              new BotApiResponse("ok", Collections.emptyList())
      ));
      when(User.getUser("testUserMenuText", "state")).thenReturn("menu text");
      when(Food.createMenuText("testUserMenuText", "food")).thenReturn(true);
      when(Food.getMenuInfo("testUserMenuText")).thenReturn("You haven't input a menu");
      underTest.handleTextMessageEvent(event);
      verify(lineMessagingClient).replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("You haven't input a menu"))
      ));
    }
    private void handleTextMessageEventTestMenuJsonTF() throws Exception {
      MessageEvent event = new MessageEvent<TextMessageContent>(
              "replyToken",
              new UserSource("testUserMenuJson"),
              new TextMessageContent("testUserMenuJson", "food"),
              Instant.now()
      );
      when(lineMessagingClient.replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("You haven't input a menu"))
      ))).thenReturn(CompletableFuture.completedFuture(
              new BotApiResponse("ok", Collections.emptyList())
      ));
      when(User.getUser("testUserMenuJson", "state")).thenReturn("menu json");
      when(Food.createMenuText("testUserMenuJson", "food")).thenReturn(true);
      when(Food.getMenuInfo("testUserMenuJson")).thenReturn("You haven't input a menu");
      underTest.handleTextMessageEvent(event);
      verify(lineMessagingClient).replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("You haven't input a menu"))
      ));
    }

    private void handleTextMessageEventTestWarning() throws Exception {
      MessageEvent event = new MessageEvent<TextMessageContent>(
              "replyToken",
              new UserSource("testUserWarning"),
              new TextMessageContent("testUserWarning", "food"),
              Instant.now()
      );
      when(lineMessagingClient.replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage(
              "Your selection is not in the menu you've provided. However we still make the following recommendation based on your food selection. \n" +
              "It's okay to have this food, but its energy is less than the the food we recommended." +
              " If you are going to select this food we suggest you to eat all of the food."))
      ))).thenReturn(CompletableFuture.completedFuture(
              new BotApiResponse("ok", Collections.emptyList())
      ));
      when(User.getUser("testUserWarning", "state")).thenReturn("warning");
      when(Food.FoodinMenu("testUserWarning", "food")).thenReturn(false);
      when(Food.WarningforFoodSelection("testUserWarning", "food")).thenReturn("It's okay to have this food, but its energy is less than the the food we recommended.");
      when(Food.foodPortion("testUserWarning", "food")).thenReturn("all");
      underTest.handleTextMessageEvent(event);
      verify(lineMessagingClient).replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage(
              "Your selection is not in the menu you've provided. However we still make the following recommendation based on your food selection. \n" +
              "It's okay to have this food, but its energy is less than the the food we recommended." +
              " If you are going to select this food we suggest you to eat all of the food."))
      ));

      event = new MessageEvent<TextMessageContent>(
              "replyToken",
              new UserSource("testUserWarning"),
              new TextMessageContent("testUserWarning", "food"),
              Instant.now()
      );
      when(lineMessagingClient.replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage(
              "database"))
      ))).thenReturn(CompletableFuture.completedFuture(
              new BotApiResponse("ok", Collections.emptyList())
      ));
      when(User.getUser("testUserWarning", "state")).thenReturn("warning");
      // when(Food.FoodinMenu("testUserWarning", "food")).thenReturn(false);
      when(Food.WarningforFoodSelection("testUserWarning", "food")).thenReturn("database");
      // when(Food.foodPortion("testUserWarning", "food")).thenReturn("all");
      underTest.handleTextMessageEvent(event);
      verify(lineMessagingClient).replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage(
              "database"))
      ));

      event = new MessageEvent<TextMessageContent>(
              "replyToken",
              new UserSource("testUserWarning"),
              new TextMessageContent("testUserWarning", "food"),
              Instant.now()
      );
      when(lineMessagingClient.replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage(
              " If you are going to select this food we suggest you to eat all of the food."))
      ))).thenReturn(CompletableFuture.completedFuture(
              new BotApiResponse("ok", Collections.emptyList())
      ));
      when(User.getUser("testUserWarning", "state")).thenReturn("warning");
      when(Food.FoodinMenu("testUserWarning", "food")).thenReturn(true);
      when(Food.WarningforFoodSelection("testUserWarning", "food")).thenReturn("");
      when(Food.foodPortion("testUserWarning", "food")).thenReturn("all");
      underTest.handleTextMessageEvent(event);
      verify(lineMessagingClient).replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage(
              " If you are going to select this food we suggest you to eat all of the food."))
      ));
    }

    private void handleTextMessageEventTestCheckNutrition() throws Exception {
      MessageEvent event = new MessageEvent<TextMessageContent>(
              "replyToken",
              new UserSource("testUserCheckNutrition"),
              new TextMessageContent("testUserCheckNutrition", "food"),
              Instant.now()
      );
      when(lineMessagingClient.replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("nutrition"))
      ))).thenReturn(CompletableFuture.completedFuture(
              new BotApiResponse("ok", Collections.emptyList())
      ));
      when(User.getUser("testUserCheckNutrition", "state")).thenReturn("check nutrition");
      when(Food.checkNutrition("food")).thenReturn("nutrition");
      underTest.handleTextMessageEvent(event);
      verify(lineMessagingClient).replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("nutrition"))
      ));
    }
    private void handleTextMessageEventTestCheckAppropriate() throws Exception {
      MessageEvent event = new MessageEvent<TextMessageContent>(
              "replyToken",
              new UserSource("testUserCheckAppropriate"),
              new TextMessageContent("testUserCheckAppropriate", "food"),
              Instant.now()
      );
      when(lineMessagingClient.replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("yes"))
      ))).thenReturn(CompletableFuture.completedFuture(
              new BotApiResponse("ok", Collections.emptyList())
      ));
      when(User.getUser("testUserCheckAppropriate", "state")).thenReturn("check appropriate");
      when(Food.checkAppropriate("testUserCheckAppropriate", "food")).thenReturn("yes");
      underTest.handleTextMessageEvent(event);
      verify(lineMessagingClient).replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("yes"))
      ));
    }
    private void handleTextMessageEventTestCheckDefault() throws Exception {
      MessageEvent event = new MessageEvent<TextMessageContent>(
              "replyToken",
              new UserSource("testUserCheckDefault"),
              new TextMessageContent("testUserCheckDefault", "string"),
              Instant.now()
      );
      when(lineMessagingClient.replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("Wrong state in handleTextContent(): check default"))
      ))).thenReturn(CompletableFuture.completedFuture(
              new BotApiResponse("ok", Collections.emptyList())
      ));
      when(User.getUser("testUserCheckDefault", "state")).thenReturn("check default");
      underTest.handleTextMessageEvent(event);
      verify(lineMessagingClient).replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("Wrong state in handleTextContent(): check default"))
      ));
    }

    private void handleTextMessageEventTestDefault() throws Exception {
      MessageEvent event = new MessageEvent<TextMessageContent>(
              "replyToken",
              new UserSource("testUserDefault"),
              new TextMessageContent("testUserDefault", "string"),
              Instant.now()
      );
      when(lineMessagingClient.replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("Wrong state in handleTextContent(): wrong state"))
      ))).thenReturn(CompletableFuture.completedFuture(
              new BotApiResponse("ok", Collections.emptyList())
      ));
      when(User.getUser("testUserDefault", "state")).thenReturn("wrong state");
      underTest.handleTextMessageEvent(event);
      verify(lineMessagingClient).replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("Wrong state in handleTextContent(): wrong state"))
      ));
    }


    @Test
    public void handlePostbackEventTest() throws Throwable {
      setup();
      handlePostbackEventTestUpdate();
      handlePostbackEventTestMenu();
      handlePostbackEventTestSummaryDailyEqual();
      // handlePostbackEventTestSummaryDailyLess();
      // handlePostbackEventTestSummaryDailyMore();
      handlePostbackEventTestSummaryWeekly();
      handlePostbackEventTestSummaryOverall();
      handlePostbackEventTestSummaryWrong();
      handlePostbackEventTestCheck();
      handlePostbackEventTestDefault();
    }
    private void handlePostbackEventTestUpdate() {
        PostbackEvent event = new PostbackEvent(
                "replyToken",
                new UserSource("test"),
                new PostbackContent("update food", null),
                Instant.now()
        );
        when(lineMessagingClient.replyMessage(new ReplyMessage(
                "replyToken", singletonList(new TextMessage("What did you eat?"))
        ))).thenReturn(CompletableFuture.completedFuture(
                new BotApiResponse("ok", Collections.emptyList())
        ));
        underTest.handlePostbackEvent(event);
        verify(lineMessagingClient).replyMessage(new ReplyMessage(
                "replyToken", singletonList(new TextMessage("What did you eat?"))
        ));

        event = new PostbackEvent(
                "replyToken",
                new UserSource("test"),
                new PostbackContent("update weight", null),
                Instant.now()
        );
        when(lineMessagingClient.replyMessage(new ReplyMessage(
                "replyToken", singletonList(new TextMessage("What is your current weight(kg)? (Please input a number)"))
        ))).thenReturn(CompletableFuture.completedFuture(
                new BotApiResponse("ok", Collections.emptyList())
        ));
        underTest.handlePostbackEvent(event);
        verify(lineMessagingClient).replyMessage(new ReplyMessage(
                "replyToken", singletonList(new TextMessage("What is your current weight(kg)? (Please input a number)"))
        ));

        event = new PostbackEvent(
                "replyToken",
                new UserSource("test"),
                new PostbackContent("update target_weight", null),
                Instant.now()
        );
        when(lineMessagingClient.replyMessage(new ReplyMessage(
                "replyToken", singletonList(new TextMessage("What is your new target weight(kg)? (Please input a number)"))
        ))).thenReturn(CompletableFuture.completedFuture(
                new BotApiResponse("ok", Collections.emptyList())
        ));
        underTest.handlePostbackEvent(event);
        verify(lineMessagingClient).replyMessage(new ReplyMessage(
                "replyToken", singletonList(new TextMessage("What is your new target weight(kg)? (Please input a number)"))
        ));

        event = new PostbackEvent(
                "replyToken",
                new UserSource("test"),
                new PostbackContent("update wrong", null),
                Instant.now()
        );
        when(lineMessagingClient.replyMessage(new ReplyMessage(
                "replyToken", singletonList(new TextMessage("Wrong postback: " + event.getPostbackContent().getData()))
        ))).thenReturn(CompletableFuture.completedFuture(
                new BotApiResponse("ok", Collections.emptyList())
        ));
        underTest.handlePostbackEvent(event);
        verify(lineMessagingClient).replyMessage(new ReplyMessage(
                "replyToken", singletonList(new TextMessage("Wrong postback: " + event.getPostbackContent().getData()))
        ));
    }
    private void handlePostbackEventTestMenu() {
        PostbackEvent event = new PostbackEvent(
                "replyToken",
                new UserSource("test"),
                new PostbackContent("menu text", null),
                Instant.now()
        );
        when(lineMessagingClient.replyMessage(new ReplyMessage(
                "replyToken", singletonList(new TextMessage("Please input your text menu"))
        ))).thenReturn(CompletableFuture.completedFuture(
                new BotApiResponse("ok", Collections.emptyList())
        ));
        underTest.handlePostbackEvent(event);
        verify(lineMessagingClient).replyMessage(new ReplyMessage(
                "replyToken", singletonList(new TextMessage("Please input your text menu"))
        ));

        event = new PostbackEvent(
                "replyToken",
                new UserSource("test"),
                new PostbackContent("menu json", null),
                Instant.now()
        );
        when(lineMessagingClient.replyMessage(new ReplyMessage(
                "replyToken", singletonList(new TextMessage("Please input your json menu"))
        ))).thenReturn(CompletableFuture.completedFuture(
                new BotApiResponse("ok", Collections.emptyList())
        ));
        underTest.handlePostbackEvent(event);
        verify(lineMessagingClient).replyMessage(new ReplyMessage(
                "replyToken", singletonList(new TextMessage("Please input your json menu"))
        ));

        event = new PostbackEvent(
                "replyToken",
                new UserSource("test"),
                new PostbackContent("menu jpeg", null),
                Instant.now()
        );
        when(lineMessagingClient.replyMessage(new ReplyMessage(
                "replyToken", singletonList(new TextMessage("Please input your jpeg menu"))
        ))).thenReturn(CompletableFuture.completedFuture(
                new BotApiResponse("ok", Collections.emptyList())
        ));
        underTest.handlePostbackEvent(event);
        verify(lineMessagingClient).replyMessage(new ReplyMessage(
                "replyToken", singletonList(new TextMessage("Please input your jpeg menu"))
        ));
    }
    private void handlePostbackEventTestSummaryDailyEqual() throws Throwable {
      PostbackEvent event = new PostbackEvent(
              "replyToken",
              new UserSource("test"),
              new PostbackContent("summary daily", null),
              Instant.now()
      );
      // when(lineMessagingClient.replyMessage(new ReplyMessage(
      //         "replyToken", singletonList(new TextMessage("Today you consumed 0 kcal. Based on your personal information, we recommand you to consume 0 kcal per day. "
      //           + "Good Job! Continue on your current consumption plan."))
      // ))).thenReturn(CompletableFuture.completedFuture(
      //         new BotApiResponse("ok", Collections.emptyList())
      // ));
      // Date current_time = Mockito.mock(Date.class);
      // SimpleDateFormat dateFormat = Mockito.mock(SimpleDateFormat.class);
      // PowerMockito.whenNew(SimpleDateFormat.class).withArguments(Mockito.anyString()).thenReturn(dateFormat);
      // when(new SimpleDateFormat("dd/MM/yyyy").format(current_time)).thenReturn("date");
      // when(User.getDailyIntake("test", "date")).thenReturn((float)0.0);
      // when(User.getIdealDailyIntake("test")).thenReturn(0);
      // underTest.handlePostbackEvent(event);
      // verify(lineMessagingClient).replyMessage(new ReplyMessage(
      //         "replyToken", singletonList(new TextMessage("Today you consumed 0 kcal. Based on your personal information, we recommand you to consume 0 kcal per day. "
      //           + "Good Job! Continue on your current consumption plan."))
      // ));
    }
    private void handlePostbackEventTestSummaryWeekly() throws Throwable {
      PostbackEvent event = new PostbackEvent(
              "replyToken",
              new UserSource("test"),
              new PostbackContent("summary weekly", null),
              Instant.now()
      );
      when(lineMessagingClient.replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("summary"))
      ))).thenReturn(CompletableFuture.completedFuture(
              new BotApiResponse("ok", Collections.emptyList())
      ));
      when(User.generateWeeklySummary("test")).thenReturn("summary");
      underTest.handlePostbackEvent(event);
      verify(lineMessagingClient).replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("summary"))
      ));
    }
    private void handlePostbackEventTestSummaryOverall() throws Throwable {
      PostbackEvent event = new PostbackEvent(
              "replyToken",
              new UserSource("test"),
              new PostbackContent("summary overall", null),
              Instant.now()
      );
      when(lineMessagingClient.replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("summary"))
      ))).thenReturn(CompletableFuture.completedFuture(
              new BotApiResponse("ok", Collections.emptyList())
      ));
      when(User.generateSummary("test")).thenReturn("summary");
      underTest.handlePostbackEvent(event);
      verify(lineMessagingClient, atLeast(1)).replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("summary"))
      ));
    }
    private void handlePostbackEventTestSummaryWrong() throws Throwable {
      PostbackEvent event = new PostbackEvent(
              "replyToken",
              new UserSource("test"),
              new PostbackContent("summary wrong", null),
              Instant.now()
      );
      when(lineMessagingClient.replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("Wrong postback: summary wrong"))
      ))).thenReturn(CompletableFuture.completedFuture(
              new BotApiResponse("ok", Collections.emptyList())
      ));
      underTest.handlePostbackEvent(event);
      verify(lineMessagingClient, atLeast(1)).replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("Wrong postback: summary wrong"))
      ));
    }
    private void handlePostbackEventTestCheck() {
        PostbackEvent event = new PostbackEvent(
                "replyToken",
                new UserSource("test"),
                new PostbackContent("check nutrition", null),
                Instant.now()
        );
        when(lineMessagingClient.replyMessage(new ReplyMessage(
                "replyToken", singletonList(new TextMessage("Please input a food name"))
        ))).thenReturn(CompletableFuture.completedFuture(
                new BotApiResponse("ok", Collections.emptyList())
        ));
        underTest.handlePostbackEvent(event);
        verify(lineMessagingClient).replyMessage(new ReplyMessage(
                "replyToken", singletonList(new TextMessage("Please input a food name"))
        ));

        // PostbackEvent event1 = new PostbackEvent(
        //         "replyToken",
        //         new UserSource("test"),
        //         new PostbackContent("check appropriate", null),
        //         Instant.now()
        // );
        // when(lineMessagingClient.replyMessage(new ReplyMessage(
        //         "replyToken", singletonList(new TextMessage("Please input a food name"))
        // ))).thenReturn(CompletableFuture.completedFuture(
        //         new BotApiResponse("ok", Collections.emptyList())
        // ));
        // underTest.handlePostbackEvent(event1);
        // verify(lineMessagingClient).replyMessage(new ReplyMessage(
        //         "replyToken", singletonList(new TextMessage("Please input a food name"))
        // ));

        //check tips
        event = new PostbackEvent(
                "replyToken",
                new UserSource("test"),
                new PostbackContent("check tips", null),
                Instant.now()
        );
        when(lineMessagingClient.replyMessage(new ReplyMessage(
                "replyToken", singletonList(new TextMessage("tip"))
        ))).thenReturn(CompletableFuture.completedFuture(
                new BotApiResponse("ok", Collections.emptyList())
        ));
        when(Food.getTip()).thenReturn("tip");
        underTest.handlePostbackEvent(event);
        verify(lineMessagingClient).replyMessage(new ReplyMessage(
                "replyToken", singletonList(new TextMessage("tip"))
        ));

        event = new PostbackEvent(
                "replyToken",
                new UserSource("test"),
                new PostbackContent("check wrong", null),
                Instant.now()
        );
        when(lineMessagingClient.replyMessage(new ReplyMessage(
                "replyToken", singletonList(new TextMessage("Wrong postback: " + event.getPostbackContent().getData()))
        ))).thenReturn(CompletableFuture.completedFuture(
                new BotApiResponse("ok", Collections.emptyList())
        ));
        underTest.handlePostbackEvent(event);
        verify(lineMessagingClient).replyMessage(new ReplyMessage(
                "replyToken", singletonList(new TextMessage("Wrong postback: " + event.getPostbackContent().getData()))
        ));
    }
    private void handlePostbackEventTestDefault() {
      PostbackEvent event = new PostbackEvent(
              "replyToken",
              new UserSource("test"),
              new PostbackContent("postback wrong", null),
              Instant.now()
      );
      when(lineMessagingClient.replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("Wrong postback: " + event.getPostbackContent().getData()))
      ))).thenReturn(CompletableFuture.completedFuture(
              new BotApiResponse("ok", Collections.emptyList())
      ));
      underTest.handlePostbackEvent(event);
      verify(lineMessagingClient).replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("Wrong postback: " + event.getPostbackContent().getData()))
      ));
    }

    @Test
    public void handleFollowEventTest() {
      setup();
      handleFollowEventTestInsertF();
      // handleFollowEventTestSetF();
      // handleFollowEventTestNormal();
    }
    private void handleFollowEventTestInsertF() {
      FollowEvent event = new FollowEvent(
              "replyToken",
              new UserSource("test"),
              Instant.now()
      );
      when(lineMessagingClient.replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("Insert user failed: test"))
      ))).thenReturn(CompletableFuture.completedFuture(
              new BotApiResponse("ok", Collections.emptyList())
      ));
      when(User.insert("test", coupon_date)).thenReturn(false);
      underTest.handleFollowEvent(event);
      verify(lineMessagingClient).replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("Insert user failed: test"))
      ));
    }
    private void handleFollowEventTestSetF() {
      FollowEvent event = new FollowEvent(
              "replyToken",
              new UserSource("test"),
              Instant.now()
      );
      when(lineMessagingClient.replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("Set state failed: set name"))
      ))).thenReturn(CompletableFuture.completedFuture(
              new BotApiResponse("ok", Collections.emptyList())
      ));
      when(User.insert("test", coupon_date)).thenReturn(true);
      when(User.setUser("test", "state", "set name")).thenReturn(false);
      underTest.handleFollowEvent(event);
      verify(
      lineMessagingClient, atLeast(1)).
      replyMessage(
      new ReplyMessage("replyToken",
              singletonList(new TextMessage("Set state failed: set name"))
      ));
    }
    private void handleFollowEventTestNormal() {
      FollowEvent event = new FollowEvent(
              "replyToken",
              new UserSource("test"),
              Instant.now()
      );
      when(lineMessagingClient.replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("Welcome! This is a helpful chatbot. We would like to collect your personal information. May I know your name?"))
      ))).thenReturn(CompletableFuture.completedFuture(
              new BotApiResponse("ok", Collections.emptyList())
      ));
      when(User.insert("test", coupon_date)).thenReturn(true);
      when(User.setUser("test", "state", "set name")).thenReturn(true);
      underTest.handleFollowEvent(event);
      verify(lineMessagingClient).replyMessage(new ReplyMessage(
              "replyToken", singletonList(new TextMessage("Welcome! This is a helpful chatbot. We would like to collect your personal information. May I know your name?"))
      ));
    }

/*
        @Test
        public void handleTextMessageEvent() throws Exception {
            // if group user send "bye" as text message
            final MessageEvent request = new MessageEvent<>(
                    "replyToken",
                    new GroupSource("groupId", "userId"),
                    new TextMessageContent("id", "bye"),
                    Instant.now()
            );

            // mock line bot api client response
            when(lineMessagingClient.replyMessage(new ReplyMessage(
                    "replyToken", singletonList(new TextMessage("Leaving group"))
            ))).thenReturn(CompletableFuture.completedFuture(
                    new BotApiResponse("ok", Collections.emptyList())
            ));
            when(lineMessagingClient.leaveGroup("groupId"))
                    .thenReturn(CompletableFuture.completedFuture(
                            new BotApiResponse("ok", Collections.emptyList())
                    ));

            underTest.handleTextMessageEvent(request);

            // confirm replyMessage is called with following parameter
            verify(lineMessagingClient).replyMessage(new ReplyMessage(
                    "replyToken", singletonList(new TextMessage("Leaving group"))
            ));
            // leave group api must be called
            verify(lineMessagingClient).leaveGroup("groupId");
        }
    */
}
