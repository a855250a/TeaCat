# TeaCat verification notes

Checked in this package:
- TeaCat-only naming; no PetHealthCloud/pethealthcloud text remains.
- Owner demo account initializer keeps `a / a` deterministic.
- Guest login resets guest-owned demo data to 茶茶 + two records on every guest login.
- Pet APIs are scoped to authenticated user; search is also user-scoped.
- Pet update preserves existing photo when no new photo is uploaded.
- Deleting a pet deletes its health records first to avoid FK failures.
- Health-record create/update validates pet ownership; record list/delete are user-scoped.
- TeaCat AI endpoint requires JWT and is reachable from guest mode.
- Upload endpoint requires JWT and accepts JPG/PNG/WEBP only; Cloudinary is used when configured.
- `/health`, `${PORT:8080}`, DB env vars and Cloudinary env vars are present.
- Front-end JS syntax passed `node --check`.
- pom.xml parsed as valid XML; static route/package/legacy-name assertions passed.
- H2-backed Spring Boot integration tests are included for owner login, guest reset, AI, Pet CRUD, Health Record CRUD, access isolation, upload validation, health and invalid JWT.

Environment limitation:
This ChatGPT runtime cannot resolve repo.maven.apache.org/github.com, so Maven itself and dependencies cannot be downloaded here. Therefore the included Spring Boot integration test suite could not be executed in this runtime. Do not treat that part as an executed PASS until Maven runs in a network-enabled environment (Render/GitHub/local machine).
