-- Create enum types for PostgreSQL
CREATE TYPE statut_reservation AS ENUM ('CRÉÉE', 'CONFIRMÉE', 'TERMINÉE', 'ANNULÉE');
CREATE TYPE statut_chauffeur AS ENUM ('DISPONIBLE', 'EN_COURSE', 'INDISPONIBLE');
CREATE TYPE type_vehicule AS ENUM ('BERLINE', 'VAN', 'MINIBUS');
CREATE TYPE statut_vehicule AS ENUM ('DISPONIBLE', 'EN_COURSE', 'INDISPONIBLE');

-- Create Chauffeur (Driver) table
CREATE TABLE chauffeur (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    statut statut_chauffeur NOT NULL DEFAULT 'DISPONIBLE',
    disponibilite_debut TIME NOT NULL,
    disponibilite_fin TIME NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create Vehicule (Vehicle) table
CREATE TABLE vehicule (
    id BIGSERIAL PRIMARY KEY,
    modele VARCHAR(100) NOT NULL,
    immatriculation VARCHAR(20) NOT NULL UNIQUE,
    kilometrage DOUBLE PRECISION NOT NULL DEFAULT 0,
    statut statut_vehicule NOT NULL DEFAULT 'DISPONIBLE',
    type type_vehicule NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create Reservation table
CREATE TABLE reservation (
    id BIGSERIAL PRIMARY KEY,
    date_heure TIMESTAMP NOT NULL,
    ville_depart VARCHAR(100) NOT NULL,
    quartier_depart VARCHAR(100) NOT NULL,
    ville_arrivee VARCHAR(100) NOT NULL,
    quartier_arrivee VARCHAR(100) NOT NULL,
    prix DOUBLE PRECISION NOT NULL,
    distance_km DOUBLE PRECISION NOT NULL,
    statut statut_reservation NOT NULL DEFAULT 'CRÉÉE',
    chauffeur_id BIGINT NOT NULL,
    vehicule_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes
CREATE INDEX idx_reservation_date_heure ON reservation(date_heure);
CREATE INDEX idx_reservation_statut ON reservation(statut);
CREATE INDEX idx_chauffeur_statut ON chauffeur(statut);
CREATE INDEX idx_vehicule_statut ON vehicule(statut);
CREATE INDEX idx_vehicule_type ON vehicule(type);

-- Create trigger function for updating timestamps
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create triggers for updating timestamps
CREATE TRIGGER update_chauffeur_updated_at
    BEFORE UPDATE ON chauffeur
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_vehicule_updated_at
    BEFORE UPDATE ON vehicule
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_reservation_updated_at
    BEFORE UPDATE ON reservation
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Create drivers table
CREATE TABLE drivers (
    id BIGSERIAL PRIMARY KEY,
    last_name VARCHAR(100) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL,
    availability_start DATE NOT NULL,
    availability_end DATE NOT NULL
);

-- Create vehicles table
CREATE TABLE vehicles (
    id UUID PRIMARY KEY,
    model VARCHAR(100) NOT NULL,
    registration_number VARCHAR(20) UNIQUE NOT NULL,
    mileage INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL,
    type VARCHAR(20) NOT NULL
);

-- Create reservations table
CREATE TABLE reservations (
    id BIGSERIAL PRIMARY KEY,
    date_time TIMESTAMP NOT NULL,
    course_start_time TIMESTAMP,
    course_end_time TIMESTAMP,
    departure_city VARCHAR(100) NOT NULL,
    departure_district VARCHAR(100) NOT NULL,
    arrival_city VARCHAR(100) NOT NULL,
    arrival_district VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    distance_km DECIMAL(10,2) NOT NULL,
    driver_id BIGINT REFERENCES drivers(id),
    vehicle_id UUID REFERENCES vehicles(id)
);