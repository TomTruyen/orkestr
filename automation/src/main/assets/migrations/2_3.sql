CREATE TABLE IF NOT EXISTS `automation_logs` (
    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `timestampEpochMillis` INTEGER NOT NULL,
    `message` TEXT NOT NULL,
    `stackTrace` TEXT
);
