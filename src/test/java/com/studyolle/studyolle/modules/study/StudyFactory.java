package com.studyolle.studyolle.modules.study;

import com.studyolle.studyolle.infra.AbstractContainerBaseTest;
import com.studyolle.studyolle.modules.account.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudyFactory extends AbstractContainerBaseTest {

    @Autowired
    StudyService studyService;

    @Autowired
    StudyRepository studyRepository;

    public Study createStudy(String path, Account manager){
        Study study = new Study();
        study.setPath(path);
        studyService.createNewStudy(study, manager);
        return study;
    }
}
