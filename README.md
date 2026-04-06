# orkestr

Orkestr is an Android automation app. Users build local rules from triggers, optional constraints, and actions, and the app evaluates those rules on-device through a foreground runtime.

The project stays within normal Android app boundaries:

- No root
- No hidden APIs
- No permanent ADB dependency
- Explicit permissions and platform limitations

## Project Gist

Orkestr has two main layers:

- Compose UI modules for creating and configuring automation rules
- An `automation` engine module for persistence, generated registries, runtime evaluation, and Android integrations

Each rule is stored in Room as:

- one or more triggers
- zero or more constraints
- one or more actions

At runtime, the foreground service keeps only the necessary trigger integrations active, forwards platform events into the automation engine, evaluates matching rules, and executes actions.

## Architecture

### Modules

| Module                        | Responsibility |
|------------------------------|----------------|
| `:app`                        | Thin application shell. Owns startup, top-level navigation host, manifest placeholders, and app theme wiring. |
| `:automation`                 | Core automation engine: Room, repositories, runtime services, event model, permissions, definitions, delegates, and trigger integrations. |
| `:automation-ksp`             | KSP processors that generate trigger/constraint/action providers, receiver factory providers, registry providers, and migration registration. |
| `:automation-ksp-annotations` | Source annotations consumed by `:automation-ksp`. |
| `:ui:automation`              | Generic automation editor UI: rules list, rule editor, definition picker, generic field forms, and trigger-specific navigation branching. |
| `:ui:common`                  | Shared UI components, shared transitions, common view-model helpers, and permission UI helpers. |
| `:ui:geofence`                | Dedicated geofence picker/editor flow used by the generic automation editor. |
| `:ui:timebased`               | Dedicated time-based trigger configuration UI. |
| `:ui:wifi`                    | Dedicated Wi-Fi trigger configuration and network selection UI. |

### High-level flow

1. The user edits a rule in `:ui:automation`.
2. The UI reads available definitions from `AutomationDefinitionRegistry`.
3. The selected node config is persisted through `AutomationRuleRepository` into Room.
4. `AutomationForegroundService` observes enabled rules and computes the active `TriggerReceiverKey` set.
5. Generated receiver factories register only the platform integrations required by enabled triggers.
6. Receivers and services emit `AutomationEvent` instances into `AutomationRuntimeService`.
7. `TriggerMatcher` checks whether any trigger in the rule matches the event.
8. `ConstraintEvaluator` checks all configured constraints.
9. `ActionExecutor` runs the configured actions.

### Generated registration

The project does not maintain manual registries for triggers, constraints, or actions.

- Definitions are annotated with `@GenerateTriggerDefinition`, `@GenerateConstraintDefinition`, or `@GenerateActionDefinition`.
- Runtime delegates are annotated with `@GenerateTriggerDelegate`, `@GenerateConstraintDelegate`, or `@GenerateActionDelegate`.
- Trigger receiver factories are annotated with `@GenerateReceiverFactory`.
- `:automation-ksp` generates providers such as `GeneratedAutomationRegistryProvider`, `GeneratedTriggerProvider`, `GeneratedConstraintProvider`, `GeneratedActionProvider`, and `GeneratedReceiverProvider`.
- [`AutomationModules.kt`](/home/tom/Documents/GitHub/orkestr/automation/src/main/java/com/tomtruyen/automation/di/AutomationModules.kt) wires those generated providers into Koin.

That means adding a new capability is mostly a matter of creating the config/definition/delegate pair and annotating it correctly.

### Where the core pieces live

