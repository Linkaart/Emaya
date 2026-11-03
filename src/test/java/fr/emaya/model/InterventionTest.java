package fr.emaya.model;

import fr.emaya.exception.RegleMetierException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InterventionTest {

    private static final LocalDate DATE = LocalDate.of(2026, 11, 3);

    @Test
    @DisplayName("Vidange sur voiture : 0,5 h x 60 € + 45 € de pièces")
    void montantVidangeVoiture() {
        Vehicule voiture = new Voiture("AB-123-CD", "Peugeot", "208", 2019, 54_000, 5);
        Intervention vidange = new Intervention(1, voiture, TypeIntervention.VIDANGE, DATE, new BigDecimal("45"));

        assertEquals(new BigDecimal("75.00"), vidange.montantHT());
        assertEquals(new BigDecimal("90.00"), vidange.montantTTC());
    }

    @Test
    @DisplayName("Freins sur moto : le coefficient 0,8 réduit la main-d'œuvre")
    void montantFreinsMoto() {
        Vehicule moto = new Moto("EF-456-GH", "Yamaha", "MT-07", 2021, 12_000, 689);
        Intervention freins = new Intervention(2, moto, TypeIntervention.FREINS, DATE, BigDecimal.ZERO);

        assertEquals(new BigDecimal("72.00"), freins.montantHT());
        assertEquals(new BigDecimal("86.40"), freins.montantTTC());
    }

    @Test
    @DisplayName("Distribution sur gros utilitaire : coefficient 1,5")
    void montantDistributionUtilitaire() {
        Vehicule master = new Utilitaire("IJ-789-KL", "Renault", "Master", 2018, 143_000, 1_400);
        Intervention distribution = new Intervention(3, master, TypeIntervention.DISTRIBUTION, DATE, new BigDecimal("250.00"));

        assertEquals(new BigDecimal("610.00"), distribution.montantHT());
    }

    @Test
    @DisplayName("Le cycle de vie suit PLANIFIEE -> EN_COURS -> TERMINEE")
    void transitionsAutorisees() {
        Intervention intervention = nouvelleIntervention();
        intervention.changerStatut(StatutIntervention.EN_COURS);
        intervention.changerStatut(StatutIntervention.TERMINEE);
        assertEquals(StatutIntervention.TERMINEE, intervention.getStatut());
    }

    @Test
    @DisplayName("On ne peut pas terminer une intervention qui n'a pas commencé")
    void transitionInterdite() {
        Intervention intervention = nouvelleIntervention();
        assertThrows(RegleMetierException.class, () -> intervention.changerStatut(StatutIntervention.TERMINEE));
    }

    @Test
    @DisplayName("Un coût de pièces négatif est refusé")
    void piecesNegatives() {
        Vehicule voiture = new Voiture("AB-123-CD", "Peugeot", "208", 2019, 54_000, 5);
        assertThrows(RegleMetierException.class,
                () -> new Intervention(4, voiture, TypeIntervention.PNEUS, DATE, new BigDecimal("-10")));
    }

    private Intervention nouvelleIntervention() {
        Vehicule voiture = new Voiture("AB-123-CD", "Peugeot", "208", 2019, 54_000, 5);
        return new Intervention(5, voiture, TypeIntervention.DIAGNOSTIC, DATE, BigDecimal.ZERO);
    }
}
