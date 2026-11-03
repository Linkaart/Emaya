package fr.emaya.model;

import fr.emaya.exception.RegleMetierException;

public class Voiture extends Vehicule {

    private final int nombrePortes;

    public Voiture(String immatriculation, String marque, String modele, int annee, int kilometrage, int nombrePortes) {
        super(immatriculation, marque, modele, annee, kilometrage);
        if (nombrePortes < 2 || nombrePortes > 5) {
            throw new RegleMetierException("Une voiture a entre 2 et 5 portes");
        }
        this.nombrePortes = nombrePortes;
    }

    @Override
    public String getType() {
        return "Voiture";
    }

    @Override
    public double getCoefficientMainOeuvre() {
        return 1.0;
    }

    public int getNombrePortes() {
        return nombrePortes;
    }
}
