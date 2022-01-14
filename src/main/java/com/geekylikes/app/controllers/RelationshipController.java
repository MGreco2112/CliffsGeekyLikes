package com.geekylikes.app.controllers;

import com.geekylikes.app.models.auth.User;
import com.geekylikes.app.models.developer.Developer;
import com.geekylikes.app.models.relationship.ERelationship;
import com.geekylikes.app.models.relationship.Relationship;
import com.geekylikes.app.payloads.response.MessageResponse;
import com.geekylikes.app.repositories.DeveloperRepository;
import com.geekylikes.app.repositories.RelationshipRepository;
import com.geekylikes.app.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.Set;

@RestController
@CrossOrigin
@RequestMapping("/api/developers/relationships")
public class RelationshipController {
    @Autowired
    private RelationshipRepository repository;
    @Autowired
    private UserService userService;
    @Autowired
    private DeveloperRepository developerRepository;

    @GetMapping("/friends")
    public ResponseEntity<?> getAllFriends() {
        User currentUser = userService.getCurrentUser();

        if (currentUser == null) {
            return new ResponseEntity<>(new MessageResponse("Invalid user"), HttpStatus.BAD_REQUEST);
        }

        Developer developer = developerRepository.findByUser_id(currentUser.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Set<Relationship> rels = repository.findAllByOriginator_IdAndType(developer.getId(), ERelationship.ACCEPTED);

        Set<Relationship> invRels = repository.findAllByRecipient_IdAndType(developer.getId(), ERelationship.ACCEPTED);

        rels.addAll(invRels);

        return new ResponseEntity<>(rels, HttpStatus.OK);
    }

    @PostMapping("/add/{rId}")
    public ResponseEntity<MessageResponse> addRelationship(@PathVariable Long rId) {
        User currentUser = userService.getCurrentUser();

        if (currentUser == null) {
            return new ResponseEntity<>(new MessageResponse("Invalid user"), HttpStatus.BAD_REQUEST);
        }

        Developer originator = developerRepository.findByUser_id(currentUser.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Developer recipient = developerRepository.findById(rId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Optional<Relationship> rel = repository.findAllByOriginator_IdAndRecipient_Id(originator.getId(), recipient.getId());

        if (rel.isPresent()) {
            return new ResponseEntity<>(new MessageResponse("Nice try, be patient"), HttpStatus.OK);
        }

        Optional<Relationship> inverseRel = repository.findAllByOriginator_IdAndRecipient_Id(recipient.getId(), originator.getId());

        if (inverseRel.isPresent()) {
            switch (inverseRel.get().getType()) {
                case PENDING:
                    inverseRel.get().setType(ERelationship.ACCEPTED);
                    repository.save(inverseRel.get());
                    return new ResponseEntity<>(new MessageResponse("Request Accepted"), HttpStatus.CREATED);
                case ACCEPTED:
                    return new ResponseEntity<>(new MessageResponse("Already Accepted"), HttpStatus.OK);
                case BLOCKED:
                    return new ResponseEntity<>(new MessageResponse("Success"), HttpStatus.CREATED);
                default:
                    return new ResponseEntity<>(new MessageResponse("SERVER_ERROR: DEFAULT RELATIONSHIP"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        repository.save(new Relationship(originator, recipient, ERelationship.PENDING));

        return new ResponseEntity<>(new MessageResponse("Success"), HttpStatus.CREATED);
    }

    @PostMapping("/block/{rId}")
    public ResponseEntity<MessageResponse> blockRecipient(@PathVariable Long rId) {
        User currentUser = userService.getCurrentUser();

        if (currentUser == null) {
            return new ResponseEntity<>(new MessageResponse("Invalid user"), HttpStatus.BAD_REQUEST);
        }

        Developer originator = developerRepository.findByUser_id(currentUser.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Developer recipient = developerRepository.findById(rId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));


        Optional<Relationship> rel = repository.findAllByOriginator_IdAndRecipient_Id(originator.getId(), recipient.getId());

        if (rel.isPresent()) {
            switch (rel.get().getType()) {
                case PENDING:
                case ACCEPTED:
                    rel.get().setType(ERelationship.BLOCKED);
                    repository.save(rel.get());
                    return new ResponseEntity<>(new MessageResponse("Blocked"), HttpStatus.OK);
                case BLOCKED:
                    return new ResponseEntity<>(new MessageResponse("Blocked"), HttpStatus.OK);
                default:
                    return new ResponseEntity<>(new MessageResponse("SERVER_ERROR: INVALID RELATIONSHIP STATUS"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        Optional<Relationship> inverseRel = repository.findAllByOriginator_IdAndRecipient_Id(recipient.getId(), originator.getId());

        if (inverseRel.isPresent()) {
            switch (inverseRel.get().getType()) {
                case PENDING:
                case ACCEPTED:
                    inverseRel.get().setType(ERelationship.BLOCKED);
                    repository.save(inverseRel.get());
                    return new ResponseEntity<>(new MessageResponse("Blocked"), HttpStatus.OK);
                case BLOCKED:
                    return new ResponseEntity<>(new MessageResponse("Blocked"), HttpStatus.OK);
                default:
                    return new ResponseEntity<>(new MessageResponse("SERVER_ERROR: INVALID RELATIONSHIP STATUS"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }


        try {
            repository.save(new Relationship(originator, recipient, ERelationship.BLOCKED));
        } catch (Exception e) {
            System.out.println("error " + e.getLocalizedMessage());
            return new ResponseEntity<>(new MessageResponse("SERVER_ERROR"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(new MessageResponse("User Blocked"), HttpStatus.CREATED);
    }

    @PutMapping("/approve/{id}")
    public ResponseEntity<MessageResponse> approveRelationship(@PathVariable Long id) {
        User currentUser = userService.getCurrentUser();

        if (currentUser == null) {
            return new ResponseEntity<>(new MessageResponse("Invalid user"), HttpStatus.BAD_REQUEST);
        }

        Developer recipient = developerRepository.findByUser_id(currentUser.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));


        //My Way
//        Developer originator = developerRepository.findByUser_id(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

//        Optional<Relationship> rel = repository.findAllByOriginator_IdAndRecipient_Id(originator.getId(), recipient.getId());
//
//        if (rel.isEmpty()) {
//            return new ResponseEntity<>(new MessageResponse("SERVER_ERROR: INVALID RELATIONSHIP"), HttpStatus.BAD_REQUEST);
//        }
//
//        switch (rel.get().getType()) {
//            case PENDING:
//                rel.get().setType(ERelationship.ACCEPTED);
//                repository.save(rel.get());
//                return new ResponseEntity<>(new MessageResponse("Accepted"), HttpStatus.OK);
//            case BLOCKED:
//                return new ResponseEntity<>(new MessageResponse("ERROR"), HttpStatus.BAD_REQUEST);
//            case ACCEPTED:
//                return new ResponseEntity<>(new MessageResponse("Already Accepted"), HttpStatus.OK);
//            default:
//                return new ResponseEntity<>(new MessageResponse("SERVER_ERROR: INVALID RELATIONSHIP STATUS"), HttpStatus.INTERNAL_SERVER_ERROR);
//        }

        //Cliff's Way
        Relationship rel = repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (rel.getRecipient().getId() != recipient.getId()) {
            return new ResponseEntity<>(new MessageResponse("UNAUTHORIZED"), HttpStatus.UNAUTHORIZED);
        }

        if (rel.getType() == ERelationship.PENDING) {
            rel.setType(ERelationship.ACCEPTED);
            repository.save(rel);
        }

        return new ResponseEntity<>(new MessageResponse("Approved"), HttpStatus.OK);

    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<MessageResponse> removeRelationship(@PathVariable Long id) {
        User currentUser = userService.getCurrentUser();

        if (currentUser == null) {
            return new ResponseEntity<>(new MessageResponse("Invalid user"), HttpStatus.BAD_REQUEST);
        }

        Developer developer = developerRepository.findByUser_id(currentUser.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Relationship rel = repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (rel.getOriginator().getId() != developer.getId() && rel.getRecipient().getId() != developer.getId()) {
            return new ResponseEntity<>(new MessageResponse("UNAUTHORIZED"), HttpStatus.UNAUTHORIZED);
        }

        if (rel.getType() != ERelationship.BLOCKED) {
            repository.delete(rel);
        }

        return new ResponseEntity<>(new MessageResponse("Success"), HttpStatus.OK);
    }

}
