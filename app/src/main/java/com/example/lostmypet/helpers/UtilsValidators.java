package com.example.lostmypet.helpers;

import androidx.core.util.PatternsCompat;

public class UtilsValidators {

    public static boolean isValidEmail(String email) {
        return !email.isEmpty() && PatternsCompat.EMAIL_ADDRESS.matcher(email).matches();
    }
    public static boolean isValidPassword(String password) {
        String numRegex   = ".*[0-9].*";
        String alphaRegex = ".*[a-zA-Z].*";

        return password.length()>=6 && password.matches(numRegex)
                && password.matches(alphaRegex);
    }
    public static boolean isValidPhone(String phone) {
        int count=0;
        for(int i=0;i<phone.length();i++)
        {
            if(Character.isDigit(phone.charAt(i)))
                count++;
            else
                return false;

        }
        return count == 10;
    }
}
