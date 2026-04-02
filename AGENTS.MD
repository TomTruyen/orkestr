# AGENTS.md

## Project Overview

Orkestr is a modern Android automation app inspired by tools like Tasker and MacroDroid.

The goal is to let users build automations using:
- **Triggers**
- **Conditions**
- **Actions**

Orkestr must only support functionality that works on **standard Android devices without root and without ADB-dependent setup**.  
Do not propose, implement, or rely on features that require:
- root access
- Shizuku
- persistent ADB permissions
- `WRITE_SECURE_SETTINGS`
- hidden/system APIs not intended for normal third-party apps
- privileged/system app access
- OEM-specific private APIs unless explicitly marked as optional and unsupported by default

The app should be built using:
- **Kotlin**
- **Jetpack Compose**
- modern Android architecture
- maintainable, modular, testable code

---

## Core Product Philosophy

Orkestr should be:

- **Open source**
- **User-friendly**
- **Reliable**
- **Transparent about platform limitations**
- **Safe by default**
- **Extensible over time**

When suggesting features or implementations, prefer solutions that are:
1. officially supported by Android
2. understandable by users
3. realistic for Play Store-safe distribution
4. maintainable long-term

Do not optimize for "hacky" behavior.  
Always optimize for correctness, stability, and user trust.

---

## Platform Constraints

Android automation has many platform limitations.  
The assistant must always respect them.

### Hard constraints
Never assume the app can:
- silently interact with arbitrary UI without Accessibility-based user-enabled support
- toggle secure system settings unless Android officially allows it
- simulate privileged system behavior
- bypass battery restrictions without user action
- access restricted logs/calls/messages/background behavior unless explicitly allowed by Android APIs and granted by the user
- perform exact behavior on all OEMs the same way

### Important rule
If a requested feature is partially possible, the assistant must:
- clearly explain what **is** possible
- clearly explain what **is not** possible
- propose the **best supported fallback**

Do not pretend unsupported Android behavior is feasible.

---

## OS Version Rules

When implementing or documenting features, always add **OS-version labels** if behavior depends on Android version support.

Examples:
- `Requires Android 8.0+`
- `Available on Android 10+`
- `Limited on Android 12+`
- `Unavailable on Android 13+`
- `Behavior varies by OEM`
- `Deprecated after Android 11`

This applies to:
- code comments
- feature documentation
- UI labels/descriptions where relevant
- developer notes
- capability matrices

If a feature only works up to a certain Android version, explicitly mention that.  
If a feature only works from a certain Android version onward, explicitly mention that.  
If behavior differs significantly by OEM or manufacturer, mention that too.

Never leave platform support ambiguous.

---

## Required Tech Stack

Use these defaults unless there is a strong reason not to:

- **Language:** Kotlin
- **UI:** Jetpack Compose
- **Architecture:** Clean-ish modular architecture with clear boundaries
- **State management:** ViewModel + immutable UI state
- **Dependency injection:** Hilt
- **Async work:** Coroutines + Flow
- **Persistence:** Room and/or DataStore depending on data type
- **Background work:** WorkManager when appropriate
- **Navigation:** Navigation Compose
- **Serialization:** kotlinx.serialization where useful
- **Testing:** JUnit, Turbine, Compose UI tests

Do not introduce legacy Android patterns unless necessary for platform APIs.

Prefer:
- unidirectional data flow
- small composables
- strongly typed models
- sealed classes/interfaces for automation definitions and execution events

---

## UI Guidelines

Use Jetpack Compose for all new UI.

The UI should be:
- clean
- modern
- accessible
- Android-native in behavior
- easy to scale as more trigger/action types are added

### Compose expectations
- Keep composables small and focused
- Hoist state where appropriate
- Avoid large monolithic screens
- Use previewable composables when useful
- Prefer stateless composables with state passed in
- Model screen state explicitly (`Loading`, `Empty`, `Error`, `Success` if applicable)

### Accessibility
Always consider:
- content descriptions
- touch target sizes
- contrast
- screen reader friendliness
- clear wording for permissions and restrictions

### UX principles
Automation can become confusing quickly.  
Favor clarity over density.

Always make it obvious:
- what triggers an automation
- what conditions must be true
- what action will happen
- whether the automation is enabled
- whether a permission is missing
- whether the feature has OS/version limitations

---

## Automation Domain Modeling

Orkestr revolves around:
- **Triggers**
- **Conditions**
- **Actions**
- **Constraints / permissions / capability requirements**
- **Execution history / logs**

When designing the domain, prefer explicit models over vague generic maps.

