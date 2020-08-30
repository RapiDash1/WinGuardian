package winGuardian;

import java.io.Console;

public class CommandLine {
	
	
	public CommandLine() {
		checkMasterPassword();
		chooseOption();
	}
	
	
	public void chooseOption() {
		Console c = System.console();
		System.err.println("Choose an option: ");
		System.err.println("  1.Get passowrd: ");
		System.err.println("  2.Save passowrd: ");
		String option = c.readLine("Enter your option here: ");
		if (option.equals("1")) {
			System.out.println("");
			System.out.println("You have selected - Get password" );
			System.out.println("");
			getPassword();
		}
		else if (option.equals("2")) {
			System.out.println("");
			System.out.println("You have selected - Save password" );
			System.out.println("");
			savePassword();
		}
		else {
			System.out.println("");
			System.out.println("*******************************");
			System.err.println("Please choose a valid option!!!");
			System.out.println("*******************************");
			System.out.println("");
			chooseOption();
		}
		System.out.println("");
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
			System.out.println("");
			System.out.println("***You are logged in***");
			System.out.println("");
			return enteredPassword.equals(actualPassword);
		}
		return false;
	} 
	
	
	public void savePassword() {
		Console c = System.console();
		String key = c.readLine("Enter key: ");
		char[] passwordChars = c.readPassword("Enter passowrd: ");
		System.out.println("");
		Steganography steg = new Steganography();
		steg.savePassword(key, new String(passwordChars));
	}
	
	
	public void getPassword() {
		Console c = System.console();
		String key = c.readLine("Enter key to reveal password: ");
		System.out.println("");
		Steganography steg = new Steganography();
		steg.showPassword(key);
	}
}
