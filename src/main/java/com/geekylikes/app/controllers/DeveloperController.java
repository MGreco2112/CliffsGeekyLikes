package com.geekylikes.app.controllers;

import com.geekylikes.app.models.auth.User;
import com.geekylikes.app.models.avatar.Avatar;
import com.geekylikes.app.models.developer.Developer;
import com.geekylikes.app.models.geekout.Geekout;
import com.geekylikes.app.models.language.Language;
import com.geekylikes.app.models.relationship.ERelationship;
import com.geekylikes.app.models.relationship.Relationship;
import com.geekylikes.app.payloads.response.FriendDeveloper;
import com.geekylikes.app.payloads.response.MessageResponse;
import com.geekylikes.app.payloads.response.PublicDeveloper;
import com.geekylikes.app.repositories.*;
import com.geekylikes.app.security.services.UserDetailsImpl;
import com.geekylikes.app.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@CrossOrigin
@RestController
@RequestMapping("/api/developers")
public class DeveloperController {
    @Autowired
    private DeveloperRepository repository;

    @Autowired
    private AvatarRepository avatarRepository;

    @Autowired
    private GeekoutRepository geekoutRepository;

    @Autowired
    private RelationshipRepository relationshipRepository;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private UserService userService;

    @GetMapping
    public @ResponseBody List<Developer> getDevelopers() {
        return repository.findAll();
    }

    @GetMapping("/lang/{langId}")
    public List<Developer> getDevsByLanguage(@PathVariable Long langId) {
        return repository.findAllByLanguages_id(langId);
    }

    @GetMapping("/cohort/{cohort}")
    public ResponseEntity<List<Developer>> getDevelopersByCohort(@PathVariable Integer cohort) {
        return new ResponseEntity<>(repository.findAllByCohort(cohort, Sort.by("name")), HttpStatus.OK);
    }

    @GetMapping("/likes/{devId}")
    public List<Geekout> getApprovedGeekouts(@PathVariable Long devId) {
        return geekoutRepository.findAllByApprovals_developer_id(devId);
    }

//    @GetMapping("/{id}")
//    public @ResponseBody Developer getOneDeveloper(@PathVariable Long id) {
//        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
//    }

    @GetMapping("/self")
    public  Developer getCurrentUser() {
        User currentUser = userService.getCurrentUser();

        if (currentUser == null) {
            return null;
        }


        return repository.findByUser_id(currentUser.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDeveloperById(@PathVariable Long id) {

        User currentUser = userService.getCurrentUser();

        if (currentUser == null) {
            return new ResponseEntity<>(new MessageResponse("Invalid user"), HttpStatus.BAD_REQUEST);
        }

        Developer currentDeveloper = repository.findByUser_id(currentUser.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Developer developer = repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        //TODO write logic to determine what to send back for Friends, nonFriends, Blocked

        if (relationshipRepository.existsByOriginator_idAndRecipient_idAndType(currentDeveloper.getId(), developer.getId(), ERelationship.ACCEPTED) ||
                relationshipRepository.existsByOriginator_idAndRecipient_idAndType(developer.getId(), currentDeveloper.getId(), ERelationship.ACCEPTED)) {

            Set<Relationship> developerFriends = relationshipRepository.findAllByOriginator_idAndType(developer.getId(), ERelationship.ACCEPTED);
            developerFriends.addAll(relationshipRepository.findAllByRecipient_idAndType(developer.getId(), ERelationship.ACCEPTED));

            return new ResponseEntity<>(FriendDeveloper.build(developer), HttpStatus.OK);

        }

        return new ResponseEntity<>(PublicDeveloper.build(developer), HttpStatus.OK);
    }


    @PostMapping
    public ResponseEntity<Developer> createDeveloper(@RequestBody Developer newDeveloper) {

//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
//        Above lines outmoded by userService.getCurrentUser();

        User currentUser = userService.getCurrentUser();

        if (currentUser == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }


        for (Developer developer : repository.findAll()) {
            if (developer.getUser() == currentUser) {
                return new ResponseEntity<>(null, HttpStatus.CONFLICT);
            }
        }


        newDeveloper.setUser(currentUser);

        return new ResponseEntity<>(repository.save(newDeveloper), HttpStatus.CREATED);
    }

    @PostMapping("/photo")
    public Developer addPhoto(@RequestBody Developer dev) { //TODO refactor dev to updates

        User currentUser = userService.getCurrentUser();

        if (currentUser == null) {
            return null;
        }

        Developer developer = repository.findByUser_id(currentUser.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        // check if developer has an avatar and if so, delete or modify existing avatar before creating new.
        if (developer.getAvatar() != null) {
            Avatar avatar = developer.getAvatar();
            avatar.setUrl(dev.getAvatar().getUrl());
            avatarRepository.save(avatar);
            return developer;
        }
        Avatar avatar = avatarRepository.save(dev.getAvatar());
        developer.setAvatar(avatar);
        return repository.save(developer);

    }

    @PutMapping("/language")
    public Developer addLanguage(@RequestBody List<Language> updates) {
        User currentUser = userService.getCurrentUser();

        if (currentUser == null) {
            return null;
        }

        Developer developer = repository.findByUser_id(currentUser.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        for (Language lang : updates) {
            Language addedLanguage = languageRepository.findById(lang.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            developer.languages.add(addedLanguage);
        }

        return repository.save(developer);
    }

    @PutMapping
    public @ResponseBody Developer updateDeveloper(@RequestBody Developer updates) {
        User currentUser = userService.getCurrentUser();

        if (currentUser == null) {
            return null;
        }

        Developer developer = repository.findByUser_id(currentUser.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

//        updates.setId(developer.getId());
//        return repository.save(updates);
        if (updates.getName() != null) developer.setName(updates.getName());
        if (updates.getEmail() != null) developer.setEmail(updates.getEmail());
        if (updates.getCohort() != null) developer.setCohort(updates.getCohort());
//        if (updates.languages != null) developer.languages = updates.languages;

        return repository.save(developer);
    }

    @DeleteMapping
    public ResponseEntity<String> destroyDeveloper(@PathVariable Long id) {
        User currentUser = userService.getCurrentUser();

        if (currentUser == null) {
            return null;
        }

        repository.deleteByUser_id(currentUser.getId());
        return new ResponseEntity<>("Deleted", HttpStatus.OK);
    }


}
