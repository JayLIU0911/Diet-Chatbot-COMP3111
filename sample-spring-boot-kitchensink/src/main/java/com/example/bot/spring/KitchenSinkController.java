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

@Slf4j
@LineMessageHandler
public class KitchenSinkController {



	@Autowired
	private LineMessagingClient lineMessagingClient;

	// modification starts
	private Queue< String > contextQ;
	private String state;
	private ArrayList< String > userInit;
	private ArrayList< User > userList = new ArrayList< User >();
	private Menu menu;

	public void promptUser(@NonNull String replyToken) {
		String message = "1. Update weight\n"
						+"2. Get recommendation from menu\n"
						+"3. Get food quality data\n"
						+"4. Show Dieting Summary\n"
						+"5. Generate weekly progress chart\n"
						+"6. Bye~\n"
						+"\nPlease select an option";
		this.replyText(replyToken, message);
	}
	//modification ends

	@EventMapping
	public void handleTextMessageEvent(MessageEvent<TextMessageContent> event) throws Exception {
		log.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		log.info("This is your entry point:");
		log.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		TextMessageContent message = event.getMessage();
		handleTextContent(event.getReplyToken(), event, message);
	}

	@EventMapping
	public void handleStickerMessageEvent(MessageEvent<StickerMessageContent> event) {
		handleSticker(event.getReplyToken(), event.getMessage());
	}

	@EventMapping
	public void handleLocationMessageEvent(MessageEvent<LocationMessageContent> event) {
		LocationMessageContent locationMessage = event.getMessage();
		reply(event.getReplyToken(), new LocationMessage(locationMessage.getTitle(), locationMessage.getAddress(),
				locationMessage.getLatitude(), locationMessage.getLongitude()));
	}

	@EventMapping
	public void handleImageMessageEvent(MessageEvent<ImageMessageContent> event) throws IOException {
		String replyToken = event.getReplyToken();
		if (state.equals("Get recommendation from menu")) {
			// boolean success = false;
			// menu = new Menu(text);
			// for (User u : userList) {
			// 	if (u.getUserId() == id) {
			// 		this.replyText(replyToken, menu.findOptimal().getName());
			// 		success = true;
			// 		break;
			// 	}
			// }
			// if (!success)
			// 	this.replyText(replyToken, "Get recommendation from text menu failed");
		} else {
			final MessageContentResponse response;

			String messageId = event.getMessage().getId();
			try {
				response = lineMessagingClient.getMessageContent(messageId).get();
			} catch (InterruptedException | ExecutionException e) {
				reply(replyToken, new TextMessage("Cannot get image: " + e.getMessage()));
				throw new RuntimeException(e);
			}
			DownloadedContent jpg = saveContent("jpg", response);
			reply(((MessageEvent) event).getReplyToken(), new ImageMessage(jpg.getUri(), jpg.getUri()));
		}
	}

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

	@EventMapping
	public void handleUnfollowEvent(UnfollowEvent event) {
		log.info("unfollowed this bot: {}", event);
	}

	@EventMapping
	public void handleFollowEvent(FollowEvent event) {
		userInit = new ArrayList< String >();
		userInit.add(event.getSource().getUserId());
		String replyToken = event.getReplyToken();

		String imageUrl = createUri("/static/buttons/1040.jpg");
		CarouselTemplate carouselTemplate = new CarouselTemplate(
						Arrays.asList(
										new CarouselColumn(imageUrl, "Welcome! This is a helpful chatbot. We would like to collect your personal information", "", Arrays.asList(
														new PostbackAction("name", "name"),
														new PostbackAction("gender", "gender"),
														new PostbackAction("age", "age"),
														new PostbackAction("height", "height"),
														new PostbackAction("weight", "weight"),
														new PostbackAction("target weight", "target weight"),
														new PostbackAction("target time", "target time")
										))
						));
		TemplateMessage templateMessage = new TemplateMessage("welcome", carouselTemplate);
		this.reply(replyToken, templateMessage);
	}

