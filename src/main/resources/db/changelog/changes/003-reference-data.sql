-- Insert sample drivers with different availability schedules
INSERT INTO chauffeur (nom, prenom, statut, disponibilite_debut, disponibilite_fin) VALUES
    ('Dupont', 'Jean', 'DISPONIBLE', '08:00', '16:00'),
    ('Martin', 'Sophie', 'DISPONIBLE', '16:00', '00:00'),
    ('Garcia', 'Miguel', 'DISPONIBLE', '00:00', '08:00'),
    ('Dubois', 'Marie', 'DISPONIBLE', '09:00', '17:00'),
    ('Smith', 'John', 'DISPONIBLE', '14:00', '22:00');

-- Insert sample vehicles of different types
INSERT INTO vehicule (modele, immatriculation, kilometrage, statut, type) VALUES
    ('Peugeot 508', 'AA-123-BB', 15000.0, 'DISPONIBLE', 'BERLINE'),
    ('Renault Trafic', 'BB-456-CC', 25000.0, 'DISPONIBLE', 'VAN'),
    ('Mercedes Sprinter', 'CC-789-DD', 20000.0, 'DISPONIBLE', 'MINIBUS'),
    ('Toyota Camry', 'DD-012-EE', 18000.0, 'DISPONIBLE', 'BERLINE'),
    ('Volkswagen Caravelle', 'EE-345-FF', 22000.0, 'DISPONIBLE', 'VAN');

-- Insert sample reservations with different statuses
INSERT INTO reservation (
    date_heure,
    ville_depart,
    quartier_depart,
    ville_arrivee,
    quartier_arrivee,
    prix,
    distance_km,
    statut,
    chauffeur_id,
    vehicule_id
) VALUES
    ('2024-03-15 09:00:00', 'Casablanca', 'Maarif', 'Rabat', 'Agdal', 250.0, 87.5, 'TERMINÉE', 1, 1),
    ('2024-03-15 14:30:00', 'Rabat', 'Hassan', 'Kenitra', 'Centre', 150.0, 46.2, 'CONFIRMÉE', 2, 2),
    ('2024-03-16 08:00:00', 'Casablanca', 'Ain Diab', 'El Jadida', 'Marina', 200.0, 93.0, 'CRÉÉE', 3, 3),
    ('2024-03-16 16:00:00', 'Mohammedia', 'Centre', 'Casablanca', 'Ain Sebaa', 100.0, 28.5, 'ANNULÉE', 4, 4),
    ('2024-03-17 10:00:00', 'Casablanca', 'Anfa', 'Mohammedia', 'Port', 120.0, 35.0, 'CONFIRMÉE', 5, 5);

-- Insert additional reservations for analytics purposes
INSERT INTO reservation (
    date_heure,
    ville_depart,
    quartier_depart,
    ville_arrivee,
    quartier_arrivee,
    prix,
    distance_km,
    statut,
    chauffeur_id,
    vehicule_id
) VALUES
    ('2024-03-15 08:00:00', 'Casablanca', 'Maarif', 'Mohammedia', 'Centre', 150.0, 40.0, 'TERMINÉE', 1, 1),
    ('2024-03-15 09:00:00', 'Rabat', 'Agdal', 'Salé', 'Centre', 80.0, 15.0, 'TERMINÉE', 2, 2),
    ('2024-03-15 10:00:00', 'Casablanca', 'Ain Diab', 'Mohammedia', 'Port', 130.0, 35.0, 'TERMINÉE', 3, 3),
    ('2024-03-15 11:00:00', 'Rabat', 'Hassan', 'Témara', 'Centre', 90.0, 20.0, 'TERMINÉE', 4, 4),
    ('2024-03-15 12:00:00', 'Casablanca', 'Anfa', 'El Jadida', 'Marina', 280.0, 95.0, 'TERMINÉE', 5, 5);