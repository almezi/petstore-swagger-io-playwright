package com.almezi.steps;

import com.almezi.context.TestContext;
import com.almezi.model.Category;
import com.almezi.model.Pet;
import com.almezi.model.Tag;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PetStepDefinitions {

    private final TestContext context = TestContext.get();

    @Given("the pet API endpoint is available")
    public void thePetApiEndpointIsAvailable() {
        assertThat(context.getRequestContext()).isNotNull();
    }

    @When("I create a pet with id {long}, name {string} and status {string}")
    public void iCreateAPetWithIdNameAndStatus(long id, String name, String status) throws IOException {
        Pet pet = new Pet(id, name, status);
        pet.setCategory(new Category(1L, "Dogs"));
        pet.setPhotoUrls(Collections.singletonList("https://example.com/photo.jpg"));
        pet.setTags(Collections.singletonList(new Tag(1L, "friendly")));

        String payload = context.getObjectMapper().writeValueAsString(pet);

        APIResponse response = context.getRequestContext().post("pet",
                RequestOptions.create().setData(payload));
        context.setResponse(response);
    }

    @When("I retrieve the pet with id {long}")
    public void iRetrieveThePetWithId(long id) {
        APIResponse response = context.getRequestContext().get("pet/" + id);
        context.setResponse(response);
    }

    @When("I update the pet id {long} with name {string} and status {string}")
    public void iUpdateThePetIdWithNameAndStatus(long id, String name, String status) throws IOException {
        Pet pet = new Pet(id, name, status);
        pet.setCategory(new Category(1L, "Dogs"));
        pet.setPhotoUrls(Collections.singletonList("https://example.com/updated.jpg"));
        pet.setTags(Collections.singletonList(new Tag(1L, "domestic")));

        String payload = context.getObjectMapper().writeValueAsString(pet);

        APIResponse response = context.getRequestContext().put("pet",
                RequestOptions.create().setData(payload));
        context.setResponse(response);
    }

    @When("I find pets by status {string}")
    public void iFindPetsByStatus(String status) {
        APIResponse response = context.getRequestContext().get("pet/findByStatus",
                RequestOptions.create().setQueryParam("status", status));
        context.setResponse(response);
    }

    @When("I find pets by multiple statuses {string} and {string}")
    public void iFindPetsByMultipleStatuses(String status1, String status2) {
        APIResponse response = context.getRequestContext().get("pet/findByStatus?status=" + status1 + "&status=" + status2);
        context.setResponse(response);
    }

    @When("I find pets without specifying the status parameter")
    public void iFindPetsWithoutSpecifyingTheStatusParameter() {
        APIResponse response = context.getRequestContext().get("pet/findByStatus");
        context.setResponse(response);
    }

    @When("I delete the pet with id {long}")
    public void iDeleteThePetWithId(long id) {
        APIResponse response = context.getRequestContext().delete("pet/" + id);
        context.setResponse(response);
    }

    @Given("a pet exists with dynamic ID and name {string}")
    public void aPetExistsWithDynamicIdAndName(String name) throws IOException {
        long id = context.generateDynamicPetId();
        Pet pet = Pet.createValidPet(id, name, "available");
        String payload = context.getObjectMapper().writeValueAsString(pet);

        APIResponse response = context.getRequestContext().post("pet",
                RequestOptions.create().setData(payload));
        context.setResponse(response);
        assertThat(response.status()).isEqualTo(200);
    }

    @When("I delete the previously created pet")
    public void iDeleteThePreviouslyCreatedPet() {
        Long id = context.getLastPetId();
        assertThat(id).isNotNull();
        APIResponse response = context.getRequestContext().delete("pet/" + id);
        context.setResponse(response);
    }

    @When("I retrieve the previously created pet")
    public void iRetrieveThePreviouslyCreatedPet() {
        Long id = context.getLastPetId();
        assertThat(id).isNotNull();
        APIResponse response = context.getRequestContext().get("pet/" + id);
        context.setResponse(response);
    }

    @When("I delete a pet with non-existent ID {long}")
    public void iDeleteAPetWithNonExistentId(long nonExistentId) {
        APIResponse response = context.getRequestContext().delete("pet/" + nonExistentId);
        context.setResponse(response);
    }

    @When("I delete a pet with invalid non-numeric ID {string}")
    public void iDeleteAPetWithInvalidNonNumericId(String invalidId) {
        APIResponse response = context.getRequestContext().delete("pet/" + invalidId);
        context.setResponse(response);
    }

    @When("I repeat the deletion of the same pet")
    public void iRepeatTheDeletionOfTheSamePet() {
        Long id = context.getLastPetId();
        assertThat(id).isNotNull();
        APIResponse response = context.getRequestContext().delete("pet/" + id);
        context.setResponse(response);
    }

    @When("I create a pet with dynamic ID, name {string} and status {string}")
    public void iCreateAPetWithDynamicIdNameAndStatus(String name, String status) throws IOException {
        long id = context.generateDynamicPetId();
        Pet pet = Pet.createValidPet(id, name, status);
        String payload = context.getObjectMapper().writeValueAsString(pet);

        APIResponse response = context.getRequestContext().post("pet",
                RequestOptions.create().setData(payload));
        context.setResponse(response);
    }

    @When("I submit a pet creation request with missing name field")
    public void iSubmitAPetCreationRequestWithMissingNameField() throws IOException {
        long id = context.generateDynamicPetId();
        Pet pet = Pet.createWithoutName(id, "available");
        String payload = context.getObjectMapper().writeValueAsString(pet);

        APIResponse response = context.getRequestContext().post("pet",
                RequestOptions.create().setData(payload));
        context.setResponse(response);
    }

    @When("I submit a pet creation request with missing photoUrls field")
    public void iSubmitAPetCreationRequestWithMissingPhotoUrlsField() throws IOException {
        long id = context.generateDynamicPetId();
        Pet pet = Pet.createWithoutPhotoUrls(id, "NoPhotoPet", "available");
        String payload = context.getObjectMapper().writeValueAsString(pet);

        APIResponse response = context.getRequestContext().post("pet",
                RequestOptions.create().setData(payload));
        context.setResponse(response);
    }

    @When("I submit a raw pet creation payload:")
    public void iSubmitARawPetCreationPayload(String rawPayload) {
        context.postRaw("pet", rawPayload);
    }

    @Then("the response status code should be {int}")
    public void theResponseStatusCodeShouldBe(int expectedStatusCode) {
        APIResponse response = context.getResponse();
        assertThat(response).isNotNull();
        assertThat(response.status()).isEqualTo(expectedStatusCode);
    }

    @Then("the response status code should be one of {int}, {int}, or {int}")
    public void theResponseStatusCodeShouldBeOneOf(int code1, int code2, int code3) {
        APIResponse response = context.getResponse();
        assertThat(response).isNotNull();
        assertThat(response.status()).isIn(code1, code2, code3);
    }

    @Then("the response status code should be one of {int} or {int}")
    public void theResponseStatusCodeShouldBeOneOf(int code1, int code2) {
        APIResponse response = context.getResponse();
        assertThat(response).isNotNull();
        assertThat(response.status()).isIn(code1, code2);
    }

    @And("the response error message should contain {string}")
    public void theResponseErrorMessageShouldContain(String expectedMessage) {
        String body = context.getResponseBody();
        assertThat(body).isNotNull();
        assertThat(body).containsIgnoringCase(expectedMessage);
    }

    @And("the response pet id should match the created dynamic ID")
    public void theResponsePetIdShouldMatchTheCreatedDynamicId() throws IOException {
        Long expectedId = context.getLastPetId();
        assertThat(expectedId).isNotNull();
        JsonNode jsonNode = context.getObjectMapper().readTree(context.getResponseBody());
        assertThat(jsonNode.get("id").asLong()).isEqualTo(expectedId);
    }

    @And("the response pet name should be {string}")
    public void theResponsePetNameShouldBe(String expectedName) throws IOException {
        JsonNode jsonNode = context.getObjectMapper().readTree(context.getResponseBody());
        assertThat(jsonNode.get("name").asText()).isEqualTo(expectedName);
    }

    @And("the response pet status should be {string}")
    public void theResponsePetStatusShouldBe(String expectedStatus) throws IOException {
        JsonNode jsonNode = context.getObjectMapper().readTree(context.getResponseBody());
        assertThat(jsonNode.get("status").asText()).isEqualTo(expectedStatus);
    }

    @And("the response pet id should be {long}")
    public void theResponsePetIdShouldBe(long expectedId) throws IOException {
        JsonNode jsonNode = context.getObjectMapper().readTree(context.getResponseBody());
        assertThat(jsonNode.get("id").asLong()).isEqualTo(expectedId);
    }

    @Then("the response should contain a list of pets with status {string}")
    public void theResponseShouldContainAListOfPetsWithStatus(String expectedStatus) throws IOException {
        List<Pet> pets = context.getObjectMapper().readValue(context.getResponseBody(), new TypeReference<List<Pet>>() {});
        assertThat(pets).isNotEmpty();
        assertThat(pets).allMatch(pet -> expectedStatus.equalsIgnoreCase(pet.getStatus()));
    }

    @Then("the response should contain only pets matching one of statuses {string}")
    public void theResponseShouldContainOnlyPetsMatchingOneOfStatuses(String statuses) throws IOException {
        List<Pet> pets = context.getObjectMapper().readValue(context.getResponseBody(), new TypeReference<List<Pet>>() {});
        List<String> allowed = Arrays.stream(statuses.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .toList();
        assertThat(pets).isNotEmpty();
        assertThat(pets).allMatch(pet -> pet.getStatus() != null && allowed.contains(pet.getStatus().toLowerCase()));
    }

    @And("the response should contain an empty list of pets")
    public void theResponseShouldContainAnEmptyListOfPets() throws IOException {
        JsonNode node = context.getObjectMapper().readTree(context.getResponseBody());
        assertThat(node.isArray()).isTrue();
        assertThat(node.isEmpty()).isTrue();
    }

    @And("the response should be a valid JSON array")
    public void theResponseShouldBeAValidJsonArray() throws IOException {
        JsonNode node = context.getObjectMapper().readTree(context.getResponseBody());
        assertThat(node.isArray()).isTrue();
    }
}
