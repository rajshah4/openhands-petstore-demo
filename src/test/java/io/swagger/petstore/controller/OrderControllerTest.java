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

public class OrderControllerTest {

    private OrderController controller;
    private RequestContext request;

    @Before
    public void setUp() {
        controller = new OrderController();
        request = new RequestContext()
                .headers(new MultivaluedHashMap<>())
                .acceptableMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON_TYPE));
    }

    @Test
    public void deleteOrder_existingOrder_returnsDeletedMessageNotNull() {
        // Place a fresh order so we know its ID
        long testOrderId = 9991L;
        controller.placeOrder(request, testOrderId, 1L, 1, null, "placed", false);

        ResponseContext response = controller.deleteOrder(request, testOrderId);

        assertNotNull("Response must not be null", response);
        Object entity = response.getEntity();
        assertNotNull("Entity must not be null after successful deletion", entity);
        assertEquals("Entity should be 'Order deleted'", "Order deleted", entity);
    }

    @Test
    public void deleteOrder_nullOrderId_returnsBadRequest() {
        ResponseContext response = controller.deleteOrder(request, null);

        assertNotNull(response);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }
}
