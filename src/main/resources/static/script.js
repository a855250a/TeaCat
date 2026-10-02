console.log("TeaCat script.js loaded");

let editPetId = null;


// =========================
// Page Detection
// =========================

const currentPath = window.location.pathname;

const isDashboardPage = currentPath.includes("dashboard.html");
const isPetsPage = currentPath.includes("pets.html");
const isRecordsPage = currentPath.includes("records.html");
const isAiPage = currentPath.includes("ai.html");


// =========================
// Authentication Guard
// =========================

const protectedPages = [
    "dashboard.html",
    "pets.html",
    "records.html",
    "ai.html"
];

const isProtectedPage = protectedPages.some(function (page) {
    return currentPath.includes(page);
});

if (isProtectedPage) {

    const token = localStorage.getItem("token");

    if (!token || token === "null" || token === "undefined") {

        localStorage.removeItem("token");
        localStorage.removeItem("loginUser");
        localStorage.removeItem("loginMode");

        window.location.href =
        "/login.html";
    }
}


// =========================
// Login
// =========================

const loginButton = document.getElementById("loginButton");
const passwordInput = document.getElementById("password");

if (passwordInput && loginButton) {

    passwordInput.addEventListener("keydown", function (event) {

        if (event.key === "Enter") {
            loginButton.click();
        }

    });
}


if (loginButton) {

    loginButton.addEventListener("click", function () {

        const emailInput = document.getElementById("email");
        const passwordInput = document.getElementById("password");

        const email = emailInput.value.trim();
        const password = passwordInput.value;

        if (!email || !password) {

            alert("請輸入 Email 與密碼");

            return;
        }


        loginButton.disabled = true;
        loginButton.textContent = "登入中...";


        fetch("/login", {

            method: "POST",

            headers: {
                "Content-Type": "application/json"
            },

            body: JSON.stringify({
                email: email,
                password: password
            })

        })

        .then(async function (response) {

            const data = await response.json();

            if (!response.ok || !data.token) {

                throw new Error(
                    data.message || "帳號或密碼錯誤"
                );
            }

            return data;
        })

        .then(function (data) {

            localStorage.setItem(
                "token",
                data.token
            );
            
            localStorage.setItem(
            "loginUser",
            data.email
            );

            localStorage.setItem(
            "loginMode",
            "user"
            );
            
            window.location.href =
                "/dashboard.html";

        })

        .catch(function (error) {

            console.error(
                "Login Error:",
                error
            );

            localStorage.removeItem("token");

            alert(
                "登入失敗，請確認帳號與密碼"
            );

            loginButton.disabled = false;
            loginButton.textContent = "登入";

        });

    });
}


// =========================
// Guest Login
// =========================

const guestLoginButton =
    document.getElementById("guestLoginButton");

if (guestLoginButton) {

    guestLoginButton.addEventListener(
        "click",
        async function () {

            guestLoginButton.disabled = true;
            guestLoginButton.textContent = "登入中...";

            try {

                const response = await fetch(
                    "/guest-login",
                    {
                        method: "POST"
                    }
                );

                const data = await response.json();

                if (!response.ok || !data.token) {
                    throw new Error(
                        data.message || "訪客登入失敗"
                    );
                }

                localStorage.setItem(
                "token",
                data.token
                );

                localStorage.setItem(
                "loginUser",
                data.email
                );

                localStorage.setItem(
                "loginMode",
                "guest"
                );

                window.location.href =
                "/dashboard.html";

            } catch (error) {

                console.error(
                    "Guest Login Error:",
                    error
                );

                localStorage.removeItem("token");

                alert("訪客登入暫時無法使用");

                guestLoginButton.disabled = false;
                guestLoginButton.textContent =
                    "🐾 訪客體驗登入";
            }
        }
    );
}


// =========================
// Logout
// =========================

const logoutButton =
    document.getElementById("logoutButton");


if (logoutButton) {

    logoutButton.addEventListener(
        "click",
        function () {

            localStorage.removeItem("token");
            localStorage.removeItem("loginUser");
            localStorage.removeItem("loginMode");

            window.location.href =
                "/login.html";
        }
    );
}


// =========================
// Load Pets
// =========================

const loadPetsButton =
    document.getElementById("loadPetsButton");


if (loadPetsButton) {

    loadPetsButton.addEventListener(
        "click",
        loadPets
    );

    // Dashboard / Pets 頁面進入後自動載入
    loadPets();
}


