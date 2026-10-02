package com.teacat.service;


import com.teacat.repository.PetRepository;
import com.teacat.repository.HealthRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.teacat.entity.Pet;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.teacat.dto.PetRequest;
import com.teacat.dto.PetResponse;
import com.teacat.exception.PetNotFoundException;
import com.teacat.exception.InvalidSearchConditionException;
import com.teacat.entity.User;
import com.teacat.exception.ForbiddenException;



@Service
public class PetService {
    private final PetRepository petRepository;
    private final HealthRecordRepository healthRecordRepository;

    public PetService(
            PetRepository petRepository,
            HealthRecordRepository healthRecordRepository
    ) {
        this.petRepository = petRepository;
        this.healthRecordRepository = healthRecordRepository;
    }
    // 取得所有寵物資料
    public List<PetResponse> getAllPets(Long userId) {
        List<Pet> pets = petRepository.findByUserId(userId);
        List<PetResponse> responses = new ArrayList<>();

        for (Pet pet : pets) {
            PetResponse response = new PetResponse();

            response.setId(pet.id);
            response.setName(pet.name);
            response.setAge(pet.age);
            response.setWeight(pet.weight);
            response.setVaccine(pet.vaccine);
            response.setPhoto(pet.photo);

            responses.add(response);

        }
        return responses;
    }
    // 新增寵物資料
    public PetResponse addPet(
            PetRequest petRequest,
            User user
    ) {
        Pet pet = new Pet();

        pet.name = petRequest.getName();
        pet.age = petRequest.getAge();
        pet.weight = petRequest.getWeight();
        pet.vaccine = petRequest.getVaccine();
        pet.photo = petRequest.getPhoto();

        pet.setUser(user);

        Pet savedPet = petRepository.save(pet);
        PetResponse response = new PetResponse();

        response.setId(savedPet.id);
        response.setName(savedPet.name);
        response.setAge(savedPet.age);
        response.setWeight(savedPet.weight);
        response.setVaccine(savedPet.vaccine);
        response.setPhoto(savedPet.photo);
        return response;
    }
    // 根據 ID 刪除寵物資料
    @Transactional
    public void deletePet(
            Long id,
            Long userId
    ) {
        Optional<Pet> optionalPet =
                petRepository.findById(id);

        if (optionalPet.isEmpty()) {
            throw new PetNotFoundException("找不到寵物");
        }

        Pet pet = optionalPet.get();

        if (!pet.getUser().getId().equals(userId)) {
            throw new ForbiddenException("無權限刪除此寵物");
        }

        // 先刪除子健康紀錄，避免資料庫外鍵阻止刪除寵物。
        healthRecordRepository.deleteByPetId(id);
        petRepository.delete(pet);
    }
    // 根據 ID 更新寵物資料
    public PetResponse updatePet(
            Long id,
            PetRequest petRequest,
            Long userId
    ) {
        Optional<Pet> optionalPet =
                petRepository.findById(id);

        if (optionalPet.isEmpty()) {
            throw new PetNotFoundException("找不到寵物");
        }
        Pet pet = optionalPet.get();

        if (!pet.getUser().getId().equals(userId)) {
            throw new ForbiddenException("無權限修改此寵物");
        }

        pet.name = petRequest.getName();
        pet.age = petRequest.getAge();
        pet.weight = petRequest.getWeight();
        pet.vaccine = petRequest.getVaccine();
        // 編輯時沒有重新上傳照片，就保留既有照片。
        if (petRequest.getPhoto() != null && !petRequest.getPhoto().isBlank()) {
            pet.photo = petRequest.getPhoto();
        }

        Pet savedPet = petRepository.save(pet);
        PetResponse response = new PetResponse();

        response.setId(savedPet.id);
        response.setName(savedPet.name);
        response.setAge(savedPet.age);
        response.setWeight(savedPet.weight);
        response.setVaccine(savedPet.vaccine);
        response.setPhoto(savedPet.photo);

        return response;

    }
    // 根據 ID 取得單一寵物資料
    public PetResponse getPetById(
            Long id,
            Long userId
    ) {
        Optional<Pet> optionalPet
                = petRepository.findById(id);

        if (optionalPet.isEmpty()) {
            throw new PetNotFoundException("找不到寵物");
        }

        Pet pet = optionalPet.get();
        if (!pet.getUser().getId().equals(userId)) {
            throw new ForbiddenException("無權限查看此寵物");
        }

        PetResponse response = new PetResponse();

        response.setId(pet.id);
        response.setName(pet.name);
        response.setAge(pet.age);
        response.setWeight(pet.weight);
        response.setVaccine(pet.vaccine);
        response.setPhoto(pet.photo);
        return response;
    }

    // 動態搜尋寵物
    // 可依照名字、年齡或名字+年齡搜尋
    public List<PetResponse> searchPets(
            Long userId,
            String name,
            Integer age
    ){

        List<Pet> pets;

        if(name != null && age != null) {
            pets = petRepository.findByUserIdAndNameContainingIgnoreCaseAndAge(
                    userId,
                    name,
                    age
            );
        }else if (name != null) {
            pets = petRepository.findByUserIdAndNameContainingIgnoreCase(userId, name);

        }else if(age != null) {
            pets = petRepository.findByUserIdAndAge(userId, age);
        }else {
            throw new InvalidSearchConditionException(
                    "請至少輸入一個搜尋條件"
            );
        }

        List<PetResponse> responses =
                new ArrayList<>();

        for (Pet pet : pets) {
            PetResponse response = new PetResponse();
            response.setId(pet.id);
            response.setName(pet.name);
            response.setAge(pet.age);
            response.setWeight(pet.weight);
            response.setVaccine(pet.vaccine);
            response.setPhoto(pet.photo);

            responses.add(response);
        }
        return responses;
    }

}
