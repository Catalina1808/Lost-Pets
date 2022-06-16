package com.example.lostmypet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.lostmypet.helpers.UtilsValidators;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class UtilsValidatorsTests  {
    @Test
    public void validateEmail_CorrectEmail_ReturnsTrue() {
        assertTrue(UtilsValidators.isValidEmail("name@email.com"));
    }

    @Test
    public void validateEmail_WrongEmail_ReturnsFalse() {
        assertFalse(UtilsValidators.isValidEmail("name@email"));
    }

    @Test
    public void validatePhone_CorrectPhone_ReturnsTrue() {
        assertTrue(UtilsValidators.isValidPhone("0757550182"));
    }

    @Test
    public void validatePhone_WrongPhone_ReturnsFalse() {
        assertFalse(UtilsValidators.isValidPhone("123ab"));
    }

    @Test
    public void validatePassword_CorrectPassword_ReturnsTrue() {
        assertTrue(UtilsValidators.isValidPassword("12345abc"));
    }

    @Test
    public void validatePassword_WrongPassword_ReturnsFalse() {
        assertFalse(UtilsValidators.isValidPassword("123ab"));
        assertFalse(UtilsValidators.isValidPassword("123456"));
        assertFalse(UtilsValidators.isValidPassword("abcdef"));
    }
}
