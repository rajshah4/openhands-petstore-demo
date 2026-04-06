package ip.swagger.petstore;

import io.swagger.oas.inflector.models.RequestContext;
import io.swagger.oas.inflector.models.ResponseContext;
import io.swagger.petstore.controller.OrderController;
import io.swagger.petstore.controller.PetController;
import io.swagger.petstore.controller.UserController;
import io.swagger.petstore.data.OrderData;
import io.swagger.petstore.data.UserData;
import io.swagger.petstore.notification.Notifier;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.MultivaluedHashMap;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class PetStoreTest {

    private RequestContext minimalRequest;

    @Before
    public void setUp() {
        minimalRequest = new RequestContext()
                .headers(new MultivaluedHashMap<>())
                .acceptableMediaTypes(Collections.emptyList());
    }

    /**
     * Regression test: findPetsByStatus must NOT call the error notifier on a successful lookup.
     * Before the fix, line 65 of PetController fired notifier.notify even when pets were found.
     */
    @Test
    public void findPetsByStatus_successDoesNotTriggerErrorNotifier() throws Exception {
        PetController controller = new PetController();

        AtomicInteger notifyCount = new AtomicInteger(0);
        Notifier spy = ex -> notifyCount.incrementAndGet();

        Field notifierField = PetController.class.getDeclaredField("notifier");
        notifierField.setAccessible(true);
        notifierField.set(controller, spy);

        ResponseContext response = controller.findPetsByStatus(minimalRequest, "available");

        assertEquals("Success response should not trigger the error notifier", 0, notifyCount.get());
        assertNotNull("Response entity should not be null on success", response.getEntity());
    }

    /**
     * Regression test: deleteUser must return a descriptive message (not a null entity) after
     * successful deletion. Before the fix UserController.deleteUser returned .entity(user)
     * where user was null.
     */
    @Test
    public void deleteUser_returnsDescriptiveMessageOnSuccess() {
        UserController controller = new UserController();
        UserData.createUser(99L, "testuser-regression", "Test", "User",
                "test@example.com", "000-000-0000", 1);
        // Add user directly through the controller
        controller.createUser(minimalRequest, 99L, "testuser-regression", "Test", "User",
                "test@example.com", "secret", "000-000-0000", 1);

        ResponseContext response = controller.deleteUser(minimalRequest, "testuser-regression");

        assertNotNull("Entity must not be null after successful deletion", response.getEntity());
        assertEquals("User deleted", response.getEntity());
    }

    /**
     * Regression test: deleteOrder must return a descriptive message (not a null entity) after
     * successful deletion. Before the fix OrderController.deleteOrder returned .entity(order)
     * where order was null.
     */
    @Test
    public void deleteOrder_returnsDescriptiveMessageOnSuccess() {
        OrderController controller = new OrderController();
        controller.placeOrder(minimalRequest, OrderData.createOrder(999L, 1L, 1,
                new Date(), "placed", false));

        ResponseContext response = controller.deleteOrder(minimalRequest, 999L);

        assertNotNull("Entity must not be null after successful deletion", response.getEntity());
        assertEquals("Order deleted", response.getEntity());
    }
}