	@EventMapping
	public void handleJoinEvent(JoinEvent event) {
		String replyToken = event.getReplyToken();
		this.replyText(replyToken, "Joined " + event.getSource());
	}

	@EventMapping
	public void handlePostbackEvent(PostbackEvent event) {
			String replyToken = event.getReplyToken();
			//this.replyText(replyToken, "Got postback " + event.getPostbackContent().getData());
			String postback = event.getPostbackContent().getData();
	    switch (postback) {
					// state = welcome
	        case "name": {
						state = "welcome";
	        	this.replyText(replyToken, "What is your name?");
	        	break;
	        }
	        case "gender": {
	        	this.replyText(replyToken, "Please input your gender(male/female)");
	        	break;
	        }
	        case "age": {
	        	this.replyText(replyToken, "How old are you?");
	        	break;
	        }
	        case "height": {
	        	this.replyText(replyToken, "Please input your height(cm)");
	        	break;
	        }
	        case "weight": {
	        	this.replyText(replyToken, "Please input your weight(kg)");
	        	break;
	        }
	        case "target weight": {
	        	this.replyText(replyToken, "Can you tell me your target weight?(kg)");
	        	break;
	        }
	        case "target time": {
	        	this.replyText(replyToken, "How long do you want to achieve your goal?(day)");
	        	state=null;
	        	break;
	        }

					// state = Update daily record
	        case "update weight": {
						state = "Update daily record";
	        	this.replyText(replyToken, "Please input your weight(kg)");
	        	break;
	        }
	        case "update food": {
						state = null;
	        	this.replyText(replyToken, "what did you eat?");
	        	break;
	        }

					// state = null
	        case "weekly progress chart": {
						String id = event.getSource().getUserId();
						for (User u : userList) {
							if (u.getUserId() == Integer.parseInt(id)) {
								String uri = u.generateWeeklySummary().toURI().toString();
								reply(replyToken, new ImageMessage(uri, uri));
								break;
							}
						}
						break;
	        }
	        case "show Dieting Summary": {
						String id = event.getSource().getUserId();
						for (User u : userList) {
							if (u.getUserId() == Integer.parseInt(id)) {
								this.replyText(replyToken, u.generateSummary());
								break;
							}
						}
						break;
	        }

					// state = Get recommendation from menu
	        case "text menu": {
						state = "Get recommendation from menu";
	        	this.replyText(replyToken, "Please upload a menu in text");
	        	break;
	        }
	        case "json menu": {
						state = "Get recommendation from menu";
	        	this.replyText(replyToken, "Please upload a menu in json");
	        	break;
	        }
	        case "jpeg menu": {
						state = "Get recommendation from menu";
	        	this.replyText(replyToken, "Please upload a menu in jpeg");
	        	break;
	        }
	        
	        default:{
	        	this.replyText(replyToken, "Got postback " + event.getPostbackContent().getData());
	        	break;
	        }
		}
	}

	@EventMapping
	public void handleBeaconEvent(BeaconEvent event) {
		String replyToken = event.getReplyToken();
		this.replyText(replyToken, "Got beacon message " + event.getBeacon().getHwid());
	}

	@EventMapping
	public void handleOtherEvent(Event event) {
		log.info("Received message(Ignored): {}", event);
	}

	private void reply(@NonNull String replyToken, @NonNull Message message) {
		reply(replyToken, Collections.singletonList(message));
	}

