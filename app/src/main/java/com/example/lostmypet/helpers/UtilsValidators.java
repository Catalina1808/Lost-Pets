package com.example.lostmypet.helpers;

import android.text.TextUtils;
import android.util.Patterns;

public class UtilsValidators {

    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        return !TextUtils.isEmpty(password) && password.length()>=6;
    }

    public static boolean isValidPhone(String phone) {
        int count=0;
        for(int i=0;i<phone.length();i++)
        {
            if(Character.isDigit(phone.charAt(i)))
                count++;

        }
        return count == 10 && !TextUtils.isEmpty(phone) && Patterns.PHONE.matcher(phone).matches();
    }

    public static boolean isEmptyField(String field) {
        return TextUtils.isEmpty(field);
    }

}
