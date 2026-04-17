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
- an action execution mode, defaulting to parallel

Reusable trigger, constraint, and action groups are stored separately in Room. Groups are templates only: selecting a group while editing a rule copies that group's nodes into the rule, and later edits to either the rule or the group do not stay linked.

For testing, the rules list also exposes a manual `Run now` action. It bypasses triggers and evaluates the rule’s constraints before executing actions.

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
| `:ui:logs`                    | Execution log UI backed by Room. Shows persisted automation logs with severity badges and stack traces when present. |
| `:ui:timebased`               | Dedicated time-based trigger configuration UI. |
| `:ui:wifi`                    | Dedicated Wi-Fi trigger configuration and network selection UI. |

### High-level flow

1. The user edits a rule in `:ui:automation`.
2. The UI reads available definitions from `AutomationDefinitionRegistry`.
3. The user can either select one definition or insert a saved group template for the same section.
4. The selected node config is persisted through `AutomationRuleRepository` into Room.
5. `AutomationForegroundService` observes enabled rules and computes the active `TriggerReceiverKey` set.
6. Generated receiver factories register only the platform integrations required by enabled triggers.
7. Receivers and services emit `AutomationEvent` instances into `AutomationRuntimeService`.
8. `TriggerMatcher` checks whether any trigger in the rule matches the event.
9. `ConstraintEvaluator` checks all configured constraints.
10. `ActionExecutor` runs the configured actions using the rule's execution mode. Parallel is the default; sequential is available when action order matters.

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

## Automation Groups

Orkestr supports three reusable template types:

| Group type | Contents | Where it appears |
|------------|----------|------------------|
| Trigger Groups | One or more configured triggers. | The trigger picker while editing a rule, and the Groups bottom-navigation screen. |
| Constraint Groups | One or more configured constraints. | The constraint picker while editing a rule, and the Groups bottom-navigation screen. |
| Action Groups | One or more configured actions. | The action picker while editing a rule, and the Groups bottom-navigation screen. |

Groups are not shared live references. When a user selects a group, Orkestr inserts copies of the group's current trigger, constraint, or action configs into the flow. The inserted nodes can then be configured independently per rule.

Current behavior:

- Groups are persisted in Room through `AutomationNodeGroupRepository`.
- The Groups bottom-navigation screen lists, edits, and deletes saved groups.
- Group editing supports renaming, adding nodes, and removing nodes. It does not configure individual nodes from the management screen; configure a node in a rule first, then save it or its section as a group.
- Rule editor sections can save the current trigger, constraint, or action list as a multi-node group.
- Generic node configuration screens can save the current draft as a group after validation.
- The custom Time Of Day constraint screen also supports saving the current draft as a group.
- Android permission prompts still come from the inserted configs. Selecting a group with permission-backed nodes prompts for those permissions before insertion.

Limitations:

- Groups are editor templates only; they do not change runtime semantics.
- Some highly custom configuration flows may need an explicit "Save as Group" button added to their dedicated screen.

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
| Application Lifecycle | Matches an app being launched or closed. Requires Usage Access special app access (`PACKAGE_USAGE_STATS`). |
| Notification Received | Matches notifications from a selected app. Requires notification listener access. |
| Wi-Fi SSID In Range | Matches a selected SSID entering or leaving range. Requires location and nearby Wi-Fi permissions. |
| Geofence | Matches entry/exit for a saved geofence. Requires fine and background location. |
| Network Connectivity | Matches the default network connecting or disconnecting. Requires `ACCESS_NETWORK_STATE`. Available on Android 8.0+. This does not guarantee validated internet access. |
| Bluetooth Device Connection | Matches any Bluetooth device connecting or disconnecting. Requires `BLUETOOTH_CONNECT` on Android 12+. OEM Bluetooth behavior may vary. |
| Headphone Connection | Matches wired, USB, or Bluetooth headphones connecting or disconnecting. Available on Android 8.0+. Audio route reporting can vary by device. |
| App Installed/Removed | Matches app install, removal, or update package broadcasts. Available on Android 8.0+. Package visibility rules may limit metadata, but package change events remain supported. |
| Time Zone Changed | Matches device time zone changes. Available on Android 8.0+. |
| Do Not Disturb Mode | Matches public Android DND mode changes. Requires Notification Policy Access on Android 6.0+. |

