 /*
 * Copyright 2016 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.example.bot.spring;

import java.io.IOException;
import java.net.URISyntaxException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import com.linecorp.bot.model.profile.UserProfileResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.google.common.io.ByteStreams;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.client.MessageContentResponse;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.action.MessageAction;
import com.linecorp.bot.model.action.PostbackAction;
import com.linecorp.bot.model.action.URIAction;
import com.linecorp.bot.model.event.BeaconEvent;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.FollowEvent;
import com.linecorp.bot.model.event.JoinEvent;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.UnfollowEvent;
import com.linecorp.bot.model.event.message.AudioMessageContent;
import com.linecorp.bot.model.event.message.ImageMessageContent;
import com.linecorp.bot.model.event.message.LocationMessageContent;
import com.linecorp.bot.model.event.message.StickerMessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.event.source.GroupSource;
import com.linecorp.bot.model.event.source.RoomSource;
import com.linecorp.bot.model.event.source.Source;
import com.linecorp.bot.model.message.AudioMessage;
import com.linecorp.bot.model.message.ImageMessage;
import com.linecorp.bot.model.message.ImagemapMessage;
import com.linecorp.bot.model.message.LocationMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.StickerMessage;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.imagemap.ImagemapArea;
import com.linecorp.bot.model.message.imagemap.ImagemapBaseSize;
import com.linecorp.bot.model.message.imagemap.MessageImagemapAction;
import com.linecorp.bot.model.message.imagemap.URIImagemapAction;
import com.linecorp.bot.model.message.template.ButtonsTemplate;
import com.linecorp.bot.model.message.template.CarouselColumn;
import com.linecorp.bot.model.message.template.CarouselTemplate;
import com.linecorp.bot.model.message.template.ConfirmTemplate;
import com.linecorp.bot.model.response.BotApiResponse;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;

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

import com.asprise.ocr.*;
import java.io.*;

@Slf4j
@LineMessageHandler
public class KitchenSinkController {


    /**
     * This object is used to reply message to user
     */
	@Autowired
	private LineMessagingClient lineMessagingClient;

    /**
     * This array contains the order for users to update their states
     */
	private String[] setOrder = {"name", "status", "age", "weight", "target_weight", "height", "days_for_target"};

    /**
     * Starting date of the ice cream coupon activity as an calendar obejct
     */
	private Calendar calendar = new GregorianCalendar(2017,10,11);


    /**
     * Starting date of the ice cream coupon activity as an date obejct
     */
	private Date coupon_date =  calendar.getTime();


    /**
     * This method is used to handle text message event from user, the detailed handling is passed to handleTextContent function
     * @param event This is the event from user
     * @throws Exception for all errors
     */
	@EventMapping
	public void handleTextMessageEvent(MessageEvent<TextMessageContent> event) throws Exception {
		log.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		log.info("This is your entry point:");
		log.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		TextMessageContent message = event.getMessage();
		handleTextContent(event.getReplyToken(), event, message);
	}

    /**
     * This method is used to handle sticker message event from the user by replying the same sticker back to user.
     * @param event This is the corresponding sticker event object
     */
	@EventMapping
	public void handleStickerMessageEvent(MessageEvent<StickerMessageContent> event) {
		handleSticker(event.getReplyToken(), event.getMessage());
	}

    /**
     * This method is used to handle location message event from the user by replying the same location back to user.
     * @param event This is the corresponding location message event object
     */
	@EventMapping
	public void handleLocationMessageEvent(MessageEvent<LocationMessageContent> event) {
		LocationMessageContent locationMessage = event.getMessage();
		reply(event.getReplyToken(), new LocationMessage(locationMessage.getTitle(), locationMessage.getAddress(),
				locationMessage.getLatitude(), locationMessage.getLongitude()));
	}

    /**
     * This method is used to handle image message event from the user by replying the same image back to user.
     * @param event This is the corresponding image event object
     * @throws IOException if an input error occurs
     */
	 @EventMapping
	 public void handleImageMessageEvent(MessageEvent<ImageMessageContent> event) throws IOException {
	 		String replyToken = event.getReplyToken();
	 		String userID = event.getSource().getUserId();
	 		final MessageContentResponse response;

	 		String messageId = event.getMessage().getId();
	 		try {
	 			response = lineMessagingClient.getMessageContent(messageId).get();
	 		} catch (InterruptedException | ExecutionException e) {
	 			reply(replyToken, new TextMessage("Cannot get image: " + e.getMessage()));
	 			throw new RuntimeException(e);
	 		}
	 		DownloadedContent jpg = saveContent("jpg", response);
	 		// reply(((MessageEvent) event).getReplyToken(), new ImageMessage(jpg.getUri(), jpg.getUri()));

	 		String state = User.getUser(userID, "state");
         
	 		if (state == null) {
	 			this.replyText(replyToken, "Get user state failed: " + userID);
	 		}	else if (state.equals("menu jpeg")) {
	 			try {
	 				if (!Food.createMenuImage(userID, new URI(jpg.getUri()))) {
	 					this.replyText(replyToken, "createMenuImage() failed with userID: " + userID + " and menu URI: " + jpg.getUri());
	 					User.setUser(userID, "state", "0");
	 					return;
	 				}
	 			} catch (URISyntaxException e) {
	 				this.replyText(replyToken, "URISyntaxException in createMenuImage(): " + e.getMessage());
	 				User.setUser(userID, "state", "0");
	 				return;
	 			}

	 			String recommendation = Food.getMenuInfo(userID);
	 			if (recommendation != null)
	 				this.replyText(replyToken, recommendation + "\nWhat do you want to choose?");
	 			else
	 				this.replyText(replyToken, "getMenuInfo() failed with userID: " + userID);
	 		} else {
	 			this.replyText(replyToken, "Wrong state in handleImageMessageEvent(): " + state);
	 		}

	 		User.setUser(userID, "state", "0");
	 }

    /**
     * This method is used to handle audio message event from the user by replying the same audio back to user.
     * @param event This is the corresponding audio message event object
     * @throws IOException if an input error occurs
     */
	@EventMapping
	public void handleAudioMessageEvent(MessageEvent<AudioMessageContent> event) throws IOException {
		final MessageContentResponse response;
		String replyToken = event.getReplyToken();
		String messageId = event.getMessage().getId();
		try {
			response = lineMessagingClient.getMessageContent(messageId).get();
		} catch (InterruptedException | ExecutionException e) {
			reply(replyToken, new TextMessage("Cannot get image: " + e.getMessage()));
			throw new RuntimeException(e);
		}
		DownloadedContent mp4 = saveContent("mp4", response);
		reply(event.getReplyToken(), new AudioMessage(mp4.getUri(), 100));
	}

    /**
     * This method is used to handle unfollwer event from user by deleting the user from user database
     * @param event this is the corresponding unfollow event object
     */
	@EventMapping
	public void handleUnfollowEvent(UnfollowEvent event) {
		log.info("unfollowed this bot: {}", event);

		String userID = event.getSource().getUserId();
        User.delete(userID);
	
	}

    /**
     * This method is used to handle follow event from new user by inserting the new into user database
     * , setting state of user to be "set name" and giving welcoming message
     * @param event this is the corresponding follow event object
     */
	@EventMapping
	public void handleFollowEvent(FollowEvent event) {
		String userID = event.getSource().getUserId();
		String replyToken = event.getReplyToken();

		if (!User.insert(userID,coupon_date)) {
			this.replyText(replyToken, "Insert user failed: " + userID);
			return;
		}

		String state = new String("set name");
		if (!User.setUser(userID, "state", state)) {
			this.replyText(replyToken, "Set state failed: " + state);
			return;
		}

		String message = new String("Welcome! This is a helpful chatbot. We would like to collect your personal information. May I know your name?");
		this.replyText(replyToken, message);
	}

    /**
     * This method is used to handle join event from user by sending a text message
     * @param event this is the corresponding join event object
     */
	@EventMapping
	public void handleJoinEvent(JoinEvent event) {
		String replyToken = event.getReplyToken();
		this.replyText(replyToken, "Joined " + event.getSource());
	}

    /**
     * This method is used to handle postback event from users
     * @param event This is the corresponding event from user
     */
	@EventMapping
	public void handlePostbackEvent(PostbackEvent event) {
			String userID = event.getSource().getUserId();
			String replyToken = event.getReplyToken();
			//this.replyText(replyToken, "Got postback " + event.getPostbackContent().getData());
			String postback = event.getPostbackContent().getData();
			String[] postbackArr = postback.split(" ");

			if(postbackArr[0].equals("update")){
						
							if(postbackArr[1].equals("food")){ this.replyText(replyToken, "What did you eat?");}
							else if(postbackArr[1].equals("weight")){ this.replyText(replyToken, "What is your current weight(kg)? (Please input a number)");}
							else if(postbackArr[1].equals("target_weight")){ this.replyText(replyToken, "What is your new target weight(kg)? (Please input a number)");}
							else{ this.replyText(replyToken, "Wrong postback: " + postback); User.setUser(userID, "state", "0");}
						
						User.setUser(userID, "state", postback);
						
					}
			else if(postbackArr[0].equals("menu")){
						this.replyText(replyToken, "Please input your " + postbackArr[1] + " menu");
						User.setUser(userID, "state", postback);
						
					}
			else if(postbackArr[0].equals("summary")){
						
							if(postbackArr[1].equals("daily")){
                                Date current_time = new Date();
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                String date = dateFormat.format(current_time);
                                float calories = User.getDailyIntake(userID, date);

								float idealCalories = User.getIdealDailyIntake(userID);
								if (calories == idealCalories)
									this.replyText(replyToken, "Today you consumed " + calories + " kcal. Based on your personal information, we recommand you to consume " + idealCalories + " kcal per day. "
										+ "Good Job! Continue on your current consumption plan.");
								else if (calories < idealCalories)
									this.replyText(replyToken, "Today you consumed " + calories + " kcal. Based on your personal information, we recommand you to consume " + idealCalories + " kcal per day. "
										+ "Therefore we recommand you to increase your food consumption.");
								else
									this.replyText(replyToken, "Today you consumed " + calories + " kcal. Based on your personal information, we recommand you to consume " + idealCalories + " kcal per day. "
										+ "Therefore we recommand you to decrease your food consumption.");
								
							}
							else if(postbackArr[1].equals("weekly")){
								this.replyText(replyToken, User.generateWeeklySummary(userID));
								
							}
							else if(postbackArr[1].equals("overall")){
								this.replyText(replyToken, User.generateSummary(userID));
								
							}
							else{
								this.replyText(replyToken, "Wrong postback: " + postback);
								User.setUser(userID, "state", "0");
			        	
							}
						
					}
					else if(postbackArr[0].equals("check")){
						
							if(postbackArr[1].equals("nutrition") || postbackArr[1].equals("appropriate")){
							
								User.setUser(userID, "state", postback);
								this.replyText(replyToken, "Please input a food name");
								
							}
							else if(postbackArr[1].equals("tips")) {
								this.replyText(replyToken, Food.getTip());
								
							}
							else{
								this.replyText(replyToken, "Wrong postback: " + postback);
								User.setUser(userID, "state", "0");
			        	
							}
						
					}
	        else{
	        	this.replyText(replyToken, "Wrong postback: " + postback);
						User.setUser(userID, "state", "0");
	        	
	        }
		}
	

    /**
     * This method is used to handle beacon event from user by sending a text message
     * @param event this is the corresponding beacon event object
     */
	@EventMapping
	public void handleBeaconEvent(BeaconEvent event) {
		String replyToken = event.getReplyToken();
		this.replyText(replyToken, "Got beacon message " + event.getBeacon().getHwid());
	}

    /**
     * This method is used to handle other events not metioned in the class from user by sending a text message
     * @param event this is the corresponding event object
     */
	@EventMapping
	public void handleOtherEvent(Event event) {
		log.info("Received message(Ignored): {}", event);
	}




    /**
     * This method is used to handle other events not metioned in the class from user by sending a text message
     * @param message this is the corresponding event object
     * @param replyToken this is the replyToken from event
     */
	private void reply(@NonNull String replyToken, @NonNull Message message) {
		reply(replyToken, Collections.singletonList(message));
	}

    /**
     * This method is used to reply message according to replyToken
     * @param replyToken This is the replyToken for the message
     * @param messages This is the message to be replied
     */
	private void reply(@NonNull String replyToken, @NonNull List<Message> messages) {
		try {
			BotApiResponse apiResponse = lineMessagingClient.replyMessage(new ReplyMessage(replyToken, messages)).get();
			log.info("Sent messages: {}", apiResponse);
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

    /**
     * This method is used to simplify text reply function
     * @param replyToken This is the replyToken for the message
     * @param message This is the message to be replied
     * @throws IllegalArgumentException If replyToken is empty
     */
	private void replyText(@NonNull String replyToken, @NonNull String message) {
		if (replyToken.isEmpty()) {
			throw new IllegalArgumentException("replyToken must not be empty");
		}
		if (message.length() > 1000) {
			message = message.substring(0, 1000 - 2) + "..";
		}
		this.reply(replyToken, new TextMessage(message));
	}

    /**
     * This method is used to reply sticker to user
     * @param replyToken This is the replyToken for the sticker message
     * @param content This is the content of the sticker to be reply
     */
	private void handleSticker(String replyToken, StickerMessageContent content) {
		reply(replyToken, new StickerMessage(content.getPackageId(), content.getStickerId()));
	}

    /**
     * This method is to handle in valid input from users for age , weight, target weight,
     * height, days for target and gender
     * @param state This is the corresponding state of user for the input
     * @param input This is the input from the user
     * @return boolean Return true for valid input and false for invalid input
     */
	private boolean isValidInput(String state, String input) {
		String[] numberInput = {"age", "weight", "target_weight", "height", "days_for_target"};
		if (Arrays.asList(numberInput).contains(state)) {
			try {
				Integer.parseInt(input);
			} catch (Exception e1) {
				try {
					Float.parseFloat(input);
				} catch (Exception e2) {
					return false;
				}
			}
		}
		if (state.equals("status")) {
			if (!input.equals("male") && !input.equals("female"))
				return false;
		}
		return true;
	}

    /**
     * This method is used to handle user state setting in order to update users' basic information
     * @param replyToken This is the replyToken from user
     * @param userID This is the user id
     * @param stateArr This is a array store the user state
     * @param input Thie is the user input
     */
	private void handleSetState(String replyToken, String userID, String[] stateArr, String input) throws Exception {
		if (!isValidInput(stateArr[1], input)) {
			this.replyText(replyToken, "Please follow the input instruction (•ૢ⚈͒⌄⚈͒•ૢ)");
			return;
		}

		if (User.setUser(userID, stateArr[1], input)) {
			int i = 0;
			for (; i < setOrder.length - 1; ++i) {
				if (stateArr[1].equals(setOrder[i])) {
					stateArr[1] = setOrder[i+1];
					break;
				}
			}
			if (i == setOrder.length - 1) {
				this.replyText(replyToken, "Great! Nice to meet you! You can input anything at any time to wake me up ✧⁺⸜(●˙▾˙●)⸝⁺✧");
				User.setUser(userID, "state", "0");

                
                Timer T1 = new Timer(userID);
                T1.start();

			}	else {
				
					if(stateArr[1].equals("status")) { this.replyText(replyToken, "What is your gender(male/female)?");}
					else if(stateArr[1].equals("age")) { this.replyText(replyToken, "How old are you? (Please input a number)");}
					else if(stateArr[1].equals("weight")) { this.replyText(replyToken, "What is your weight(kg)? (Please input a number)");}
					else if(stateArr[1].equals("target_weight")) { this.replyText(replyToken, "What is your target weight(kg)? (Please input a number)");}
					else if(stateArr[1].equals("height")) { this.replyText(replyToken, "What is your height(cm)? (Please input a number)");}
					else if(stateArr[1].equals("days_for_target")) { this.replyText(replyToken, "How long do you plan to achieve your goal? (Please input a day number)");}
					else { this.replyText(replyToken, "Wrong state in handleSetState(): " + String.join(" ", stateArr)); User.setUser(userID, "state", "0");}
				
				User.setUser(userID, "state", String.join(" ", stateArr));
			}
		} else
			this.replyText(replyToken, "Set " + stateArr[1] + " failed. Please try to input again :(");
	}

    /**
     * This method is used to input users' basic information.
     * @param replyToken This is the replyToken of the event
     * @param userID This is the userID of the user
     * @param stateArr This is the state array for the basic information
     * @param input This is the input from the users for their basic information
     */
	private void handleUpdateState(String replyToken, String userID, String[] stateArr, String input) throws Exception {
		if (!isValidInput(stateArr[1], input)) {
			this.replyText(replyToken, "Please input a number (๑•́₃•̀๑)");
			return;
		}

		
			if(stateArr[1].equals("weight")) {
				if (User.setUser(userID, stateArr[1], input)) {
					this.replyText(replyToken, "Update " + stateArr[1] + " succeeded");
				} else {
					this.replyText(replyToken, "Update " + stateArr[1] + " failed");
				}
				
                if (User.check_goal(userID)==1 || User.check_goal(userID)==2){
                        push(userID, new TextMessage("You have successfully reached your target weight! \n What is your next target weight"));
                    User.setUser(userID, "state", "update target_weight");}
                else if (User.check_goal(userID)==3) {
                    push(userID, new TextMessage("I'm sorry to inform you fail to reach your target weight within your planned period. \n Please update your new goal. \n What is your next target weight"));
                    User.setUser(userID, "state", "update target_weight");
                    }
                else
                    User.setUser(userID, "state", "0");
      
			}
			else if(stateArr[1].equals("food")) {
				if (User.updateRecord(userID, input)) {
					this.replyText(replyToken, "Update " + stateArr[1] + " succeeded");
					User.setUser(userID, "state", "0");
				} else {
					this.replyText(replyToken, "Update " + stateArr[1] + " failed");
					User.setUser(userID, "state", "0");
				}
				
			}
			else if(stateArr[1].equals("target_weight")) {
				if (User.setUser(userID, "target_weight", input)) {
					this.replyText(replyToken, "How long do you plan to achieve your new goal? (Please input a day number)");
					User.setUser(userID, "state", "update days_for_target");
				} else {
					User.setUser(userID, "state", "0");
					this.replyText(replyToken, "Update " + stateArr[1] + " failed");
				}
				
			}
			else if(stateArr[1].equals("days_for_target")) {
				if (User.setUser(userID, "days_for_target", input)) {
					this.replyText(replyToken, "Update goal succeeded");
				} else {
					this.replyText(replyToken, "Update " + stateArr[1] + " failed");
				}
				User.setUser(userID, "state", "0");
				
			}
			else {
				this.replyText(replyToken, "Wrong state in handleUpdateState(): " + String.join(" ", stateArr));
				User.setUser(userID, "state", "0");
			
			}
		
	}

    /**
     * This method is used to handle text content of a message from user
     * @param replyToken this is the relpyToken to for replying to user
     * @param event this is the message event to be handled
     * @param content this is the text content contained in the message event
     */
	private void handleTextContent(String replyToken, Event event, TextMessageContent content)
            throws Exception {
        String text = content.getText();
				String userID = event.getSource().getUserId();
				String state = User.getUser(userID, "state");
				if (state == null) {
					this.replyText(replyToken, "Get user state failed: " + userID);
					return;
				}
				String[] stateArr = state.split(" ");

        log.info("Got text message from {}: {}", replyToken, text);

				if (text.length() >= 6) {
					if (text.equalsIgnoreCase("friend")) {
						this.replyText(replyToken, Coupon.getCoupon().friend_call(userID));
						return;
					}
					if (text.substring(0, 4).equalsIgnoreCase("code")) {
						String s = Coupon.getCoupon().code_call(userID, text.substring(4));
                        String couponImage = createUri("/static/coupon.jpg");
                        switch(s){
                                case "1":
                                    this.replyText(replyToken, "The coupon has been sold out or you have already used the coupon");
                                    break;
                                case "2":
                                    this.replyText(replyToken, "You cannot use your own code");
                                    break;
                                case "3":
                                    this.replyText(replyToken,"Code not found");
                                    break;
                                default:
                                    reply(replyToken, new ImageMessage(couponImage,couponImage));
                                    push (s, new TextMessage("You have one extra ice coupon since your friend has claimed the ice-cream coupon! "));
                                    push(s,new ImageMessage(couponImage,couponImage) );
                        }
						return;
					}
				}
if(stateArr[0].equals("set")) {
                        handleSetState(replyToken, userID, stateArr, text);
                        
                    }
                    else if(stateArr[0].equals("update")) {
                        handleUpdateState(replyToken, userID, stateArr, text);
                        
                    }
                    else if(stateArr[0].equals("menu")) {
                        
                            if(stateArr[1].equals("text")) {
                                if (!Food.createMenuText(userID, text)) {
                                    this.replyText(replyToken, "createMenuText() failed with userID: " + userID + " and menu: " + text);
                                    User.setUser(userID, "state", "0");
                                    return;
                                }
                                
                            }
                            else if(stateArr[1].equals("json")) {
                                if (!Food.createMenuJson(userID, text)) {
                                    this.replyText(replyToken, "createMenuJson() failed with userID: " + userID + " and menu: " + text);
                                    User.setUser(userID, "state", "0");
                                    return;
                                }
                                
                            }
                            else{
                                this.replyText(replyToken, "Wrong state in handleTextContent(): " + state);
                                User.setUser(userID, "state", "0");
                                return;
                            }
                        
                        String recommendation = Food.getMenuInfo(userID);
                        if (!recommendation.contains("You haven't input a menu")) {
                            this.replyText(replyToken, recommendation + "\nWhat do you want to choose?");
                            User.setUser(userID, "state", "warning");
                        } else {
                            this.replyText(replyToken, recommendation);
                            User.setUser(userID, "state", "0");
                            return;
                        }
                        
                    }
                    else if(stateArr[0].equals("warning")) {
                        String response = new String();
                        if(Food.WarningforFoodSelection(userID,text).contains("database"))
                        {response+=Food.WarningforFoodSelection(userID, text);
                            this.replyText(replyToken, response);
                            User.setUser(userID, "state", "0");
                            }
                        else if(!Food.FoodinMenu(userID, text)){
                            response += "Your selection is not in the menu you've provided. However we still make the following recommendation based on your food selection. \n";
                            response += Food.WarningforFoodSelection(userID, text) + " If you are going to select this food we suggest you to eat " + Food.foodPortion(userID, text) + " of the food.";
                            this.replyText(replyToken, response);
                            
                            User.setUser(userID, "state", "0");
                        }
                        else{
                        response += Food.WarningforFoodSelection(userID, text) + " If you are going to select this food we suggest you to eat " + Food.foodPortion(userID, text) + " of the food.";
                        this.replyText(replyToken, response);

                            User.setUser(userID, "state", "0");}
                        
                    }
                    else if(stateArr[0].equals("check")) {
                        
                            if(stateArr[1].equals("nutrition")) { this.replyText(replyToken, Food.checkNutrition(text)); }
                            else if(stateArr[1].equals("appropriate")) { this.replyText(replyToken, Food.checkAppropriate(userID, text)); }
                            else { this.replyText(replyToken, "Wrong state in handleTextContent(): " + state); }
                        
                        User.setUser(userID, "state", "0");
                        
                    }
                    else if(stateArr[0].equals("0")) {
                        String updateImage = createUri("/static/buttons/update.jpg");
                        String recommendationImage = createUri("/static/buttons/recommend.jpg");
                        String summaryImage = createUri("/static/buttons/summary.jpg");
                        String checkImage = createUri("/static/buttons/check.jpg");
                        // String imageUrl = createUri("/static/buttons/1040.jpg");
                        CarouselTemplate carouselTemplate = new CarouselTemplate(
                                        Arrays.asList(
                                                        new CarouselColumn(updateImage, "Update", "update", Arrays.asList(
                                                                        new PostbackAction("weight", "update weight"),
                                                                        new PostbackAction("food", "update food"),
                                                                        new PostbackAction("goal", "update target_weight")
                                                        )),
                                                        new CarouselColumn(recommendationImage, "Get recommendation from menu", "get recommendation from menu", Arrays.asList(
                                                                        new PostbackAction("text menu", "menu text"),
                                                                        new PostbackAction("json menu", "menu json"),
                                                                        new PostbackAction("jpeg menu", "menu jpeg")
                                                        )),
                                                        new CarouselColumn(summaryImage, "Summary", "summary", Arrays.asList(
                                                                        new PostbackAction("daily summary", "summary daily"),
                                                                        new PostbackAction("weekly summary", "summary weekly"),
                                                                        new PostbackAction("overall summary", "summary overall")
                                                        )),
                                                        new CarouselColumn(checkImage, "Check", "check", Arrays.asList(
                                                                        new PostbackAction("check nutrition", "check nutrition"),
                                                                        new PostbackAction("should I eat this?", "check appropriate"),
                                                                        new PostbackAction("healthy tips", "check tips")
                                                        ))
                                        ));
                        TemplateMessage templateMessage = new TemplateMessage("general options", carouselTemplate);
                        this.reply(replyToken, templateMessage);
                        
                }
                else {
                    this.replyText(replyToken, "Wrong state in handleTextContent(): " + state);
                    User.setUser(userID, "state", "0");
                    
                }
    }

    /**
     * This method create URL string for the object on the corresponding path
     * @param path This the path for the object needed to create URL
     * @return String This is the URL after transformation
     */
	static String createUri(String path) {
		return ServletUriComponentsBuilder.fromCurrentContextPath().path(path).build().toUriString();
	}

	private void system(String... args) {
		ProcessBuilder processBuilder = new ProcessBuilder(args);
		try {
			Process start = processBuilder.start();
			int i = start.waitFor();
			log.info("result: {} =>  {}", Arrays.toString(args), i);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		} catch (InterruptedException e) {
			log.info("Interrupted", e);
			Thread.currentThread().interrupt();
		}
	}

	private static DownloadedContent saveContent(String ext, MessageContentResponse responseBody) {
		log.info("Got content-type: {}", responseBody);

		DownloadedContent tempFile = createTempFile(ext);
		try (OutputStream outputStream = Files.newOutputStream(tempFile.path)) {
			ByteStreams.copy(responseBody.getStream(), outputStream);
			log.info("Saved {}: {}", ext, tempFile);
			return tempFile;
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

    /**
     * This method is used to created temperory file for downloaded content
     * @param ext This is the file extension of the temperory file
     * @return DownloadedContent Return a DownloadedContent object as an temporary file
     */
	private static DownloadedContent createTempFile(String ext) {
		String fileName = LocalDateTime.now().toString() + '-' + UUID.randomUUID().toString() + '.' + ext;
		Path tempFile = KitchenSinkApplication.downloadedContentDir.resolve(fileName);
		tempFile.toFile().deleteOnExit();
		return new DownloadedContent(tempFile, createUri("/downloaded/" + tempFile.getFileName()));
	}



    /**
     * This class is to generate constructor and getter for the class below
     * see example at https://projectlombok.org/features/Value
     */
    @Value //The annontation @Value is from the package lombok.Value
    public static class DownloadedContent {

        /**
         * path of the downloaded content
         */
        Path path;

        /**
         * URI of the downloaded content
         */
        String uri;
    }



    /**
     * This class is to get user profile and status message
     */
	class ProfileGetter implements BiConsumer<UserProfileResponse, Throwable> {


		private KitchenSinkController ksc;

		private String replyToken;

        /**
         * This is the constructor for the class
         * @param ksc This is the kitChenSinController object
         * @param replyToken This is the replyToken from user
         */
		public ProfileGetter(KitchenSinkController ksc, String replyToken) {
			this.ksc = ksc;
			this.replyToken = replyToken;
		}


		@Override
    	public void accept(UserProfileResponse profile, Throwable throwable) {
    		if (throwable != null) {
            	ksc.replyText(replyToken, throwable.getMessage());
            	return;
        	}
        	ksc.reply(
                	replyToken,
                	Arrays.asList(new TextMessage(
                		"Display name: " + profile.getDisplayName()),
                              	new TextMessage("Status message: "
                            		  + profile.getStatusMessage()))
        	);
    	}
    }

    /**
     * This medthod simplifys the input of function "push"
     * @param userID This is the userID of the receiver of pushed message
     * @param message This is the message to be pushed to the receiver
     */
    private void push(@NonNull String userID, @NonNull Message message) {
        push(userID, Collections.singletonList(message));
    }

    /**
     * This method is used to push message to user
     * @param userID This is the user ID for the receiver of pushed message
     * @param messages This is the message being pushed
     */
    private void push(@NonNull String userID, @NonNull List<Message> messages) {
        try {
            BotApiResponse apiResponse = lineMessagingClient.pushMessage(new PushMessage(userID, messages)).get();
            log.info("Push messages: {}", apiResponse);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * The Timer class works as a remainder with two functions to
     * remaind user to update intaked food at specific time (hk time 10am, 2pm and 9pm)
     */
    private class Timer implements Runnable {
        
        /**
         * A new thread for immplementation of Timer class
         */
        private Thread t;
        
        /**
         * user ID of the receiver for pushing remainder mesasge
         */
        private String userID;
        
        
        
        /**
         * constructor for Timer class
         * @param userID This is the string input assigned to the data member userID
         */
        Timer(String userID) {
            this.userID=userID;
            
            
        }
        
        
        /**
         * This method implements the functions of timer class. By checking the current time in specific time,
         * this function able to send update food remainder at specific time.
         */
        public void run() {
            while(true){
                Date current = new Date();
                SimpleDateFormat minuteFormat = new SimpleDateFormat("mm");
                SimpleDateFormat hourFormat = new SimpleDateFormat("HH");
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                String current_time_string = timeFormat.format(current);
                String current_minute_string = minuteFormat.format(current);
                String current_hour_string = hourFormat.format(current);
                int current_hour = Integer.parseInt(current_hour_string);
                int current_minutes = Integer.parseInt(current_minute_string);
                
                int b_t=2;
                int l_t=6;
                int d_t = 13;
                int target_min = 00;
                
                int breakfast_call_distant = b_t - current_hour;
                int lunch_call_distant = l_t -current_hour;
                int dinner_call_distant = d_t-current_hour;
                
                
                
                int waithour = 0;
                int waitminute =0;
                
                
                if (breakfast_call_distant==0 || lunch_call_distant==0 || dinner_call_distant==0){
                    if(breakfast_call_distant==0) waithour = l_t - b_t -1;
                    else if(lunch_call_distant==0) waithour = d_t - l_t -1;
                    else waithour = 24 - d_t + b_t - 1;
                    
                }
                
                
                else {
                    
                    if ( breakfast_call_distant < 0 && lunch_call_distant<0 && dinner_call_distant<0){
                        waithour = b_t + 24 -current_hour-1;
                    }
                    
                    else if ( breakfast_call_distant < 0 && lunch_call_distant<0 ){
                        waithour =dinner_call_distant-1;
                    }
                    
                    else if (breakfast_call_distant < 0){
                        waithour=lunch_call_distant-1;
                    }
                    
                    else
                        waithour=breakfast_call_distant-1;
                }
                
                
                if (target_min==0){
                    waitminute = 60 - current_minutes;
                }
                else if (current_minutes>target_min && target_min!=0){
                    waitminute = current_minutes - target_min;
                }
                else
                    waitminute = target_min - current_minutes;
                
                
                
                int totalSleepTime = (60*60*waithour+60*waitminute-2*60)*1000;
                int totalmin=60*waithour+waitminute ;
                
                //push(userID, new TextMessage("sleep"+ Integer.toString(totalmin)));
                
                try {
                    Thread.sleep(totalSleepTime);
                } catch (Exception e) {
                    log.info("thread fail");
                  // push(userID, new TextMessage("fail"));
                }
                
              // push(userID, new TextMessage("wake up"));
              //  push(userID, new TextMessage(current_time_string));
                while(true){
                    Date current2 = new Date();
                    current_time_string = timeFormat.format(current2);
                   // push(userID, new TextMessage(current_time_string));
                    if (current_time_string.equals("02:00:00") || current_time_string.equals("06:00:00") || current_time_string.equals("13:00:00")){
                        
                        
                      push(userID, new TextMessage("Please update food of your last meal if you have not done yet."));
                        try {
                            Thread.sleep(1000); // thread to sleep for 1000 milliseconds
                        } catch (Exception e) {
                            log.info("thread fail");
                        }
                        try {

                        Thread.sleep(600000);
                        } catch (Exception e) {
                            log.info("thread fail");
                        }
                        break;
                    }
                }
 
            }
        }
        
        /**
         * This method starts the new thread of the Timer class
         */
        public void start() {
            if (t==null){
                t = new Thread (this, "Timer");
            }
            t.start();
            
        }
    }

}
