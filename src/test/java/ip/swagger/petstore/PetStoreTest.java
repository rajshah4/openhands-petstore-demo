package ip.swagger.petstore;

import io.swagger.petstore.data.PetData;
import io.swagger.petstore.model.Pet;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for the KAN-13 budget filter feature.
 *
 * Business requirement: families need to search pets by maximum adoption fee so
 * they only see pets they can afford before visiting the shelter.
 *
 * Log evidence that triggered this fix:
 *   customer intake: budget_limit_dollars=75  (7500 cents)
 *   search returned: adoption_fee_cents=12500 ($125) — exceeds budget
 */
public class PetStoreTest {

    private PetData petData;

    @Before
    public void setUp() {
        petData = new PetData();
    }

    // --- adoptionFeeCents on Pet model ---

    @Test
    public void petModelStoresAdoptionFee() {
        final Pet pet = new Pet();
        pet.setAdoptionFeeCents(7500L);
        assertEquals(Long.valueOf(7500L), pet.getAdoptionFeeCents());
    }

    @Test
    public void petModelDefaultFeeIsNull() {
        final Pet pet = new Pet();
        assertNull(pet.getAdoptionFeeCents());
    }

    // --- Sample data has fees assigned ---

    @Test
    public void samplePetsHaveAdoptionFees() {
        // Every seeded pet should carry a non-null fee
        final List<Pet> pets = petData.findPetByStatus("available,pending,sold");
        for (final Pet pet : pets) {
            assertNotNull("Pet " + pet.getName() + " is missing adoptionFeeCents",
                    pet.getAdoptionFeeCents());
        }
    }

    // --- Budget filter: core correctness ---

    /**
     * KAN-13 regression: budget $75 (7500 cents) must NOT return pets that cost more.
     * The log showed adoption_fee_cents=12500 ($125) being returned for a $75 budget.
     */
    @Test
    public void budgetFilter_excludesPetsAboveBudget() {
        // budget_limit_dollars=75 → maxFee=7500 cents
        final long maxFee = 7500L;
        final List<Pet> result = petData.findPetsByMaxFee(maxFee);
        for (final Pet pet : result) {
            assertTrue(
                "Pet " + pet.getName() + " has fee " + pet.getAdoptionFeeCents()
                    + " which exceeds maxFee " + maxFee,
                pet.getAdoptionFeeCents() <= maxFee
            );
        }
    }

    @Test
    public void budgetFilter_includesPetExactlyAtBudget() {
        // Cat 2 costs exactly 7500 cents — must be included at maxFee=7500
        final List<Pet> result = petData.findPetsByMaxFee(7500L);
        final boolean found = result.stream()
                .anyMatch(p -> "Cat 2".equals(p.getName()));
        assertTrue("Cat 2 (fee=7500) should be returned for maxFee=7500", found);
    }

    @Test
    public void budgetFilter_includesPetsBelowBudget() {
        // Rabbit 1 (3500 cents) and Cat 1 (5000 cents) should appear for a $75 budget
        final List<Pet> result = petData.findPetsByMaxFee(7500L);
        assertTrue("Rabbit 1 should be within a $75 budget",
                result.stream().anyMatch(p -> "Rabbit 1".equals(p.getName())));
        assertTrue("Cat 1 should be within a $75 budget",
                result.stream().anyMatch(p -> "Cat 1".equals(p.getName())));
    }

    @Test
    public void budgetFilter_excludesExpensivePets() {
        // Dog 1 (15000 cents), Lion 1 (50000 cents) should not appear for a $75 budget
        final List<Pet> result = petData.findPetsByMaxFee(7500L);
        assertFalse("Dog 1 ($150) should not appear in a $75 budget search",
                result.stream().anyMatch(p -> "Dog 1".equals(p.getName())));
        assertFalse("Lion 1 ($500) should not appear in a $75 budget search",
                result.stream().anyMatch(p -> "Lion 1".equals(p.getName())));
    }

    @Test
    public void budgetFilter_zeroBudgetReturnsNoPets() {
        final List<Pet> result = petData.findPetsByMaxFee(0L);
        assertTrue("No pet should have a $0 adoption fee", result.isEmpty());
    }

    @Test
    public void budgetFilter_veryHighBudgetReturnsAllPets() {
        // 1,000,000 cents ($10,000) — all seeded pets should qualify
        final List<Pet> result = petData.findPetsByMaxFee(1_000_000L);
        final List<Pet> all = petData.findPetByStatus("available,pending,sold");
        assertEquals("All seeded pets should be within a $10,000 budget",
                all.size(), result.size());
    }
}
