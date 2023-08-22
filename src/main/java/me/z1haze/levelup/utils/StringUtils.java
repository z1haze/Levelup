package me.z1haze.levelup.utils;

public class StringUtils {
    public static String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNPQRSTUVWXYZ123456789";
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * chars.length());
            stringBuilder.append(chars.charAt(index));
        }
        return stringBuilder.toString();
    }
}
