package org.neo4j.cloudfoundry.odb.adapter.command.persistence

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.neo4j.cloudfoundry.odb.adapter.command.generator.PasswordGenerator
import org.neo4j.cloudfoundry.odb.adapter.domain.Credentials
import org.neo4j.cloudfoundry.odb.adapter.domain.User
import org.neo4j.driver.v1.AccessMode
import org.neo4j.driver.v1.Driver
import org.neo4j.driver.v1.Record
import org.neo4j.driver.v1.Session
import org.neo4j.driver.v1.StatementResult

class CredentialsRepositoryTest {

    private val driver = mock<Driver>()
    private val session = mock<Session>()
    private val statementResult = mock<StatementResult>()
    private val passwordGenerator = mock<PasswordGenerator>()
    private val subject = CredentialsRepository(passwordGenerator)

    @Before
    fun prepare() {
        whenever(driver.session(any<AccessMode>())).thenReturn(session)
        whenever(session.run("CALL dbms.security.listUsers() YIELD username RETURN username")).thenReturn(statementResult)
        whenever(session.run(eq("CALL dbms.security.createUser({username}, {password}, false)"), any<Map<String, Any>>())).thenReturn(statementResult)
        whenever(statementResult.list()).thenReturn(listOf())
    }

    @Test
    fun `exists returns false when the provided user doesn't exist`() {
        assertThat(subject.exists(driver, "binding-id")).isFalse()
    }

    @Test
    fun `exists returns true when the provided user already exists`() {
        whenever(statementResult.list(any<org.neo4j.driver.v1.util.Function<Record, String>>())).thenReturn(listOf("binding-id"))
        assertThat(subject.exists(driver, "binding-id")).isTrue()
    }

    @Test
    fun `saves the users from their sins`() {
        whenever(passwordGenerator.generate()).thenReturn("sup3r-s3cr3t")

        val credentials = subject.save(driver, "binding-id")

        assertThat(credentials).isEqualTo(Credentials(User("binding-id", "sup3r-s3cr3t")))
        verify(session).run("CALL dbms.security.createUser({username}, {password}, false)", mapOf(
                Pair("username", "binding-id"),
                Pair("password", "sup3r-s3cr3t")
        ))
    }

    @Test
    fun `removes existing user`() {
        subject.remove(driver, "binding-id")

        verify(session).run("CALL dbms.security.deleteUser({username})", mapOf(Pair("username", "binding-id")))
    }
}
