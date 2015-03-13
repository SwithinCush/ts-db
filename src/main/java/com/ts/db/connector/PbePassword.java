/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ts.db.connector;

import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/**
 * Class PbePassword
 *
 * @author daibheid
 */
public class PbePassword extends CipherPassword {

    private static final String TRANSFORMATION_NAME = "PBEWithMD5AndDES";
    private static final byte[] SALT = "1066ts-db".getBytes();
    private static final int ITERATION = 10;
    
    protected Cipher getCipherInstance(String code, int mode) {
        try {
            PBEKeySpec keySpec = new PBEKeySpec(code.toCharArray());
            SecretKey key = SecretKeyFactory.getInstance(TRANSFORMATION_NAME).generateSecret(keySpec);
            PBEParameterSpec spec = new PBEParameterSpec(SALT, ITERATION);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION_NAME);
            cipher.init(mode, key,spec);
            return cipher;
        }
        catch (GeneralSecurityException ex) {
            throw new RuntimeException(ex);
        }
    }
} 

