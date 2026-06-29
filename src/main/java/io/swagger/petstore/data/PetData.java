/**
 * Copyright 2018 SmartBear Software
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.swagger.petstore.data;

import io.swagger.petstore.model.Category;
import io.swagger.petstore.model.Pet;
import io.swagger.petstore.model.Tag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PetData {
    private static List<Pet> pets = new ArrayList<>();
    private static List<Category> categories = new ArrayList<>();

    static {
        categories.add(createCategory(1, "Dogs"));
        categories.add(createCategory(2, "Cats"));
        categories.add(createCategory(3, "Rabbits"));
        categories.add(createCategory(4, "Lions"));

        pets.add(createPet(1, categories.get(1), "Cat 1", new String[]{
                "url1", "url2"}, new String[]{"tag1", "tag2"}, "available", 25.00));
        pets.add(createPet(2, categories.get(1), "Cat 2", new String[]{
                "url1", "url2"}, new String[]{"tag2", "tag3"}, "available", 35.50));
        pets.add(createPet(3, categories.get(1), "Cat 3", new String[]{
                "url1", "url2"}, new String[]{"tag3", "tag4"}, "pending", 120.00));

        pets.add(createPet(4, categories.get(0), "Dog 1", new String[]{
                "url1", "url2"}, new String[]{"tag1", "tag2"}, "available", 75.00));
        pets.add(createPet(5, categories.get(0), "Dog 2", new String[]{
                "url1", "url2"}, new String[]{"tag2", "tag3"}, "sold", 250.00));
        pets.add(createPet(6, categories.get(0), "Dog 3", new String[]{
                "url1", "url2"}, new String[]{"tag3", "tag4"}, "pending", 60.00));

        pets.add(createPet(7, categories.get(3), "Lion 1", new String[]{
                "url1", "url2"}, new String[]{"tag1", "tag2"}, "available", 5000.00));
        pets.add(createPet(8, categories.get(3), "Lion 2", new String[]{
                "url1", "url2"}, new String[]{"tag2", "tag3"}, "available", 7500.00));
        pets.add(createPet(9, categories.get(3), "Lion 3", new String[]{
                "url1", "url2"}, new String[]{"tag3", "tag4"}, "available", 4200.00));

        pets.add(createPet(10, categories.get(2), "Rabbit 1", new String[]{
                "url1", "url2"}, new String[]{"tag3", "tag4"}, "available", 15.00));
    }

    public Pet getPetById(final long petId) {
        for (final Pet pet : pets) {
            if (pet.getId() == petId) {
                return pet;
            }
        }
        return null;
    }

    public List<Pet> findPetByStatus(final String status) {
        final String[] statues = status.split(",");
        final List<Pet> result = new ArrayList<>();
        for (final Pet pet : pets) {
            for (final String s : statues) {
                if (s.equals(pet.getStatus())) {
                    result.add(pet);
                }
            }
        }
        return result;
    }

    public List<Pet> findPetByTags(final List<String> tags) {
        final List<Pet> result = new ArrayList<>();
        for (final Pet pet : pets) {
            if (null != pet.getTags()) {
                for (final Tag tag : pet.getTags()) {
                    for (final String tagListString : tags) {
                        if (tagListString.equals(tag.getName())) {
                            result.add(pet);
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * Returns pets whose adoption fee is within the supplied budget.
     * <p>
     * Pets with no adoption fee set are excluded. A {@code null} budget returns all
     * pets that carry an adoption fee, sorted by fee ascending so the most
     * affordable options are surfaced first.
     *
     * @param budget maximum adoption fee a family is willing to pay (inclusive)
     * @return pets with an adoption fee less than or equal to the budget
     */
    public List<Pet> findPetByBudget(final Double budget) {
        final List<Pet> result = new ArrayList<>();
        for (final Pet pet : pets) {
            final Double fee = pet.getAdoptionFee();
            if (fee == null) {
                continue;
            }
            if (budget == null || fee <= budget) {
                result.add(pet);
            }
        }
        result.sort((a, b) -> {
            final double fa = a.getAdoptionFee() == null ? Double.MAX_VALUE : a.getAdoptionFee();
            final double fb = b.getAdoptionFee() == null ? Double.MAX_VALUE : b.getAdoptionFee();
            return Double.compare(fa, fb);
        });
        return result;
    }

    public void addPet(final Pet pet) {
        if (pets.size() > 0) {
            for (int i = pets.size() - 1; i >= 0; i--) {
                if (pets.get(i).getId() == pet.getId()) {
                    pets.remove(i);
                }
            }
        }
        pets.add(pet);
    }

    public void deletePetById(final Long petId) {
        pets.removeIf(pet -> pet.getId() == petId);
    }

    public static Pet createPet(final Long id, final Category cat, final String name,
                            final List<String> urls, final List<Tag> tags, final String status) {
        return createPet(id, cat, name, urls, tags, status, null);
    }

    public static Pet createPet(final Long id, final Category cat, final String name,
                            final List<String> urls, final List<Tag> tags, final String status,
                            final Double adoptionFee) {
        final Pet pet = new Pet();
        pet.setId(id);
        pet.setCategory(cat);
        pet.setName(name);
        pet.setPhotoUrls(urls);
        pet.setTags(tags);
        pet.setStatus(status);
        pet.setAdoptionFee(adoptionFee);
        return pet;
    }

    private static Pet createPet(final long id, final Category cat, final String name, final String[] urls,
                                 final String[] tags, final String status, final double adoptionFee) {
        final Pet pet = new Pet();
        pet.setId(id);
        pet.setCategory(cat);
        pet.setName(name);
        if (null != urls) {
            final List<String> urlObjs = new ArrayList<>(Arrays.asList(urls));
            pet.setPhotoUrls(urlObjs);
        }
        final List<Tag> tagObjs = new ArrayList<>();
        int i = 0;
        if (null != tags) {
            for (final String tagString : tags) {
                i = i + 1;
                final Tag tag = new Tag();
                tag.setId(i);
                tag.setName(tagString);
                tagObjs.add(tag);
            }
        }
        pet.setTags(tagObjs);
        pet.setStatus(status);
        pet.setAdoptionFee(adoptionFee);
        return pet;
    }

    private static Category createCategory(final long id, final String name) {
        final Category category = new Category();
        category.setId(id);
        category.setName(name);
        return category;
    }
}