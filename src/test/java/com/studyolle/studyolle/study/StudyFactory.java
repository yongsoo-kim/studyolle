package com.studyolle.studyolle.study;

import com.studyolle.studyolle.domain.Account;
import com.studyolle.studyolle.domain.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudyFactory {

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
