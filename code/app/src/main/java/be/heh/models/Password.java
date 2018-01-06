package be.heh.models;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This class creates an encrypted password.
 *
 * @author DUCOBU Alexandre
 */
public class Password {

    private static String generatedPassword;

    /**
     * Constructor of an encrypted password.
     * The password is salted and hashed with SHA-512.
     *
     * @param passwordToHash
     *      The password to hash.
     */
    public Password(String passwordToHash) {
        String salt = "+%#hkgjdf" + passwordToHash.length() + "¨*$mkl@67";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt.getBytes("UTF-8"));
            byte[] bytes = md.digest(passwordToHash.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++){
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the generated password.
     *
     * @return the generated password.
     */
    public static String getGeneratedPassword() {
        return generatedPassword;
    }
}