Example direction:
- `Automation`
- `TriggerDefinition`
- `ConditionDefinition`
- `ActionDefinition`
- `ExecutionResult`
- `PermissionRequirement`
- `CapabilityAvailability`

The assistant should prefer extensible patterns so new trigger/action types can be added without rewriting the whole app.

Avoid deeply hardcoded screen-to-screen logic tied to one specific trigger type.

---

## Permissions and User Trust

Permissions are a core part of this product.

The assistant must:
- request the minimum permissions needed
- explain clearly why each permission is needed
- never hide limitations behind vague messaging
- never design deceptive permission flows

For any feature depending on:
- Accessibility Service
- Notification Listener
- Device Admin
- exact alarms
- location
- foreground service
- battery optimization exemptions

the app should present:
1. why it is needed
2. what the user gains
3. any OS-version or OEM limitations

Prefer transparent onboarding over aggressive prompting.

---

## Background Execution Rules

Android background behavior is heavily restricted.

The assistant must:
- use **WorkManager** for deferrable background work
- use foreground services only when truly necessary and policy-compliant
- avoid promising guaranteed instant background execution unless it is actually realistic
- account for Doze, app standby, battery optimization, and OEM background killing

If timing precision is not guaranteed by Android, say so.

Do not describe background execution as reliable if it can be delayed by the system.

---

## Implementation Priorities

When helping build the project, prioritize in this order:

1. **Strong domain model**
2. **Permission/capability modeling**
3. **Reliable trigger execution architecture**
4. **Clear user-facing flows**
5. **Extensibility for new trigger/action types**
6. **Polish and advanced features**

Avoid jumping into flashy features before the foundations are solid.

---

## Suggested MVP Scope

Unless told otherwise, optimize for a realistic MVP that could actually work well on modern Android.

Good MVP candidates:
- time/date trigger
- app opened trigger (if technically feasible within Android limitations)
- notification received trigger via Notification Listener
- battery state / charging trigger
- network connectivity trigger
- headphone/Bluetooth connection trigger
- location trigger
- DND-related informational or user-assisted flows where allowed
- simple conditions (time range, battery %, Wi-Fi connected, device charging)
- simple actions (show notification, play sound, open app, send intent where supported, toggle app-internal state, clipboard read/write where allowed, etc.)

Be cautious with:
- SMS/call features
- accessibility-driven actions
- quick settings/system toggles
- exact alarm behavior
- app usage detection
- device-wide UI manipulation

These may be possible, but they must be modeled with clear restrictions.

---

## Restricted Feature Labeling

Whenever a feature has important caveats, explicitly label it.

Examples:
- `Requires Accessibility Service`
- `Requires Notification Access`
- `Requires Location Permission`
- `Requires foreground service while active`
- `May not work reliably on some OEM devices`
- `Requires Android 9+`
- `Not available on Android 14+`
- `Limited by battery optimization policies`

These labels should be easy for both developers and users to notice.

---

## Code Quality Rules

All code generated for this project should be:

- idiomatic Kotlin
- strongly typed
- modular
- easy to test
- documented when the behavior is non-obvious
- free of unnecessary abstraction

### Prefer
- descriptive names
- small focused classes
- interfaces only when they add clear value
- `data class` for immutable models
- sealed hierarchies for finite behavior sets
- explicit error handling
- repository/use-case separation where useful

### Avoid
- massive god classes
- unnecessary inheritance
- reflection-heavy approaches
- stringly typed business logic
- tightly coupling UI to platform/service implementations
- adding libraries without clear need

---

## Testing Expectations

The assistant should encourage tests for:
- automation execution logic
- trigger/condition/action evaluation
- version/capability gating
- serialization/deserialization of automation definitions
- ViewModel behavior
- critical Compose UI states

Prefer unit tests for domain logic first.

Important areas to test:
- permission missing scenarios
- unsupported OS version scenarios
- disabled capability scenarios
- automation ordering and evaluation
- failure handling and retries where applicable

---

## Documentation Expectations

Whenever implementing a feature, include or update documentation for:
- what it does
- what permission it needs
- what Android version it supports
- what OEM limitations may apply
- what fallback behavior exists if full support is unavailable

Assume that Android automation features can be misunderstood easily.  
Documentation should remove ambiguity.

---

## Feature Proposal Rules for the Assistant

When proposing a new feature, always include:
1. a short summary
2. whether it is feasible without root/ADB
3. required permissions/services
4. Android version support
5. OEM/reliability caveats
6. recommended implementation approach
7. whether it fits MVP or post-MVP

Do not propose impossible features as normal roadmap items.

If a feature is only possible through a workaround, explicitly call it a workaround.

---

## Security and Privacy

Orkestr may process sensitive user context such as:
- notifications
- installed apps
- device state
- location
- clipboard
- usage-related signals

