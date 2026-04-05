# orkestr

Orkestr is an Android automation app. Users build local rules from triggers, optional constraints, and actions, and the app evaluates those rules on-device through a foreground runtime.

The project stays within normal Android app boundaries:

- No root
- No hidden APIs
- No permanent ADB dependency
- Explicit permissions and platform limitations

## Project Gist

At a high level, Orkestr has two halves:

- UI feature modules for rule creation and configuration
- An `automation` engine module for persistence, runtime evaluation, and Android integrations

A rule is persisted in Room, observed by the foreground runtime, matched against incoming events, filtered by constraints, and then executed through actions.

## Architecture

### Modules

| Module                        | Responsibility                                                                                                                         |
|-------------------------------|----------------------------------------------------------------------------------------------------------------------------------------|
| `:app`                        | Thin application shell. Owns `Application`, top-level navigation host, theme, manifest, and startup wiring.                            |
| `:ui:automation`              | Generic automation UI: rules list, rule editor, node picker, and feature-level nav graph.                                              |
| `:ui:geofence`                | Standalone geofence UI flow: saved geofence selection, geofence editor, map, search, and result handoff back to the automation editor. |
| `:ui:common`                  | Shared UI/view-model helpers used by UI modules.                                                                                       |
| `:automation`                 | Automation engine: Room, repositories, definitions, delegates, trigger receivers, runtime services, permissions, and domain models.    |
| `:automation-ksp`             | KSP processor that generates provider registries and database migration registration from source annotations and SQL migration assets. |
| `:automation-ksp-annotations` | Annotation definitions consumed by `:automation-ksp`.                                                                                  |

### UI flow

- `:app` hosts the main nav entry point in [`app/src/main/java/com/tomtruyen/orkestr/navigation/AppNavigation.kt`](/home/tom/Documents/GitHub/orkestr/app/src/main/java/com/tomtruyen/orkestr/navigation/AppNavigation.kt).
- `:ui:automation` owns the automation feature graph in [`ui/automation/src/main/java/com/tomtruyen/orkestr/features/automation/navigation/AutomationNavGraph.kt`](/home/tom/Documents/GitHub/orkestr/ui/automation/src/main/java/com/tomtruyen/orkestr/features/automation/navigation/AutomationNavGraph.kt).
- `:ui:geofence` owns the geofence-specific UI state and screens, then returns a selected geofence back to the editor so the generic trigger form can finish configuration.

### Automation engine

- Definitions live under [`automation/src/main/java/com/tomtruyen/automation/features`](/home/tom/Documents/GitHub/orkestr/automation/src/main/java/com/tomtruyen/automation/features).
- Runtime entry points live in [`AutomationForegroundService.kt`](/home/tom/Documents/GitHub/orkestr/automation/src/main/java/com/tomtruyen/automation/core/AutomationForegroundService.kt) and [`AutomationRuntimeService.kt`](/home/tom/Documents/GitHub/orkestr/automation/src/main/java/com/tomtruyen/automation/core/AutomationRuntimeService.kt).
- Room persistence lives under [`automation/src/main/java/com/tomtruyen/automation/data`](/home/tom/Documents/GitHub/orkestr/automation/src/main/java/com/tomtruyen/automation/data).
- Trigger receivers live under [`automation/src/main/java/com/tomtruyen/automation/features/triggers/receiver`](/home/tom/Documents/GitHub/orkestr/automation/src/main/java/com/tomtruyen/automation/features/triggers/receiver).

### Persistence and migrations

- Room schema and DAOs live in [`automation/src/main/java/com/tomtruyen/automation/data`](/home/tom/Documents/GitHub/orkestr/automation/src/main/java/com/tomtruyen/automation/data).
- SQL migrations live as assets in [`automation/src/main/assets/migrations`](/home/tom/Documents/GitHub/orkestr/automation/src/main/assets/migrations).
- `:automation-ksp` scans that directory at build time and generates a migration provider consumed by [`AutomationModules.kt`](/home/tom/Documents/GitHub/orkestr/automation/src/main/java/com/tomtruyen/automation/di/AutomationModules.kt).

### Rule execution flow

1. A feature module creates or edits a rule.
2. The rule is saved through the `automation` repositories into Room.
3. The foreground runtime observes enabled rules and computes the active receiver set.
4. Receivers emit `AutomationEvent` instances.
5. `TriggerMatcher` finds matching triggers.
6. `ConstraintEvaluator` applies optional constraints.
7. `ActionExecutor` runs the resulting actions.

## Supported Capabilities

### Triggers

| Trigger      | Description                                                 | Android / platform constraints                                                                                                                                                               |
|--------------|-------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Charge State | Fires when the device enters the configured charging state. | Effective background evaluation relies on the foreground automation service. Practical support target is Android 8.0+ due to background execution limits.                                    |
| Geofence     | Fires when entering or exiting a saved circular geofence.   | Requires Google Play services location APIs, `ACCESS_FINE_LOCATION`, and `ACCESS_BACKGROUND_LOCATION` on Android 10+ for background delivery. The editor map requires a Google Maps API key. |

### Constraints

| Constraint    | Description                                                                     | Android / platform constraints                                            |
|---------------|---------------------------------------------------------------------------------|---------------------------------------------------------------------------|
| Battery Level | Compares the current battery percentage with the configured operator and value. | No extra permission. Uses battery state already available to the runtime. |

### Actions

| Action            | Description                                       | Android / platform constraints                                                            |
|-------------------|---------------------------------------------------|-------------------------------------------------------------------------------------------|
| Show Notification | Posts a local notification when a rule completes. | Requires `POST_NOTIFICATIONS` on Android 13+.                                             |
| Log Message       | Writes a message to the automation logger.        | No extra permission.                                                                      |
| Do Not Disturb    | Changes the public Android DND mode.              | Requires notification policy access. Final behavior depends on Android’s public DND APIs. |

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

### Geofence contributor notes

- The geofence editor lives entirely in [`ui/geofence`](/home/tom/Documents/GitHub/orkestr/ui/geofence).
- Address search uses Android `Geocoder`, not Google Places, to avoid paid APIs.
- The actual geofence trigger runtime, repository, DAO, Room entity, and receivers remain in [`automation`](/home/tom/Documents/GitHub/orkestr/automation).
- Reliable testing should cover both precise location and background location flows on Android 10+.
- Runtime geofence delivery also depends on device settings, Play services availability, and OEM background restrictions.

## Contributor Guidelines

- Keep `:app` thin. New product UI should generally live in a dedicated feature module with its own nav graph.
- Keep runtime automation logic, persistence, receivers, and Android trigger integrations in `:automation`.
- Prefer adding new capabilities through the existing definition/config/delegate patterns.
- Add or update tests when changing runtime behavior, generated code, or feature view-model logic.
