package fr.emaya.service;

import fr.emaya.exception.ElementIntrouvableException;
import fr.emaya.exception.RegleMetierException;
import fr.emaya.model.Client;
import fr.emaya.model.Facturable;
import fr.emaya.model.Intervention;
import fr.emaya.model.StatutIntervention;
import fr.emaya.model.TypeIntervention;
import fr.emaya.model.Vehicule;
import fr.emaya.repository.InMemoryRepository;
import fr.emaya.repository.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service métier : c'est la seule porte d'entrée de l'interface console.
 * Il applique les règles (client existant, plaque unique, transitions de statut)
 * et délègue le stockage aux repositories.
 */
public class Atelier {

    private final Repository<Client, Integer> clients;
    private final Repository<Vehicule, String> vehicules;
    private final Repository<Intervention, Integer> interventions;

    private int prochainIdClient = 1;
    private int prochainIdIntervention = 1;

    public Atelier() {
        this(new InMemoryRepository<>(Client::getId),
             new InMemoryRepository<>(Vehicule::getImmatriculation),
             new InMemoryRepository<>(Intervention::getId));
    }

    /** Injection des dépendances par le constructeur : pratique pour tester ou changer de stockage. */
    public Atelier(Repository<Client, Integer> clients,
                   Repository<Vehicule, String> vehicules,
                   Repository<Intervention, Integer> interventions) {
        this.clients = clients;
        this.vehicules = vehicules;
        this.interventions = interventions;
    }

    public Client ajouterClient(String nom, String telephone) {
        return clients.save(new Client(prochainIdClient++, nom, telephone));
    }

    public void ajouterVehicule(int idClient, Vehicule vehicule) {
        Client client = trouverClient(idClient);
        if (vehicules.findById(vehicule.getImmatriculation()).isPresent()) {
            throw new RegleMetierException("Un véhicule avec la plaque " + vehicule.getImmatriculation() + " existe déjà");
        }
        client.ajouterVehicule(vehicule);
        vehicules.save(vehicule);
    }

    public Intervention planifierIntervention(String immatriculation, TypeIntervention type,
                                              LocalDate date, BigDecimal coutPiecesHT) {
        Vehicule vehicule = trouverVehicule(immatriculation);
        return interventions.save(new Intervention(prochainIdIntervention++, vehicule, type, date, coutPiecesHT));
    }

    public void changerStatut(int idIntervention, StatutIntervention nouveauStatut) {
        trouverIntervention(idIntervention).changerStatut(nouveauStatut);
    }

    /** Toutes les interventions d'un véhicule, de la plus récente à la plus ancienne. */
    public List<Intervention> historique(String immatriculation) {
        Vehicule vehicule = trouverVehicule(immatriculation);
        return interventions.findAll().stream()
                .filter(i -> i.getVehicule().equals(vehicule))
                .sorted(Comparator.comparing(Intervention::getDatePrevue).reversed())
                .collect(Collectors.toList());
    }

    /** Planning d'une journée : interventions planifiées ou en cours à cette date. */
    public List<Intervention> planning(LocalDate jour) {
        return interventions.findAll().stream()
                .filter(i -> i.getDatePrevue().equals(jour))
                .filter(i -> i.getStatut() == StatutIntervention.PLANIFIEE || i.getStatut() == StatutIntervention.EN_COURS)
                .collect(Collectors.toList());
    }

    /** Seules les interventions terminées sont facturées. */
    public BigDecimal chiffreAffairesTTC() {
        return interventions.findAll().stream()
                .filter(i -> i.getStatut() == StatutIntervention.TERMINEE)
                .map(Facturable::montantTTC)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Nombre d'interventions terminées par type de prestation. */
    public Map<TypeIntervention, Long> statistiquesParType() {
        return interventions.findAll().stream()
                .filter(i -> i.getStatut() == StatutIntervention.TERMINEE)
                .collect(Collectors.groupingBy(Intervention::getType,
                        () -> new EnumMap<>(TypeIntervention.class),
                        Collectors.counting()));
    }

    public Client proprietaireDe(String immatriculation) {
        Vehicule vehicule = trouverVehicule(immatriculation);
        return clients.findAll().stream()
                .filter(c -> c.possede(vehicule))
                .findFirst()
                .orElseThrow(() -> new ElementIntrouvableException("Aucun propriétaire pour " + immatriculation));
    }

    public List<Client> getClients() {
        return clients.findAll();
    }

    public List<Intervention> getInterventions() {
        return interventions.findAll();
    }

    public Client trouverClient(int idClient) {
        return clients.findById(idClient)
                .orElseThrow(() -> new ElementIntrouvableException("Client introuvable : #" + idClient));
    }

    public Vehicule trouverVehicule(String immatriculation) {
        String immat = Vehicule.normaliserImmatriculation(immatriculation);
        return vehicules.findById(immat)
                .orElseThrow(() -> new ElementIntrouvableException("Véhicule introuvable : " + immat));
    }

    public Intervention trouverIntervention(int idIntervention) {
        return interventions.findById(idIntervention)
                .orElseThrow(() -> new ElementIntrouvableException("Intervention introuvable : #" + idIntervention));
    }
}
