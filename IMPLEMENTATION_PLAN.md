# Implementation Plan: Mock Rules Visual Suspended States

This plan describes how we will visually indicate that mock rules are inactive when the global master mocking switch is turned off.

---

## Technical Design

### 1. Warning Banner
* When `mockingEnabled` is `false`, render an amber/red colored warning banner immediately below the master switch header.
* Layout: Rounded card containing an warning icon and explanatory text: *"All mocks are bypassed. Toggle the master switch to activate rules."*

### 2. Card Opacity Reduction
* Pass the `globalMocksEnabled` state to `MockRuleCard`.
* If `globalMocksEnabled` is `false`, render the card with `Modifier.graphicsLayer(alpha = 0.5f)` or `Modifier.alpha(0.5f)`.
* This gives a clean "grayed-out" suspended appearance to the rules list.

---

## Proposed Changes

### UI Component (`tracea-ui`)

#### [MODIFY] [MockRulesScreen.kt](file:///Users/hari/Documents/kids/Learning/Android/tracea/tracea-ui/src/main/kotlin/com/hari/tracea/ui/screens/mocks/MockRulesScreen.kt)
* Add warning icon import (`Icons.Default.Warning` / `Icons.Default.Info`).
* Add the warning banner below the master switch column in the topBar layout.
* Pass `globalMocksEnabled` to `MockRuleCard` and apply the alpha modifier if false.

---

## Verification Plan

### Manual Verification
1. Open the Mock Rules tab.
2. Verify that when the master switch is **enabled**:
   - The warning banner is hidden.
   - All cards display at full opacity.
3. Toggle the master switch to **disabled**:
   - Verify a clean warning banner appears below the header.
   - Verify all rules in the list are grayed out (50% opacity).
