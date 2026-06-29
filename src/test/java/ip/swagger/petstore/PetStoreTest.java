package ip.swagger.petstore;

import io.swagger.petstore.controller.PetController;
import io.swagger.petstore.data.PetData;
import io.swagger.petstore.model.Pet;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for the adoption-fee / budget search feature (KAN-17).
 *
 * These tests exercise the three layers touched by the change:
 *   - the {@link Pet} model's adoptionFee accessor,
 *   - {@link PetData#findPetByBudget(Double)} filtering and ordering, and
 *   - {@link PetController#findPetsByBudget} request validation.
 *
 * They depend only on JUnit and the project's own classes, so they run as part
 * of the standard {@code mvn test} build without booting the inflector server.
 */
public class PetStoreTest {

    @Test
    public void petAdoptionFeeIsReadWriteRoundTripped() {
        final Pet pet = new Pet();
        assertNull("new pet should have no adoption fee", pet.getAdoptionFee());

        pet.setAdoptionFee(42.50);
        assertEquals(Double.valueOf(42.50), pet.getAdoptionFee());
    }

    @Test
    public void seededPetsCarryAdoptionFees() {
        final PetData data = new PetData();
        // Rabbit 1 is the cheapest seeded pet at 15.00
        final Pet rabbit = data.getPetById(10);
        assertNotNull(rabbit);
        assertEquals(Double.valueOf(15.00), rabbit.getAdoptionFee());

        // Lion 2 is the most expensive seeded pet at 7500.00
        final Pet lion = data.getPetById(8);
        assertNotNull(lion);
        assertEquals(Double.valueOf(7500.00), lion.getAdoptionFee());
    }

    @Test
    public void findPetByBudgetReturnsOnlyPetsAtOrBelowBudget() {
        final PetData data = new PetData();
        final List<Pet> affordable = data.findPetByBudget(35.00);

        assertFalse("expected at least one affordable pet", affordable.isEmpty());
        for (final Pet pet : affordable) {
            assertNotNull(pet.getAdoptionFee());
            assertTrue("pet " + pet.getName() + " fee " + pet.getAdoptionFee() + " exceeds budget",
                    pet.getAdoptionFee() <= 35.00);
        }
    }

    @Test
    public void findPetByBudgetIsSortedCheapestFirst() {
        final PetData data = new PetData();
        final List<Pet> affordable = data.findPetByBudget(100.00);

        // 15.00 (Rabbit 1), 25.00 (Cat 1), 35.50 (Cat 2 -> over 100? no, 35.50 <= 100), 60.00 (Dog 3), 75.00 (Dog 1)
        assertEquals(5, affordable.size());
        for (int i = 1; i < affordable.size(); i++) {
            final double prev = affordable.get(i - 1).getAdoptionFee();
            final double curr = affordable.get(i).getAdoptionFee();
            assertTrue("results must be sorted ascending: " + prev + " > " + curr, prev <= curr);
        }
        // The cheapest seeded pet is Rabbit 1 (15.00)
        assertEquals("Rabbit 1", affordable.get(0).getName());
    }

    @Test
    public void findPetByBudgetExcludesExpensivePets() {
        final PetData data = new PetData();
        final List<Pet> affordable = data.findPetByBudget(10.00);
        assertTrue("nothing should be affordable under $10", affordable.isEmpty());
    }

    @Test
    public void findPetByBudgetWithNullBudgetReturnsAllPricedPets() {
        final PetData data = new PetData();
        final List<Pet> all = data.findPetByBudget(null);
        // all 10 seeded pets carry an adoption fee
        assertEquals(10, all.size());
    }

    @Test
    public void controllerRejectsMissingBudget() {
        final PetController controller = new PetController();
        final int status = controller.findPetsByBudget(null, null).getStatus();
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), status);
    }

    @Test
    public void controllerRejectsNegativeBudget() {
        final PetController controller = new PetController();
        final int status = controller.findPetsByBudget(null, -5.00).getStatus();
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), status);
    }

    @Test
    public void findPetByBudgetCoversTheHappyPathDelegatedByTheController() {
        // The controller's findPetsByBudget simply validates input then delegates to
        // PetData.findPetByBudget (see PetController.findPetsByBudget). The success path
        // therefore reduces to the data-layer behaviour, exercised here. The controller
        // validation paths are covered by controllerRejectsMissingBudget /
        // controllerRejectsNegativeBudget above.
        final PetData data = new PetData();
        final List<Pet> affordable = data.findPetByBudget(30.00);

        assertFalse(affordable.isEmpty());
        for (final Pet pet : affordable) {
            assertNotNull(pet.getAdoptionFee());
            assertTrue(pet.getAdoptionFee() <= 30.00);
        }
        // Cheapest affordable pet under $30 is Rabbit 1 (15.00)
        assertEquals("Rabbit 1", affordable.get(0).getName());
    }
}
