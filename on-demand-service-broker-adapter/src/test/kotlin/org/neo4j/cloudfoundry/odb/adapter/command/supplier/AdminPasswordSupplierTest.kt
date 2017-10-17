package org.neo4j.cloudfoundry.odb.adapter.command.supplier

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.neo4j.cloudfoundry.odb.adapter.command.Fixtures

class AdminPasswordSupplierTest {

    val subject = AdminPasswordSupplier()

    @Test
    fun `retrieves the admin password`() {
        val result = subject.getAdminPassword(Fixtures.manifest)

        assertThat(result).isEqualTo("pff")
    }

    @Test
    fun `retrieves null if no admin password is set`() {
        val result = subject.getAdminPassword(Fixtures.manifest.copy(properties = null))

        assertThat(result).isNull()
    }
}