# Coverage Gaps

**File:** `UserAdapter.java`

**Branches:** 59 / 108 covered — 49 missed

## Methods Not Entered

| Line | Method |
|---:|---|
| 267 | `getFirstAttribute(String) : String` |
| 301 | `getAttributes() : Map<String, List<String>>` |
| 446 | `getGroupsCountByNameContaining(String) : long` |
| 452 | `joinGroup(GroupModel) : void` |
| 457 | `hasDirectGroup(GroupModel) : boolean` |
| 469 | `joinGroup(GroupModel,MembershipMetadata) : void` |
| 475 | `joinGroupImpl(GroupModel) : void` |
| 479 | `joinGroupImpl(GroupModel,MembershipMetadata) : void` |
| 524 | `hasRole(RoleModel) : boolean` |
| 603 | `getFederationLink() : String` |
| 608 | `setFederationLink(String) : void` |
| 613 | `getServiceAccountClientLink() : String` |
| 618 | `setServiceAccountClientLink(String) : void` |
| 623 | `credentialManager() : SubjectCredentialManager` |
| 628 | `equals(Object) : boolean` |
| 637 | `hashCode() : int` |

## Partially Covered Methods

### Line 142 — `setSingleAttribute(String,String) : void`

| Line | Missing coverage |
|---:|---|
| 169 | `false` branch |

### Line 195 — `setAttribute(String,List<String>) : void`

| Line | Missing coverage |
|---:|---|
| 198 | `true` branch |
| 201 | `true` branch |
| 204 | `true` branch |
| 207 | `true` branch |
| 213 | `true` branch |
| 220 | `false` branch |

### Line 237 — `removeAttribute(String) : void`

| Line | Missing coverage |
|---:|---|
| 241 | `false` branch |
| 248 | `true` branch |
| 250 | `true` branch |
| 252 | `true` branch |

### Line 286 — `getAttributeStream(String) : Stream<String>`

| Line | Missing coverage |
|---:|---|
| 288 | `true` branch |
| 290 | `true` branch |
| 292 | `true` branch |
| 294 | `true` branch |

### Line 319 — `addRequiredAction(String) : void`

| Line | Missing coverage |
|---:|---|
| 322 | `false` branch |

### Line 333 — `removeRequiredAction(String) : void`

| Line | Missing coverage |
|---:|---|
| 338 | `false` branch |

### Line 433 — `getGroupsCount() : long`

| Line | Missing coverage |
|---:|---|
| 436 | `true` branch |
| 437 | `true` branch |
| 437 | `false` branch |

### Line 493 — `leaveGroup(GroupModel) : void`

| Line | Missing coverage |
|---:|---|
| 495 | `true` branch |
| 505 | `true` branch |

### Line 557 — `grantRoleImpl(RoleModel) : void`

| Line | Missing coverage |
|---:|---|
| 562 | `false` branch |

### Line 583 — `deleteRoleMapping(RoleModel) : void`

| Line | Missing coverage |
|---:|---|
| 585 | `true` branch |
| 590 | `true` branch |
