package exercise;

import static io.restassured.RestAssured.basePath;
import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class testingAPIs {

	Gson gson;

	@BeforeTest
	public void configureTest() {
		RestAssured.baseURI = "https://f2245895-d4fa-45a4-a6d2-9873c2792d6e.mock.pstmn.io";
		RestAssured.basePath = "/api/v1/resources/";
		gson = new Gson();
	}

	@Test
	public void getSingleResource() {
		String expectedId = "3fe5a362-6980-4266-a819-aa6882839292";
		Response response = given().log().all().and().baseUri(baseURI).and().basePath(basePath).when().get(expectedId);

		// Assert status code
		response.then().assertThat().statusCode(200);

		String responseBody = response.body().asString();
		JsonObject responseObj = gson.fromJson(responseBody, JsonObject.class);

		String actualId = responseObj.getAsJsonObject("results").get("id").getAsString();

		// Assert id
		Assert.assertEquals(actualId, expectedId, "Id mismatch");
	}

	@Test
	public void getResourceList() {
		int expectedTotal = 3;
		String resourseList = "resources?sortBy=modificationDate&direction=desc&limit=50&offset=0";
		Response response = given().log().all().and().baseUri(baseURI).and().basePath(basePath).when()
				.get(resourseList);

		// Assert status code
		response.then().assertThat().statusCode(200);

		JsonObject responseObj = response.body().as(JsonObject.class);

		int actualTotal = responseObj.getAsJsonObject("pagination").get("total").getAsInt();

		// Assert total
		Assert.assertEquals(actualTotal, expectedTotal, "Total mismatch");
	}

	@Test
	public void postResource() {
		String payload = "{" + "\"deleted\": false,"
				+ "\"description\": \"9-RM resource 20200928121933264 - description\"," + "\"metadata\": null,"
				+ "\"name\": \"r-RM resource 20200928121933264 - name\"" + "}";

		JsonObject payloadObj = gson.fromJson(payload, JsonObject.class);

		String expectedName = payloadObj.get("name").getAsString();

		Response response = given().log().all().and().baseUri(baseURI).and().basePath(basePath).and().body(payloadObj)
				.when().post("resources");

		// Assert status code
		response.then().assertThat().statusCode(201);

		String responseBody = response.body().asString();
		JsonObject responseObj = gson.fromJson(responseBody, JsonObject.class);

		String actualName = responseObj.getAsJsonObject("results").get("name").getAsString();

		// Assert name
		Assert.assertEquals(actualName, expectedName, "Name mismatch");
	}

	@Test
	public void deleteResource() {
		Response response = given().log().all().and().baseUri(baseURI).and().basePath(basePath).when()
				.delete("resources/17bb4ca1-f0e9-4f05-b606-70aab69b78b1");

		// Assert status code
		response.then().assertThat().statusCode(204);
	}

	@Test
	public void getNoResource() {
		String expectedError = "mockRequestNotFoundError";

		Response response = given().log().all().and().baseUri(baseURI).and().basePath(basePath).when()
				.get("3fe5a362-6980-4266-a819-aa688283929x");

		// Assert status code
		response.then().assertThat().statusCode(404);

		JsonObject responseObj = response.body().as(JsonObject.class);

		String actualError = responseObj.getAsJsonObject("error").get("name").getAsString();

		// Assert error
		Assert.assertEquals(actualError, expectedError, "Error mismatch");
	}

}