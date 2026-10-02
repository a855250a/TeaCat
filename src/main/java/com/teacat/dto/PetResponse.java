package com.teacat.dto;

import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
public class PetResponse {
    @Schema(
            description = "寵物ID",
            example = "1"
    )
    private Long id;

    @Schema(
            description = "寵物名稱",
            example = "茶茶"
    )
    private String name;

    @Schema(
            description = "寵物年齡",
            example = "3"
    )
    private int age;

    @Schema(
            description = "寵物體重(公斤)",
            example = "4.2"
    )
    private double weight;

    @Schema(
            description = "疫苗紀錄",
            example = "三合一疫苗"
    )
    private String vaccine;

    @Schema(
            description = "寵物照片",
            example = "uplodas/chacha.jpg"
    )
    private String photo;


}
