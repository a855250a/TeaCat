package com.teacat.repository;

import com.teacat.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PetRepository extends JpaRepository<Pet, Long> {
    // 名字模糊搜尋（包含指定文字，不分大小寫)
    List<Pet> findByUserIdAndNameContainingIgnoreCase(Long userId, String name);
    // 根據年齡搜尋寵物
    List<Pet> findByUserIdAndAge(Long userId, Integer age);
    // 同時依照名字與年齡搜尋寵物(尋找名稱包含忽略大小寫和年齡的google翻譯)
    List<Pet> findByUserIdAndNameContainingIgnoreCaseAndAge(
            Long userId,
            String name,
            Integer age
    );

    //根據使用者查詢寵物
    List<Pet> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}
