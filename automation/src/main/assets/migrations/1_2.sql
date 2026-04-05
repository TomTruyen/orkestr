CREATE TABLE IF NOT EXISTS `geofences` (
    `id` TEXT NOT NULL,
    `name` TEXT NOT NULL,
    `latitude` REAL NOT NULL,
    `longitude` REAL NOT NULL,
    `radiusMeters` REAL NOT NULL,
    `address` TEXT,
    `updatedAtEpochMillis` INTEGER NOT NULL,
    PRIMARY KEY(`id`)
);
