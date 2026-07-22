package com.gsvn.shipmentservice.common.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;

@Component
public class TokenEncryptionUtil {

    private final TextEncryptor encryptor;

    public TokenEncryptionUtil(@Value("${encryption.secret-key}") String secretKey,
                               @Value("${encryption.salt}") String salt) {
        this.encryptor = Encryptors.text(secretKey, salt);
    }

    public String encrypt(String plainText) {
        return encryptor.encrypt(plainText);
    }

    public String decrypt(String encryptedText) {
        return encryptor.decrypt(encryptedText);
    }
}