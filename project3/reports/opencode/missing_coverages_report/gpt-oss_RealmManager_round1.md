# Coverage Gaps

**File:** `RealmManager.java`

**Branches:** 66 / 160 covered — 94 missed

## Methods Not Entered

| Line | Method |
|---:|---|
| 101 | `getSession() : KeycloakSession` |
| 109 | `isAdministrationRealm(RealmModel) : boolean` |
| 113 | `getRealm(String) : RealmModel` |
| 328 | `rollbackImpl() : void` |
| 376 | `createMasterAdminManagement(RealmModel) : void` |
| 407 | `checkMasterAdminManagementRoles(RealmModel) : void` |
| 450 | `checkRealmAdminManagementRoles(RealmModel) : void` |
| 576 | `importRealm(RealmRepresentation) : RealmModel` |
| 585 | `importRealm(RealmRepresentation,boolean) : RealmModel` |
| 708 | `commitImpl() : void` |
| 714 | `rollbackImpl() : void` |
| 855 | `getCreatedRealm() : RealmModel` |
| 859 | `getKeycloakSession() : KeycloakSession` |
| 903 | `setDefaultsForNewRealm(RealmModel) : void` |

## Partially Covered Methods

### Line 161 — `setupAuthenticationFlows(RealmModel) : void`

| Line | Missing coverage |
|---:|---|
| 162 | `false` branch |

### Line 165 — `setupRequiredActions(RealmModel) : void`

| Line | Missing coverage |
|---:|---|
| 166 | `false` branch |

### Line 169 — `setupOfflineTokens(RealmModel,RealmRepresentation) : void`

| Line | Missing coverage |
|---:|---|
| 172 | `true` branch |
| 176 | `true` branch |
| 176 | `false` branch |
| 183 | `false` branch |

### Line 192 — `setupAdminConsole(RealmModel) : void`

| Line | Missing coverage |
|---:|---|
| 194 | `false` branch |

### Line 215 — `setupAdminConsoleLocaleMapper(RealmModel) : void`

| Line | Missing coverage |
|---:|---|
| 219 | `false` branch |
| 221 | `true` branch |

### Line 227 — `setupAdminCli(RealmModel) : void`

| Line | Missing coverage |
|---:|---|
| 229 | `false` branch |

### Line 269 — `setupRealmDefaults(RealmModel,RealmRepresentation) : void`

| Line | Missing coverage |
|---:|---|
| 288 | `true` branch |
| 291 | `true` branch |
| 291 | `false` branch |

### Line 301 — `removeRealm(RealmModel) : boolean`

| Line | Missing coverage |
|---:|---|
| 306 | `false` branch |
| 311 | `false` branch |
| 316 | `false` branch |

### Line 337 — `updateRealmEventsConfig(RealmEventsConfigRepresentation,RealmModel) : void`

| Line | Missing coverage |
|---:|---|
| 340 | `false` branch |
| 352 | `false` branch |
| 355 | `false` branch |
| 358 | `false` branch |

### Line 364 — `setupMasterAdminManagement(RealmModel) : void`

| Line | Missing coverage |
|---:|---|
| 369 | `true` branch |

### Line 422 — `setupRealmAdminManagement(RealmModel) : void`

| Line | Missing coverage |
|---:|---|
| 423 | `true` branch |
| 427 | `true` branch |

### Line 473 — `setupAccountManagement(RealmModel) : void`

| Line | Missing coverage |
|---:|---|
| 475 | `false` branch |
| 510 | `true` branch |
| 521 | `false` branch |

### Line 555 — `setupBrokerService(RealmModel) : void`

| Line | Missing coverage |
|---:|---|
| 556 | `false` branch |
| 561 | `true` branch |
| 561 | `false` branch |

### Line 593 — `importRealm(RealmRepresentation,Runnable) : RealmModel`

| Line | Missing coverage |
|---:|---|
| 623 | `false` branch |
| 629 | `true` branch |
| 630 | `true` branch |
| 630 | `false` branch |
| 634 | `true` branch |
| 634 | `false` branch |
| 640 | `false` branch |
| 644 | `false` branch |
| 645 | `false` branch |
| 648 | `false` branch |
| 652 | `false` branch |
| 653 | `false` branch |
| 656 | `false` branch |
| 659 | `false` branch |
| 664 | `false` branch |
| 668 | `false` branch |
| 678 | `true` branch |
| 682 | `true` branch |
| 690 | `true` branch |
| 694 | `true` branch |
| 701 | `false` branch |

### Line 745 — `determineDefaultRoleName(RealmRepresentation) : String`

| Line | Missing coverage |
|---:|---|
| 747 | `false` branch |
| 750 | `true` branch |
| 750 | `false` branch |
| 752 | `true` branch |
| 752 | `false` branch |

### Line 760 — `postponeMasterClientSetup(RealmRepresentation) : boolean`

| Line | Missing coverage |
|---:|---|
| 761 | `false` branch |

### Line 768 — `hasRealmAdminManagementClient(RealmRepresentation) : boolean`

| Line | Missing coverage |
|---:|---|
| 769 | `true` branch |

### Line 789 — `hasClient(RealmRepresentation,String) : boolean`

| Line | Missing coverage |
|---:|---|
| 790 | `false` branch |
| 792 | `true` branch |
| 792 | `false` branch |
| 799 | `false` branch |
| 801 | `true` branch |
| 801 | `false` branch |
| 806 | `false` branch |
| 808 | `true` branch |
| 808 | `false` branch |

### Line 817 — `hasRealmRole(RealmRepresentation,String) : boolean`

| Line | Missing coverage |
|---:|---|
| 818 | `false` branch |
| 823 | `true` branch |
| 823 | `false` branch |

### Line 831 — `hasClientScope(RealmRepresentation,String) : boolean`

| Line | Missing coverage |
|---:|---|
| 832 | `false` branch |
| 837 | `true` branch |
| 837 | `false` branch |

### Line 867 — `setupClientServiceAccountsAndAuthorizationOnImport(RealmRepresentation,boolean) : void`

| Line | Missing coverage |
|---:|---|
| 871 | `false` branch |
| 881 | `true` branch |
| 881 | `false` branch |
| 886 | `true` branch |
| 886 | `false` branch |
| 888 | `true` branch |
| 888 | `false` branch |
| 894 | `true` branch |
| 894 | `false` branch |
