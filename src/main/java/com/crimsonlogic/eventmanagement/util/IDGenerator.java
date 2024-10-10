package com.crimsonlogic.eventmanagement.util;

import java.util.UUID;

public class IDGenerator {

	public static String generateCustomID(String prefix, int length) {
		String randomString = UUID.randomUUID().toString().replaceAll("-", "").substring(0, length);
		return prefix + randomString.toUpperCase();
	}

	public static String generateCategoryID() {
		return generateCustomID("CAT-", 6);
	}

	public static String generateEventID() {
		return generateCustomID("EVE-", 6);
	}
	
	public static String generateUserID() {
		return generateCustomID("URI-", 6);
	}
	
	public static String generateUserDetailID() {
		return generateCustomID("UDI-", 6);
	}
	
	public static String generateRoleID() {
		return generateCustomID("RLI-", 6);
	}
	
	public static String generateWalletID() {
		return generateCustomID("WLI-", 6);
	}
	
	public static String generateSponsorID() {
		return generateCustomID("SPI-", 6);
	}
	
	public static String generateTicketPriceID() {
		return generateCustomID("TPI-", 6);
	}
	
	public static String generateVenueID() {
		return generateCustomID("VEI-", 6);
	}
	
	public static String generateBookingID() {
		return generateCustomID("BKI-", 6);
	}
	
	public static String generateBookingPaymentID() {
		return generateCustomID("BPI-", 6);
	}
	
}
