package com.driver.controllers;


import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.google.common.hash.HashCode;
import org.springframework.stereotype.Repository;
import com.driver.model.Booking;
import com.driver.model.Facility;
import com.driver.model.Hotel;
import com.driver.model.User;

import java.util.*;

@Repository
public class HotelManagementRepository {

    static HashMap<String,Hotel> hotels = new HashMap<String,Hotel>();
    static HashMap<Integer,User> users = new HashMap<Integer,User>();
    static HashMap<String,Booking> bookings = new HashMap<String,Booking>();
    static HashMap<Integer,List<String>> userBookings = new HashMap<Integer, List<String>>();

    public String addHotel(Hotel hotel) {
        if(hotels.containsKey(hotel.getHotelName())) return "FAILURE";
        hotels.put(hotel.getHotelName(),hotel);
        return "SUCCESS";
    }

    public void addUser(User user) {
        users.put(user.getaadharCardNo(),user);
    }

    public String getHotelWithMostFacilities() {
        String ans=null;
        int max = 0;
        for(Map.Entry<String,Hotel> e: hotels.entrySet()){
            if(e.getValue().getFacilities().size()>max){
                ans=e.getKey();
                max = e.getValue().getFacilities().size();
            }else if(e.getValue().getFacilities().size()==max && e.getKey().compareTo(ans)<0){
                ans=e.getKey();
                max = e.getValue().getFacilities().size();
            }
        }
        return ans;
    }

    public int bookARoom(Booking booking) {
        int ans = -1;
        Hotel currHotel = hotels.getOrDefault(booking.getHotelName(),null);
        if(currHotel!=null && currHotel.getAvailableRooms()>=booking.getNoOfRooms()){
            String uuid = generateUUID(booking);
            booking.setBookingId(uuid);
            ans=booking.getNoOfRooms()*currHotel.getPricePerNight();
            bookings.put(uuid,booking);
            List<String> ub = userBookings.getOrDefault(booking.getBookingAadharCard(), new ArrayList<String>());
            ub.add(uuid);
            userBookings.put(booking.getBookingAadharCard(),ub);
        }
        return ans;
    }


    private String generateUUID(Booking booking){
        return UUID.randomUUID().toString();
//        return HashCode.fromString(booking.getBookingPersonName()+booking.getBookingPersonName()+booking.getNoOfRooms()).toString();
    }

    public int getBookings(Integer aadharCard) {
        return userBookings.getOrDefault(aadharCard, new ArrayList<String>()).size();
    }

    public Hotel updateFacilities(List<Facility> newFacilities, String hotelName) {
        Hotel currHotel = hotels.getOrDefault(hotelName,null);
        if(currHotel==null) return null;
        if(currHotel.getFacilities()==null) currHotel.setFacilities(new ArrayList<Facility>());
        List<Facility> facilities = currHotel.getFacilities();
        for(Facility newone: newFacilities){
            if(!facilities.contains(newone)){
                facilities.add(newone);
            }
        }
        currHotel.setFacilities(facilities);
        hotels.put(hotelName,currHotel);
        return currHotel;
    }
}
