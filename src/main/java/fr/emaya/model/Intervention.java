package fr.emaya.model;

import fr.emaya.exception.RegleMetierException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Intervention implements Facturable {

    public static final BigDecimal TAUX_HORAIRE_HT = new BigDecimal("60.00");

    private static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final int id;
    private final Vehicule vehicule;
    private final TypeIntervention type;
    private final LocalDate datePrevue;
    private final BigDecimal coutPiecesHT;
    private StatutIntervention statut = StatutIntervention.PLANIFIEE;

    public Intervention(int id, Vehicule vehicule, TypeIntervention type, LocalDate datePrevue, BigDecimal coutPiecesHT) {
        this.id = id;
        this.vehicule = Objects.requireNonNull(vehicule, "Le véhicule est obligatoire");
        this.type = Objects.requireNonNull(type, "Le type d'intervention est obligatoire");
        this.datePrevue = Objects.requireNonNull(datePrevue, "La date est obligatoire");
        BigDecimal pieces = coutPiecesHT == null ? BigDecimal.ZERO : coutPiecesHT;
        if (pieces.signum() < 0) {
            throw new RegleMetierException("Le coût des pièces ne peut pas être négatif");
        }
        this.coutPiecesHT = pieces.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Main-d'œuvre = taux horaire x heures forfaitaires x coefficient du véhicule, plus les pièces.
     * Le coefficient vient de la sous-classe réelle du véhicule : c'est ici que joue le polymorphisme.
     */
    @Override
    public BigDecimal montantHT() {
        BigDecimal mainOeuvre = TAUX_HORAIRE_HT
                .multiply(BigDecimal.valueOf(type.getHeuresForfait()))
                .multiply(BigDecimal.valueOf(vehicule.getCoefficientMainOeuvre()));
        return mainOeuvre.add(coutPiecesHT).setScale(2, RoundingMode.HALF_UP);
    }

    public void changerStatut(StatutIntervention nouveauStatut) {
        if (!statut.peutPasserA(nouveauStatut)) {
            throw new RegleMetierException("Passage de " + statut + " à " + nouveauStatut + " impossible");
        }
        this.statut = nouveauStatut;
    }

    public int getId() {
        return id;
    }

    public Vehicule getVehicule() {
        return vehicule;
    }

    public TypeIntervention getType() {
        return type;
    }

    public LocalDate getDatePrevue() {
        return datePrevue;
    }

    public BigDecimal getCoutPiecesHT() {
        return coutPiecesHT;
    }

    public StatutIntervention getStatut() {
        return statut;
    }

    @Override
    public String toString() {
        return String.format("#%d %s - %s - %s - %s - %s € TTC",
                id, datePrevue.format(FORMAT_DATE), type.getLibelle(), vehicule.getImmatriculation(), statut, montantTTC());
    }
}
