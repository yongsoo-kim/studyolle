package com.studyolle.studyolle.infra;

import org.testcontainers.containers.PostgreSQLContainer;


public abstract class AbstractContainerBaseTest {

    //For testDB's docker container, this class need to be extended to all test classes.
    static final PostgreSQLContainer POSTGRE_SQL_CONTAINER;

    static {
        POSTGRE_SQL_CONTAINER = new PostgreSQLContainer();
        POSTGRE_SQL_CONTAINER.start();
    }

}
