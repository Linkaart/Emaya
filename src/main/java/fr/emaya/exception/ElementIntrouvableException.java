package fr.emaya.exception;

/**
 * Levée quand on cherche un client, un véhicule ou une intervention qui n'existe pas.
 */
public class ElementIntrouvableException extends RuntimeException {

    public ElementIntrouvableException(String message) {
        super(message);
    }
}
