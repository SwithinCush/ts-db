/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ts.db.connector;

import java.io.ByteArrayOutputStream;
import javax.crypto.Cipher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class CipherPassword
 *
 * @author daibheid
 */
public abstract class CipherPassword implements Password {

    private static final Logger log = LoggerFactory.getLogger(CipherPassword.class);
    
    private static String secretKey = "";
    
    private String transformedString;
    
    public final String getTransformedString() {
        if(transformedString != null)
            return transformedString;
        return "";
    }
    
    public final void setTransformedString(String transformedString) {
        if(transformedString != null)
            this.transformedString = transformedString;
    }
    
    public final String getRowString() {
        if(transformedString != null) 
            return decrypt(transformedString);
        return "";
    }
    
    public final void setRowString(String rowString) {
        if (rowString != null)
            this.transformedString = encrypt(rowString);
    }
    
    public final boolean hasPassword() {
        return transformedString != null;
    }
    
    public static void setSecretKey(String secretKey) {
        assert secretKey != null && secretKey.length() > 0;
        CipherPassword.secretKey = secretKey;
    }
    
    private String encrypt(String rowString) {
        try {
            Cipher cipher = getCipherInstance(secretKey, Cipher.ENCRYPT_MODE);
            byte[] encrypted = cipher.doFinal(rowString.getBytes());
            return toHexString(encrypted);
        }
        catch (Exception ex) {
            log.warn("", ex);
            return "";
        }
    }
    
    private String decrypt(String cryptedString) {
        try {
            Cipher cipher = getCipherInstance(secretKey, Cipher.DECRYPT_MODE);
            byte[] decrypted = cipher.doFinal(toBytes(cryptedString));
            return new String(decrypted);
        }
        catch (Exception ex) {
            log.warn("", ex);
            return "";
        }
    }
    
    private static String toHexString(byte[] bytes) {
        StringBuffer buffer = new StringBuffer();
        for(byte b : bytes) {
            buffer.append(String.format("%02X", b & 0xFF));
        }
        return buffer.toString();
    }
    
    private static byte[] toBytes(String hexString) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        for(int i = 0; i < hexString.length(); i += 2) {
            String s = hexString.substring(i, i + 2);
            bos.write(Integer.parseInt(s, 16));
        }
        return bos.toByteArray();
    }
    
    protected abstract Cipher getCipherInstance(String key, int mode);
} 

