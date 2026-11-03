package fr.emaya.model;

import fr.emaya.exception.RegleMetierException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Client {

    private final int id;
    private final String nom;
    private final String telephone;
    private final List<Vehicule> vehicules = new ArrayList<>();

    public Client(int id, String nom, String telephone) {
        if (nom == null || nom.isBlank()) {
            throw new RegleMetierException("Le nom du client est obligatoire");
        }
        this.id = id;
        this.nom = nom.trim();
        this.telephone = telephone == null ? "" : telephone.trim();
    }

    public void ajouterVehicule(Vehicule vehicule) {
        if (vehicules.contains(vehicule)) {
            throw new RegleMetierException("Ce véhicule appartient déjà au client : " + vehicule.getImmatriculation());
        }
        vehicules.add(vehicule);
    }

    public boolean possede(Vehicule vehicule) {
        return vehicules.contains(vehicule);
    }

    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getTelephone() {
        return telephone;
    }

    /** Encapsulation : on renvoie une vue en lecture seule, la liste ne se modifie que via ajouterVehicule. */
    public List<Vehicule> getVehicules() {
        return Collections.unmodifiableList(vehicules);
    }

    @Override
    public String toString() {
        return String.format("#%d %s (%s) - %d véhicule(s)", id, nom, telephone.isEmpty() ? "pas de téléphone" : telephone, vehicules.size());
    }
}
