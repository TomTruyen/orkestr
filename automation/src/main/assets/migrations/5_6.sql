CREATE TABLE IF NOT EXISTS `automation_node_groups` (
    `id` TEXT NOT NULL,
    `name` TEXT NOT NULL,
    `type` TEXT NOT NULL,
    `triggersJson` TEXT NOT NULL,
    `constraintsJson` TEXT NOT NULL,
    `actionsJson` TEXT NOT NULL,
    `updatedAtEpochMillis` INTEGER NOT NULL,
    PRIMARY KEY(`id`)
);
