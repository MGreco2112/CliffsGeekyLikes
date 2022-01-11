package com.geekylikes.app.repositories;

import com.geekylikes.app.models.relationship.ERelationship;
import com.geekylikes.app.models.relationship.Relationship;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RelationshipRepository extends JpaRepository<Relationship, Long> {
    List<Relationship> findAllByOriginator_Id(Long id);
    List<Relationship> findAllByRecipient_IdAndType(Long id, ERelationship type);
//    List<Relationship> findAllByOriginator_IdOrRecipient_Id(Long id, Long id);
    Optional<Relationship> findAllByOriginator_IdAndRecipient_Id(long oId, Long rId);
}
