package com.casumo;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;

@Testcontainers
public class DevowelizerServiceTest {

    public static final String INPUT_ENDPOINT = "/input";
    public static final String NPT = "npt";
    private String baseUri;

    @Container
    public static GenericContainer webServer
            = new GenericContainer(DockerImageName.parse("casumo/devowelizer:latest"))
            .withExposedPorts(8080);

    @BeforeEach
    public void setUp() {
        String address = webServer.getHost();
        Integer port = webServer.getFirstMappedPort();

        baseUri = "http://" + address + ":" + port;
    }

    // this test should be a part of regression suite
    @Test
    public void verifyGetExpectedInput() {
        given()
                .baseUri(baseUri)
                .when()
                .get(INPUT_ENDPOINT)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .body("", hasItem(NPT));
    }

    // this test can be a part of regression suite on higher environments
    @Test
    public void verifyPostForbidden() {
        given()
                .baseUri(baseUri)
                .when()
                .body("{}")
                .post(INPUT_ENDPOINT)
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND); // but should be 405 Method Not Allowed
    }

    // this test can be a part of regression suite on higher environments
    @Test
    public void verifyOptions() {
        given()
                .baseUri(baseUri)
                .when()
                .options()
                .then()
                .body("", hasItems("GET,HEAD"));
    }
}
