package com.example.bot.spring;
import java.util.ArrayList;
import java.util.HashMap;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.Date;
import java.text.*;
import java.util.Calendar;
import java.util.Random;

import java.sql.*;
import java.net.URISyntaxException;
import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * This class is going to do the tasks directly related to the coupon
 * This is a singleton class
 * @author Liu Shuyue, Sun Yusen
 * @since 2017-11-18
 */
@Slf4j
public class Coupon{
    
    /**
     * This is a private static member coupon to let the class be a singleton
     */
    private static Coupon coupon;
    
    /**
     * This is a User private member in order to call the function in User.class
     */
	private User user = new User();
	
	/**
	 * This is a CouponDB private member
	 * We can use this member to change and get things in database
	 */
	private CouponDB couponDB = new CouponDB();
    
    /**
     * This is a singleton implementation of the coupon object. If there is already this object, then we don't create it again.
     * @return coupon If there isn't a coupon object, we create a new one then return it. Else, we return the already existed one.
     */
    public static Coupon getCoupon(){
        if (coupon==null){
            coupon = new Coupon();
        }
        return coupon;
    }

    /**
     * This is a count number
     * It counts the number that the amount of coupons have already been given out
     */
	private int number=0;

	/**
	 * This function is going to generate a random 6-digit code and return it to the user
	 * The 6-digit code should be between 100000 and 999999 and it should be not already exist in the coupon table
	 * And insert the code and the user id into the coupon table
	 * @param id The user id in order to insert
	 * @return code Return the coupon code to the user
	 */
	public String friend_call(String id){
		int code = 1000000;

		do{
			Random rand = new Random();
			code = rand.nextInt((999999 - 100000) + 1) + 100000;
		} while(couponDB.check_code(Integer.toString(code))!=null);

		couponDB.insert_code(id,Integer.toString(code));

		return Integer.toString(code);
	}

	/**
	 * This function is going to check when the user try to get the coupon
	 * It returns the user id of the coupon giver only if the user can get the coupon and the coupon exist
	 * And What's more, the code does not belong to the user and the coupon hasn't been sold out
	 * @param id The user id in order to check whether the user is able to get the coupon. It will check it in the user list table
	 * @param code The coupon code the user type in the chatbot. We should check the code in coupon table
	 * @return result Return "1" if the user cannot get the coupon or coupon sold out. Return "2" if the user use his/her own code. Return "3" if the code cannot found in coupon table. And return the user id of the coupon giver if all the constrains are passed
	 */
	public String code_call(String id, String code){
		String result = null;
		if(!couponDB.check_available(id) || number>5000)
			return "1"; //user follow before time or coupon sold out or user already use the coupon
		else{
			result = couponDB.check_code(code);
			if(result!=null){
			if(result.equals(id))
				return "2";
			else
			{
				number++;
				user.setUser(id,"coupon","no");
				couponDB.delete_code(code);
				return result;
			}}
			else
				return "3";
			
		}
	}

}
