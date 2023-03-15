import io.restassured.RestAssured;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;
import org.junit.Test;
import org.testng.Assert;

public class Booking {
    @Test
    public static void main(String[] args) {
        String endpoint = "https://restful-booker.herokuapp.com/booking";

        //Create a booking
        Response request = given()
                .baseUri(endpoint)
                .header("Content-Type", ContentType.JSON)
                .body("{\n" +
                        "   \"firstname\" : \"Melanie\",\n" +
                        "    \"lastname\" : \"Navarro\",\n" +
                        "    \"totalprice\" : 500,\n" +
                        "    \"depositpaid\" : true,\n" +
                        "    \"bookingdates\" : {\n" +
                        "        \"checkin\" : \"2023-03-12\",\n" +
                        "        \"checkout\" : \"2023-03-16\"\n" +
                        "    },\n" +
                        "    \"additionalneeds\" : \"Breakfast\"\n" +
                        "}")
                .post();

        String responseBody = request.getBody().asString();
        Assert.assertEquals(200, request.getStatusCode());
        System.out.println("Create Booking Response: " + responseBody);

        //Validate that the response contains a “bookingid” information and copy it
        String bookingId = request.jsonPath().getString("bookingid");
        System.out.println("Validate Booking ID: " + bookingId);

        String getBookingByID = "https://restful-booker.herokuapp.com/booking/" + bookingId;

        //Search the booking with GET
        Response response = RestAssured.get(getBookingByID);
        String responseBookingByID = response.getBody().asString();
        System.out.println("Get Booking Response By ID:" + responseBookingByID);

        //Update the additionalneeds
        String baseUrl = "https://restful-booker.herokuapp.com";
        String authHeader = "Basic YWRtaW46cGFzc3dvcmQxMjM=";
        String token = "abc123";

        Response updateResponse = given()
                .baseUri(baseUrl)
                .basePath("/booking/" + bookingId)
                .header("Content-Type", ContentType.JSON)
                .header("Authorization", authHeader)
                .cookie("token", token)
                .body("{\n" +
                        "   \"firstname\" : \"Melanie\",\n" +
                        "   \"lastname\" : \"Navarro\",\n" +
                        "   \"totalprice\" : 500,\n" +
                        "   \"depositpaid\" : true,\n" +
                        "   \"bookingdates\" : {\n" +
                        "       \"checkin\" : \"2023-03-12\",\n" +
                        "       \"checkout\" : \"2023-03-16\"\n" +
                        "   },\n" +
                        "   \"additionalneeds\" : \"More Coffe\"\n" +
                        "}")
                .put();
        Assert.assertEquals(200, updateResponse.getStatusCode());
        String updateresponse = updateResponse.getBody().asString();
        System.out.println("Update Booking Response: " + updateresponse);

        //Delete Booking
        given()
                .baseUri(baseUrl)
                .header("Authorization", authHeader)
                .header("Content-Type", ContentType.JSON)
                .header("Cookie", "token=" + token)
                .when()
                .delete("/booking/" + bookingId)
                .then()
                .statusCode(201);

        System.out.println("Booking with ID " + bookingId + " has been deleted.");

    }
}
