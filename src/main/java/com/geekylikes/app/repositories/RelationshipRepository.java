package com.geekylikes.app.repositories;

import com.geekylikes.app.models.relationship.ERelationship;
import com.geekylikes.app.models.relationship.Relationship;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RelationshipRepository extends JpaRepository<Relationship, Long> {
    List<Relationship> findAllByOriginator_Id(Long id);
    Set<Relationship> findAllByRecipient_idAndType(Long id, ERelationship type);
    Set<Relationship> findAllByOriginator_idAndType(Long id, ERelationship type);
//    List<Relationship> findAllByOriginator_idOrRecipient_Id(Long id, Long id);
    Optional<Relationship> findAllByOriginator_idAndRecipient_id(Long oId, Long rId);
    Boolean existsByOriginator_idAndRecipient_idAndType(Long oId, Long rId, ERelationship type);
}
