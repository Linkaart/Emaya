package fr.emaya.model;

import fr.emaya.exception.RegleMetierException;

import java.time.Year;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Classe abstraite : on ne crée jamais un "Vehicule" tout court,
 * seulement une Voiture, une Moto ou un Utilitaire.
 * Chaque sous-classe définit son propre coefficient de main-d'œuvre (polymorphisme).
 */
public abstract class Vehicule {

    /** Format des plaques françaises (SIV) : AB-123-CD. */
    private static final Pattern FORMAT_IMMATRICULATION = Pattern.compile("^[A-Z]{2}-\\d{3}-[A-Z]{2}$");

    private final String immatriculation;
    private final String marque;
    private final String modele;
    private final int annee;
    private int kilometrage;

    protected Vehicule(String immatriculation, String marque, String modele, int annee, int kilometrage) {
        String immat = validerImmatriculation(immatriculation);
        if (annee < 1900 || annee > Year.now().getValue() + 1) {
            throw new RegleMetierException("Année de mise en circulation invalide : " + annee);
        }
        if (kilometrage < 0) {
            throw new RegleMetierException("Le kilométrage ne peut pas être négatif");
        }
        this.immatriculation = immat;
        this.marque = exigerTexte(marque, "La marque");
        this.modele = exigerTexte(modele, "Le modèle");
        this.annee = annee;
        this.kilometrage = kilometrage;
    }

    /** Libellé du type de véhicule, affiché dans la console. */
    public abstract String getType();

    /** Multiplicateur appliqué au temps de main-d'œuvre selon le type de véhicule. */
    public abstract double getCoefficientMainOeuvre();

    /** Un compteur ne recule jamais : on refuse un kilométrage inférieur à l'actuel. */
    public void mettreAJourKilometrage(int nouveauKilometrage) {
        if (nouveauKilometrage < kilometrage) {
            throw new RegleMetierException("Le kilométrage ne peut pas diminuer (" + kilometrage + " km actuellement)");
        }
        this.kilometrage = nouveauKilometrage;
    }

    public static String normaliserImmatriculation(String immatriculation) {
        return immatriculation == null ? "" : immatriculation.trim().toUpperCase();
    }

    /** Renvoie la plaque normalisée, ou lève une exception si elle ne respecte pas le format SIV. */
    public static String validerImmatriculation(String immatriculation) {
        String immat = normaliserImmatriculation(immatriculation);
        if (!FORMAT_IMMATRICULATION.matcher(immat).matches()) {
            throw new RegleMetierException("Immatriculation invalide (format attendu : AB-123-CD) : " + immatriculation);
        }
        return immat;
    }

    protected static String exigerTexte(String valeur, String nomChamp) {
        if (valeur == null || valeur.isBlank()) {
            throw new RegleMetierException(nomChamp + " est obligatoire");
        }
        return valeur.trim();
    }

    public String getImmatriculation() {
        return immatriculation;
    }

    public String getMarque() {
        return marque;
    }

    public String getModele() {
        return modele;
    }

    public int getAnnee() {
        return annee;
    }

    public int getKilometrage() {
        return kilometrage;
    }

    /** Deux véhicules sont identiques s'ils ont la même plaque. */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vehicule)) return false;
        return immatriculation.equals(((Vehicule) o).immatriculation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(immatriculation);
    }

    @Override
    public String toString() {
        return String.format("%s %s %s [%s] - %d - %d km", getType(), marque, modele, immatriculation, annee, kilometrage);
    }
}
