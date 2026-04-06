package io.swagger.petstore.controller;

import io.swagger.oas.inflector.models.RequestContext;
import io.swagger.oas.inflector.models.ResponseContext;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.Response;
import java.util.Collections;

import static org.junit.Assert.*;

public class UserControllerTest {

    private UserController controller;
    private RequestContext request;

    @Before
    public void setUp() {
        controller = new UserController();
        request = new RequestContext()
                .headers(new MultivaluedHashMap<>())
                .acceptableMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON_TYPE));
    }

    @Test
    public void deleteUser_existingUser_returnsDeletedMessageNotNull() {
        // First create a user to ensure it exists
        controller.createUser(request, 999L, "test-delete-user", "Test", "User",
                "test@example.com", "secret", "555-0000", 1);

        ResponseContext response = controller.deleteUser(request, "test-delete-user");

        assertNotNull("Response must not be null", response);
        Object entity = response.getEntity();
        assertNotNull("Entity must not be null after successful deletion", entity);
        assertEquals("Entity should be 'User deleted'", "User deleted", entity);
    }

    @Test
    public void deleteUser_nullUsername_returnsBadRequest() {
        ResponseContext response = controller.deleteUser(request, null);

        assertNotNull(response);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }
}
