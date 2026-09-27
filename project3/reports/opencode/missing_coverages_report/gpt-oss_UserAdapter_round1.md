# Coverage Gaps

**File:** `UserAdapter.java`

**Branches:** 57 / 108 covered — 51 missed

## Methods Not Entered

| Line | Method |
|---:|---|
| 96 | `getId() : String` |
| 101 | `getUsername() : String` |
| 112 | `getCreatedTimestamp() : Long` |
| 117 | `setCreatedTimestamp(Long) : void` |
| 122 | `getLastModifiedTimestamp() : Long` |
| 127 | `setLastModifiedTimestamp(Long) : void` |
| 132 | `isEnabled() : boolean` |
| 137 | `setEnabled(boolean) : void` |
| 267 | `getFirstAttribute(String) : String` |
| 301 | `getAttributes() : Map<String, List<String>>` |
| 345 | `getFirstName() : String` |
| 350 | `setFirstName(String) : void` |
| 355 | `getLastName() : String` |
| 360 | `setLastName(String) : void` |
| 365 | `getEmail() : String` |
| 379 | `isEmailVerified() : boolean` |
| 384 | `setEmailVerified(boolean) : void` |
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
| 163 | `true` branch |
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

### Line 370 — `setEmail(String) : void`

| Line | Missing coverage |
|---:|---|
| 372 | `true` branch |

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
