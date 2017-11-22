package org.neo4j.cloudfoundry.odb.adapter.command

import com.google.gson.Gson
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.neo4j.cloudfoundry.odb.adapter.command.error.CommandOutput
import org.neo4j.cloudfoundry.odb.adapter.command.persistence.CredentialsRepository
import org.neo4j.cloudfoundry.odb.adapter.command.persistence.PersistenceError
import org.neo4j.cloudfoundry.odb.adapter.command.supplier.AdminPasswordSupplier
import org.neo4j.cloudfoundry.odb.adapter.command.supplier.BoltUriSupplier
import org.neo4j.cloudfoundry.odb.adapter.command.supplier.DriverSupplier
import org.neo4j.cloudfoundry.odb.adapter.domain.BoshVms
import org.neo4j.cloudfoundry.odb.adapter.domain.Credentials
import org.neo4j.cloudfoundry.odb.adapter.domain.User
import org.neo4j.driver.v1.Driver
import org.neo4j.driver.v1.exceptions.ServiceUnavailableException
import org.neo4j.harness.junit.Neo4jRule
import org.slf4j.bridge.SLF4JBridgeHandler

class CreateBindingCommandTest {

    @get:Rule
    val neo4jRule = Neo4jRule()
            .withConfig("dbms.connector.bolt.listen_address", "0.0.0.0:7687")
            .withConfig("dbms.connector.bolt.advertised_address", "127.0.0.1:7687")

    private val gson = Gson()
    private val userRepository = mock<CredentialsRepository>()
    private val adminPasswordSupplier = mock<AdminPasswordSupplier>()
    private val boltUriSupplier = mock<BoltUriSupplier>()
    private val driverSupplier = mock<DriverSupplier>()
    private val subject = CreateBindingCommand(userRepository, gson, driverSupplier, boltUriSupplier, adminPasswordSupplier)

    companion object {
        @JvmStatic
        @BeforeClass
        fun prepareAll() {
            SLF4JBridgeHandler.removeHandlersForRootLogger()
        }
    }

    @Before
    fun prepare() {
        val boltURI = neo4jRule.boltURI()
        subject.boshVms = BoshVms(arrayOf(boltURI.host))
        subject.bindingId = "awesome-binding-id"
        subject.manifest = Fixtures.manifest
        whenever(boltUriSupplier.getBoltUri(any())).thenReturn("bolt-wonderful-uri")
        whenever(adminPasswordSupplier.getAdminPassword(any())).thenReturn("password")
        val driver = mock<Driver>()
        whenever(driverSupplier.getDriver("bolt-wonderful-uri", "password")).thenReturn(driver)
    }

    @Test
    fun `fails if no bosh VMs are provided`() {
        whenever(boltUriSupplier.getBoltUri(any())).thenReturn(null)

        val result = subject.execute() as CommandOutput.Error

        assertThat(result.errorStatus).isEqualTo(199)
        assertThat(result.errorMessage).isEqualTo("No VM IP provided for binding 'awesome-binding-id'")
    }

    @Test
    fun `fails if the Neo4j node is unreachable`() {
        whenever(driverSupplier.getDriver(any(), any())).thenThrow(ServiceUnavailableException::class.java)

        val result = subject.execute() as CommandOutput.Error

        assertThat(result.errorStatus).isEqualTo(79)
        assertThat(result.errorMessage).startsWith("Could not reach VM for binding 'awesome-binding-id'.")
    }

    @Test
    fun `fails if the Neo4j admin password is not provided`() {
        whenever(adminPasswordSupplier.getAdminPassword(any())).thenReturn(null)
        subject.manifest = Fixtures.manifest.copy(properties = mapOf())
        val result = subject.execute() as CommandOutput.Error

        assertThat(result.errorStatus).isEqualTo(299)
        assertThat(result.errorMessage).isEqualTo("No Neo4j password provided for binding 'awesome-binding-id'")
    }

    @Test
    fun `returns an error if the binding already exists`() {
        whenever(userRepository.exists(any(), eq("awesome-binding-id"))).thenReturn(true)

        val result = subject.execute() as CommandOutput.Error

        assertThat(result.errorStatus).isEqualTo(49)
        assertThat(result.errorMessage).isEqualTo("Binding 'awesome-binding-id' already exists")
    }

    @Test
    fun `returns an error if the user existence check fails`() {
        val persistenceError = PersistenceError("oopsie", RuntimeException("something bad happened"))
        whenever(userRepository.exists(any(), eq("awesome-binding-id"))).thenThrow(persistenceError)

        val result = subject.execute() as CommandOutput.Error

        assertThat(result.errorStatus).isEqualTo(99)
        assertThat(result.errorMessage).isEqualTo("User creation for binding 'awesome-binding-id' failed.\n" +
                "Details: oopsie\n" +
                "Caused by: something bad happened")
    }

    @Test
    fun `returns an error if the user insertion fails`() {
        whenever(userRepository.exists(any(), eq("awesome-binding-id"))).thenReturn(false)
        val persistenceError = PersistenceError("oopsie", RuntimeException("something bad happened"))
        whenever(userRepository.save(any(), eq("awesome-binding-id"))).thenThrow(persistenceError)

        val result = subject.execute() as CommandOutput.Error

        assertThat(result.errorStatus).isEqualTo(99)
        assertThat(result.errorMessage).isEqualTo("User creation for binding 'awesome-binding-id' failed.\n" +
                "Details: oopsie\n" +
                "Caused by: something bad happened")
    }

    @Test
    fun `returns a User if the user insertion succeeds`() {
        whenever(userRepository.exists(any(), eq("awesome-binding-id"))).thenReturn(false)
        whenever(userRepository.save(any(), eq("awesome-binding-id"))).thenReturn(Credentials(User("username", "password-impossible-to-decipher")))

        val result = subject.execute() as CommandOutput.Standard

        assertThat(result.content).isEqualToIgnoringWhitespace(Fixtures.credentialsJson)
    }
}
