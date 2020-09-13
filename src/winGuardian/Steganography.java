package winGuardian;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Properties;

import javax.imageio.ImageIO;

public class Steganography {
	
	
	private int noOfBytesAllotedToPasswordLength = 32;
	private int bitsInByte = 8;
	private String pngExtension = ".png";

	public String imagePath() {
		Properties prop = new Properties();
		try {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();           
			InputStream stream = loader.getResourceAsStream("credentials.properties");
			prop.load(stream);	
			return prop.getProperty("imagePath");
		} 
		catch (IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * Save Password
	 * @param key
	 * @param password
	 * @return true if password was successfully saved
	 */
	public boolean savePassword(String key, String password) {
		if (downloadImage(key)) {
			return writePasswordToImage(key, password);	
		}
		return false;
	}
	
	
	/**
	 * Donwload Image
	 * @param key
	 * @return true if image was successfully downloaded
	 */
	public boolean downloadImage(String key) {
		try {
			URL imageUrl = new URL("https://source.unsplash.com/random");
			InputStream inpStream = imageUrl.openStream();
			OutputStream optStream = new FileOutputStream(imagePath() + key + pngExtension);
			
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
	
	
	/**
	 * Write Password To Image
	 * @param key
	 * @param password
	 * @return true if password was successfully written to image
	 */
	public boolean writePasswordToImage(String key, String password) {
		BufferedImage stegImage = getImage(key);
		try {
			byte[] stringBytes = password.getBytes();
			int imageBytesLen = stegImage.getHeight()*stegImage.getWidth();
			int stringBytesLen = stringBytes.length;
			if (stringBytesLen*bitsInByte + noOfBytesAllotedToPasswordLength > imageBytesLen) {
				System.err.println("Image is small to hide entire phrase");
				return false;
			} else {
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
						stringBytes[index] = (byte) (stringBytes[index] >>> 1);
						x += 1;
						if (x == stegImage.getWidth()) {
							x = 0;
							y += 1;
						}
					}
				}
				saveImage(stegImage, key);	
				System.out.println("New password was created for: " + key);
				return true;
			}
		} catch (NullPointerException e) {
			// No file found
		}
		return false;
	}
	
	
	/**
	 * Show Password
	 * @param key
	 */
	public void showPassword(String key) {
		BufferedImage stegImage = getImage(key);
		try {
			int length = 0;
			for (int x=31; x>=0; --x) {
				length = length << 1;
				int rgbVal = stegImage.getRGB(x, 0);
				length = (length | (rgbVal & 1)); 
			}
			byte[] password = new byte[length];
			int x = 32;
			int y = 0;
			for (int index=0; index<length; index++) {
				int passwordByte =  0;
				for (int bitPos=0;bitPos<8; bitPos++) {
					int rgbVal = stegImage.getRGB(x, y);
					passwordByte = (passwordByte | ((rgbVal & 1) << bitPos));
					x += 1;
					if (x == stegImage.getWidth()) {
						x = 0;
						y += 1;
					}	
				}
				password[index] = (byte) passwordByte;
			}
			try {
				String passwordStr = new String(password, "UTF-8");
				System.out.println("Password: " + passwordStr);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} 
		} catch (NullPointerException e) {
			// No file found
		}
	}
	
	
	/**
	 * Get Image
	 * @param key
	 * @return returns a BufferedImage with saved with name '{key}'
	 */
	private BufferedImage getImage(String key) {
		File imageFile = getImageFile(key);
		try {
			return ImageIO.read(imageFile);
		} catch (IOException e) {
			System.out.println("This key has not been saved. Please save it before accessing it.");
		}
		return null;
	}
	
	
	/**
	 * Save Image
	 * @param image
	 * @param key
	 * Saves image with the name '{key}'
	 */
	private void saveImage(BufferedImage image, String key) {
		File imageFile = getImageFile(key);
		try {
			ImageIO.write(image, pngExtension.substring(1), imageFile);	
		} catch (IOException e) {
			System.out.println("No such file to save password. Please try again.");
		}
	}
	
	
	/**
	 * Get Image File
	 * @param key
	 * @return returns a file object with the specifies key
	 */
	private File getImageFile(String key) {
		return new File(imagePath() + key + pngExtension);
	}
	
}