function loadPets() {

    const token =
        localStorage.getItem("token");

    const petList =
        document.getElementById("petList");


    if (!petList) {
        return;
    }


    petList.innerHTML = `
        <p class="loading">
            正在載入寵物資料...
        </p>
    `;


    fetch("/pets", {

        method: "GET",

        headers: {
            "Authorization":
                "Bearer " + token
        }

    })

    .then(async function (response) {

        if (response.status === 401) {

            localStorage.removeItem(
                "token"
            );

            window.location.replace(
                "/login.html"
            );

            throw new Error(
                "登入狀態已失效"
            );
        }


        if (!response.ok) {

            throw new Error(
                "無法取得寵物資料"
            );
        }


        return response.json();
    })

    .then(function (data) {

        if (!Array.isArray(data)) {

            throw new Error(
                "寵物資料格式錯誤"
            );
        }


        // =========================
        // Health Overview
        // =========================

        updateHealthOverview(data);


        // =========================
        // Empty Pets
        // =========================

        if (data.length === 0) {

            petList.innerHTML = `
                <div class="empty-state">

                    <p>
                        目前還沒有寵物資料
                    </p>

                    <span>
                        前往「我的寵物」建立第一份毛孩資料。
                    </span>

                </div>
            `;

            return;
        }


        // =========================
        // Render Pets
        // =========================

        petList.innerHTML = "";


        data.forEach(function (pet) {

            petList.innerHTML +=
                createPetCard(pet);

        });


        // =========================
        // Pets Page Actions
        // =========================

        if (isPetsPage) {

            bindDeleteButtons();
            bindEditButtons(data);
        }

    })

    .catch(function (error) {

        console.error(
            "Load Pets Error:",
            error
        );


        if (
            error.message ===
            "登入狀態已失效"
        ) {
            return;
        }


        petList.innerHTML = `
            <div class="empty-state">

                <p>
                    寵物資料載入失敗
                </p>

                <span>
                    請稍後重新整理頁面。
                </span>

            </div>
        `;

    });
}


// =========================
// Create Pet Card
// =========================

function createPetCard(pet) {

    const photoHtml = pet.photo
        ? `
            <img
                src="${pet.photo}"
                class="pet-photo"
                alt="${pet.name}">
        `
        : `
            <div class="pet-photo-placeholder">
                🐱
            </div>
        `;


    let actionHtml = "";


    if (isPetsPage) {

        actionHtml = `
            <div class="pet-actions">

                <button
                    class="editPetButton btn-secondary"
                    data-id="${pet.id}">
                    編輯
                </button>

                <button
                    class="deletePetButton btn-danger"
                    data-id="${pet.id}">
                    刪除
                </button>

            </div>
        `;

    } else if (isDashboardPage) {

        actionHtml = `
            <div class="pet-actions">

                <a
                    href="/pets.html"
                    class="pet-manage-link">
                    管理寵物
                </a>

            </div>
        `;
    }


    return `
        <div class="pet-item">

            <div class="pet-avatar">
                ${photoHtml}
            </div>


            <div class="pet-info">

                <div class="pet-name-row">

                    <h3>
                        ${pet.name}
                    </h3>

                    <span class="pet-status">
                        健康資料
                    </span>

                </div>


                <div class="pet-details">

                    <span>
                        🎂 ${pet.age} 歲
                    </span>

                    <span>
                        ⚖️ ${pet.weight} kg
                    </span>

                    <span>
                        💉 ${pet.vaccine ?? "尚無疫苗資料"}
                    </span>

                </div>

            </div>


            ${actionHtml}

        </div>
    `;
}


// =========================
// Health Overview
// =========================

function updateHealthOverview(data) {

    const currentWeight =
        document.getElementById(
            "currentWeight"
        );

    const currentVaccine =
        document.getElementById(
            "currentVaccine"
        );


    if (
        Array.isArray(data) &&
        data.length > 0
    ) {

        const pet = data[0];


        if (currentWeight) {

            currentWeight.textContent =
                pet.weight + " kg";
        }


        if (currentVaccine) {

            currentVaccine.textContent =
                pet.vaccine ||
                "尚無資料";
        }

    } else {

        if (currentWeight) {

            currentWeight.textContent =
                "-- kg";
        }


        if (currentVaccine) {

            currentVaccine.textContent =
                "尚無資料";
        }
    }
}


// =========================
// Delete Pet
// =========================

