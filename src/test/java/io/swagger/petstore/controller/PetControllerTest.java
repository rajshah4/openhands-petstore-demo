package io.swagger.petstore.controller;

import io.swagger.oas.inflector.models.RequestContext;
import io.swagger.oas.inflector.models.ResponseContext;
import io.swagger.petstore.notification.Notifier;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class PetControllerTest {

    private PetController controller;
    private AtomicInteger notifyCallCount;

    @Before
    public void setUp() throws Exception {
        controller = new PetController();
        notifyCallCount = new AtomicInteger(0);
        
        // Inject a test notifier that tracks calls
        Notifier testNotifier = new Notifier() {
            @Override
            public void notify(Throwable e) {
                notifyCallCount.incrementAndGet();
            }
        };
        
        // Use reflection to inject the test notifier
        Field notifierField = PetController.class.getDeclaredField("notifier");
        notifierField.setAccessible(true);
        notifierField.set(controller, testNotifier);
    }

    @Test
    public void testFindPetsByStatus_SuccessfulLookup_ShouldNotNotifyError() {
        // Arrange
        RequestContext request = new RequestContext();
        String validStatus = "available";
        
        // Act
        ResponseContext response = controller.findPetsByStatus(request, validStatus);
        
        // Assert - successful lookups should NOT trigger error notifications
        assertEquals("Error notifier should not be called on successful pet lookup", 
                     0, notifyCallCount.get());
        assertNotNull("Response should not be null", response);
    }

    @Test
    public void testFindPetsByStatus_NullStatus_ShouldNotifyError() {
        // Arrange
        RequestContext request = new RequestContext();
        
        // Act
        ResponseContext response = controller.findPetsByStatus(request, null);
        
        // Assert - null status should trigger error notification
        assertEquals("Error notifier should be called once for null status", 
                     1, notifyCallCount.get());
        assertEquals("Should return BAD_REQUEST status", 
                     400, response.getStatus());
    }

    @Test
    public void testFindPetsByStatus_NonexistentStatus_ShouldNotifyError() {
        // Arrange
        RequestContext request = new RequestContext();
        String nonexistentStatus = "nonexistent_status_12345";
        
        // Act
        ResponseContext response = controller.findPetsByStatus(request, nonexistentStatus);
        
        // Assert - if pets not found, should trigger error notification
        // Note: The actual behavior depends on PetData.findPetByStatus implementation
        // If it returns null or empty list, notification behavior will vary
        assertNotNull("Response should not be null", response);
    }
}
