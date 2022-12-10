package com.bookit.step_definitions;

import com.bookit.pages.SelfPage;
import com.bookit.utilities.BookItApiUtil;
import com.bookit.utilities.ConfigurationReader;
import com.bookit.utilities.DBUtils;
import com.bookit.utilities.Environment;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.Assert;

import java.util.Map;

import static io.restassured.RestAssured.*;

public class ApiStepDefs {
    String token; // I declare the token here to make it global, and assign the value in methods
    Response response;
    String emailGlobal;
    String studentEmail;
    String studentPassword;

    @Given("I logged Bookit api using {string} and {string}")
    public void i_logged_Bookit_api_using_and(String email, String password) {

        token = BookItApiUtil.generateToken(email, password);

        System.out.println("email = " + email);
        System.out.println("password = " + password);
        emailGlobal = email;
    }

    @When("I get the current user information from api")
    public void i_get_the_current_user_information_from_api() {
        System.out.println("token = " + token);

        //send a GET request "/api/users/me" endpoint to get current user info
        response = given().accept(ContentType.JSON)
                .and()
                .header("Authorization", token)
                .when()
                .get(Environment.BASE_URL + "/api/users/me");

    }

    @Then("status code should be {int}")
    public void status_code_should_be(int statusCode) {
        //verify status code matches with the feature file expected status code
        Assert.assertEquals(statusCode, response.statusCode());
        System.out.println("statusCode = " + statusCode);

    }

    @Then("the information about current user from api and database should match")
    public void theInformationAboutCurrentUserFromApiAndDatabaseShouldMatch() {
        System.out.println("we will compare database and api in this step");

        //get information from database
        //connection is from hooks, and it will be ready
        String query = "select firstname,lastname,role from users where email = '" + emailGlobal + "'";

        Map<String, Object> dbMap = DBUtils.getRowMap(query);
        System.out.println("dbMap = " + dbMap);
        //save db info into variables
        String expectedFirstName = (String) dbMap.get("firstname");
        System.out.println("expectedFirstName = " + expectedFirstName);
        String expectedLastName = (String) dbMap.get("lastname");
        System.out.println("expectedLastName = " + expectedLastName);
        String expectedRole = (String) dbMap.get("role");
        System.out.println("expectedRole = " + expectedRole);

        //get information from api
        JsonPath jsonPath = response.jsonPath();
        System.out.println("jsonPath = " + jsonPath);

        //save api info into variables
        String actualFirstName = jsonPath.getString("firstName");
        System.out.println("actualFirstName = " + actualFirstName);
        String actualLastName = jsonPath.getString("lastName");
        System.out.println("actualLastName = " + actualLastName);
        String actualRole = jsonPath.getString("role");
        System.out.println("actualRole = " + actualRole);

        //compare database vs api
        Assert.assertEquals(expectedFirstName, actualFirstName);
        Assert.assertEquals(expectedLastName, actualLastName);
        Assert.assertEquals(expectedRole, actualRole);

    }

    @Then("UI,API and Database user information must be match")
    public void uiAPIAndDatabaseUserInformationMustBeMatch() {
        //get information from database
        //connection is from hooks and it will be ready
        String query = "select firstname,lastname,role from users\n" +
                "where email = '" + emailGlobal + "'";

        Map<String, Object> dbMap = DBUtils.getRowMap(query);
        System.out.println("dbMap = " + dbMap);
        //save db info into variables
        String expectedFirstName = (String) dbMap.get("firstname");
        String expectedLastName = (String) dbMap.get("lastname");
        String expectedRole = (String) dbMap.get("role");

        //get information from api
        JsonPath jsonPath = response.jsonPath();
        //save api info into variables
        String actualFirstName = jsonPath.getString("firstName");
        String actualLastName = jsonPath.getString("lastName");
        String actualRole = jsonPath.getString("role");

        //get information from UI
        SelfPage selfPage = new SelfPage();
        String actualUIName = selfPage.name.getText();
        String actualUIRole = selfPage.role.getText();

        System.out.println("actualUIName = " + actualUIName);
        System.out.println("actualUIRole = " + actualUIRole);

        //UI vs DB
        String expectedFullName = expectedFirstName + " " + expectedLastName;
        //verify ui fullname vs db fullname
        Assert.assertEquals(expectedFullName, actualUIName);
        Assert.assertEquals(expectedRole, actualUIRole);

        //UI vs API
        //Create a fullname for api

        String actualFullName = actualFirstName + " " + actualLastName;

        Assert.assertEquals(actualFullName, actualUIName);
        Assert.assertEquals(actualRole, actualUIRole);

    }

