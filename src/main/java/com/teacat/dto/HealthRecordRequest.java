package com.teacat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class HealthRecordRequest {
    @NotNull private Long petId;
    @NotNull private LocalDate recordDate;
    @NotBlank private String type;
    @NotBlank private String title;
    private String notes;
    private Double weight;
    private String attachment;
    public Long getPetId(){return petId;} public void setPetId(Long v){petId=v;}
    public LocalDate getRecordDate(){return recordDate;} public void setRecordDate(LocalDate v){recordDate=v;}
    public String getType(){return type;} public void setType(String v){type=v;}
    public String getTitle(){return title;} public void setTitle(String v){title=v;}
    public String getNotes(){return notes;} public void setNotes(String v){notes=v;}
    public Double getWeight(){return weight;} public void setWeight(Double v){weight=v;}
    public String getAttachment(){return attachment;} public void setAttachment(String v){attachment=v;}
}