- Config models: [`automation/src/main/java/com/tomtruyen/automation/features`](/home/tom/Documents/GitHub/orkestr/automation/src/main/java/com/tomtruyen/automation/features)
- Base definition APIs: [`AutomationNodeDefinition.kt`](/home/tom/Documents/GitHub/orkestr/automation/src/main/java/com/tomtruyen/automation/core/definition/AutomationNodeDefinition.kt) and [`AutomationFieldDefinition.kt`](/home/tom/Documents/GitHub/orkestr/automation/src/main/java/com/tomtruyen/automation/core/definition/AutomationFieldDefinition.kt)
- Registry: [`AutomationDefinitionRegistry.kt`](/home/tom/Documents/GitHub/orkestr/automation/src/main/java/com/tomtruyen/automation/core/definition/AutomationDefinitionRegistry.kt)
- Runtime: [`AutomationForegroundService.kt`](/home/tom/Documents/GitHub/orkestr/automation/src/main/java/com/tomtruyen/automation/core/AutomationForegroundService.kt) and [`AutomationRuntimeService.kt`](/home/tom/Documents/GitHub/orkestr/automation/src/main/java/com/tomtruyen/automation/core/AutomationRuntimeService.kt)
- Trigger matching: [`TriggerMatcher.kt`](/home/tom/Documents/GitHub/orkestr/automation/src/main/java/com/tomtruyen/automation/features/triggers/TriggerMatcher.kt)
- Constraint evaluation: [`ConstraintEvaluator.kt`](/home/tom/Documents/GitHub/orkestr/automation/src/main/java/com/tomtruyen/automation/features/constraints/ConstraintEvaluator.kt)
- Action execution: [`ActionExecutor.kt`](/home/tom/Documents/GitHub/orkestr/automation/src/main/java/com/tomtruyen/automation/features/actions/ActionExecutor.kt)
- Editor integration helpers: [`AutomationRuleEditorDefinitionHelpers.kt`](/home/tom/Documents/GitHub/orkestr/ui/automation/src/main/java/com/tomtruyen/orkestr/features/automation/viewmodel/AutomationRuleEditorDefinitionHelpers.kt)

## Adding A New Capability

### Action

To add a new action:

1. Add a new enum entry in [`ActionType.kt`](/home/tom/Documents/GitHub/orkestr/automation/src/main/java/com/tomtruyen/automation/features/actions/ActionType.kt).
2. Add a serializable config model under [`automation/features/actions/config`](/home/tom/Documents/GitHub/orkestr/automation/src/main/java/com/tomtruyen/automation/features/actions/config) implementing `ActionConfig`.
3. Add an annotated definition object under [`automation/features/actions/definition`](/home/tom/Documents/GitHub/orkestr/automation/src/main/java/com/tomtruyen/automation/features/actions/definition) with `@GenerateActionDefinition`.
4. Define fields through `TypedAutomationFieldDefinition` if the generic editor can configure it.
5. Add an annotated runtime delegate under [`automation/features/actions/delegate`](/home/tom/Documents/GitHub/orkestr/automation/src/main/java/com/tomtruyen/automation/features/actions/delegate) with `@GenerateActionDelegate`.
6. Add strings for title, description, field labels, option labels, and summaries.
7. Add unit tests for the delegate and any definition validation logic.

Use [`LogMessageActionDefinition.kt`](/home/tom/Documents/GitHub/orkestr/automation/src/main/java/com/tomtruyen/automation/features/actions/definition/LogMessageActionDefinition.kt) plus [`LogMessageActionDelegate.kt`](/home/tom/Documents/GitHub/orkestr/automation/src/main/java/com/tomtruyen/automation/features/actions/delegate/LogMessageActionDelegate.kt) as the simplest reference implementation.

### Constraint

To add a new constraint:

1. Add a new enum entry in [`ConstraintType.kt`](/home/tom/Documents/GitHub/orkestr/automation/src/main/java/com/tomtruyen/automation/features/constraints/ConstraintType.kt).
2. Add a serializable config model under [`automation/features/constraints/config`](/home/tom/Documents/GitHub/orkestr/automation/src/main/java/com/tomtruyen/automation/features/constraints/config) implementing `ConstraintConfig`.
3. Add an annotated definition object under [`automation/features/constraints/definition`](/home/tom/Documents/GitHub/orkestr/automation/src/main/java/com/tomtruyen/automation/features/constraints/definition) with `@GenerateConstraintDefinition`.
4. Add an annotated runtime delegate under [`automation/features/constraints/delegate`](/home/tom/Documents/GitHub/orkestr/automation/src/main/java/com/tomtruyen/automation/features/constraints/delegate) with `@GenerateConstraintDelegate`.
5. Add strings and validation rules.
6. Add unit tests for evaluation and definition validation.

