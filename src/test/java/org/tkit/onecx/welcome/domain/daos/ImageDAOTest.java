package org.tkit.onecx.welcome.domain.daos;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class ImageDAOTest extends AbstractDAOTest {

    @Inject
    ImageDAO dao;

    @Test
    @SuppressWarnings("java:S2699")
    void methodExceptionTests() {
        methodExceptionTests(() -> dao.findAllByWorkspaceName(null),
                ImageDAO.ErrorKeys.ERROR_FIND_IMAGE_INFO_BY_WORKSPACE);
        methodExceptionTests(() -> dao.deleteAllByWorkspaceName(null),
                ImageDAO.ErrorKeys.ERROR_DELETE_IMAGES_BY_WORKSPACE);
    }
}