The assistant must favor:
- local-first processing where practical
- minimum data retention
- explicit user consent
- no unnecessary collection
- no surprise behavior

Do not suggest sending sensitive automation data to remote services unless explicitly required and clearly disclosed.

---

## Architecture Direction

Prefer a modular structure over time, for example:

- `app` -> app entry, DI, navigation
- `core:model` -> domain models
- `core:common` -> shared utilities
- `core:ui` -> shared Compose components/design system
- `feature:automations` -> automation listing/editing
- `feature:permissions` -> permission education and setup
- `feature:executionlog` -> run history/logging
- `automation:engine` -> evaluation and execution
- `automation:triggers:*`
- `automation:conditions:*`
- `automation:actions:*`

Exact module names can evolve, but separation of concerns matters.

Do not over-modularize too early, but design with future modularization in mind.

---

## How the Assistant Should Respond

When acting as a coding companion for Orkestr, the assistant should:
- be practical
- respect Android platform reality
- prefer implementation-ready suggestions
- call out limitations early
- include OS-version notes
- include permission implications
- prefer Kotlin + Compose-first solutions
- avoid recommending root/ADB-only approaches
- mention tradeoffs honestly

If asked to implement or design a feature that is not realistically possible on normal Android devices, the assistant should say so clearly and suggest supported alternatives.

---

## Default Output Expectations

Unless asked otherwise, code suggestions should:
- compile cleanly
- use Kotlin
- use Jetpack Compose for UI
- follow modern Android best practices
- include notes for version restrictions
- include notes for required permissions
- avoid placeholders that hide important logic

When useful, provide:
- file structure suggestions
- interface definitions
- data models
- ViewModel scaffolding
- Compose screen scaffolding
- test suggestions

---

## Non-Goals

Do not turn Orkestr into:
- a root-only power-user tool
- an ADB-assisted setup tool
- an OEM-specific hack collection
- a hidden API experiment
- an unreliable "maybe works" automation toy

The product should remain grounded in what normal Android apps can realistically do.

---

## Guiding Principle

Build the best possible open-source Android automation app **within real platform limits**.

Be ambitious in architecture and UX.  
Be honest about Android restrictions.  
Never trade user trust for fake capability.

## README Maintenance Rules

The assistant must treat the `README.md` as a **living source of truth** for the project.

Whenever a new feature is added or an existing feature is modified, the assistant must:
- update the README accordingly
- ensure documentation reflects the current state of the app
- avoid leaving outdated or incomplete information

### When to Update the README

The README must be updated when:
- a new **Trigger**, **Condition**, or **Action** is introduced
- a feature gains new permissions or restrictions
- OS-version support changes
- behavior changes in a way that affects users or developers
- new modules or architecture changes are introduced (if relevant to contributors)
- setup or build steps change

---

## Automation Feature Documentation Structure

The README should contain clear sections for:

### Triggers
Each trigger must include:
- **Name**
- **Description**
- **Requirements** (permissions, services, etc.)
- **OS Version Support**
- **Restrictions / Notes** (OEM issues, reliability, limitations)

Example:

#### Notification Received
- **Description:** Triggers when a notification is posted
- **Requires:** Notification Access
- **OS Support:** Android 5.0+
- **Notes:** Some OEMs may delay or suppress notifications in background

---

### Conditions
Each condition must include:
- **Description**
- **Any required permissions**
- **OS/version constraints**
- **Limitations if applicable**

---

### Actions
Each action must include:
- **Description**
- **Requirements**
- **OS Version Support**
- **Restrictions / Notes**

---

## Documentation Standards

When updating the README:

- Be **concise but clear**
- Use consistent formatting across all entries
- Always include:
    - permissions
    - OS-version labels
    - known limitations
- Do not assume behavior is universal across all devices
- Explicitly mention if something:
    - requires user setup (e.g., enabling a service)
    - may not work on all OEM devices
    - is limited by Android system restrictions

---

## Accuracy Rule

The README must never:
- claim features that are not implemented
- omit important restrictions or limitations
- imply reliability where Android does not guarantee it

If a feature is:
- partially implemented → mark it clearly
- experimental → label it
- limited by platform behavior → explain it

---

## Sync with Codebase

The assistant should ensure:
- feature names in README match code terminology
- permission names are accurate and up-to-date
- OS version claims are correct
- removed features are also removed from README

---

## Guiding Principle

The README should allow a new user or contributor to:
- understand what Orkestr can and cannot do
- know what permissions are required
- understand platform limitations
- quickly see available triggers, conditions, and actions

If the README is outdated, the assistant must prioritize fixing it.