[`BatteryLevelConstraintDefinition.kt`](/home/tom/Documents/GitHub/orkestr/automation/src/main/java/com/tomtruyen/automation/features/constraints/definition/BatteryLevelConstraintDefinition.kt) and [`BatteryLevelConstraintDelegate.kt`](/home/tom/Documents/GitHub/orkestr/automation/src/main/java/com/tomtruyen/automation/features/constraints/delegate/BatteryLevelConstraintDelegate.kt) are the current template.

### Trigger

Triggers follow the same config/definition/delegate pattern, but may also need an event source and custom editor routing.

To add a new trigger:

1. Add a new enum entry in [`TriggerType.kt`](/home/tom/Documents/GitHub/orkestr/automation/src/main/java/com/tomtruyen/automation/features/triggers/TriggerType.kt).
2. Add a serializable config model under [`automation/features/triggers/config`](/home/tom/Documents/GitHub/orkestr/automation/src/main/java/com/tomtruyen/automation/features/triggers/config) implementing `TriggerConfig`.
3. If the trigger depends on a foreground receiver integration, add `requiredReceiverKeys` to the config and extend [`TriggerReceiverKey.kt`](/home/tom/Documents/GitHub/orkestr/automation/src/main/java/com/tomtruyen/automation/features/triggers/receiver/TriggerReceiverKey.kt).
4. Add an annotated definition object under [`automation/features/triggers/definition`](/home/tom/Documents/GitHub/orkestr/automation/src/main/java/com/tomtruyen/automation/features/triggers/definition) with `@GenerateTriggerDefinition`.
5. Add an annotated runtime delegate under [`automation/features/triggers/delegate`](/home/tom/Documents/GitHub/orkestr/automation/src/main/java/com/tomtruyen/automation/features/triggers/delegate) with `@GenerateTriggerDelegate`.
6. If Android needs a broadcast receiver style integration, implement a `TriggerReceiver` and annotate its factory companion with `@GenerateReceiverFactory`.
7. If the trigger is driven by some other Android component, wire that component to emit `AutomationEvent` into `AutomationRuntimeService`. `NotificationReceivedTrigger` is the existing example; it uses [`AutomationNotificationListenerService.kt`](/home/tom/Documents/GitHub/orkestr/automation/src/main/java/com/tomtruyen/automation/features/triggers/receiver/AutomationNotificationListenerService.kt) rather than a generated receiver factory.
8. If the generic field form is not enough, add a custom editor screen/module and route it from [`AutomationRuleEditorDefinitionHelpers.kt`](/home/tom/Documents/GitHub/orkestr/ui/automation/src/main/java/com/tomtruyen/orkestr/features/automation/viewmodel/AutomationRuleEditorDefinitionHelpers.kt) and [`AutomationCustomRouteScreens.kt`](/home/tom/Documents/GitHub/orkestr/ui/automation/src/main/java/com/tomtruyen/orkestr/features/automation/navigation/AutomationCustomRouteScreens.kt).
9. Add strings, permissions, and tests for the definition, delegate, and receiver/event source.

Reference points by complexity:

- Generic trigger with receiver: [`BatterySaverStateTriggerDefinition.kt`](/home/tom/Documents/GitHub/orkestr/automation/src/main/java/com/tomtruyen/automation/features/triggers/definition/BatterySaverStateTriggerDefinition.kt), [`BatterySaverStateTriggerDelegate.kt`](/home/tom/Documents/GitHub/orkestr/automation/src/main/java/com/tomtruyen/automation/features/triggers/delegate/BatterySaverStateTriggerDelegate.kt), [`BatterySaverModeReceiver.kt`](/home/tom/Documents/GitHub/orkestr/automation/src/main/java/com/tomtruyen/automation/features/triggers/receiver/BatterySaverModeReceiver.kt)
- Trigger with custom editor UI: [`TimeBasedTriggerConfig.kt`](/home/tom/Documents/GitHub/orkestr/automation/src/main/java/com/tomtruyen/automation/features/triggers/config/TimeBasedTriggerConfig.kt) and [`TimeBasedTriggerConfigurationScreen.kt`](/home/tom/Documents/GitHub/orkestr/ui/timebased/src/main/java/com/tomtruyen/orkestr/features/timebased/screen/TimeBasedTriggerConfigurationScreen.kt)
- Trigger with repository-backed Android integration: [`GeofenceTriggerConfig.kt`](/home/tom/Documents/GitHub/orkestr/automation/src/main/java/com/tomtruyen/automation/features/triggers/config/GeofenceTriggerConfig.kt) and [`GeofenceRegistrationReceiver.kt`](/home/tom/Documents/GitHub/orkestr/automation/src/main/java/com/tomtruyen/automation/features/triggers/receiver/GeofenceRegistrationReceiver.kt)

