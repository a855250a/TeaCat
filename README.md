# 🐾 TeaCat

TeaCat 是一個以 **Java Spring Boot + MySQL** 建立的寵物健康管理作品集，目標是把寵物基本資料、健康時間軸與醫療報告整理集中在同一個平台。

## 目前功能

- 一般帳號登入 / 註冊、BCrypt 密碼雜湊
- JWT 驗證與登入逾期處理
- 一鍵訪客體驗登入（首次登入自動建立 TeaCat Demo 資料）
- 寵物 CRUD、照片上傳（JPG / PNG / WEBP）、使用者資料隔離
- 健康紀錄 CRUD：看診、疫苗、檢驗、體重、其他
- Dashboard 寵物資訊與健康紀錄統計
- TeaCat AI Demo：醫療報告文字重點整理與常見檢驗關鍵字辨識
- Swagger / OpenAPI
- Responsive Web UI（桌面與手機）

> TeaCat AI 目前是作品集示範版的文字整理器，不做醫療診斷，也尚未串接外部 LLM。下一階段規劃以 Python OCR + LLM service 處理圖片報告與結構化摘要。

## 技術

**Backend:** Java 21, Spring Boot, Spring Data JPA, Hibernate, Bean Validation  
**Database:** MySQL 8  
**Security:** JWT, BCrypt  
**Frontend:** HTML, CSS, Vanilla JavaScript  
**Tools:** Maven, Swagger/OpenAPI, Git, Docker

## 本機啟動

建立 MySQL database `teacat`，並在環境變數設定資料庫密碼：

Windows PowerShell:
```powershell
$env:DB_PASSWORD="你的本機 MySQL 密碼"
.\mvnw.cmd spring-boot:run
```

可選擇另外設定 JWT secret：
```powershell
$env:JWT_SECRET="請使用足夠長度的隨機字串"
```

啟動後開啟 `http://localhost:8080/login.html`。

## 主要 API

- `POST /register`, `POST /login`, `POST /guest-login`
- `GET/POST /pets`, `GET/PUT/DELETE /pets/{id}`, `GET /pets/search`（登入者資料範圍）
- `GET/POST /records`, `PUT/DELETE /records/{id}`
- `POST /upload`
- `POST /ai/analyze`

## 架構

`Browser UI → Spring MVC Controller → Service → Spring Data JPA → MySQL`

AI 擴充方向：`Spring Boot → Python OCR/AI Service → structured result → Health Record`

## Demo 重點

HR / 面試官可直接使用「訪客體驗登入」，不需要建立帳號。Demo 帳號第一次登入會自動建立寵物「茶茶」與健康紀錄，方便快速展示 Dashboard、Pet CRUD 與健康時間軸。

## 作者

**啟華 蔡** — Software / Backend Developer Portfolio


## v1.0 Portfolio Release

- Pet / Health Record API 均以 JWT 登入者做資料隔離。
- 寵物搜尋限制於目前登入者，避免跨帳號資料洩漏。
- 圖片上傳限制為 JPG / PNG / WEBP，最大 10 MB。
- AI 功能明確標示為 Demo 文字整理器，不宣稱醫療診斷或已串接 LLM。

### 部署提醒

正式環境請務必設定 `DB_URL`、`DB_USERNAME`、`DB_PASSWORD` 與足夠長度的 `JWT_SECRET`。`uploads/` 為本機檔案儲存；若部署平台使用 ephemeral filesystem，正式產品應改用物件儲存服務。

## TeaCat v1.0 deployment

Required environment variables:

- `DB_PASSWORD` — database password (required)
- `DB_URL` — JDBC URL; defaults locally to `jdbc:mysql://localhost:3306/teacat`
- `DB_USERNAME` — defaults locally to `root`
- `JWT_SECRET` — set a long random secret in production (do not commit it)
- `PORT` — optional locally; cloud platforms can inject it automatically

Local Windows start:

```bat
set DB_PASSWORD=YOUR_MYSQL_PASSWORD
mvnw.cmd spring-boot:run
```

Then open `http://localhost:8080/login.html`.

### Image storage note
Pet images are currently stored under the application's local `uploads/` directory. This works locally. On an ephemeral cloud filesystem (including a default Render web service), uploaded images can be lost after a restart/redeploy. For persistent production image storage, attach persistent storage or replace this with an object-storage provider.

## TeaCat v1.0 Portfolio deployment

This release is designed to run as an independent public portfolio demo.

### Required environment variables
- `DB_URL` — cloud MySQL JDBC URL, e.g. `jdbc:mysql://host:3306/teacat?useSSL=true&serverTimezone=Asia/Taipei`
- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET` — use a long random value (32+ bytes)
- `CLOUDINARY_CLOUD_NAME`
- `CLOUDINARY_API_KEY`
- `CLOUDINARY_API_SECRET`

`PORT` is read automatically when provided by the hosting platform.

### Images
When the three Cloudinary variables are configured, uploaded pet images are stored permanently in Cloudinary and the HTTPS URL is saved in MySQL. Render restarts/redeploys therefore do not remove uploaded images. Without Cloudinary variables, local development falls back to `./uploads` only.

### Demo access
- HR/recruiter: use **訪客體驗登入**. TeaCat creates the guest account and demo 茶茶 data automatically when needed.
- Owner/test account: **a / a** is automatically created on a fresh database and is preserved for personal testing.

### Health check
`GET /health` returns a small JSON response and can be used as the hosting health-check path.

### Render example
- Build command: `./mvnw clean package -DskipTests`
- Start command: `java -jar target/teacat-0.0.1-SNAPSHOT.jar`
- Health check path: `/health`

Do not commit real database passwords, JWT secrets, or Cloudinary secrets to Git.
