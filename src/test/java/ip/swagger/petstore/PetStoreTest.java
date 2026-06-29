package ip.swagger.petstore;

import io.swagger.petstore.data.PetData;
import io.swagger.petstore.model.Pet;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class PetStoreTest {

    private final PetData petData = new PetData();

    @Test
    public void seedPetsCarryAnAdoptionFee() {
        final Pet cat1 = petData.getPetById(1);
        assertNotNull("seed Cat 1 should exist", cat1);
        assertEquals("Cat 1 should have a seeded adoption fee", Integer.valueOf(50), cat1.getAdoptionFee());

        final Pet lion1 = petData.getPetById(7);
        assertNotNull(lion1);
        assertEquals("Lion 1 should have an expensive fee", Integer.valueOf(5000), lion1.getAdoptionFee());
    }

    @Test
    public void findPetsByAdoptionBudgetReturnsOnlyAffordablePets() {
        final List<Pet> affordable = petData.findPetsByAdoptionBudget(70);

        // Within a 70 budget only Rabbit 1 (25), Cat 1 (50), Cat 2 (60) and Cat 3 (70) qualify.
        assertEquals("four pets should be within a budget of 70", 4, affordable.size());
        for (final Pet pet : affordable) {
            assertNotNull(pet.getAdoptionFee());
            assertTrue("returned pet must be within budget",
                    pet.getAdoptionFee() <= 70);
        }
    }

    @Test
    public void findPetsByAdoptionBudgetBoundaryIsInclusive() {
        // Cat 3 costs exactly 70, so a budget of 70 must include it.
        final List<Pet> atBoundary = petData.findPetsByAdoptionBudget(70);
        boolean includesCat3 = false;
        for (final Pet pet : atBoundary) {
            if (pet.getId() == 3) {
                includesCat3 = true;
                break;
            }
        }
        assertTrue("budget filter must be inclusive of the exact fee", includesCat3);
    }

    @Test
    public void findPetsByAdoptionBudgetOfZeroReturnsNoPets() {
        // No seed pet is free, so a zero budget matches nothing.
        final List<Pet> none = petData.findPetsByAdoptionBudget(0);
        assertTrue("no pets should be affordable on a zero budget", none.isEmpty());
    }

    @Test
    public void findPetsByAdoptionBudgetHighBudgetReturnsAllSeedPets() {
        final List<Pet> all = petData.findPetsByAdoptionBudget(Integer.MAX_VALUE);
        // Every seeded pet has a fee, so all 10 must be affordable on an unlimited budget.
        assertEquals("an unlimited budget should return every seeded pet", 10, all.size());
    }

    @Test
    public void petsWithoutAnAdoptionFeeAreExcluded() {
        // A freshly created pet via the no-fee factory has no adoption fee set,
        // so it must never be returned by the budget filter.
        final Pet pet = PetData.createPet(999L, null, "Feeless", null, null, "available");
        assertNull("createPet without a fee leaves adoptionFee unset", pet.getAdoptionFee());

        petData.addPet(pet);
        try {
            final List<Pet> withinBudget = petData.findPetsByAdoptionBudget(Integer.MAX_VALUE);
            boolean included = false;
            for (final Pet result : withinBudget) {
                if (result.getId() == 999L) {
                    included = true;
                    break;
                }
            }
            assertTrue("pets without an adoption fee must be excluded from budget results",
                    !included);
        } finally {
            // The pet list is process-wide static, so clean up to keep other tests isolated.
            petData.deletePetById(999L);
        }
    }
}