function bindDeleteButtons() {

    const deleteButtons =
        document.querySelectorAll(
            ".deletePetButton"
        );


    deleteButtons.forEach(
        function (button) {

            button.addEventListener(
                "click",
                function () {

                    const petId =
                        button.dataset.id;


                    const confirmed =
                        confirm(
                            "確定要刪除這隻寵物嗎？"
                        );


                    if (!confirmed) {
                        return;
                    }


                    const token =
                        localStorage.getItem(
                            "token"
                        );


                    fetch(
                        "/pets/" + petId,
                        {

                            method: "DELETE",

                            headers: {
                                "Authorization":
                                    "Bearer " +
                                    token
                            }

                        }
                    )

                    .then(function (response) {

                        if (!response.ok) {

                            throw new Error(
                                "刪除失敗"
                            );
                        }


                        loadPets();

                    })

                    .catch(function (error) {

                        console.error(
                            "Delete Pet Error:",
                            error
                        );

                        alert(
                            "刪除寵物失敗"
                        );

                    });

                }
            );

        }
    );
}


// =========================
// Edit Pet
// =========================

function bindEditButtons(data) {

    const editButtons =
        document.querySelectorAll(
            ".editPetButton"
        );


    editButtons.forEach(
        function (button) {

            button.addEventListener(
                "click",
                function () {

                    const petId =
                        Number(
                            button.dataset.id
                        );


                    const pet =
                        data.find(
                            function (item) {

                                return (
                                    item.id === petId
                                );
                            }
                        );


                    if (!pet) {
                        return;
                    }


                    editPetId = pet.id;


                    const petName =
                        document.getElementById(
                            "petName"
                        );

                    const petAge =
                        document.getElementById(
                            "petAge"
                        );

                    const petWeight =
                        document.getElementById(
                            "petWeight"
                        );

                    const petVaccine =
                        document.getElementById(
                            "petVaccine"
                        );


                    if (petName) {
                        petName.value =
                            pet.name ?? "";
                    }


                    if (petAge) {
                        petAge.value =
                            pet.age ?? "";
                    }


                    if (petWeight) {
                        petWeight.value =
                            pet.weight ?? "";
                    }


                    if (petVaccine) {
                        petVaccine.value =
                            pet.vaccine ?? "";
                    }


                    if (addPetButton) {

                        addPetButton.textContent =
                            "更新寵物";
                    }


                    const petForm =
                        document.querySelector(
                            ".pet-form"
                        );


                    if (petForm) {

                        petForm.scrollIntoView({
                            behavior: "smooth",
                            block: "start"
                        });
                    }

                }
            );

        }
    );
}


// =========================
// Add / Update Pet
// =========================

const addPetButton =
    document.getElementById("addPetButton");


if (addPetButton) {

    addPetButton.addEventListener(
        "click",
        function () {

            const petNameInput =
                document.getElementById(
                    "petName"
                );

            const petAgeInput =
                document.getElementById(
                    "petAge"
                );

            const petWeightInput =
                document.getElementById(
                    "petWeight"
                );

            const petVaccineInput =
                document.getElementById(
                    "petVaccine"
                );

            const petPhotoInput =
                document.getElementById(
                    "petPhoto"
                );


            const petName =
                petNameInput.value.trim();

            const petAge =
                petAgeInput.value;

            const petWeight =
                petWeightInput.value;

            const petVaccine =
                petVaccineInput.value;

            const petPhoto =
                petPhotoInput.files[0];


            // =========================
            // Validation
            // =========================

            if (!petName) {

                alert(
                    "請輸入寵物名稱"
                );

                return;
            }


            if (!petAge) {

                alert(
                    "請輸入寵物年齡"
                );

                return;
            }


            if (!petWeight) {

                alert(
                    "請輸入寵物體重"
                );

                return;
            }


            const token =
                localStorage.getItem(
                    "token"
                );


            const url =
                editPetId === null
                    ? "/pets"
                    : "/pets/" + editPetId;


            const method =
                editPetId === null
                    ? "POST"
                    : "PUT";


            addPetButton.disabled = true;

            addPetButton.textContent =
                editPetId === null
                    ? "新增中..."
                    : "更新中...";


            // =========================
            // Upload Photo
            // =========================

            if (petPhoto) {

                const formData =
                    new FormData();

                formData.append(
                    "file",
                    petPhoto
                );


                fetch("/upload", {

                    method: "POST",

                    headers: {
                        "Authorization":
                            "Bearer " + token
                    },

                    body: formData

                })

                .then(function (response) {

                    if (!response.ok) {

                        throw new Error(
                            "照片上傳失敗"
                        );
                    }


                    return response.text();
                })

                .then(function (photoPath) {

                    savePet(photoPath);

                })

                .catch(function (error) {

                    console.error(
                        "Upload Error:",
                        error
                    );

                    alert(
                        "照片上傳失敗"
                    );

                    resetPetButton();

                });

            } else {

                savePet("");
            }


            // =========================
            // Save Pet
            // =========================

            function savePet(photoPath) {

                fetch(url, {

                    method: method,

                    headers: {

                        "Content-Type":
                            "application/json",

                        "Authorization":
                            "Bearer " + token

                    },

                    body: JSON.stringify({

                        name: petName,

                        age:
                            Number(petAge),

                        weight:
                            Number(petWeight),

                        vaccine:
                            petVaccine,

                        photo:
                            photoPath

                    })

                })

                .then(async function (response) {

                    if (!response.ok) {

                        let message =
                            "儲存寵物資料失敗";


                        try {

                            const errorData =
                                await response.json();

                            message =
                                errorData.message ||
                                message;

                        } catch (error) {

                            // Response may not be JSON.
                        }


                        throw new Error(
                            message
                        );
                    }


                    return response.json();
                })

                .then(function () {

                    editPetId = null;


                    petNameInput.value = "";
                    petAgeInput.value = "";
                    petWeightInput.value = "";
                    petVaccineInput.value = "";
                    petPhotoInput.value = "";


                    resetPetButton();


                    loadPets();

                })

                .catch(function (error) {

                    console.error(
                        "Save Pet Error:",
                        error
                    );

                    alert(
                        error.message ||
                        "儲存寵物資料失敗"
                    );


                    resetPetButton();

                });
            }


            // =========================
            // Reset Button
            // =========================

            function resetPetButton() {

                addPetButton.disabled =
                    false;

                addPetButton.textContent =
                    editPetId === null
                        ? "新增寵物"
                        : "更新寵物";
            }

        }
    );
}