    @When("I send POST request to {string} endpoint with following information")
    public void i_send_POST_request_to_endpoint_with_following_information(String path, Map<String, String> studentInfo) {
        //why we prefer to get information as a map from feature file ?
        //bc we have queryParams method that takes map and pass to url as query key&value structure
        System.out.println("studentInfo = " + studentInfo);

        //assign email and password value to these variables so that we can use them later for deleting
        studentEmail = studentInfo.get("email");
        studentPassword = studentInfo.get("password");

        //I added this token from Jamal's video
        token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxNzEiLCJhdWQiOiJ0ZWFjaGVyIn0.AKGnMkwrNdDO-1yDmV6usbK8eTennqG7RZEPL5LO8cY";

        response = given().accept(ContentType.JSON)
                .queryParams(studentInfo)
                .and().header("Authorization", token)
                .log().all()
                .when()
                .post(Environment.BASE_URL + path)
                .then().log().all().extract().response();
    }

    @Then("I delete previously added student")
    public void i_delete_previously_added_student() {
        //We have a custom delete method in the util:
        //BookItApiUtil.deleteStudent(studentEmail, studentPassword);

        //Send a GET request to get token with student information
        //String studentToken = BookItApiUtil.generateToken(studentInfo.get("email"), studentInfo.get("password"));
        //OR
        String studentToken = BookItApiUtil.generateToken(studentEmail, studentPassword);

        //Send a GET request to /api/user/me endpoint and get the id number
        int idToDelete = given().accept(ContentType.JSON)
                .and().header("Authorization", studentToken)
                .when()
                .get(ConfigurationReader.get("base_url") + "/api/users/me")
                .then().statusCode(200).extract().jsonPath().getInt("id");

        System.out.println("idToDelete = " + idToDelete);
        //send a delete request as a teacher to /api/students/{id} endpoint to delete the students
        //Normally, it should have been like this:
        //String teacherToken = BookItApiUtil.generateToken(ConfigurationReader.get("teacherEmail"), ConfigurationReader.get("teacherPassword"));
        //However, Zulpikar didnt provide the right password, I copied the one below from Jamal's.
        String teacherToken = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxNzEiLCJhdWQiOiJ0ZWFjaGVyIn0.AKGnMkwrNdDO-1yDmV6usbK8eTennqG7RZEPL5LO8cY";
        int statusCodeOnDeletion = given()
                .pathParam("id", idToDelete)
                .and().header("Authorization", teacherToken)
                .when()
                .delete(ConfigurationReader.get("base_url") + "/api/students/{id}")
                .then().statusCode(204).extract().statusCode();

        //just my check
        int statusCode = given()
                .pathParam("id", idToDelete)
                .and().header("Authorization", teacherToken)
                .when()
                .delete(ConfigurationReader.get("base_url") + "/api/students/{id}")
                .then().extract().statusCode();

        System.out.println("statusCodeOnDeletion = " + statusCodeOnDeletion);
        System.out.println("statusCode after deletion = " + statusCode);
        System.out.println("teacherToken = " + teacherToken);
    }


    @Given("I logged Bookit api as {string}")
    public void iLoggedBookitApiAs(String role) {
        token = BookItApiUtil.getTokenByRole(role);
    }

}
