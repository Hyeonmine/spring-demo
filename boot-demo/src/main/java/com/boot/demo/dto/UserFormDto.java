package com.boot.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserFormDto {

    @NotBlank
    private String id;

    @NotBlank
    private String name;

    @NotBlank
    private String password;

}
