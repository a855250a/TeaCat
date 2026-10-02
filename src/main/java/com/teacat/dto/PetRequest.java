package com.teacat.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
public class PetRequest {
    @Schema(
            description = "寵物名稱",
            example = "茶茶"
    )
    @NotBlank(message = "名字不能空白")
    private String name;

    @Schema(
            description = "寵物年齡",
            example = "3"
    )
    @Min(value = 0, message = "年齡不能小於0")
    private int age;

    @Schema(
            description = "寵物體重(公斤)",
            example = "4.2"
    )
    @Min(value = 0, message = "體重不能小於0")
    private double weight;

    @Schema(
            description = "疫苗紀錄",
            example = "三合一疫苗"
    )
    private String vaccine;

    @Schema(
            description = "寵物照片",
            example = "uploads/chacha.jpg"
    )
    private String photo;

}
