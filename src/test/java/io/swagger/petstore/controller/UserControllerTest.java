package io.swagger.petstore.controller;

import io.swagger.oas.inflector.models.RequestContext;
import io.swagger.oas.inflector.models.ResponseContext;
import io.swagger.petstore.model.User;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import java.util.Collections;

import static org.junit.Assert.*;

/**
 * Regression tests for {@link UserController}.
 *
 * Known bug covered here: {@code deleteUser} returns {@code entity(user)} where
 * {@code user} is {@code null} after a successful deletion, so the response
 * body is null instead of a confirmation message.
 */
public class UserControllerTest {

    private UserController controller;
    private RequestContext request;

    @Before
    public void setUp() {
        controller = new UserController();
        request = new RequestContext()
                .headers(new MultivaluedHashMap<String, String>())
                .acceptableMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON_TYPE));
    }

    // ---- getUserByName ----

    @Test
    public void getUserByName_nullUsername_returns400() {
        ResponseContext response = controller.getUserByName(request, null);
        assertEquals(400, response.getStatus());
    }

    @Test
    public void getUserByName_existingUser_returns200WithUser() {
        ResponseContext response = controller.getUserByName(request, "user1");
        assertEquals(200, response.getStatus());
        assertNotNull(response.getEntity());
        User user = (User) response.getEntity();
        assertEquals("user1", user.getUsername());
    }

    @Test
    public void getUserByName_nonExistentUser_returns404() {
        ResponseContext response = controller.getUserByName(request, "nobody_xyzzy");
        assertEquals(404, response.getStatus());
    }

    // ---- createUser ----

    @Test
    public void createUser_nullUser_returns400() {
        ResponseContext response = controller.createUser(request, (User) null);
        assertEquals(400, response.getStatus());
    }

    @Test
    public void createUser_validUser_returns200WithUser() {
        User user = new User();
        user.setUsername("testuser_9901");
        user.setFirstName("Test");
        user.setLastName("User");

        ResponseContext response = controller.createUser(request, user);
        assertEquals(200, response.getStatus());
        assertEquals(user, response.getEntity());

        controller.deleteUser(request, "testuser_9901"); // cleanup
    }

    // ---- deleteUser ----

    @Test
    public void deleteUser_nullUsername_returns400() {
        ResponseContext response = controller.deleteUser(request, null);
        assertEquals(400, response.getStatus());
    }

    @Test
    public void deleteUser_existingUser_returns200() {
        User user = new User();
        user.setUsername("testuser_9902");
        controller.createUser(request, user);

        ResponseContext response = controller.deleteUser(request, "testuser_9902");
        assertEquals(200, response.getStatus());
        // Bug: entity is null because deleteUser calls entity(user) where user is the
        // result of findUserByName after deletion (which is null).
        assertNull("Bug: deleteUser returns null entity instead of a confirmation", response.getEntity());
    }

    @Test
    public void deleteUser_nonExistentUser_returns200() {
        // Deleting a non-existent user does not fail — the store is silent.
        ResponseContext response = controller.deleteUser(request, "doesnotexist_xyz");
        assertEquals(200, response.getStatus());
    }

    // ---- updateUser ----

    @Test
    public void updateUser_nullUsername_returns400() {
        User user = new User();
        user.setUsername("any");
        ResponseContext response = controller.updateUser(request, null, user);
        assertEquals(400, response.getStatus());
    }

    @Test
    public void updateUser_nullUser_returns400() {
        ResponseContext response = controller.updateUser(request, "user1", (User) null);
        assertEquals(400, response.getStatus());
    }

    @Test
    public void updateUser_nonExistentUser_returns404() {
        User user = new User();
        user.setUsername("replacement");
        ResponseContext response = controller.updateUser(request, "nobody_xyzzy", user);
        assertEquals(404, response.getStatus());
    }

    // ---- loginUser / logoutUser ----

    @Test
    public void loginUser_returnsSessionToken() {
        ResponseContext response = controller.loginUser(request, "user1", "pass");
        assertEquals(200, response.getStatus());
        assertNotNull(response.getEntity());
        assertTrue(response.getEntity().toString().contains("Logged in user session"));
    }

    @Test
    public void logoutUser_returnsConfirmation() {
        ResponseContext response = controller.logoutUser(request);
        assertEquals(200, response.getStatus());
        assertEquals("User logged out", response.getEntity());
    }
}
