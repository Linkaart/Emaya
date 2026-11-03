package fr.emaya.model;

import fr.emaya.exception.RegleMetierException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VehiculeTest {

    @Test
    @DisplayName("L'immatriculation est normalisée en majuscules")
    void immatriculationNormalisee() {
        Voiture voiture = new Voiture("  ab-123-cd ", "Peugeot", "208", 2019, 54_000, 5);
        assertEquals("AB-123-CD", voiture.getImmatriculation());
    }

    @Test
    @DisplayName("Une immatriculation au mauvais format est refusée")
    void immatriculationInvalide() {
        assertThrows(RegleMetierException.class,
                () -> new Voiture("1234-AB-57", "Peugeot", "208", 2019, 54_000, 5));
    }

    @Test
    @DisplayName("Le kilométrage ne peut pas diminuer")
    void kilometrageNePeutPasBaisser() {
        Moto moto = new Moto("EF-456-GH", "Yamaha", "MT-07", 2021, 12_000, 689);
        moto.mettreAJourKilometrage(15_000);
        assertEquals(15_000, moto.getKilometrage());
        assertThrows(RegleMetierException.class, () -> moto.mettreAJourKilometrage(10_000));
    }

    @Test
    @DisplayName("Chaque type de véhicule a son propre coefficient de main-d'œuvre")
    void coefficientsPolymorphes() {
        Vehicule voiture = new Voiture("AB-123-CD", "Peugeot", "208", 2019, 54_000, 5);
        Vehicule moto = new Moto("EF-456-GH", "Yamaha", "MT-07", 2021, 12_000, 689);
        Vehicule petitUtilitaire = new Utilitaire("IJ-789-KL", "Renault", "Kangoo", 2020, 80_000, 800);
        Vehicule grosUtilitaire = new Utilitaire("QR-345-ST", "Renault", "Master", 2018, 143_000, 1_400);

        assertEquals(1.0, voiture.getCoefficientMainOeuvre());
        assertEquals(0.8, moto.getCoefficientMainOeuvre());
        assertEquals(1.3, petitUtilitaire.getCoefficientMainOeuvre());
        assertEquals(1.5, grosUtilitaire.getCoefficientMainOeuvre());
    }
}
