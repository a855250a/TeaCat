package com.teacat.controller;

import com.teacat.dto.PetRequest;
import com.teacat.dto.PetResponse;
import com.teacat.entity.User;
import com.teacat.service.AuthService;
import com.teacat.service.PetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Pet API", description = "寵物管理相關 API")
@RestController
public class PetController {
    private final PetService petService;
    private final AuthService authService;

    public PetController(PetService petService, AuthService authService) {
        this.petService = petService;
        this.authService = authService;
    }

    @Operation(summary = "取得我的所有寵物", description = "只回傳目前登入使用者的寵物資料")
    @ApiResponse(responseCode = "200", description = "成功取得寵物資料")
    @GetMapping("/pets")
    public ResponseEntity<List<PetResponse>> getPets(@RequestHeader("Authorization") String authHeader) {
        User user = authService.requireUser(authHeader);
        return ResponseEntity.ok(petService.getAllPets(user.getId()));
    }

    @Operation(summary = "新增寵物", description = "建立目前登入使用者的新寵物資料")
    @PostMapping("/pets")
    public ResponseEntity<PetResponse> addPet(@Valid @RequestBody PetRequest petRequest,
                                               @RequestHeader("Authorization") String authHeader) {
        User user = authService.requireUser(authHeader);
        return ResponseEntity.status(HttpStatus.CREATED).body(petService.addPet(petRequest, user));
    }

    @Operation(summary = "刪除寵物", description = "只能刪除目前登入使用者擁有的寵物")
    @DeleteMapping("/pets/{id}")
    public ResponseEntity<Void> deletePet(@PathVariable Long id,
                                          @RequestHeader("Authorization") String authHeader) {
        User user = authService.requireUser(authHeader);
        petService.deletePet(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "更新寵物", description = "只能更新目前登入使用者擁有的寵物")
    @PutMapping("/pets/{id}")
    public ResponseEntity<PetResponse> updatePet(@PathVariable Long id,
                                                  @Valid @RequestBody PetRequest petRequest,
                                                  @RequestHeader("Authorization") String authHeader) {
        User user = authService.requireUser(authHeader);
        return ResponseEntity.ok(petService.updatePet(id, petRequest, user.getId()));
    }

    @Operation(summary = "取得單一寵物", description = "只能查看目前登入使用者擁有的寵物")
    @GetMapping("/pets/{id}")
    public ResponseEntity<PetResponse> getPetById(@PathVariable Long id,
                                                   @RequestHeader("Authorization") String authHeader) {
        User user = authService.requireUser(authHeader);
        return ResponseEntity.ok(petService.getPetById(id, user.getId()));
    }

    @Operation(summary = "搜尋我的寵物", description = "依名稱、年齡或兩者搜尋；結果只包含目前登入使用者的寵物")
    @GetMapping("/pets/search")
    public ResponseEntity<List<PetResponse>> searchPets(@RequestParam(required = false) String name,
                                                         @RequestParam(required = false) Integer age,
                                                         @RequestHeader("Authorization") String authHeader) {
        User user = authService.requireUser(authHeader);
        return ResponseEntity.ok(petService.searchPets(user.getId(), name, age));
    }
}
