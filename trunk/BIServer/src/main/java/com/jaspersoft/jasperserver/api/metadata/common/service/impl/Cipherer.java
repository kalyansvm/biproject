/*
 * Copyright (C) 2007 JasperSoft http://www.jaspersoft.com
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed WITHOUT ANY WARRANTY; and without the 
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see http://www.gnu.org/licenses/gpl.txt 
 * or write to:
 * 
 * Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330,
 * Boston, MA  USA  02111-1307
 */

package com.jaspersoft.jasperserver.api.metadata.common.service.impl;


import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class provides the utilities of a cryptographic cipher for encryption and decryption.
 *
 */
public class Cipherer {

	private static Log log = LogFactory.getLog(Cipherer.class);
	
    
	private static final byte[] DEFAULT_KEY_BYTES = {(byte)0xC8, (byte)0x43, (byte)0x29, (byte)0x49, 
		                               (byte)0xAE, (byte)0x25, (byte)0x2F, (byte)0xA1, 
		                               (byte)0xC1, (byte)0xF2, (byte)0xC8, (byte)0xD9, 
		                               (byte)0x31, (byte)0x01, (byte)0x2C, (byte)0x52, 
		                               (byte)0x54, (byte)0x0B, (byte)0x5E, (byte)0xEA, 
		                               (byte)0x9E, (byte)0x37, (byte)0xA8, (byte)0x61 };
	
	//Create an 8-byte initialization vector
	private static final byte[] INIT_VECTOR = { (byte)0x8E, (byte)0x12, (byte)0x39, (byte)0x9C,
												(byte)0x07, (byte)0x72, (byte)0x6F, (byte)0x5A};
	
	private static Cipher E_CIPHER = null;
	private static Cipher D_CIPHER = null;
	
	private static final String DEFAULT_CIPHER_TRANSFORMATION = "DESede/CBC/PKCS5Padding";
	private static final String DEFAULT_KEY_ALGORITHM = "DESede";
	
	private byte[] keyBytes;
	private String cipherTransformation;
	private String keyAlgorithm;
	
    /**
     * 
     */
    public Cipherer() {
    	keyBytes = DEFAULT_KEY_BYTES;
    	cipherTransformation = DEFAULT_CIPHER_TRANSFORMATION;
    	keyAlgorithm = DEFAULT_KEY_ALGORITHM;
    }

	
    /**
     * Initializes the encoder and decoder with the given parameters
     * @param cipherTransformation
     * @param keyAlgorithm
     * @param keyBytes
     * @param isPlainText
     */
    public void init(String inCipherTransformation, String inKeyAlgorithm, String inKeyBytes, boolean isPlainText) {
    	cipherTransformation = inCipherTransformation;
    	keyAlgorithm= inKeyAlgorithm;
    	setKeyBytes(inKeyBytes, isPlainText);
    	init();
    }
    
    /**
	 * Initializes the encoder and decoder. 
	 * Note: The vaues of CIPHER_TRANSFORMATION, KEY_BYTES, KEY_ALGORITHM should be set before calling this method,
	 *       otherwise it will use the default values.
	 */
	public void init() {
		try {
			AlgorithmParameterSpec paramSpec = new IvParameterSpec(INIT_VECTOR);

			E_CIPHER = Cipher.getInstance(cipherTransformation);
			D_CIPHER = Cipher.getInstance(cipherTransformation);

			SecretKeySpec spec = new SecretKeySpec(keyBytes, keyAlgorithm);

			// CBC requires an initialization vector
			E_CIPHER.init(Cipher.ENCRYPT_MODE, spec, paramSpec);
			D_CIPHER.init(Cipher.DECRYPT_MODE, spec, paramSpec);
		} catch (java.security.InvalidAlgorithmParameterException e) {
			log.error(e);
		} catch (javax.crypto.NoSuchPaddingException e) {
			log.error(e);
		} catch (java.security.NoSuchAlgorithmException e) {
			log.error(e);
		} catch (java.security.InvalidKeyException e) {
			log.error(e);
		}
	}
	
