package com.teacat.controller;

import com.teacat.dto.*;
import com.teacat.entity.User;
import com.teacat.service.*;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/records")
public class HealthRecordController {
    private final HealthRecordService service; private final AuthService auth;
    public HealthRecordController(HealthRecordService service, AuthService auth){this.service=service;this.auth=auth;}
    @GetMapping public List<HealthRecordResponse> list(@RequestHeader("Authorization") String h){return service.list(auth.requireUser(h).getId());}
    @PostMapping public ResponseEntity<HealthRecordResponse> create(@Valid @RequestBody HealthRecordRequest q,@RequestHeader("Authorization") String h){return ResponseEntity.status(201).body(service.create(q,auth.requireUser(h)));}
    @PutMapping("/{id}") public HealthRecordResponse update(@PathVariable Long id,@Valid @RequestBody HealthRecordRequest q,@RequestHeader("Authorization") String h){return service.update(id,q,auth.requireUser(h));}
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id,@RequestHeader("Authorization") String h){User u=auth.requireUser(h);service.delete(id,u.getId());return ResponseEntity.noContent().build();}
}
