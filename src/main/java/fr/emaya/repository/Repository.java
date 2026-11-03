package fr.emaya.repository;

import java.util.List;
import java.util.Optional;

/**
 * Contrat générique d'accès aux données.
 * Le service Atelier ne dépend que de cette interface : on peut remplacer
 * le stockage en mémoire par une implémentation JDBC sans toucher au métier.
 *
 * @param <T>  type de l'entité
 * @param <ID> type de son identifiant
 */
public interface Repository<T, ID> {

    T save(T entite);

    Optional<T> findById(ID id);

    List<T> findAll();

    boolean deleteById(ID id);
}
