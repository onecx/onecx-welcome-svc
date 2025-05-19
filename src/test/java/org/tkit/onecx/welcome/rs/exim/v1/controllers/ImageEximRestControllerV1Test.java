package org.tkit.onecx.welcome.rs.exim.v1.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.tkit.onecx.welcome.rs.exim.controllers.ImageEximRestControllerV1;
import org.tkit.onecx.welcome.test.AbstractTest;
import org.tkit.quarkus.security.test.GenerateKeycloakClient;
import org.tkit.quarkus.test.WithDBData;

import gen.org.tkit.onecx.welcome.rs.exim.model.ExportWelcomeRequestDTOV1;
import gen.org.tkit.onecx.welcome.rs.exim.model.WelcomeSnapshotDTOV1;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(ImageEximRestControllerV1.class)
@WithDBData(value = "data/testdata-internal.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
@GenerateKeycloakClient(clientName = "testClient", scopes = { "ocx-wc:read", "ocx-wc:write" })
class ImageEximRestControllerV1Test extends AbstractTest {

    @Test
    void exportImportSnapshotTest() {
        ExportWelcomeRequestDTOV1 requestDTOV1 = new ExportWelcomeRequestDTOV1();
        requestDTOV1.setWorkspaceName("w1");
        var data = given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .body(requestDTOV1)
                .post("/export")
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(WelcomeSnapshotDTOV1.class);

        assertThat(data).isNotNull();
        assertThat(data.getConfig().getImages()).hasSize(2);

        //import and overwrite in same workspace
        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .body(data)
                .pathParam("workspaceName", "w1")
                .post("/{workspaceName}/import")
                .then()
                .statusCode(OK.getStatusCode());

        //import into other workspace
        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .body(data)
                .pathParam("workspaceName", "w2")
                .post("/{workspaceName}/import")
                .then()
                .statusCode(OK.getStatusCode());
    }

    @Test
    void importMissingBody() {
        given()
                .auth().oauth2(getKeycloakClientToken("testClient"))
                .contentType(APPLICATION_JSON)
                .pathParam("workspaceName", "w2")
                .post("/{workspaceName}/import")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());
    }
}
