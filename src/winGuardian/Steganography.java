package winGuardian;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import javax.imageio.ImageIO;

public class Steganography {
	
	
	private int noOfBytesAllotedToPasswordLength = 32;
	private int bitsInByte = 8;
	
	
	public boolean savePassword(String key, String password) {
		if (downloadImage(key)) {
			writePasswordToImage(key, password);	
			showPassword(key);
		}
		return false;
	}
	
	
	public boolean downloadImage(String key) {
		try {
			URL imageUrl = new URL("https://source.unsplash.com/random");
			InputStream inpStream = imageUrl.openStream();
			OutputStream optStream = new FileOutputStream("C:\\Users\\Srivatsa\\Desktop\\" + key + ".jpg");
			
			byte[] b = new byte[2048];
			int length;
			
			while ((length = inpStream.read(b)) != -1) {
				optStream.write(b, 0, length);
			}

			inpStream.close();
			optStream.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	public boolean writePasswordToImage(String key, String password) {
		BufferedImage stegImage = getImage(key);
		byte[] stringBytes = password.getBytes();
		System.out.println(stringBytes);
		int imageBytesLen = stegImage.getHeight()*stegImage.getWidth();
		int stringBytesLen = stringBytes.length;
		if (stringBytesLen*bitsInByte + noOfBytesAllotedToPasswordLength > imageBytesLen) {
			System.err.println("Image is small to hide entire phrase");
			return false;
		} else {
			System.out.println(stringBytesLen);
			for (int x=0; x<32; x++) {
				int rgbVal = stegImage.getRGB(x, 0);
				rgbVal = ((rgbVal & ~1) | (stringBytesLen & 1)); 
				stegImage.setRGB(x, 0, rgbVal);
				stringBytesLen = stringBytesLen >> 1;
			}
			int x = 32;
			int y = 0;
			for (int index=0; index<stringBytes.length; index++) {
				for (int bitPos=0;bitPos<8; bitPos++) {
					int rgbVal = stegImage.getRGB(x, y);
					rgbVal = ((rgbVal & ~1) | (stringBytes[index] & 1)); 
					stegImage.setRGB(x, y, rgbVal);
					stringBytes[index] = (byte) (stringBytes[index] >> 1);
					x += 1;
					if (x == stegImage.getWidth()) {
						x = 0;
						y += 1;
					}
				}
			}
			saveImage(stegImage, key);
			
		}
		return false;
	}
	
	
	public String showPassword(String key) {
		BufferedImage stegImage = getImage(key);
		int length = 0;
		for (int x=31; x>=0; --x) {
			length = length << 1;
			int rgbVal = stegImage.getRGB(x, 0);
			length = (length | (rgbVal & 1)); 
		}
		System.out.println(length);
		byte[] password = new byte[length];
		int x = 32;
		int y = 0;
		for (int index=0; index<length; index++) {
			int passwordByte =  0;
			for (int bitPos=0;bitPos<8; bitPos++) {
				int rgbVal = stegImage.getRGB(x, y);
				passwordByte = (passwordByte | (rgbVal & 1)); 
				passwordByte = (passwordByte << 1);
				x += 1;
				if (x == stegImage.getWidth()) {
					x = 0;
					y += 1;
				}	
			}
			System.out.println(passwordByte);
			password[index] = (byte) passwordByte;
		}
		String str;
		try {
			str = new String(password, "UTF-8");
			System.out.println(str);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} 
		return "Password is not retrievable";
	}
	
	
	private BufferedImage getImage(String key) {
		File imageFile = getImageFile(key);
		try {
			return ImageIO.read(imageFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	private void saveImage(BufferedImage image, String key) {
		File imageFile = getImageFile(key);
		try {
			ImageIO.write(image, "png", imageFile);	
		} catch (IOException e) {
			
		}
	}
	
	
	private File getImageFile(String key) {
		return new File("C:\\Users\\Srivatsa\\Desktop\\" + key + ".jpg");
	}
	
}
