# TeaCat v1.0 Portfolio Release Notes

## 本次整理

- 修正 `/pets/search` 未驗證登入者且可能搜尋到其他使用者寵物的問題。
- Pet Controller 統一使用 `AuthService.requireUser()` 驗證 JWT，避免各 API 重複且不一致的 token parsing。
- PetRepository 搜尋方法加入 `userId` 條件，搜尋結果限制在目前登入者資料範圍。
- 確認 HealthRecord 建立 / 修改會驗證 pet ownership，避免以其他使用者的 petId 建立紀錄。
- 圖片上傳限制 JPG / JPEG / PNG / WEBP，並改用 UUID + 安全副檔名儲存。
- README 補上 v1.0 安全與部署提醒，明確說明 TeaCat AI 為 Demo 文字整理器。

## 驗收狀態

### 已完成靜態檢查
- JWT 保護：Pet / HealthRecord / Upload / AI API。
- Pet CRUD owner check。
- HealthRecord owner / pet ownership check。
- 前端健康紀錄輸出有 HTML escape。
- DB 密碼由環境變數取得。

### 尚需在可連 Maven Central 的環境確認
本次環境執行 `./mvnw -DskipTests package` 時，Maven Wrapper 無法下載 Maven 3.9.16：

`wget: Failed to fetch https://repo.maven.apache.org/.../apache-maven-3.9.16-bin.zip`

因此本次沒有宣稱 Maven build/test PASS。請在一般可連網開發環境執行：

```powershell
$env:DB_PASSWORD="你的 MySQL 密碼"
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
```

## Portfolio v1.0 範圍
TeaCat v1.0 以完整 Web Application 展示為目標，不再新增大型功能：登入/註冊、JWT、Pet CRUD、Health Record、Dashboard、圖片上傳、AI Demo、Swagger、Responsive UI、Docker。