## Supported Capabilities

### Triggers

| Trigger | Notes |
|---------|-------|
| Charge State | Matches battery charging state changes from `ACTION_BATTERY_CHANGED`. |
| Time Based | Matches a configured hour, minute, and weekday set. |
| Battery Level | Matches battery percentage against a comparison operator. |
| Power Connection | Matches power connected or disconnected events. |
| Battery Saver State | Matches battery saver turning on or off. |
| Application Lifecycle | Matches an app being launched or closed. Requires usage access. |
| Notification Received | Matches notifications from a selected app. Requires notification listener access. |
| Wi-Fi SSID In Range | Matches a selected SSID entering or leaving range. Requires location and nearby Wi-Fi permissions. |
| Geofence | Matches entry/exit for a saved geofence. Requires fine and background location. |

### Constraints

| Constraint | Notes |
|------------|-------|
| Battery Level | Compares current battery percentage against a configured operator and value. |

### Actions

| Action | Notes |
|--------|-------|
| Show Notification | Posts a local notification. Requires `POST_NOTIFICATIONS` on Android 13+. |
| Log Message | Writes a message to the automation logger. |
| Do Not Disturb | Changes the public Android DND mode. Requires notification policy access. |

## Persistence And Migrations

- Room schema, entities, and DAOs live under [`automation/src/main/java/com/tomtruyen/automation/data`](/home/tom/Documents/GitHub/orkestr/automation/src/main/java/com/tomtruyen/automation/data).
- SQL migrations live under [`automation/src/main/assets/migrations`](/home/tom/Documents/GitHub/orkestr/automation/src/main/assets/migrations).
- `:automation-ksp` generates a migration provider consumed by [`AutomationModules.kt`](/home/tom/Documents/GitHub/orkestr/automation/src/main/java/com/tomtruyen/automation/di/AutomationModules.kt).

## Development Setup

### Prerequisites

- JDK 17
- Android Studio with the Android SDK installed
- A device or emulator running Android 8.0+ for realistic runtime testing

### Clone and build

```bash
git clone <repo-url>
cd orkestr
./gradlew :app:compileDebugKotlin
```

### Useful verification commands

```bash
./gradlew :automation:testDebugUnitTest
./gradlew :ui:geofence:testDebugUnitTest
./gradlew :ui:wifi:testDebugUnitTest
./gradlew :automation-ksp:test
```

### Credentials and local configuration

The geofence editor uses Google Maps Compose, so local development needs a Maps SDK for Android key.

1. Create a Google Maps API key with Maps SDK for Android enabled.
2. Put it in `local.properties` under `googleMapsApiKey=...`, or export `GOOGLE_MAPS_API_KEY` in your shell.

Example `local.properties` entry:

```properties
googleMapsApiKey=YOUR_KEY_HERE
```

The value is injected into [`app/src/main/AndroidManifest.xml`](/home/tom/Documents/GitHub/orkestr/app/src/main/AndroidManifest.xml) from [`app/build.gradle.kts`](/home/tom/Documents/GitHub/orkestr/app/build.gradle.kts). `local.properties` is already gitignored, so the key is not committed.

If the key is missing, the project still compiles, but the geofence map will not function correctly at runtime.

For an open source project, restrict the key in Google Cloud to:

- `Maps SDK for Android`
- your app package name
- your signing certificate SHA-1/SHA-256

## Contributor Guidelines

- Keep `:app` thin.
- Keep runtime automation logic, persistence, permissions, and Android integrations in `:automation`.
- Prefer the existing config/definition/delegate pattern and let KSP generate registration.
- Only add custom trigger UI when the generic field form is not sufficient.
- Add or update tests when changing runtime behavior, generated code, or editor flow.
