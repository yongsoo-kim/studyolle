package com.studyolle.studyolle.modules.study;

import com.studyolle.studyolle.infra.AbstractContainerBaseTest;
import com.studyolle.studyolle.infra.MockMvcTest;
import com.studyolle.studyolle.modules.account.AccountFactory;
import com.studyolle.studyolle.modules.account.AccountRepository;
import com.studyolle.studyolle.modules.account.Account;
import com.studyolle.studyolle.modules.account.WithAccount;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcTest
class StudyControllerTest extends AbstractContainerBaseTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    StudyRepository studyRepository;

    @Autowired
    StudyService studyService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    StudyFactory studyFactory;

    @Autowired
    AccountFactory accountFactory;

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

    @Test
    @WithAccount("yongsoo")
    @DisplayName("Study view test")
    void viewStudy() throws Exception {
        Study study = new Study();
        study.setPath("test-path");
        study.setTitle("test study");
        study.setShortDescription("short description");
        study.setFullDescription("<p>full description</p>");

        Account yongsoo = accountRepository.findByNickname("yongsoo");
        studyService.createNewStudy(study, yongsoo);

        mockMvc.perform(get("/study/test-path"))
                .andExpect(view().name("study/view"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"));

    }


    @Test
    @WithAccount("yongsoo")
    @DisplayName("study join test")
    void joinStudy() throws Exception {
        Account testuser = accountFactory.createAccount("testuser");
        Study study = studyFactory.createStudy("test-study", testuser);

        mockMvc.perform((get("/study/" + study.getPath() + "/join")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/members"));

        Account yongsoo = accountRepository.findByNickname("yongsoo");
        Study savedStudy = studyRepository.findByPath("test-study");
        assertTrue(savedStudy.getMembers().contains(yongsoo));
    }


    @Test
    @WithAccount("yongsoo")
    @DisplayName("study join test")
    void leaveStudy() throws Exception {
        Account testuser = accountFactory.createAccount("testuser");
        Study study = studyFactory.createStudy("test-study", testuser);
        Account yongsoo = accountRepository.findByNickname("yongsoo");
        studyService.addMember(study, yongsoo);

        mockMvc.perform((get("/study/" + study.getPath() + "/leave")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/members"));

        Study savedStudy = studyRepository.findByPath("test-study");
        assertFalse(savedStudy.getMembers().contains(yongsoo));
    }


}