// =========================
// Login Identity
// =========================

const loginIdentityText =
    document.getElementById("loginIdentityText");

const loginIdentityIcon =
    document.getElementById("loginIdentityIcon");

if (loginIdentityText && loginIdentityIcon) {

    const loginMode =
        localStorage.getItem("loginMode");

    const loginUser =
        localStorage.getItem("loginUser");

    if (loginMode === "guest") {

        loginIdentityIcon.textContent = "🐾";
        loginIdentityText.textContent = "訪客模式";

    } else {

        loginIdentityIcon.textContent = "👤";
        loginIdentityText.textContent =
            loginUser || "使用者";
    }
}

// =========================
// TeaCat v1 - Health Records
// =========================
const authHeaders = () => ({"Authorization": "Bearer " + localStorage.getItem("token")});
async function apiFetch(url, options = {}) {
    options.headers = {...(options.headers || {}), ...authHeaders()};
    const response = await fetch(url, options);
    if (response.status === 401) {
        localStorage.removeItem("token"); localStorage.removeItem("loginUser"); localStorage.removeItem("loginMode");
        window.location.replace("/login.html"); throw new Error("登入狀態已失效");
    }
    return response;
}

async function loadRecordPetOptions() {
    const select = document.getElementById("recordPetId"); if (!select) return;
    const response = await apiFetch("/pets"); if (!response.ok) return;
    const pets = await response.json();
    select.innerHTML = pets.length ? pets.map(p => `<option value="${p.id}">${escapeHtml(p.name)}</option>`).join("") : `<option value="">請先新增寵物</option>`;
}

