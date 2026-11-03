package fr.emaya.model;

/**
 * Catalogue des prestations de l'atelier, avec leur temps forfaitaire (en heures)
 * pour une voiture standard.
 */
public enum TypeIntervention {

    VIDANGE("Vidange", 0.5),
    PNEUS("Changement de pneus", 1.0),
    FREINS("Plaquettes et disques", 1.5),
    DISTRIBUTION("Courroie de distribution", 4.0),
    DIAGNOSTIC("Diagnostic électronique", 0.5);

    private final String libelle;
    private final double heuresForfait;

    TypeIntervention(String libelle, double heuresForfait) {
        this.libelle = libelle;
        this.heuresForfait = heuresForfait;
    }

    public String getLibelle() {
        return libelle;
    }

    public double getHeuresForfait() {
        return heuresForfait;
    }
}
