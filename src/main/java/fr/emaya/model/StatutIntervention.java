package fr.emaya.model;

/**
 * Cycle de vie d'une intervention :
 * PLANIFIEE -> EN_COURS -> TERMINEE, et PLANIFIEE -> ANNULEE.
 */
public enum StatutIntervention {

    PLANIFIEE,
    EN_COURS,
    TERMINEE,
    ANNULEE;

    public boolean peutPasserA(StatutIntervention suivant) {
        return switch (this) {
            case PLANIFIEE -> suivant == EN_COURS || suivant == ANNULEE;
            case EN_COURS -> suivant == TERMINEE;
            case TERMINEE, ANNULEE -> false;
        };
    }
}