	private void reply(@NonNull String replyToken, @NonNull List<Message> messages) {
		try {
			BotApiResponse apiResponse = lineMessagingClient.replyMessage(new ReplyMessage(replyToken, messages)).get();
			log.info("Sent messages: {}", apiResponse);
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	private void replyText(@NonNull String replyToken, @NonNull String message) {
		if (replyToken.isEmpty()) {
			throw new IllegalArgumentException("replyToken must not be empty");
		}
		if (message.length() > 1000) {
			message = message.substring(0, 1000 - 2) + "..";
		}
		this.reply(replyToken, new TextMessage(message));
	}


	private void handleSticker(String replyToken, StickerMessageContent content) {
		reply(replyToken, new StickerMessage(content.getPackageId(), content.getStickerId()));
	}

	private void handleTextContent(String replyToken, Event event, TextMessageContent content)
            throws Exception {
        String text = content.getText();

        log.info("Got text message from {}: {}", replyToken, text);
				int id = Integer.parseInt(event.getSource().getUserId());

        switch (state) {
        case "welcome": {
        	userInit.add(text);

					if (state.equals("welcome")) {
						String imageUrl = createUri("/static/buttons/1040.jpg");
						CarouselTemplate carouselTemplate = new CarouselTemplate(
										Arrays.asList(
														new CarouselColumn(imageUrl, "Welcome! This is a helpful chatbot. We would like to collect your personal information", "", Arrays.asList(
																		new PostbackAction("name", "name"),
																		new PostbackAction("gender", "gender"),
																		new PostbackAction("age", "age"),
																		new PostbackAction("height", "height"),
																		new PostbackAction("weight", "weight"),
																		new PostbackAction("target weight", "target weight"),
																		new PostbackAction("target time", "target time")
														))
										));
						TemplateMessage templateMessage = new TemplateMessage("welcome", carouselTemplate);
						this.reply(replyToken, templateMessage);
					} else {
						userList.add(new User(userInit));
						userInit = null;
					}
					break;
        }
        case "Update daily record": {
					boolean success = false;
					try {
		        Integer.parseInt(text);
						for (User u : userList) {
							if (u.getUserId() == id) {
								u.setWeight(text);
								success = true;
								break;
							}
						}
						if (!success)
							this.replyText(replyToken, "Weight update failed");
						else {
							String imageUrl = createUri("/static/buttons/1040.jpg");
							CarouselTemplate carouselTemplate = new CarouselTemplate(
											Arrays.asList(
															new CarouselColumn(imageUrl, "Update daily record", "", Arrays.asList(
																			new PostbackAction("weight", "update weight"),
																			new PostbackAction("food", "update food")
															))
											));
							TemplateMessage templateMessage = new TemplateMessage("general", carouselTemplate);
							this.reply(replyToken, templateMessage);
						}
			    } catch(NumberFormatException e) {
						if (User.userAdapter.updateRecord(id, text))
			      	this.replyText(replyToken, "Update food record succeed");
						else {
							this.replyText(replyToken, "Update food record succeed");
						}
			    }
					break;
        }
				case "Get recommendation from menu": {
					boolean success = false;
					menu = new Menu(text);
					for (User u : userList) {
						if (u.getUserId() == id) {
							this.replyText(replyToken, menu.findOptimal(u).getName());
							success = true;
							break;
						}
					}
					if (!success)
						this.replyText(replyToken, "Get recommendation from text menu failed");
					break;
				}
				default: {
						String imageUrl = createUri("/static/buttons/1040.jpg");
						CarouselTemplate carouselTemplate = new CarouselTemplate(
										Arrays.asList(
														new CarouselColumn(imageUrl, "Update daily record", "", Arrays.asList(
																		new PostbackAction("weight", "update weight"),
																		new PostbackAction("food", "update food")
														)),
														new CarouselColumn(imageUrl, "Progress", "", Arrays.asList(
																		new PostbackAction("generate weekly progress chart", "weekly progress chart"),
																		new PostbackAction("show Dieting Summary", "show Dieting Summary")
														)),
														new CarouselColumn(imageUrl, "Get recommendation from menu", "", Arrays.asList(
																		new PostbackAction("text menu", "text menu"),
																		new PostbackAction("json menu", "json menu"),
																		new PostbackAction("jpeg menu", "jpeg menu")
														))
										));
						TemplateMessage templateMessage = new TemplateMessage("general", carouselTemplate);
						this.reply(replyToken, templateMessage);
						break;
				}
			}
			// +"3. Get food quality data\n"
			// +"4. Show Dieting Summary\n"
			// +"5. Generate weekly progress chart\n"
            // case "profile": {
            //     String userId = event.getSource().getUserId();
            //     if (userId != null) {
            //         lineMessagingClient
            //                 .getProfile(userId)
            //                 .whenComplete(new ProfileGetter (this, replyToken));
            //     } else {
            //         this.replyText(replyToken, "Bot can't use profile API without user ID");
            //     }
            //     break;
            // }
            // case "confirm": {
            //     ConfirmTemplate confirmTemplate = new ConfirmTemplate(
            //             "Do it?",
            //             new MessageAction("Yes", "Yes!"),
            //             new MessageAction("No", "No!")
            //     );
            //     TemplateMessage templateMessage = new TemplateMessage("Confirm alt text", confirmTemplate);
            //     this.reply(replyToken, templateMessage);
            //     break;
            // }
            // case "carousel": {
            //     String imageUrl = createUri("/static/buttons/1040.jpg");
            //     CarouselTemplate carouselTemplate = new CarouselTemplate(
            //             Arrays.asList(
            //                     new CarouselColumn(imageUrl, "Update daily record", Arrays.asList(
            //                             new PostbackAction("update weight",
            //                                                "weight",
						// 																							 "I want to update my weight(kg)"),
						// 													  new PostbackAction("update food record",
            //                                                "food",
						// 																							 "I want to update my food record")
            //                     )),
            //                     new CarouselColumn(imageUrl, "Update daily record", Arrays.asList(
            //                             new PostbackAction("update weight",
            //                                                "weight",
						// 																							 "I want to update my weight(kg)"),
						// 													  new PostbackAction("update food record",
            //                                                "food",
						// 																							 "I want to update my food record")
            //                     )),
            //                     new CarouselColumn(imageUrl, "hoge", "fuga", Arrays.asList(
            //                             new URIAction("Go to line.me",
            //                                           "https://line.me"),
            //                             new PostbackAction("Say hello1",
            //                                                "hello ã�“ã‚“ã�«ã�¡ã�¯")
            //                     )),
            //                     new CarouselColumn(imageUrl, "hoge", "fuga", Arrays.asList(
            //                             new PostbackAction("è¨€ hello2",
            //                                                "hello ã�“ã‚“ã�«ã�¡ã�¯",
            //                                                "hello ã�“ã‚“ã�«ã�¡ã�¯"),
            //                             new MessageAction("Say message",
            //                                               "Rice=ç±³")
            //                     ))
            //             ));
            //     TemplateMessage templateMessage = new TemplateMessage("Carousel alt text", carouselTemplate);
            //     this.reply(replyToken, templateMessage);
            //     break;
            // }

            // default:
            // 	String reply = null;
            // 	try {
            // 		reply = database.search(text);
            // 	} catch (Exception e) {
            // 		reply = text;
            // 	}
            //     log.info("Returns echo message {}: {}", replyToken, reply);
            //     this.replyText(
            //             replyToken,
            //             itscLOGIN + " says " + reply
            //     );
            //     promptUser();
            //     contextQ = null;
            //     break;

        // contextQ.add(text);
    }

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

	private static DownloadedContent createTempFile(String ext) {
		String fileName = LocalDateTime.now().toString() + '-' + UUID.randomUUID().toString() + '.' + ext;
		Path tempFile = KitchenSinkApplication.downloadedContentDir.resolve(fileName);
		tempFile.toFile().deleteOnExit();
		return new DownloadedContent(tempFile, createUri("/downloaded/" + tempFile.getFileName()));
	}





	public KitchenSinkController() {
		database = new DatabaseEngine();
		itscLOGIN = System.getenv("ITSC_LOGIN");
	}

	private DatabaseEngine database;
	private String itscLOGIN;


	//The annontation @Value is from the package lombok.Value
	//Basically what it does is to generate constructor and getter for the class below
	//See https://projectlombok.org/features/Value
	@Value
	public static class DownloadedContent {
		Path path;
		String uri;
	}


	//an inner class that gets the user profile and status message
	class ProfileGetter implements BiConsumer<UserProfileResponse, Throwable> {
		private KitchenSinkController ksc;
		private String replyToken;

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



}
