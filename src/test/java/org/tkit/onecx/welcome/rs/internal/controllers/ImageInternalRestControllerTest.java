package org.tkit.onecx.welcome.rs.internal.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.tkit.onecx.welcome.rs.internal.mappers.ExceptionMapper.ErrorKeys.CONSTRAINT_VIOLATIONS;
import static org.tkit.quarkus.security.test.SecurityTestUtils.getKeycloakClientToken;

import java.io.File;
import java.util.Objects;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.tkit.onecx.welcome.test.AbstractTest;
import org.tkit.quarkus.security.test.GenerateKeycloakClient;
import org.tkit.quarkus.test.WithDBData;

import gen.org.tkit.onecx.welcome.rs.internal.model.ImageDataResponseDTO;
import gen.org.tkit.onecx.welcome.rs.internal.model.ImageInfoDTO;
import gen.org.tkit.onecx.welcome.rs.internal.model.ProblemDetailResponseDTO;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(ImageInternalRestController.class)
@WithDBData(value = "data/testdata-internal.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
@GenerateKeycloakClient(clientName = "testClient", scopes = { "ocx-wc:read", "ocx-wc:write", "ocx-wc:delete", "ocx-wc:all" })
class ImageInternalRestControllerTest extends AbstractTest {

    private static final String MEDIA_TYPE_IMAGE_PNG = "image/png";

    private static final File FILE = new File(
            Objects.requireNonNull(ImageInternalRestController.class.getResource("/images/Testimage.png")).getFile());

    @Test
    void getImageDataByIdTest() {
        var data = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .pathParam("id", "d-11-111")
                .get("/{id}")
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(ImageDataResponseDTO.class);

        assertThat(data).isNotNull();
        assertThat(data.getImageData()).isNotEmpty();
    }

    @Test
    void getImageInfoByIdTest() {
        var data = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .pathParam("id", "11-111")
                .get("/info/{id}")
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(ImageInfoDTO.class);

        assertThat(data).isNotNull();
        assertThat(data.getImageId()).isEqualTo("d-11-111");
        assertThat(data.getPosition()).isEqualTo("1");
        assertThat(data.getObjectFit()).isEqualTo(ImageInfoDTO.ObjectFitEnum.NONE);
    }

    @Test
    void createImageDataTest() {
        var data = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .when()
                .body(FILE)
                .contentType(MEDIA_TYPE_IMAGE_PNG)
                .post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body().as(ImageDataResponseDTO.class);
        assertThat(data).isNotNull();
    }

    @Test
    void createImageDataEmptyBodyTest() {
        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .when()
                .contentType(MEDIA_TYPE_IMAGE_PNG)
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());

    }

    @Test
    void createImageInfoTest() {

        //first create image data
        var imageData = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .when()
                .body(FILE)
                .contentType(MEDIA_TYPE_IMAGE_PNG)
                .post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body().as(ImageDataResponseDTO.class);

        ImageInfoDTO body = new ImageInfoDTO();
        body.imageId(imageData.getImageId()).position("2").visible(true).workspaceName("w1").objectPosition("top right")
                .objectFit(ImageInfoDTO.ObjectFitEnum.COVER);

        var output = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .when()
                .body(body)
                .contentType(APPLICATION_JSON)
                .post("/info")
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().as(ImageInfoDTO.class);
        assertThat(output.getObjectFit()).isEqualTo(ImageInfoDTO.ObjectFitEnum.COVER);
        assertThat(output.getObjectPosition()).isEqualTo("top right");
    }

    @Test
    void createImageInfoExternalURLTest() {
        ImageInfoDTO body = new ImageInfoDTO();
        body.url("randomURl").position("2").visible(true).workspaceName("w1");
        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .when()
                .body(body)
                .contentType(APPLICATION_JSON)
                .post("/info")
                .then()
                .statusCode(CREATED.getStatusCode());
    }

    @Test
    void updateImageDataByIdTest() {
        // first get existing image
        var data = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .pathParam("id", "d-11-111")
                .get("/{id}")
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(ImageDataResponseDTO.class);

        assertThat(data).isNotNull();
        assertThat(data.getImageData()).isNotEmpty();

        //update the image
        var updatedImage = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .when()
                .body(FILE)
                .contentType(MEDIA_TYPE_IMAGE_PNG)
                .pathParam("id", "d-11-111")
                .put("/{id}")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .body().as(ImageDataResponseDTO.class);

        assertThat(updatedImage).isNotNull();
        assertThat(updatedImage.getImageData()).isNotEqualTo(data.getImageData());

        //update not-existing image
        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .when()
                .body(FILE)
                .contentType(MEDIA_TYPE_IMAGE_PNG)
                .pathParam("id", "not-existing")
                .put("/{id}")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void updateImageInfoByIdTest() {
        // get image info
        var data = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .pathParam("id", "22-222")
                .get("/info/{id}")
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(ImageInfoDTO.class);

        assertThat(data.getUrl()).isEqualTo("http://onecx.de/test");

        var updateBody = new ImageInfoDTO();
        updateBody.url("updated-url");
        updateBody.modificationCount(0);
        updateBody.workspaceName("w1");

        var updatedInfo = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .body(updateBody)
                .pathParam("id", "22-222")
                .put("/info/{id}")
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(ImageInfoDTO.class);

        assertThat(updatedInfo).isNotNull();
        assertThat(updatedInfo.getUrl()).isEqualTo(updateBody.getUrl());

        //update second time, optimistic lock exception
        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .body(updateBody)
                .pathParam("id", "22-222")
                .put("/info/{id}")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ProblemDetailResponseDTO.class);

        //update not-existing image-info
        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .body(updateBody)
                .pathParam("id", "not-existing")
                .put("/info/{id}")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void updateImageInfoAssignedImageDataByIdTest() {

        //create new imageData first
        var data = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .when()
                .body(FILE)
                .contentType(MEDIA_TYPE_IMAGE_PNG)
                .post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(ImageDataResponseDTO.class);

        var updateBody = new ImageInfoDTO();
        updateBody.imageId(data.getImageId());
        updateBody.modificationCount(0);
        updateBody.workspaceName("w1");

        var updatedInfo = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .body(updateBody)
                .pathParam("id", "11-111")
                .put("/info/{id}")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(ImageInfoDTO.class);

        assertThat(updatedInfo.getImageId()).isEqualTo(updateBody.getImageId());

        // update with not-existing image-data id -> won't update the image-data
        updateBody.modificationCount(1);
        updateBody.setImageId("not-existing");

        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .body(updateBody)
                .pathParam("id", "11-111")
                .put("/info/{id}")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract().as(ImageInfoDTO.class);
    }

    @Test
    void deleteImageInfoByIdTest() {
        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .pathParam("id", "11-111")
                .delete("/info/{id}")
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        //assigned image data should be gone too

        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .pathParam("id", "d-11-111")
                .get("/{id}")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

        //delete not-existing image-info
        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .pathParam("id", "not-existing")
                .delete("/info/{id}")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void getAllImageInfosByWorkspaceNameTest() {
        var output = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .pathParam("workspaceName", "w1")
                .get("/{workspaceName}/info")
                .then()
                .contentType(APPLICATION_JSON)
                .extract().as(ImageInfoDTO[].class);

        assertThat(output).hasSize(2);
    }

    @Test
    void testMaxUploadSize() {

        byte[] body = new byte[1100001];
        new Random().nextBytes(body);

        var exception = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .when()
                .body(body)
                .contentType(MEDIA_TYPE_IMAGE_PNG)
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ProblemDetailResponseDTO.class);

        assertThat(exception.getErrorCode()).isEqualTo(CONSTRAINT_VIOLATIONS.name());
        assertThat(exception.getDetail()).isEqualTo(
                "createImage.contentLength: must be less than or equal to 1100000");

    }

}
