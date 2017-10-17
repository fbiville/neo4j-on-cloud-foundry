package org.neo4j.cloudfoundry.odb.adapter.command.persistence

import org.neo4j.cloudfoundry.odb.adapter.command.generator.PasswordGenerator
import org.neo4j.cloudfoundry.odb.adapter.domain.Credentials
import org.neo4j.cloudfoundry.odb.adapter.domain.User
import org.neo4j.driver.v1.AccessMode
import org.neo4j.driver.v1.Driver

class CredentialsRepository(private val passwordGenerator: PasswordGenerator) {

    @Throws(PersistenceError::class)
    fun exists(driver: Driver, bindingId: String): Boolean {
        driver.session(AccessMode.READ).use {
            return it.run("CALL dbms.security.listUsers() YIELD username RETURN username")
                    .list { it.get("username").asString() }
                    .contains(bindingId)
        }
    }

    @Throws(PersistenceError::class)
    fun save(driver: Driver, userId: String): Credentials {
        val password = passwordGenerator.generate()
        driver.session(AccessMode.WRITE).use {
            it.run("CALL dbms.security.createUser({username}, {password}, false) " +
                    "CALL dbms.security.addRoleToUser({rolename}, {username}) " +
                    "RETURN true",
                    mapOf(Pair("username", userId),
                            Pair("password", password),
                            Pair("rolename", "architect")))
            return Credentials(User(userId, password))
        }
    }

    @Throws(PersistenceError::class)
    fun remove(driver: Driver, userId: String) {
        driver.session(AccessMode.WRITE).use {
            it.run("CALL dbms.security.deleteUser({username})",
                    mapOf(Pair("username", userId)))
        }
    }
}

