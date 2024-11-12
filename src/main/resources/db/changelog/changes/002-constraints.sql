-- Add foreign key constraints for Reservation table
ALTER TABLE reservation
    ADD CONSTRAINT fk_reservation_chauffeur
    FOREIGN KEY (chauffeur_id)
    REFERENCES chauffeur (id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE;

ALTER TABLE reservation
    ADD CONSTRAINT fk_reservation_vehicule
    FOREIGN KEY (vehicule_id)
    REFERENCES vehicule (id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE;

-- Add check constraints for business rules
ALTER TABLE reservation
    ADD CONSTRAINT chk_reservation_distance
    CHECK (distance_km > 0 AND distance_km <= 100);

ALTER TABLE reservation
    ADD CONSTRAINT chk_reservation_prix
    CHECK (prix >= 0);

ALTER TABLE vehicule
    ADD CONSTRAINT chk_vehicule_kilometrage
    CHECK (kilometrage >= 0);

-- Add check constraints for time validity
ALTER TABLE chauffeur
    ADD CONSTRAINT chk_disponibilite_time
    CHECK (disponibilite_debut < disponibilite_fin);

-- Add not null constraints for embedded Adresse fields
ALTER TABLE reservation
    ALTER COLUMN ville_depart SET NOT NULL,
    ALTER COLUMN quartier_depart SET NOT NULL,
    ALTER COLUMN ville_arrivee SET NOT NULL,
    ALTER COLUMN quartier_arrivee SET NOT NULL;

-- Add unique constraints
ALTER TABLE chauffeur
    ADD CONSTRAINT uk_chauffeur_nom_prenom
    UNIQUE (nom, prenom);

-- Add business rule constraints for status transitions
CREATE OR REPLACE FUNCTION check_reservation_status_transition()
RETURNS TRIGGER AS $$
BEGIN
    IF OLD.statut = 'TERMINÉE' AND NEW.statut != 'TERMINÉE' THEN
        RAISE EXCEPTION 'Cannot change status of a completed reservation';
    END IF;
    IF OLD.statut = 'ANNULÉE' AND NEW.statut != 'ANNULÉE' THEN
        RAISE EXCEPTION 'Cannot change status of a cancelled reservation';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_reservation_status_transition
    BEFORE UPDATE OF statut ON reservation
    FOR EACH ROW
    EXECUTE FUNCTION check_reservation_status_transition();