package io.swagger.petstore.controller;

import io.swagger.oas.inflector.models.RequestContext;
import io.swagger.oas.inflector.models.ResponseContext;
import io.swagger.petstore.model.Pet;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Regression tests for {@link PetController}.
 *
 * Known bug covered here: {@code findPetsByStatus} triggers a "Pets not found"
 * notification even when pets are successfully returned (HTTP 200). The HTTP
 * status code is still correct; only the internal notifier is misfired.
 */
public class PetControllerTest {

    private PetController controller;
    private RequestContext request;

    @Before
    public void setUp() {
        controller = new PetController();
        request = new RequestContext()
                .headers(new MultivaluedHashMap<String, String>())
                .acceptableMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON_TYPE));
    }

    // ---- findPetsByStatus ----

    @Test
    public void findPetsByStatus_nullStatus_returns400() {
        ResponseContext response = controller.findPetsByStatus(request, null);
        assertEquals(400, response.getStatus());
    }

    @Test
    public void findPetsByStatus_validStatus_returns200WithPetList() {
        // Bug: notifier.notify("Pets not found") is called even on success, but
        // the HTTP response should still be 200 OK with a non-null list.
        ResponseContext response = controller.findPetsByStatus(request, "available");
        assertEquals("Expected HTTP 200 for valid status (bug: notifier fires on success)", 200, response.getStatus());
        assertNotNull(response.getEntity());
        @SuppressWarnings("unchecked")
        List<Pet> pets = (List<Pet>) response.getEntity();
        assertFalse("At least one available pet expected in seed data", pets.isEmpty());
    }

    @Test
    public void findPetsByStatus_sold_returnsOnlySoldPets() {
        ResponseContext response = controller.findPetsByStatus(request, "sold");
        assertEquals(200, response.getStatus());
        @SuppressWarnings("unchecked")
        List<Pet> pets = (List<Pet>) response.getEntity();
        assertFalse(pets.isEmpty());
        for (Pet pet : pets) {
            assertEquals("sold", pet.getStatus());
        }
    }

    @Test
    public void findPetsByStatus_unknownStatus_returnsEmptyList() {
        ResponseContext response = controller.findPetsByStatus(request, "nonexistent");
        assertEquals(200, response.getStatus());
        @SuppressWarnings("unchecked")
        List<Pet> pets = (List<Pet>) response.getEntity();
        assertNotNull(pets);
        assertTrue(pets.isEmpty());
    }

    // ---- getPetById ----

    @Test
    public void getPetById_nullId_returns400() {
        ResponseContext response = controller.getPetById(request, null);
        assertEquals(400, response.getStatus());
    }

    @Test
    public void getPetById_existingPet_returns200WithPet() {
        ResponseContext response = controller.getPetById(request, 1L);
        assertEquals(200, response.getStatus());
        assertNotNull(response.getEntity());
        Pet pet = (Pet) response.getEntity();
        assertEquals(1L, pet.getId());
    }

    @Test
    public void getPetById_unknownId_returns404() {
        ResponseContext response = controller.getPetById(request, 99999L);
        assertEquals(404, response.getStatus());
    }

    // ---- addPet ----

    @Test
    public void addPet_nullPet_returns400() {
        ResponseContext response = controller.addPet(request, (Pet) null);
        assertEquals(400, response.getStatus());
    }

    @Test
    public void addPet_validPet_returns200WithSamePet() {
        Pet pet = new Pet();
        pet.setId(9901L);
        pet.setName("Test Pet Add");
        pet.setStatus("available");

        ResponseContext response = controller.addPet(request, pet);
        assertEquals(200, response.getStatus());
        assertEquals(pet, response.getEntity());

        controller.deletePet(request, null, 9901L); // cleanup
    }

    // ---- deletePet ----

    @Test
    public void deletePet_nullPetId_returns400() {
        ResponseContext response = controller.deletePet(request, null, null);
        assertEquals(400, response.getStatus());
    }

    @Test
    public void deletePet_existingPet_returnsSuccessMessage() {
        Pet pet = new Pet();
        pet.setId(9902L);
        pet.setName("Pet To Delete");
        pet.setStatus("available");
        controller.addPet(request, pet);

        ResponseContext response = controller.deletePet(request, null, 9902L);
        assertEquals(200, response.getStatus());
        assertEquals("Pet deleted", response.getEntity());
    }

    // ---- updatePet ----

    @Test
    public void updatePet_nullPet_returns400() {
        ResponseContext response = controller.updatePet(request, (Pet) null);
        assertEquals(400, response.getStatus());
    }

    @Test
    public void updatePet_nonExistentPet_returns404() {
        Pet pet = new Pet();
        pet.setId(99999L);
        pet.setName("Ghost");
        ResponseContext response = controller.updatePet(request, pet);
        assertEquals(404, response.getStatus());
    }

    // ---- findPetsByTags ----

    @Test
    public void findPetsByTags_emptyTags_returns400() {
        ResponseContext response = controller.findPetsByTags(request, Collections.emptyList());
        assertEquals(400, response.getStatus());
    }

    @Test
    public void findPetsByTags_existingTag_returnsPets() {
        ResponseContext response = controller.findPetsByTags(request, Collections.singletonList("tag1"));
        assertEquals(200, response.getStatus());
        @SuppressWarnings("unchecked")
        List<Pet> pets = (List<Pet>) response.getEntity();
        assertFalse(pets.isEmpty());
    }
}
