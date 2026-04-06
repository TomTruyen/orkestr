ALTER TABLE `automation_rules`
ADD COLUMN `actionExecutionMode` TEXT NOT NULL DEFAULT 'PARALLEL';
