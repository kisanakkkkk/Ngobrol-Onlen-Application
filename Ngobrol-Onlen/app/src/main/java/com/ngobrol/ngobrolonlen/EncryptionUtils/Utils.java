package com.ngobrol.ngobrolonlen.EncryptionUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Utils {

    public static String AES = "AES/CBC/PKCS5Padding";

    public static String PROTOCOL_SEPARATOR = "@@";

    public static int COMMAND_INDEX      = 0;
    public static int MESSAGE_INDEX      = 1;
    public static int PUBLIC_KEY         = 3;
    public static int ENCRYPTED_MESSAGE  = 4;
    public static int REQUEST_NEW_KEY    = 5;
    public static int REQUEST_PUBLIC_KEY = 6;
    public static int READY              = 8;

    public static byte[] concatenateByteArrays(byte[]... byteArrays) {
        int totalLength = 0;
        for (byte[] array : byteArrays) {
            totalLength += array.length;
        }

        byte[] result = new byte[totalLength];
        int currentIndex = 0;

        for (byte[] array : byteArrays) {
            System.arraycopy(array, 0, result, currentIndex, array.length);
            currentIndex += array.length;
        }

        return result;
    }

    public static byte[] hexStringToByteArray(String hexString) {
        int length = hexString.length();

        if (length % 2 != 0) {
            throw new IllegalArgumentException("Hex string must have an even number of characters");
        }

        byte[] byteArray = new byte[length / 2];

        for (int i = 0; i < length; i += 2) {
            String hexPair = hexString.substring(i, i + 2);
            byteArray[i / 2] = (byte) Integer.parseInt(hexPair, 16);
        }

        return byteArray;
    }
    public static String byteArrayToHexString(byte[] byteArray) {
        StringBuilder hexStringBuilder = new StringBuilder();

        for (byte b : byteArray) {
            // Convert each byte to a two-digit hexadecimal representation
            hexStringBuilder.append(String.format("%02X", b));
        }

        return hexStringBuilder.toString();
    }
    public static String byteArrayToString(byte[] byteArray) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : byteArray) {
            stringBuilder.append((char)b);
        }
        return stringBuilder.toString();
    }
    public static String encodeBase64(byte[] data) {
        return Encoding.base64Encode(data);
    }

    public static byte[] decodeBase64(String data) {
        return Encoding.base64Decode(data);
    }

    public synchronized static String decrypt(byte[] iv, String encryptedMessage, SecretKeySpec aesKey) throws Exception {

        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.DECRYPT_MODE,aesKey, new IvParameterSpec(iv));


        byte[] decodedMessage = hexStringToByteArray(encryptedMessage);
        byte[] recovered = cipher.doFinal(decodedMessage);

        return new String(recovered);
    }

    public synchronized static String encrypt(byte[] iv, String message, SecretKeySpec aesKey) throws Exception {
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.ENCRYPT_MODE, aesKey, new IvParameterSpec(iv));

        byte[] plainText = message.getBytes();
        byte[] cipherText = cipher.doFinal(plainText);

        return byteArrayToHexString(concatenateByteArrays(iv, cipherText));
    }

    public synchronized static String decrypt(String encryptedMessage, String aesKey) throws Exception {
        Cipher cipher = Cipher.getInstance(AES);

        byte[] key = aesKey.getBytes();

        SecretKeySpec secretKeySpec = new SecretKeySpec(key, AES);

        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);



        byte[] decodedMessage = Encoding.base64Decode(encryptedMessage);
        byte[] recovered = cipher.doFinal(decodedMessage);

        return new String(recovered);
    }

    public synchronized static String encrypt(String message, String aesKey) throws Exception {
        Cipher cipher = Cipher.getInstance(AES);
        byte[] key = aesKey.getBytes();


        SecretKeySpec secretKeySpec = new SecretKeySpec(key, AES);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

        byte[] plainText = message.getBytes();
        byte[] cipherText = cipher.doFinal(plainText);

        return encodeBase64(cipherText);
    }

    public static SecretKeySpec generateAESKey(byte[] secretKey){
        return new SecretKeySpec(secretKey, 0, 16, AES);
    }

    public static void delay(int millis){
        try {
            Thread.sleep(millis);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    public static int retrieveCommand(String data){
        System.err.println(data);
        return Integer.parseInt(data.split(PROTOCOL_SEPARATOR)[COMMAND_INDEX]);
    }

    public static String retrieveMessage(String data){
        return data.split(PROTOCOL_SEPARATOR)[MESSAGE_INDEX];

    }

}