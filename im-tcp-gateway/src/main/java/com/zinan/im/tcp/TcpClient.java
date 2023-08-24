package com.zinan.im.tcp;

import com.alibaba.fastjson.JSONObject;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author lzn
 * @date 2023/06/21 14:14
 * @description Tcp Client to test the interaction with Netty tcp server
 */
public class TcpClient {

    public static byte[] generateBytesData() {
        int command = 9999;
        int version = 1;
        int clientType = 4;
        int messageType = 0x0;
        int appId = 10000;
        String name = "123";
        String imei = UUID.randomUUID().toString();

        // Convert parameters to bytes
        byte[] commandByte = intToBytes(command, 4);
        byte[] versionByte = intToBytes(version, 4);
        byte[] messageTypeByte = intToBytes(messageType, 4);
        byte[] clientTypeByte = intToBytes(clientType, 4);
        byte[] appIdByte = intToBytes(appId, 4);
        byte[] imeiBytes = imei.getBytes(StandardCharsets.UTF_8);
        int imeiLength = imeiBytes.length;
        byte[] imeiLengthByte = intToBytes(imeiLength, 4);
        byte[] password = encrypt().getBytes(StandardCharsets.UTF_8);
        int pwdLength = password.length;
        byte[] pwdLengthByte = intToBytes(pwdLength, 4);

        // Construct the JSON data
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("appId", appId);
        data.put("clientType", clientType);
        data.put("imei", imei);
        String jsonData = JSONObject.toJSONString(data);
        byte[] body = jsonData.getBytes(StandardCharsets.UTF_8);
        int bodyLen = body.length;
        byte[] bodyLenBytes = intToBytes(bodyLen, 4);

        // Create the byte array for the request
        return concatArrays(commandByte, versionByte, clientTypeByte,
                messageTypeByte, appIdByte, pwdLengthByte, imeiLengthByte, bodyLenBytes, password, imeiBytes, body);
    }

    public static byte[] intToBytes(int value, int length) {
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            bytes[length - 1 - i] = (byte) (value >> (i * 8));
        }
        return bytes;
    }

    public static byte[] concatArrays(byte[]... arrays) {
        int totalLength = 0;
        for (byte[] array : arrays) {
            totalLength += array.length;
        }
        byte[] result = new byte[totalLength];
        int offset = 0;
        for (byte[] array : arrays) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

    public static void main(String[] args) {
        // Replace with the server's address
        String serverAddress = "localhost";
        // Replace with the server's port
        int serverPort = 9000;

        try {
            // Connect to the server
            Socket socket = new Socket(serverAddress, serverPort);

            // Get the output stream of the socket
            OutputStream outputStream = socket.getOutputStream();

            // Construct your request data as a byte array
            byte[] requestData = generateBytesData(); // Your request data

            // Send the request data to the server
            outputStream.write(requestData);
            outputStream.flush();

            // Close the socket
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String encrypt() {
        String secretKey = "abcdeabcdeabcdea"; // 128-bit key
        String plainPassword = "mysecretpassword";

        // Create AES key from secret key
        Key key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");

        // Encrypt the password
        byte[] encryptedBytes = new byte[0];
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            encryptedBytes = cipher.doFinal(plainPassword.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Encode encrypted password as base64
        String pwd = Base64.getEncoder().encodeToString(encryptedBytes);
        System.out.println("encrypt password: " + pwd);

        return pwd;
    }

    public static String decrypt(String secretKey, String encryptedText) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes);
    }

    public static void generateSecretKey() {
        try {
            // Generate a 256-bit random key
            SecureRandom secureRandom = SecureRandom.getInstanceStrong();
            byte[] key = new byte[32]; // 256 bits
            secureRandom.nextBytes(key);

            // Print the key as a hexadecimal string
            StringBuilder keyHex = new StringBuilder();
            for (byte b : key) {
                keyHex.append(String.format("%02x", b));
            }
            System.out.println("Generated Key: " + keyHex.toString());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}