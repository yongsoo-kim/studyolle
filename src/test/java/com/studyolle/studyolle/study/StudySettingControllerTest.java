package com.studyolle.studyolle.study;


import com.studyolle.studyolle.domain.Account;
import com.studyolle.studyolle.domain.Study;
import com.studyolle.studyolle.settings.WithAccount;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class StudySettingControllerTest extends StudyControllerTest {

    @Test
    @WithAccount("yongsoo")
    @DisplayName("Study description form page -> success")
    void updateDescriptionForm_success() throws Exception {
        Account yongsoo = accountRepository.findByNickname("yongsoo");
        Study study = studyFactory.createStudy("test-study", yongsoo);

        mockMvc.perform(get("/study/" + study.getPath() + "/settings/description"))
                .andExpect(status().isOk())
                .andExpect(view().name("study/settings/description"))
                .andExpect(model().attributeExists("studyDescriptionForm"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"));
    }


    @Test
    @WithAccount("yongsoo")
    @DisplayName("Study description form page -> fail (No auth)")
    void updateDescriptionForm_fail() throws Exception {
        Account testuser = accountRepository.findByNickname("testuser");
        Study study = studyFactory.createStudy("test-study", testuser);

        mockMvc.perform(get("/study/" + study.getPath() + "/settings/description"))
                .andExpect(status().is4xxClientError());
    }


    @Test
    @WithAccount("yongsoo")
    @DisplayName("Study update -> Success")
    void updateDescription_success() throws Exception {
        Account yongsoo = accountRepository.findByNickname("yongsoo");
        Study study = studyFactory.createStudy("test-study", yongsoo);

        String settingsDescriptionUrl = "/study/" + study.getPath() + "/settings/description";
        mockMvc.perform(post(settingsDescriptionUrl)
                .param("shortDescription", "short description...")
                .param("fullDescription", "full description...")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(settingsDescriptionUrl))
                .andExpect(flash().attributeExists("message"));
    }

    @Test
    @WithAccount("yongsoo")
    @DisplayName("Study update -> fail")
    void updateDescription_fail() throws Exception {
        Account yongsoo = accountRepository.findByNickname("yongsoo");
        Study study = studyFactory.createStudy("test-study", yongsoo);

        String settingsDescriptionUrl = "/study/" + study.getPath() + "/settings/description";
        mockMvc.perform(post(settingsDescriptionUrl)
                .param("shortDescription", "")
                .param("fullDescription", "full description...")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("studyDescriptionForm"))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("account"));
    }


}