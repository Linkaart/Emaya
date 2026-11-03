package fr.emaya.exception;

/**
 * Levée quand une donnée ou une action ne respecte pas une règle métier
 * (immatriculation invalide, kilométrage qui baisse, transition de statut interdite...).
 */
public class RegleMetierException extends RuntimeException {

    public RegleMetierException(String message) {
        super(message);
    }
}
