package rs.ac.uns.ftn.graduation.repository;

import rs.ac.uns.ftn.graduation.model.Role;
import rs.ac.uns.ftn.graduation.model.RoleName;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleName);
}
