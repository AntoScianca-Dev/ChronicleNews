package it.aulab.progetto_finale_sciancalepore.repositories;

import org.springframework.data.repository.ListCrudRepository;

import it.aulab.progetto_finale_sciancalepore.models.Category;

public interface CategoryRepository extends ListCrudRepository<Category, Long>{
    
}