let editRecordId = null;
async function loadRecords() {
    const list = document.getElementById("recordList");
    const response = await apiFetch("/records"); if (!response.ok) throw new Error("健康紀錄載入失敗");
    const records = await response.json();
    const count = document.getElementById("healthRecordCount"); if (count) count.textContent = `${records.length} 筆`;
    const visit = document.getElementById("visitCount"), vaccine = document.getElementById("vaccineCount"), lab = document.getElementById("labCount");
    if (visit) visit.textContent = `${records.filter(r=>r.type==='VISIT').length} 筆`;
    if (vaccine) vaccine.textContent = `${records.filter(r=>r.type==='VACCINE').length} 筆`;
    if (lab) lab.textContent = `${records.filter(r=>r.type==='LAB').length} 筆`;
    if (!list) return records;
    if (!records.length) { list.innerHTML='<div class="empty-state"><p>目前尚無健康紀錄</p><span>使用上方表單建立第一筆紀錄。</span></div>'; return records; }
    list.innerHTML = records.map(r => `<article class="record-item">
      <div class="record-date">${escapeHtml(r.recordDate)}</div><div class="record-main"><span class="record-type">${recordTypeName(r.type)}</span>
      <h3>${escapeHtml(r.title)}</h3><p>${escapeHtml(r.petName)}${r.weight != null ? `・${r.weight} kg` : ''}</p>${r.notes ? `<small>${escapeHtml(r.notes)}</small>`:''}</div>
      ${isRecordsPage ? `<div class="record-actions"><button class="btn-secondary record-edit" data-id="${r.id}">編輯</button><button class="btn-danger record-delete" data-id="${r.id}">刪除</button></div>`:''}</article>`).join('');
    if (isRecordsPage) bindRecordActions(records);
    return records;
}
function recordTypeName(t){ return ({VISIT:'🏥 看診',VACCINE:'💉 疫苗',LAB:'🧪 檢驗',WEIGHT:'⚖️ 體重',OTHER:'📌 其他'})[t] || t; }
function escapeHtml(v){ return String(v ?? '').replace(/[&<>'"]/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;',"'":'&#39;','"':'&quot;'}[c])); }
function bindRecordActions(records){
 document.querySelectorAll('.record-delete').forEach(b=>b.onclick=async()=>{if(!confirm('確定刪除這筆健康紀錄？'))return; const r=await apiFetch('/records/'+b.dataset.id,{method:'DELETE'}); if(r.ok) loadRecords();});
 document.querySelectorAll('.record-edit').forEach(b=>b.onclick=()=>{const r=records.find(x=>x.id===Number(b.dataset.id)); if(!r)return; editRecordId=r.id; recordPetId.value=r.petId; recordDate.value=r.recordDate; recordType.value=r.type; recordTitle.value=r.title; recordWeight.value=r.weight??''; recordNotes.value=r.notes??''; saveRecordButton.textContent='更新紀錄'; document.querySelector('.record-form')?.scrollIntoView({behavior:'smooth'});});
}
const saveRecordButton=document.getElementById('saveRecordButton');
if(saveRecordButton) saveRecordButton.onclick=async()=>{
 const payload={petId:Number(recordPetId.value),recordDate:recordDate.value,type:recordType.value,title:recordTitle.value.trim(),weight:recordWeight.value?Number(recordWeight.value):null,notes:recordNotes.value.trim(),attachment:null};
 if(!payload.petId||!payload.recordDate||!payload.title){alert('請填寫寵物、日期與標題');return;}
 saveRecordButton.disabled=true;
 try{const r=await apiFetch(editRecordId?'/records/'+editRecordId:'/records',{method:editRecordId?'PUT':'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify(payload)}); if(!r.ok) throw new Error('儲存失敗'); editRecordId=null; recordTitle.value='';recordWeight.value='';recordNotes.value='';saveRecordButton.textContent='新增健康紀錄'; await loadRecords();}
 catch(e){alert(e.message)} finally{saveRecordButton.disabled=false;}
};
if(isRecordsPage){ loadRecordPetOptions(); loadRecords().catch(console.error); const d=document.getElementById('recordDate'); if(d&&!d.value)d.value=new Date().toISOString().slice(0,10); }
if(isDashboardPage){ loadRecords().then(records=>{const box=document.getElementById('recentRecordList'); if(box&&records.length) box.innerHTML=records.slice(0,3).map(r=>`<div class="mini-record"><strong>${recordTypeName(r.type)} ${escapeHtml(r.title)}</strong><span>${escapeHtml(r.petName)}・${escapeHtml(r.recordDate)}</span></div>`).join('');}).catch(console.error); }

// =========================
// TeaCat AI demo analyzer
// =========================
const analyzeButton=document.getElementById('analyzeButton');
if(analyzeButton) analyzeButton.onclick=async()=>{
 const text=document.getElementById('reportText').value.trim(); if(!text){alert('請貼上醫療報告文字');return;}
 analyzeButton.disabled=true; analyzeButton.textContent='分析中...';
 try{const r=await apiFetch('/ai/analyze',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({text})}); const data=await r.json(); if(!r.ok)throw new Error(data.message||'分析失敗'); document.getElementById('aiResult').innerHTML=`<h3>整理結果</h3><p>${escapeHtml(data.summary)}</p><ul>${data.highlights.map(x=>`<li>${escapeHtml(x)}</li>`).join('')}</ul><small>${escapeHtml(data.disclaimer)}</small>`;}
 catch(e){alert(e.message)} finally{analyzeButton.disabled=false;analyzeButton.textContent='✨ 分析報告';}
};
