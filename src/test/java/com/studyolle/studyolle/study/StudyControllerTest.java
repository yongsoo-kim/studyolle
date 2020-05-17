package com.studyolle.studyolle.study;

import com.studyolle.studyolle.account.AccountRepository;
import com.studyolle.studyolle.domain.Study;
import com.studyolle.studyolle.settings.WithAccount;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class StudyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    AccountRepository accountRepository;

    @AfterEach
    void afterEach() {
        studyRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @WithAccount("yongsoo")
    @DisplayName("Move to StudyForm page")
    @Test
    void move_to_studyForm() throws Exception {
        mockMvc.perform(get("/new-study"))
                .andExpect(status().isOk())
                .andExpect(view().name("study/form"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("studyForm"));
    }


    @WithAccount("yongsoo")
    @DisplayName("Post a new study -> OK")
    @Test
    void posting_new_study_ok() throws Exception {

        mockMvc.perform(post("/new-study")
                .param("title","테스트용 스터디")
                .param("path","테스트스터디경로")
                .param("fullDescription","테스트용 전체 설명문")
                .param("shortDescription","테스트용 간략 설명문")
                .with(csrf()))
                .andExpect(status().is3xxRedirection());

        Study findStudy = studyRepository.findByPath("테스트스터디경로");
        assertNotNull(findStudy);
    }


    @WithAccount("yongsoo")
    @DisplayName("Post a duplicated study -> Fail")
    @Test
    void posting_duplicate_study_fail() throws Exception {

        Study study = Study.builder()
                .title("테스트용 스터디")
                .path("테스트스터디경로")
                .shortDescription("테스트용 간략 설명문")
                .fullDescription("테스트용 전체 설명문")
                .build();

        studyRepository.save(study);

        mockMvc.perform(post("/new-study")
                .param("title","테스트용 스터디")
                .param("path","테스트스터디경로")
                .param("fullDescription","테스트용 전체 설명문")
                .param("shortDescription","테스트용 간략 설명문")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeHasErrors("studyForm"));
    }


}