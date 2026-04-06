package io.swagger.petstore.controller;

import io.swagger.oas.inflector.models.RequestContext;
import io.swagger.oas.inflector.models.ResponseContext;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class PetControllerTest {

    private PetController controller;
    private RequestContext request;

    @Before
    public void setUp() {
        controller = new PetController();
        request = new RequestContext()
                .headers(new MultivaluedHashMap<>())
                .acceptableMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON_TYPE));
    }

    @Test
    public void findPetsByStatus_validStatus_returnsListNotNull() {
        ResponseContext response = controller.findPetsByStatus(request, "available");

        assertNotNull("Response must not be null", response);
        // Success path: entity should be a non-null list of pets, not an error string
        Object entity = response.getEntity();
        assertNotNull("Entity must not be null on success", entity);
        assertFalse("Entity must not be an error string",
                entity instanceof String && ((String) entity).contains("not found"));
        assertTrue("Entity should be a List", entity instanceof List);
    }

    @Test
    public void findPetsByStatus_nullStatus_returnsBadRequest() {
        ResponseContext response = controller.findPetsByStatus(request, null);

        assertNotNull(response);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void findPetsByStatus_unknownStatus_returnsEmptyList() {
        ResponseContext response = controller.findPetsByStatus(request, "nonexistent-status-xyz");

        assertNotNull(response);
        // findPetByStatus always returns a list (never null), so unknown status yields empty list with 200
        Object entity = response.getEntity();
        assertNotNull("Entity must not be null", entity);
        assertTrue("Entity should be an empty list", entity instanceof List && ((List<?>) entity).isEmpty());
    }
}
