package fr.emaya.service;

import fr.emaya.exception.ElementIntrouvableException;
import fr.emaya.exception.RegleMetierException;
import fr.emaya.model.Client;
import fr.emaya.model.Intervention;
import fr.emaya.model.Moto;
import fr.emaya.model.StatutIntervention;
import fr.emaya.model.TypeIntervention;
import fr.emaya.model.Voiture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AtelierTest {

    private Atelier atelier;
    private Client client;

    @BeforeEach
    void initialiser() {
        atelier = new Atelier();
        client = atelier.ajouterClient("Jean Dupont", "06 12 34 56 78");
        atelier.ajouterVehicule(client.getId(), new Voiture("AB-123-CD", "Peugeot", "208", 2019, 54_000, 5));
    }

    @Test
    @DisplayName("Deux véhicules ne peuvent pas avoir la même plaque")
    void plaqueUnique() {
        Client autre = atelier.ajouterClient("Sophie Martin", "");
        assertThrows(RegleMetierException.class,
                () -> atelier.ajouterVehicule(autre.getId(), new Moto("AB-123-CD", "Yamaha", "MT-07", 2021, 12_000, 689)));
    }

    @Test
    @DisplayName("Ajouter un véhicule à un client inconnu lève une exception")
    void clientInconnu() {
        assertThrows(ElementIntrouvableException.class,
                () -> atelier.ajouterVehicule(99, new Moto("EF-456-GH", "Yamaha", "MT-07", 2021, 12_000, 689)));
    }

    @Test
    @DisplayName("Planifier sur une plaque inconnue lève une exception")
    void vehiculeInconnu() {
        assertThrows(ElementIntrouvableException.class,
                () -> atelier.planifierIntervention("ZZ-999-ZZ", TypeIntervention.VIDANGE, LocalDate.now(), BigDecimal.ZERO));
    }

    @Test
    @DisplayName("L'historique est trié de la plus récente à la plus ancienne")
    void historiqueTrie() {
        atelier.planifierIntervention("AB-123-CD", TypeIntervention.VIDANGE, LocalDate.of(2026, 1, 10), BigDecimal.ZERO);
        atelier.planifierIntervention("AB-123-CD", TypeIntervention.PNEUS, LocalDate.of(2026, 6, 20), BigDecimal.ZERO);

        List<Intervention> historique = atelier.historique("ab-123-cd");

        assertEquals(2, historique.size());
        assertEquals(TypeIntervention.PNEUS, historique.get(0).getType());
    }

    @Test
    @DisplayName("Seules les interventions terminées comptent dans le chiffre d'affaires")
    void chiffreAffaires() {
        Intervention terminee = atelier.planifierIntervention("AB-123-CD", TypeIntervention.VIDANGE,
                LocalDate.now(), new BigDecimal("45.00"));
        atelier.planifierIntervention("AB-123-CD", TypeIntervention.FREINS, LocalDate.now(), new BigDecimal("100.00"));

        atelier.changerStatut(terminee.getId(), StatutIntervention.EN_COURS);
        atelier.changerStatut(terminee.getId(), StatutIntervention.TERMINEE);

        assertEquals(new BigDecimal("90.00"), atelier.chiffreAffairesTTC());
        assertEquals(1L, atelier.statistiquesParType().get(TypeIntervention.VIDANGE));
    }

    @Test
    @DisplayName("Le propriétaire d'un véhicule est retrouvé par sa plaque")
    void proprietaire() {
        assertEquals("Jean Dupont", atelier.proprietaireDe("AB-123-CD").getNom());
        assertTrue(atelier.getClients().contains(client));
    }
}
