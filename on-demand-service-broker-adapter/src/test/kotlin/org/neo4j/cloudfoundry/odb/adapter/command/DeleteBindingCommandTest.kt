package org.neo4j.cloudfoundry.odb.adapter.command

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.neo4j.cloudfoundry.odb.adapter.command.error.CommandOutput
import org.neo4j.cloudfoundry.odb.adapter.command.persistence.CredentialsRepository
import org.neo4j.cloudfoundry.odb.adapter.command.persistence.PersistenceError
import org.neo4j.cloudfoundry.odb.adapter.command.supplier.AdminPasswordSupplier
import org.neo4j.cloudfoundry.odb.adapter.command.supplier.BoltUriSupplier
import org.neo4j.cloudfoundry.odb.adapter.command.supplier.DriverSupplier
import org.neo4j.cloudfoundry.odb.adapter.domain.BoshVms
import org.neo4j.driver.v1.Driver
import org.neo4j.driver.v1.exceptions.ServiceUnavailableException

class DeleteBindingCommandTest {

    private val credentialsRepository = mock<CredentialsRepository>()
    private val driverSupplier = mock<DriverSupplier>()
    private val boltUriSupplier = mock<BoltUriSupplier>()
    private val adminPasswordSupplier = mock<AdminPasswordSupplier>()

    private val subject = DeleteBindingCommand(credentialsRepository, driverSupplier, boltUriSupplier, adminPasswordSupplier)

    @Before
    fun prepare() {
        subject.boshVms = BoshVms(arrayOf("127.0.0.1"))
        subject.manifest = Fixtures.manifest
        subject.bindingId = "awesome-binding-id"
        whenever(boltUriSupplier.getBoltUri(any())).thenReturn("127.0.0.1")
        val driver = mock<Driver>()
        whenever(driverSupplier.getDriver(any(), any())).thenReturn(driver)
        whenever(adminPasswordSupplier.getAdminPassword(any())).thenReturn("sup3r-s3cr3t")
    }

    @Test
    fun `fails when the bolt URI cannot be retrieved`() {
        whenever(boltUriSupplier.getBoltUri(any())).thenReturn(null)

        val result = subject.execute() as CommandOutput.Error

        assertThat(result.errorStatus).isEqualTo(199)
        assertThat(result.errorMessage).isEqualTo("No VM IP provided for binding 'awesome-binding-id'")
    }

    @Test
    fun `fails when the admin password is not provided`() {
        whenever(adminPasswordSupplier.getAdminPassword(any())).thenReturn(null)

        val result = subject.execute() as CommandOutput.Error

        assertThat(result.errorStatus).isEqualTo(299)
        assertThat(result.errorMessage).isEqualTo("No Neo4j password provided for binding 'awesome-binding-id'")
    }

    @Test
    fun `fails to delete non-existing binding`() {
        whenever(credentialsRepository.exists(any(), eq("awesome-binding-id"))).thenReturn(false)

        val result = subject.execute() as CommandOutput.Error

        assertThat(result.errorStatus).isEqualTo(41)
        assertThat(result.errorMessage).isEqualTo("Binding 'awesome-binding-id' does not exist")
    }

    @Test
    fun `fails deleting if exists call fails`() {
        val error = PersistenceError("oopsie", RuntimeException("omagad something bad"))
        whenever(credentialsRepository.exists(any(), eq("awesome-binding-id"))).thenThrow(error)

        val result = subject.execute() as CommandOutput.Error

        assertThat(result.errorStatus).isEqualTo(99)
        assertThat(result.errorMessage).isEqualTo("User deletion for binding 'awesome-binding-id' failed.\n" +
                "Details: oopsie\n" +
                "Caused by: omagad something bad")
    }

    @Test
    fun `fails deleting if remove call fails`() {
        val error = PersistenceError("oopsie", RuntimeException("omagad something bad"))
        whenever(credentialsRepository.exists(any(), eq("awesome-binding-id"))).thenReturn(true)
        whenever(credentialsRepository.remove(any(), eq("awesome-binding-id"))).thenThrow(error)

        val result = subject.execute() as CommandOutput.Error

        assertThat(result.errorStatus).isEqualTo(99)
        assertThat(result.errorMessage).isEqualTo("User deletion for binding 'awesome-binding-id' failed.\n" +
                "Details: oopsie\n" +
                "Caused by: omagad something bad")
    }

    @Test
    fun `fails deleting if database is unreachable during exists call`() {
        val error = ServiceUnavailableException("oopsie", RuntimeException("omagad something bad"))
        whenever(credentialsRepository.exists(any(), eq("awesome-binding-id"))).thenThrow(error)

        val result = subject.execute() as CommandOutput.Error

        assertThat(result.errorStatus).isEqualTo(79)
        assertThat(result.errorMessage).startsWith("Could not reach VM for binding 'awesome-binding-id'.")
    }

    @Test
    fun `fails deleting if database is unreachable during remove call`() {
        val error = ServiceUnavailableException("oopsie", RuntimeException("omagad something bad"))
        whenever(credentialsRepository.exists(any(), eq("awesome-binding-id"))).thenReturn(true)
        whenever(credentialsRepository.remove(any(), eq("awesome-binding-id"))).thenThrow(error)

        val result = subject.execute() as CommandOutput.Error

        assertThat(result.errorStatus).isEqualTo(79)
        assertThat(result.errorMessage).startsWith("Could not reach VM for binding 'awesome-binding-id'.")
    }

    @Test
    fun `deletes the existing binding`() {
        whenever(credentialsRepository.exists(any(), eq("awesome-binding-id"))).thenReturn(true)

        val result = subject.execute() as CommandOutput.Standard

        assertThat(result.content).isEmpty()
        verify(credentialsRepository).remove(any(), eq("awesome-binding-id"))
    }
}
