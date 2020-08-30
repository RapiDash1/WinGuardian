package winGuardian;

import java.io.Console;

public class CommandLine {
	
	
	public CommandLine() {
//		checkMasterPassword();
		savePassword();
	}
	
	
	public boolean checkMasterPassword() {
		boolean masterPasswordVerified = verifyMasterPassword();
		while (!masterPasswordVerified) {
			System.out.println("");
			System.err.println("OOPS, you got it wrong. Please try again.");
			System.out.println("");
			masterPasswordVerified = verifyMasterPassword();
		}
		return true;
	}
	
	
	public boolean verifyMasterPassword() {
		String actualPassword = "Hi";
		System.out.println("Hi, hope you are having a good day.");
		Console c = System.console();
		if (c == null) {
			  System.err.println("No console.");
			  System.exit(1);
		} else {
			char[] passwordChars = c.readPassword("Please enter your password: ");
			String enteredPassword = new String(passwordChars);
			System.out.println("***You are logged in***");
			return enteredPassword.equals(actualPassword);
		}
		return false;
	} 
	
	
	public void savePassword() {
		Steganography steg = new Steganography();
		steg.savePassword("TEST", "TEST");
	}
}
