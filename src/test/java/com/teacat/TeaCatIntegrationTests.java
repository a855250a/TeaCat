package com.teacat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TeaCatIntegrationTests {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    private String login(String email, String password) throws Exception {
        String body = mvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\""+email+"\",\"password\":\""+password+"\"}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return json.readTree(body).path("token").asText();
    }

    private String guestLogin() throws Exception {
        String body = mvc.perform(post("/guest-login")).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return json.readTree(body).path("token").asText();
    }

    @Test void ownerAccountGuestAiCrudIsolationAndUploadWork() throws Exception {
        String owner = login("a", "a");
        assertThat(owner).isNotBlank();

        String guest = guestLogin();
        String guestPets = mvc.perform(get("/pets").header("Authorization","Bearer "+guest))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        JsonNode seeded = json.readTree(guestPets);
        assertThat(seeded.size()).isEqualTo(1);
        assertThat(seeded.get(0).path("name").asText()).isEqualTo("茶茶");

        mvc.perform(post("/ai/analyze").header("Authorization","Bearer "+guest)
                .contentType(MediaType.APPLICATION_JSON).content("{\"text\":\"WBC creatinine weight\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.highlights").isArray());

        String create = mvc.perform(post("/pets").header("Authorization","Bearer "+owner)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"測試貓\",\"age\":2,\"weight\":4.8,\"vaccine\":\"完成\",\"photo\":\"https://example.com/a.jpg\"}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long petId = json.readTree(create).path("id").asLong();

        String updated = mvc.perform(put("/pets/"+petId).header("Authorization","Bearer "+owner)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"測試貓2\",\"age\":3,\"weight\":5.0,\"vaccine\":\"完成\",\"photo\":\"\"}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertThat(json.readTree(updated).path("photo").asText()).isEqualTo("https://example.com/a.jpg");

        mvc.perform(get("/pets/"+petId).header("Authorization","Bearer "+guest))
                .andExpect(status().isForbidden());

        String rec = mvc.perform(post("/records").header("Authorization","Bearer "+owner)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"petId\":"+petId+",\"recordDate\":\"2026-10-02\",\"type\":\"WEIGHT\",\"title\":\"測試紀錄\",\"weight\":5.0,\"notes\":\"ok\"}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long recordId=json.readTree(rec).path("id").asLong();
        mvc.perform(put("/records/"+recordId).header("Authorization","Bearer "+owner)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"petId\":"+petId+",\"recordDate\":\"2026-10-03\",\"type\":\"VISIT\",\"title\":\"已更新\",\"notes\":\"done\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.title").value("已更新"));

        mvc.perform(delete("/pets/"+petId).header("Authorization","Bearer "+owner))
                .andExpect(status().isNoContent());
        mvc.perform(get("/records").header("Authorization","Bearer "+owner))
                .andExpect(status().isOk()).andExpect(content().json("[]"));

        // Guest changes disappear at next guest login.
        long guestPetId=seeded.get(0).path("id").asLong();
        mvc.perform(delete("/pets/"+guestPetId).header("Authorization","Bearer "+guest)).andExpect(status().isNoContent());
        String guest2=guestLogin();
        mvc.perform(get("/pets").header("Authorization","Bearer "+guest2)).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("茶茶"));

        MockMultipartFile bad=new MockMultipartFile("file","x.txt","text/plain","x".getBytes());
        mvc.perform(multipart("/upload").file(bad).header("Authorization","Bearer "+owner))
                .andExpect(status().isBadRequest());
        MockMultipartFile png=new MockMultipartFile("file","x.png","image/png",new byte[]{(byte)137,80,78,71});
        mvc.perform(multipart("/upload").file(png).header("Authorization","Bearer "+owner))
                .andExpect(status().isOk());
    }

    @Test void healthAndUnauthorizedWork() throws Exception {
        mvc.perform(get("/health")).andExpect(status().isOk()).andExpect(jsonPath("$.service").value("TeaCat"));
        mvc.perform(get("/pets")).andExpect(status().isBadRequest()); // missing required Authorization header
        mvc.perform(post("/ai/analyze").header("Authorization","Bearer invalid")
                .contentType(MediaType.APPLICATION_JSON).content("{\"text\":\"WBC\"}"))
                .andExpect(status().isUnauthorized());
    }
}
