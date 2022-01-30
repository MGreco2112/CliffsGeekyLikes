package com.geekylikes.app.payloads.response;

import com.geekylikes.app.models.avatar.Avatar;
import com.geekylikes.app.models.developer.Developer;
import com.geekylikes.app.models.language.Language;

import java.util.Set;

public class SelfDeveloper {
    private Long id;
    private String name;
    private String email;
    private Integer cohort;
    private Set<Developer> friends;
    private Set<Developer> pendingFriendship; //sent
    private Set<Developer> incomingFriendship; //waiting for approve
    private Avatar avatar;
    private Set<Language> languages;

    public SelfDeveloper(Long id, String name, String email, Integer cohort, Set<Developer> friends, Set<Developer> pendingFriendship, Set<Developer> incomingFriendship, Avatar avatar, Set<Language> languages) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.cohort = cohort;
        this.friends = friends;
        this.pendingFriendship = pendingFriendship;
        this.incomingFriendship = incomingFriendship;
        this.avatar = avatar;
        this.languages = languages;
    }


    //TODO finish
//    public static SelfDeveloper build(Developer developer) {
//
//        return new SelfDeveloper(developer.getId(),
//                developer.getName(),
//                developer.getEmail(),
//                developer.getCohort(),
//                );
//
//        this.friends = friends;
//        this.pendingFriendship = pendingFriendship;
//        this.incomingFriendship = incomingFriendship;
//        this.avatar = developer.getAvatar();
//        this.languages = developer.getLanguages();
//    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getCohort() {
        return cohort;
    }

    public void setCohort(Integer cohort) {
        this.cohort = cohort;
    }

    public Set<Developer> getFriends() {
        return friends;
    }

    public void setFriends(Set<Developer> friends) {
        this.friends = friends;
    }

    public Set<Developer> getPendingFriendship() {
        return pendingFriendship;
    }

    public void setPendingFriendship(Set<Developer> pendingFriendship) {
        this.pendingFriendship = pendingFriendship;
    }

    public Set<Developer> getIncomingFriendship() {
        return incomingFriendship;
    }

    public void setIncomingFriendship(Set<Developer> incomingFriendship) {
        this.incomingFriendship = incomingFriendship;
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }

    public Set<Language> getLanguages() {
        return languages;
    }

    public void setLanguages(Set<Language> languages) {
        this.languages = languages;
    }
}
