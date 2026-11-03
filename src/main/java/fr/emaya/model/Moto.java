package fr.emaya.model;

import fr.emaya.exception.RegleMetierException;

public class Moto extends Vehicule {

    private final int cylindree;

    public Moto(String immatriculation, String marque, String modele, int annee, int kilometrage, int cylindree) {
        super(immatriculation, marque, modele, annee, kilometrage);
        if (cylindree <= 0) {
            throw new RegleMetierException("La cylindrée doit être positive");
        }
        this.cylindree = cylindree;
    }

    @Override
    public String getType() {
        return "Moto";
    }

    /** Une moto demande moins de temps d'atelier qu'une voiture. */
    @Override
    public double getCoefficientMainOeuvre() {
        return 0.8;
    }

    public int getCylindree() {
        return cylindree;
    }
}
