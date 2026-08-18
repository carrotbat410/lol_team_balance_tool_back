package com.carrotbat410.lol_team_balance_tool.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ChangePasswordRequestDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void rejectsBlankPasswords() {
        ChangePasswordRequestDTO request = request(" ", " ", " ");

        Set<ConstraintViolation<ChangePasswordRequestDTO>> violations = validator.validate(request);

        assertThat(violations).extracting(violation -> violation.getPropertyPath().toString())
                .contains("currentPassword", "newPassword", "newPasswordConfirm");
    }

    @Test
    void delegatesShortPasswordValidationToService() {
        ChangePasswordRequestDTO request = request("current-password", "12345", "12345");

        Set<ConstraintViolation<ChangePasswordRequestDTO>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void delegatesLongPasswordValidationToService() {
        String password = "a".repeat(73);
        ChangePasswordRequestDTO request = request("current-password", password, password);

        Set<ConstraintViolation<ChangePasswordRequestDTO>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void delegatesSupplementaryCharacterPasswordValidationToService() {
        String password = "😀".repeat(40);
        ChangePasswordRequestDTO request = request("current-password", password, password);

        Set<ConstraintViolation<ChangePasswordRequestDTO>> violations = validator.validate(request);

        assertThat(password.codePointCount(0, password.length())).isEqualTo(40);
        assertThat(password.length()).isEqualTo(80);
        assertThat(violations).isEmpty();
    }

    private ChangePasswordRequestDTO request(
            String currentPassword,
            String newPassword,
            String newPasswordConfirm
    ) {
        ChangePasswordRequestDTO request = new ChangePasswordRequestDTO();
        request.setCurrentPassword(currentPassword);
        request.setNewPassword(newPassword);
        request.setNewPasswordConfirm(newPasswordConfirm);
        return request;
    }
}
