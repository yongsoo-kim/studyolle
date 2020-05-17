package com.studyolle.studyolle.study;


import com.studyolle.studyolle.settings.WithAccount;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class StudySettingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @DisplayName("Move to description form page ")
    @WithAccount("yongsoo")
    @Test
    void description_form_test() throws Exception {
        mockMvc.perform(get("/study/{path}/settings"))
                .andExpect(status().isOk());

    }

}