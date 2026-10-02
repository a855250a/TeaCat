# TeaCat v1.0 Final

Final portfolio package, 2026-10-02.

## Included fixes/checks
- Pet APIs require JWT and scope reads/searches/updates/deletes to the signed-in owner.
- Health-record create/update/delete verifies the signed-in user and pet ownership.
- Upload API requires JWT and accepts only JPEG/PNG/WEBP content types.
- Cloud-friendly `PORT` environment variable support added.
- Database credentials remain environment-variable based; no password is bundled.
- SQL console logging defaults off for deployment.
- Runtime uploads, `.env`, logs and build output are excluded from Git.
- Existing TeaCat UI, guest demo, dashboard, pet CRUD, health records and AI demo retained.

## Runtime verification already observed
On the user's Windows/MySQL 8.0.46 environment, the application successfully started on localhost, guest login succeeded, Dashboard loaded, TeaCat demo pet loaded, and Health Records loaded from MySQL.

## Deployment limitation
Uploaded pet images use local filesystem storage. Use persistent disk/object storage if image persistence across cloud redeploys is required.
