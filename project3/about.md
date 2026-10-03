## The Hard Project
This is  keycloak, an open source identity and access management which
users authenticate with Keycloak rather than individual applications. This means that your
applications don't have to deal with login forms, authenticating users, and storing users.
Keycloak provides user federation, strong authentication, user management, fine-grained authorization, and more.  
Repository link: https://github.com/keycloak/keycloak

This project has 26 top-level Maven modules declared directly in the root `pom.xml`.
At the time this is being written, there are 32,415 commits.

This is a very large project. I won't generate test for all of it.
Instead, I have chosen 10 files that have about 10,000 LOC.
These files have many dependencies. I am allowed to use mocking here.
Many mocks were needed to unit test for this project.

## Files to be tested
(LOC, Statements, Decisions)  
`model\jpa\src\main\java\org\keycloak\models\jpa\UserAdapter.java`
(518, 1250, 138)  
`model\jpa\src\main\java\org\keycloak\models\jpa\JpaUserProvider.java`
(1142, 3726, 208)  
`server-spi-private\src\main\java\org\keycloak\models\utils\RepresentationToModel.java`
(1459, 5265, 664)  
`server-spi-private\src\main\java\org\keycloak\models\utils\ModelToRepresentation.java`
(1304, 3943, 230)  
`server-spi-private\src\main\java\org\keycloak\models\utils\KeycloakModelUtils.java`
(918, 2116, 308)  
`services\src\main\java\org\keycloak\protocol\oidc\TokenManager.java`
(1325, 3681, 437)  
`services\src\main\java\org\keycloak\protocol\oidc\OIDCLoginProtocol.java`
(460, 1026, 99)  
`services\src\main\java\org\keycloak\protocol\oidc\endpoints\AuthorizationEndpoint.java`
(358, 2212, 176)  
`services\src\main\java\org\keycloak\authentication\AuthenticationProcessor.java`
(1060, 2824, 206)  
`services\src\main\java\org\keycloak\services\managers\RealmManager.java`
(720, 1941, 220)  

In total: (9264, 27984, 2686)

LOC is calculated with a script that counts lines with a java statement.  
Statements and decisions are given by Jacoco.

To understand how complex this project is, Gson's `pom.xml` contained three
dependency tags, while keycloak has 175 in its main "pom.xml".

`project3\run` contains a minimal repository with the files above and
all the needed dependencies to run tests on keycloak.