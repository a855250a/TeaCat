package com.teacat.dto;

import java.time.LocalDate;

public class HealthRecordResponse {
    public Long id; public Long petId; public String petName; public LocalDate recordDate;
    public String type; public String title; public String notes; public Double weight; public String attachment;
}
