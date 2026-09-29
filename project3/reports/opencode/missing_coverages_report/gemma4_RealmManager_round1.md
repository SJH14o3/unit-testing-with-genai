# Coverage Gaps

**File:** `RealmManager.java`

**Branches:** 59 / 160 covered — 101 missed

## Methods Not Entered

| Line | Method |
|---:|---|
| 121 | `createRealm(String) : RealmModel` |
| 125 | `createRealm(String,String) : RealmModel` |
| 169 | `setupOfflineTokens(RealmModel,RealmRepresentation) : void` |
| 265 | `setupRealmDefaults(RealmModel) : void` |
| 322 | `commitImpl() : void` |
| 328 | `rollbackImpl() : void` |
| 407 | `checkMasterAdminManagementRoles(RealmModel) : void` |
| 422 | `setupRealmAdminManagement(RealmModel) : void` |
| 473 | `setupAccountManagement(RealmModel) : void` |
| 585 | `importRealm(RealmRepresentation,boolean) : RealmModel` |
| 708 | `commitImpl() : void` |
| 714 | `rollbackImpl() : void` |
| 745 | `determineDefaultRoleName(RealmRepresentation) : String` |
| 768 | `hasRealmAdminManagementClient(RealmRepresentation) : boolean` |
| 773 | `hasAccountManagementClient(RealmRepresentation) : boolean` |
| 777 | `hasBrokerClient(RealmRepresentation) : boolean` |
| 781 | `hasAdminConsoleClient(RealmRepresentation) : boolean` |
| 785 | `hasAdminCliClient(RealmRepresentation) : boolean` |
| 845 | `setupAuthorizationServices(RealmModel) : void` |
| 849 | `setupClientRegistrations(RealmModel) : void` |
| 853 | `fireRealmPostCreate(RealmModel) : void` |
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

### Line 215 — `setupAdminConsoleLocaleMapper(RealmModel) : void`

| Line | Missing coverage |
|---:|---|
| 219 | `false` branch |
| 221 | `false` branch |

### Line 227 — `setupAdminCli(RealmModel) : void`

| Line | Missing coverage |
|---:|---|
| 229 | `false` branch |

### Line 269 — `setupRealmDefaults(RealmModel,RealmRepresentation) : void`

| Line | Missing coverage |
|---:|---|
| 291 | `false` branch |

### Line 337 — `updateRealmEventsConfig(RealmEventsConfigRepresentation,RealmModel) : void`

| Line | Missing coverage |
|---:|---|
| 339 | `true` branch |
| 340 | `false` branch |
| 346 | `false` branch |
| 352 | `true` branch |
| 352 | `false` branch |
| 355 | `true` branch |
| 355 | `false` branch |
| 358 | `true` branch |
| 358 | `false` branch |

### Line 376 — `createMasterAdminManagement(RealmModel) : void`

| Line | Missing coverage |
|---:|---|
| 380 | `true` branch |

### Line 450 — `checkRealmAdminManagementRoles(RealmModel) : void`

| Line | Missing coverage |
|---:|---|
| 451 | `true` branch |
| 458 | `true` branch |

### Line 555 — `setupBrokerService(RealmModel) : void`

| Line | Missing coverage |
|---:|---|
| 561 | `false` branch |

### Line 593 — `importRealm(RealmRepresentation,Runnable) : RealmModel`

| Line | Missing coverage |
|---:|---|
| 596 | `false` branch |
| 623 | `true` branch |
| 623 | `false` branch |
| 629 | `true` branch |
| 629 | `false` branch |
| 630 | `true` branch |
| 630 | `false` branch |
| 634 | `true` branch |
| 634 | `false` branch |
| 640 | `true` branch |
| 640 | `false` branch |
| 644 | `true` branch |
| 644 | `false` branch |
| 645 | `true` branch |
| 645 | `false` branch |
| 648 | `true` branch |
| 648 | `false` branch |
| 652 | `true` branch |
| 652 | `false` branch |
| 653 | `true` branch |
| 653 | `false` branch |
| 656 | `true` branch |
| 656 | `false` branch |
| 659 | `true` branch |
| 659 | `false` branch |
| 664 | `true` branch |
| 664 | `false` branch |
| 668 | `true` branch |
| 668 | `false` branch |
| 678 | `true` branch |
| 678 | `false` branch |
| 682 | `true` branch |
| 682 | `false` branch |
| 690 | `true` branch |
| 690 | `false` branch |
| 694 | `true` branch |
| 694 | `false` branch |
| 701 | `true` branch |
| 701 | `false` branch |

### Line 760 — `postponeMasterClientSetup(RealmRepresentation) : boolean`

| Line | Missing coverage |
|---:|---|
| 761 | `false` branch |

### Line 789 — `hasClient(RealmRepresentation,String) : boolean`

| Line | Missing coverage |
|---:|---|
| 790 | `false` branch |
| 808 | `false` branch |

### Line 867 — `setupClientServiceAccountsAndAuthorizationOnImport(RealmRepresentation,boolean) : void`

| Line | Missing coverage |
|---:|---|
| 881 | `true` branch |
| 881 | `false` branch |
| 886 | `true` branch |
| 886 | `false` branch |
| 888 | `true` branch |
| 888 | `false` branch |
| 894 | `true` branch |
| 894 | `false` branch |
