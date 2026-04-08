package io.swagger.petstore.controller;

import io.swagger.oas.inflector.models.RequestContext;
import io.swagger.oas.inflector.models.ResponseContext;
import io.swagger.petstore.model.Order;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Regression tests for {@link OrderController}.
 *
 * Known bug covered here: {@code deleteOrder} returns {@code entity(order)}
 * where {@code order} is {@code null} after a successful deletion, so the
 * response body is null instead of a confirmation message.
 */
public class OrderControllerTest {

    private OrderController controller;
    private RequestContext request;

    @Before
    public void setUp() {
        controller = new OrderController();
        request = new RequestContext()
                .headers(new MultivaluedHashMap<String, String>())
                .acceptableMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON_TYPE));
    }

    // ---- getOrderById ----

    @Test
    public void getOrderById_nullId_returns400() {
        ResponseContext response = controller.getOrderById(request, null);
        assertEquals(400, response.getStatus());
    }

    @Test
    public void getOrderById_existingOrder_returns200WithOrder() {
        ResponseContext response = controller.getOrderById(request, 1L);
        assertEquals(200, response.getStatus());
        assertNotNull(response.getEntity());
        Order order = (Order) response.getEntity();
        assertEquals(1L, order.getId());
    }

    @Test
    public void getOrderById_nonExistentOrder_returns404() {
        ResponseContext response = controller.getOrderById(request, 99999L);
        assertEquals(404, response.getStatus());
    }

    // ---- placeOrder ----

    @Test
    public void placeOrder_nullOrder_returns400() {
        ResponseContext response = controller.placeOrder(request, (Order) null);
        assertEquals(400, response.getStatus());
    }

    @Test
    public void placeOrder_validOrder_returns200WithOrder() {
        Order order = new Order();
        order.setId(9901L);
        order.setPetId(1L);
        order.setQuantity(2);
        order.setStatus("placed");
        order.setComplete(false);

        ResponseContext response = controller.placeOrder(request, order);
        assertEquals(200, response.getStatus());
        assertEquals(order, response.getEntity());

        controller.deleteOrder(request, 9901L); // cleanup
    }

    // ---- deleteOrder ----

    @Test
    public void deleteOrder_nullOrderId_returns400() {
        ResponseContext response = controller.deleteOrder(request, null);
        assertEquals(400, response.getStatus());
    }

    @Test
    public void deleteOrder_existingOrder_returns200() {
        Order order = new Order();
        order.setId(9902L);
        order.setPetId(1L);
        order.setQuantity(1);
        order.setStatus("placed");
        order.setComplete(false);
        controller.placeOrder(request, order);

        ResponseContext response = controller.deleteOrder(request, 9902L);
        assertEquals(200, response.getStatus());
        // Bug: entity is null because deleteOrder calls entity(order) where order is the
        // result of getOrderById after deletion (which is null).
        assertNull("Bug: deleteOrder returns null entity instead of a confirmation", response.getEntity());
    }

    @Test
    public void deleteOrder_nonExistentOrder_returns200() {
        // Deleting a non-existent order does not fail — the store is silent.
        ResponseContext response = controller.deleteOrder(request, 99998L);
        assertEquals(200, response.getStatus());
    }

    // ---- getInventory ----

    @Test
    public void getInventory_returns200WithStatusCountMap() {
        ResponseContext response = controller.getInventory(request);
        assertEquals(200, response.getStatus());
        assertNotNull(response.getEntity());
        @SuppressWarnings("unchecked")
        Map<String, Integer> inventory = (Map<String, Integer>) response.getEntity();
        assertFalse("Inventory should have at least one status entry", inventory.isEmpty());
        // Seed data has orders with statuses: placed, approved, delivered
        assertTrue(inventory.containsKey("placed"));
    }
}