	/** Encodes and hexifies the given content */
	public String encode(String content){
		try {
			if (content == null) return null;
			return hexify(encode(content.getBytes("UTF-8")));
		} catch (Exception ex) {
			log.error(ex);
			return hexify(encode(content.getBytes()));
		}
	}
	
	/** Encodes the given content*/
	public byte[] encode(byte[] content){
		try {
			return E_CIPHER.doFinal(content);
		} catch (Exception ex) {
			log.error(ex);
			ex.printStackTrace();
			return content;
		}
	}
	
	/** Dehexifies and decodes the given content
	 * * @param content string to be decoded
	 * @return the decoded content.
	 */
	public String decode(String content) {
		try{ 
			if (content == null) return null;
			return new String(decode(dehexify(content)), "UTF-8");
		} catch (Exception ex) {
			log.error(ex);
			return new String(decode(dehexify(content)));
		}
	}
	
	/** Decodes the given content*/
	public byte[] decode(byte[] content) {
		try {
			return D_CIPHER.doFinal(content);
		} catch (Exception ex) {
			log.error(ex);
			ex.printStackTrace();
			return content;
		}
	}
	
	/**
	 * Decodes the given content if the booleanValue is "true" (case insensitive). 
	 * (Utility Function) 
	 * @param content string to be decoded
	 * @param booleanValue specifies whether content needs to be decoded
	 * @return the decoded content if the booleanValue is "true" (case insensitive). Otherwise it just returns content.
	 */
	public String decode(String content, Object booleanValue) {
		if ((booleanValue == null) || !(booleanValue instanceof String) || (content == null) )
			return content;
		boolean isEncrypted = new Boolean((String) booleanValue).booleanValue();
		if (isEncrypted) return decode(content);
		return content;
	}
	
	
	//
    // used in hexifying
    //
	private static final char[] hexChars ={ '0', '1', '2', '3', '4', '5', '6', '7',
        '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	/**
	 * Convert a byte array response to a hex string
	 */
    public static String hexify(byte[] data)
    {
        StringBuffer hex = new StringBuffer();

        for (int i = 0; i < data.length; i++)
        {
            int highBits = ((int)data[i] & 0x000000F0) >> 4;
            int lowBits  = ((int)data[i] & 0x0000000F);
            hex.append(hexChars[highBits]).append(hexChars[lowBits]);
        }

        return(hex.toString());
    }
    
	/**
    * Convert a hex string response to a byte array
    */
    public static byte[] dehexify(String data)
    {
        byte[] bytes = new byte[data.length()/2];
        
        for (int i = 0; i < bytes.length; i++) {
           bytes[i] = (byte) Integer.parseInt(data.substring(2*i, (2*i)+2), 16);
        }

        return bytes;
    }
    


	/**
	 * @param key_bytes The KEY_BYTES to set.
	 * @param isPlainText Whether key_bytes is plain text or a represantation of byte sequence
	 */
	public void setKeyBytes(String inKeyBytes, boolean isPlainText) {
		if (isPlainText) {
			keyBytes = inKeyBytes.getBytes();
		}
		else {
			String[] strs = inKeyBytes.split(" +");
			byte[] b = new byte[strs.length];
			for (int i=0; i< strs.length; i++) {
				b[i] = Integer.decode(strs[i]).byteValue();
				//System.out.print(b[i]+" ");
			}
			keyBytes = b;
		}
		
	}


	/**
	 * @param cipherTransformation The cipherTransformation to set.
	 */
	public void setCipherTransformation(String cipherTransformation) {
		this.cipherTransformation = cipherTransformation;
	}


	/**
	 * @param keyAlgorithm The keyAlgorithm to set.
	 */
	public void setKeyAlgorithm(String keyAlgorithm) {
		this.keyAlgorithm = keyAlgorithm;
	}

	/**
	 * For testing purposes
	 * @param args
	 */
	public static void test(String[] args) {
		/*SecretKey key = null;
		try {
			key = KeyGenerator.getInstance("DESede").generateKey();
		} catch (NoSuchAlgorithmException ex) {
			log.error("Algorihm DESede not found");
		}
		
		byte[] bytes = key.getEncoded();
		System.out.println(hexify(bytes));
		System.out.println(new String(bytes));
		SecretKeySpec spec = new SecretKeySpec(bytes, "DESede");
		System.out.println(key.equals(spec)); //true
		System.out.println(keyBytes[0] == (byte) -56); //true*/
		/******************************************************/
		/*try {
			String s = new String(DEFAULT_KEY_BYTES);
			System.out.println("Key Bytes = <" + s + ">");
			
			byte[] array = s.getBytes();
			StringBuffer buf = new StringBuffer();
	        buf.append(array[0]);
	        for (int i = 1; i< array.length; i++) {
	            buf.append(" ");
	            buf.append(array[i]);
	        }
	       
			System.out.println("Key Bytes = <" +  buf.toString()+ ">");
			
			String myKey = "0xC8 0x43 0x29 0x49 0xAE 0x25 0x2F 0xA1 0xC1 0xF2 0xC8 " +
					       "0xD9 0x31 0x01 0x2C  0x52 0x54 0x0B 0x5E 0xEA 0x9E 0x37 0xA8 0x61";
			
			Cipherer c = new Cipherer();
			c.setKeyBytes(myKey, false);
			System.out.println();
			System.out.println("Equals = " + s.equals(new String(c.keyBytes)));
			
			test(args);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}*/
		String password = "Hello Dolly";
		byte[] passwd = null;
		Cipherer cip = new Cipherer();
		cip.setKeyBytes("this is my key bytes arg", true);
		cip.init();
		try{
			passwd = password.getBytes("UTF-8");
			System.out.println("passwd = " + new String(passwd) + " " + new String(passwd, "UTF-8"));
			
			byte[] encoded = cip.encode(passwd);
			System.out.println("Encoded = " + new String(encoded));
			
			String hex = hexify(encoded);
			System.out.println("Hexified bytes= " + hex);
			
			byte[] dehex = dehexify(hex);
			System.out.println("Dehexify = " + new String(dehex));
			
			System.out.println("valid = " + new String(encoded).equals(new String(dehex)));
			
			byte[] decoded = cip.decode(dehex);
			System.out.println("Decoded = " + new String(decoded));
			
			System.out.println("valid decode = " + new String(decoded).equals(password));
			
			String en = cip.encode(password);
			System.out.println("String encoded: " + en);
			String de = cip.decode(en);
			System.out.println("String decoded: " + de);
			System.out.println("string decoded = " + new String(de).equals(password));
		} catch(Exception ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * Utility API for password encryption/decryption.
	 * Options: -d Dehexifies and Decrypts the given password.
	 *          -e Encrypts and Hexifys the given password.
	 * @param args
	 */
	public static void main(String[] args) {
		String usage = 	"Usage: java com.panscopic.util.CipherUtils {options} <password>\n" +
						"Options:  \n" +
						"-d      decrypts password\n" +
						"-e      encrypts password" ;
		
		if (args.length < 2 || args[0] == null || args[1] == null) {
			System.out.println(usage);
			return;
		}
		Cipherer cip = new Cipherer();
		cip.setKeyBytes("this is my key bytes arg", true);
		cip.init();
		String option = args[0];
		String password = args[1];
		if (option.equals("-d")) {
			String de = cip.decode(password);
			System.out.println("The decoded password for <" + password + "> is <" + de+ ">");
			System.out.println("The re-encoded password for <" + password + "> is <" + cip.encode(de)+ ">");
		} 
		else if (option.equals("-e")) {
			String en = cip.encode(password);
			System.out.println("The encoded password for <" + password + "> is <" + en+ ">");
			System.out.println("The re-decoded password for <" + password + "> is <" + cip.decode(en)+ ">");
		}
		else {
			System.out.println("Unknown option: <" + option + ">");
			System.out.println(usage);
			return;
		}
		
	}
	
}
