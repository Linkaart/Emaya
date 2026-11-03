package fr.emaya.model;

import fr.emaya.exception.RegleMetierException;

public class Utilitaire extends Vehicule {

    private static final int SEUIL_GROS_PORTEUR_KG = 1000;

    private final int chargeUtileKg;

    public Utilitaire(String immatriculation, String marque, String modele, int annee, int kilometrage, int chargeUtileKg) {
        super(immatriculation, marque, modele, annee, kilometrage);
        if (chargeUtileKg <= 0) {
            throw new RegleMetierException("La charge utile doit être positive");
        }
        this.chargeUtileKg = chargeUtileKg;
    }

    @Override
    public String getType() {
        return "Utilitaire";
    }

    /** Plus l'utilitaire est lourd, plus l'intervention prend du temps. */
    @Override
    public double getCoefficientMainOeuvre() {
        return chargeUtileKg > SEUIL_GROS_PORTEUR_KG ? 1.5 : 1.3;
    }

    public int getChargeUtileKg() {
        return chargeUtileKg;
    }
}
