package it.aulab.progetto_finale_sciancalepore.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import it.aulab.progetto_finale_sciancalepore.models.Role;

public interface RoleRepository extends JpaRepository<Role, Long>{
    Role findByName(String name);
}
