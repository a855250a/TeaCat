package com.teacat.service;

import com.teacat.dto.*;
import com.teacat.entity.*;
import com.teacat.exception.*;
import com.teacat.repository.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class HealthRecordService {
    private final HealthRecordRepository records; private final PetRepository pets;
    public HealthRecordService(HealthRecordRepository records, PetRepository pets){this.records=records;this.pets=pets;}
    public List<HealthRecordResponse> list(Long userId){return records.findByUserIdOrderByRecordDateDescIdDesc(userId).stream().map(this::toDto).toList();}
    public HealthRecordResponse create(HealthRecordRequest req, User user){ HealthRecord r=new HealthRecord(); apply(r,req,user); return toDto(records.save(r)); }
    public HealthRecordResponse update(Long id, HealthRecordRequest req, User user){ HealthRecord r=owned(id,user.getId()); apply(r,req,user); return toDto(records.save(r)); }
    public void delete(Long id, Long userId){ records.delete(owned(id,userId)); }
    private HealthRecord owned(Long id, Long userId){ HealthRecord r=records.findById(id).orElseThrow(()->new PetNotFoundException("找不到健康紀錄")); if(!r.getUser().getId().equals(userId)) throw new ForbiddenException("無權限操作此健康紀錄"); return r; }
    private void apply(HealthRecord r, HealthRecordRequest q, User user){ Pet pet=pets.findById(q.getPetId()).orElseThrow(()->new PetNotFoundException("找不到寵物")); if(!pet.getUser().getId().equals(user.getId())) throw new ForbiddenException("無權限使用此寵物"); r.setPet(pet);r.setUser(user);r.setRecordDate(q.getRecordDate());r.setType(q.getType());r.setTitle(q.getTitle());r.setNotes(q.getNotes());r.setWeight(q.getWeight());r.setAttachment(q.getAttachment()); }
    private HealthRecordResponse toDto(HealthRecord r){ HealthRecordResponse d=new HealthRecordResponse();d.id=r.getId();d.petId=r.getPet().id;d.petName=r.getPet().name;d.recordDate=r.getRecordDate();d.type=r.getType();d.title=r.getTitle();d.notes=r.getNotes();d.weight=r.getWeight();d.attachment=r.getAttachment();return d; }
}
