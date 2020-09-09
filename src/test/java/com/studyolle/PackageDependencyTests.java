package com.studyolle;

import com.studyolle.studyolle.App;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packagesOf = App.class)
public class PackageDependencyTests {

    private static final String STUDY = "..modules.study..";
    private static final String EVENT = "..modules.event..";
    private static final String ACCOUNT = "..modules.account..";
    private static final String TAG = "..modules.tag..";
    private static final String ZONE = "..modules.zone..";


    //STUDY can only accessed by STUDY,ENVENT
    @ArchTest
    ArchRule modulePackageRule = classes().that().resideInAPackage("com.studyolle.modules..")
            .should().onlyBeAccessed().byClassesThat()
            .resideInAnyPackage("com.studyolle.modules..");

    //STUDY can only accessed by STUDY,ENVENT
    @ArchTest
    ArchRule studyPackageRule = classes().that().resideInAPackage(STUDY)
            .should().onlyBeAccessed().byClassesThat()
            .resideInAnyPackage(STUDY, EVENT);

    //EVENT can access to STUDY,ACCOUNT, EVENT
    @ArchTest
    ArchRule eventPackageRule = classes().that().resideInAPackage(EVENT)
            .should().accessClassesThat()
            .resideInAnyPackage(STUDY, ACCOUNT, EVENT);

    //ACCOUNT can access to TAG,ZONE, ACCOUNT
    @ArchTest
    ArchRule accountPackageRule = classes().that().resideInAPackage(ACCOUNT)
            .should().accessClassesThat()
            .resideInAnyPackage(TAG, ZONE, ACCOUNT);

    @ArchTest
    ArchRule cycleCheck = slices().matching("com.studyolle.modules.(*)..")
            .should().beFreeOfCycles();
}
