package com.example.a11699.tongxungenxing.Util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MathTools {
    public static int findMax(int[] numbers){
        int max = Integer.MIN_VALUE;
        if(numbers.length>=0){
            for(int i=0;i<numbers.length;i++){
                if(numbers[i]>max){
                    max = numbers[i];
                }
            }
            return max;
        }else{
            return 0;
        }
    }

    public static String getMd5(String plainText) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();

            int i;

            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            //32位加密
            return buf.toString();
            // 16位的加密
            //return buf.toString().substring(8, 24);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }

    }
}
