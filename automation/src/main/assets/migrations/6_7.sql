ALTER TABLE `automation_rules`
ADD COLUMN `constraintGroupsJson` TEXT NOT NULL DEFAULT '[]';
