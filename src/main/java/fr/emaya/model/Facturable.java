package fr.emaya.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Tout ce qui peut apparaître sur une facture.
 * La méthode par défaut montantTTC() évite de dupliquer le calcul de TVA.
 */
public interface Facturable {

    BigDecimal TAUX_TVA = new BigDecimal("0.20");

    BigDecimal montantHT();

    default BigDecimal montantTTC() {
        return montantHT().multiply(BigDecimal.ONE.add(TAUX_TVA)).setScale(2, RoundingMode.HALF_UP);
    }
}
