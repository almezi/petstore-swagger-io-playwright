package com.almezi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Pet {
    private Long id;
    private Category category;
    private String name;
    private List<String> photoUrls;
    private List<Tag> tags;
    private String status;

    public Pet() {
    }

    public Pet(Long id, String name, String status) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.photoUrls = new ArrayList<>();
        this.tags = new ArrayList<>();
    }

    public static Pet createValidPet(Long id, String name, String status) {
        Pet pet = new Pet(id, name, status);
        pet.setPhotoUrls(List.of("https://example.com/photo.jpg"));
        return pet;
    }

    public static Pet createWithoutName(Long id, String status) {
        Pet pet = new Pet();
        pet.setId(id);
        pet.setStatus(status);
        pet.setPhotoUrls(List.of("https://example.com/photo.jpg"));
        return pet;
    }

    public static Pet createWithoutPhotoUrls(Long id, String name, String status) {
        Pet pet = new Pet();
        pet.setId(id);
        pet.setName(name);
        pet.setStatus(status);
        pet.setPhotoUrls(null);
        return pet;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getPhotoUrls() {
        return photoUrls;
    }

    public void setPhotoUrls(List<String> photoUrls) {
        this.photoUrls = photoUrls;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
