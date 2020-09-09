package com.studyolle.studyolle.modules.event;

import com.studyolle.studyolle.infra.AbstractContainerBaseTest;
import com.studyolle.studyolle.infra.MockMvcTest;
import com.studyolle.studyolle.modules.account.AccountFactory;
import com.studyolle.studyolle.modules.account.AccountRepository;
import com.studyolle.studyolle.modules.account.Account;
import com.studyolle.studyolle.modules.study.Study;
import com.studyolle.studyolle.modules.account.WithAccount;
import com.studyolle.studyolle.modules.study.StudyFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockMvcTest
class EventControllerTest extends AbstractContainerBaseTest {

    @Autowired MockMvc mockMvc;
    @Autowired StudyFactory studyFactory;
    @Autowired AccountFactory accountFactory;
    @Autowired EventService eventService;
    @Autowired EnrollmentRepository enrollmentRepository;
    @Autowired AccountRepository accountRepository;

    @Test
    @DisplayName("FCFS event apply - auto acceptence")
    @WithAccount("yongsoo")
    void newEnrollment_to_FCFS_event_accepted() throws Exception {
        Account testUser = accountFactory.createAccount("testUser");
        Study study = studyFactory.createStudy("test-study", testUser);
        Event event = createEvent("test-event", EventType.FCFS, 2, study, testUser);

        mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/enroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/events/" + event.getId()));

        Account yongsoo = accountRepository.findByNickname("yongsoo");
        isAccepted(yongsoo, event);
    }

    @Test
    @DisplayName("FCFS event apply - Waiting (Acceptance full)")
    @WithAccount("yongsoo")
    void newEnrollment_to_FCFS_event_not_accepted() throws Exception {
        Account testUser = accountFactory.createAccount("testUser");
        Study study = studyFactory.createStudy("test-study", testUser);
        Event event = createEvent("test-event", EventType.FCFS, 2, study, testUser);

        Account may = accountFactory.createAccount("may");
        Account june = accountFactory.createAccount("june");
        eventService.newEnrollment(event, may);
        eventService.newEnrollment(event, june);

        mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/enroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/events/" + event.getId()));

        Account yongsoo = accountRepository.findByNickname("yongsoo");
        isNotAccepted(yongsoo, event);
    }

    @Test
    @DisplayName("If accepted member disenroll the event, a waiting member will be accepted.")
    @WithAccount("yongsoo")
    void accepted_account_cancelEnrollment_to_FCFS_event_not_accepted() throws Exception {
        Account yongsoo = accountRepository.findByNickname("yongsoo");
        Account testUser = accountFactory.createAccount("testUser");
        Account may = accountFactory.createAccount("may");
        Study study = studyFactory.createStudy("test-study", testUser);
        Event event = createEvent("test-event", EventType.FCFS, 2, study, testUser);

        eventService.newEnrollment(event, may);
        eventService.newEnrollment(event, yongsoo);
        eventService.newEnrollment(event, testUser);

        isAccepted(may, event);
        isAccepted(yongsoo, event);
        isNotAccepted(testUser, event);

        mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/disenroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/events/" + event.getId()));

        isAccepted(may, event);
        isAccepted(testUser, event);
        assertNull(enrollmentRepository.findByEventAndAccount(event, yongsoo));
    }

    @Test
    @DisplayName("If waiting member disenroll the event, accepted member count won't be changed.")
    @WithAccount("yongsoo")
    void not_accepterd_account_cancelEnrollment_to_FCFS_event_not_accepted() throws Exception {
        Account yongsoo = accountRepository.findByNickname("yongsoo");
        Account testUser = accountFactory.createAccount("testUser");
        Account may = accountFactory.createAccount("may");
        Study study = studyFactory.createStudy("test-study", testUser);
        Event event = createEvent("test-event", EventType.FCFS, 2, study, testUser);

        eventService.newEnrollment(event, may);
        eventService.newEnrollment(event, testUser);
        eventService.newEnrollment(event, yongsoo);

        isAccepted(may, event);
        isAccepted(testUser, event);
        isNotAccepted(yongsoo, event);

        mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/disenroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/events/" + event.getId()));

        isAccepted(may, event);
        isAccepted(testUser, event);
        assertNull(enrollmentRepository.findByEventAndAccount(event, yongsoo));
    }

    private void isNotAccepted(Account testUser, Event event) {
        assertFalse(enrollmentRepository.findByEventAndAccount(event, testUser).isAccepted());
    }

    private void isAccepted(Account account, Event event) {
        assertTrue(enrollmentRepository.findByEventAndAccount(event, account).isAccepted());
    }

    @Test
    @DisplayName("CONFIRMATIVE(manager) type event apply - waiting")
    @WithAccount("yongsoo")
    void newEnrollment_to_CONFIMATIVE_event_not_accepted() throws Exception {
        Account testUser = accountFactory.createAccount("testUser");
        Study study = studyFactory.createStudy("test-study", testUser);
        Event event = createEvent("test-event", EventType.CONFIRMATIVE, 2, study, testUser);

        mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/enroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/events/" + event.getId()));

        Account yongsoo = accountRepository.findByNickname("yongsoo");
        isNotAccepted(yongsoo, event);
    }

    private Event createEvent(String eventTitle, EventType eventType, int limit, Study study, Account account) {
        Event event = new Event();
        event.setEventType(eventType);
        event.setLimitOfEnrollments(limit);
        event.setTitle(eventTitle);
        event.setCreatedDateTime(LocalDateTime.now());
        event.setEndEnrollmentDateTime(LocalDateTime.now().plusDays(1));
        event.setStartDateTime(LocalDateTime.now().plusDays(1).plusHours(5));
        event.setEndDateTime(LocalDateTime.now().plusDays(1).plusHours(7));
        return eventService.createEvent(event, study, account);
    }

}