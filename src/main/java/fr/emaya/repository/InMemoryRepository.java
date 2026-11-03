package fr.emaya.repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Stockage en mémoire, réutilisable pour n'importe quelle entité grâce aux génériques.
 * La fonction extracteurId indique comment lire l'identifiant d'une entité (ex : Client::getId).
 */
public class InMemoryRepository<T, ID> implements Repository<T, ID> {

    private final Map<ID, T> stockage = new LinkedHashMap<>();
    private final Function<T, ID> extracteurId;

    public InMemoryRepository(Function<T, ID> extracteurId) {
        this.extracteurId = extracteurId;
    }

    @Override
    public T save(T entite) {
        stockage.put(extracteurId.apply(entite), entite);
        return entite;
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(stockage.get(id));
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(stockage.values());
    }

    @Override
    public boolean deleteById(ID id) {
        return stockage.remove(id) != null;
    }
}
