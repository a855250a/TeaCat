package com.teacat.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "health_records")
public class HealthRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "pet_id")
    private Pet pet;
    @ManyToOne(optional = false) @JoinColumn(name = "user_id")
    private User user;
    @Column(nullable = false)
    private LocalDate recordDate;
    @Column(nullable = false, length = 30)
    private String type;
    @Column(nullable = false, length = 120)
    private String title;
    @Column(length = 2000)
    private String notes;
    private Double weight;
    private String attachment;

    public Long getId(){ return id; }
    public Pet getPet(){ return pet; } public void setPet(Pet pet){ this.pet=pet; }
    public User getUser(){ return user; } public void setUser(User user){ this.user=user; }
    public LocalDate getRecordDate(){ return recordDate; } public void setRecordDate(LocalDate v){ this.recordDate=v; }
    public String getType(){ return type; } public void setType(String v){ this.type=v; }
    public String getTitle(){ return title; } public void setTitle(String v){ this.title=v; }
    public String getNotes(){ return notes; } public void setNotes(String v){ this.notes=v; }
    public Double getWeight(){ return weight; } public void setWeight(Double v){ this.weight=v; }
    public String getAttachment(){ return attachment; } public void setAttachment(String v){ this.attachment=v; }
}