### Constraints

| Constraint | Notes |
|------------|-------|
| Battery Level | Compares the live battery percentage against a configured operator and value. |
| Battery Saver State | Matches the current Battery Saver state. Available on Android 5.0+. |
| Power Connected | Matches whether the device is currently plugged into external power. |
| Bluetooth State | Matches whether Bluetooth is on. Requires `BLUETOOTH_CONNECT` on Android 12+. |
| GPS State | Matches whether the GPS provider is enabled. Behavior may vary by OEM location implementations. |
| Mobile Data State | Matches the default cellular data setting. Requires `READ_PHONE_STATE`. Dual-SIM behavior depends on the active data subscription. |
| Wi-Fi State | Matches whether Wi-Fi is on. |
| Connected Wi-Fi SSID | Matches whether the device is currently connected to a selected Wi-Fi network name. Requires location-based Wi-Fi access. Android may hide the current SSID when permission or device state is restricted. |
| Date Of Month | Matches one or more selected calendar dates from `1` to `31`. |
| Day Of Week | Matches one or more selected weekdays. |
| Month Of Year | Matches one or more selected months. |
| Time Of Day | Matches a configured daily time window. Windows can cross midnight. |
| Geofence | Matches whether the device is currently inside or outside a selected saved geofence. Requires fine and background location. Current location is best-effort and may be stale. |
| Headphone Connection | Matches whether wired, USB, or Bluetooth headphones are currently connected. |
| Music Active | Matches whether Android reports active media playback. |
| Call State | Matches normal telephony call state. Requires `READ_PHONE_STATE`. VoIP app calls are not included. |
| Screen State | Matches whether the screen is interactive/on. |

### Actions

| Action | Notes |
|--------|-------|
| Show Notification | Posts a local notification. Requires `POST_NOTIFICATIONS` on Android 13+. |
| Log Message | Writes a message to the automation logger with a configurable severity (`Debug`, `Info`, `Warning`, or `Error`). |
| Do Not Disturb | Changes the public Android DND mode. Requires notification policy access. |
| Launch Application | Opens a selected installed app. |
| Open Website | Opens a configured URL in the default browser. |
| Vibrate Phone | Vibrates the device for a configured duration. Requires `VIBRATE`. |
| Flash Torch | Flashes the device torch in a burst pattern. Requires camera permission and hardware flash support. Behavior varies by device. |
| Set Wallpaper | Applies a user-selected gallery/document image to the home screen, lock screen, or both. Requires `SET_WALLPAPER`. Available on Android 8.0+. Behavior may vary by OEM wallpaper implementation. |
| Force Location Update | Requests a fresh location fix. Requires fine and background location. Best-effort only; Android may still delay or deny the update. |
| Set Phone Volume | Sets media, ring, or call volume as a percentage of the stream max. Call volume behavior depends on device state and is most relevant while in a call. |
| Set Phone Vibrate | Switches the ringer mode between vibrate and normal. Behavior may vary by OEM and current sound settings. |

## Persistence And Migrations

- Room schema, entities, and DAOs live under [`automation/src/main/java/com/tomtruyen/automation/data`](/home/tom/Documents/GitHub/orkestr/automation/src/main/java/com/tomtruyen/automation/data).
- SQL migrations live under [`automation/src/main/assets/migrations`](/home/tom/Documents/GitHub/orkestr/automation/src/main/assets/migrations).
- `:automation-ksp` generates a migration provider consumed by [`AutomationModules.kt`](/home/tom/Documents/GitHub/orkestr/automation/src/main/java/com/tomtruyen/automation/di/AutomationModules.kt).
- `automation_logs` persists a severity for each entry (`DEBUG`, `INFO`, `WARNING`, `ERROR`) and is surfaced in the app-level Logs tab. The Logs tab uses Room-backed Paging 3 queries so large local histories do not need to load all at once.

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
