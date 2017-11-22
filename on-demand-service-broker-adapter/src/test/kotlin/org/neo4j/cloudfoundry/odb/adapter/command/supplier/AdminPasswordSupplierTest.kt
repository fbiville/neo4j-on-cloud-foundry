package org.neo4j.cloudfoundry.odb.adapter.command.supplier

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.neo4j.cloudfoundry.odb.adapter.command.Fixtures

class AdminPasswordSupplierTest {

    val subject = AdminPasswordSupplier()

    @Test
    fun `retrieves the admin password`() {
        val result = subject.getAdminPassword(Fixtures.manifest)

        assertThat(result).isEqualTo("cop1-2-soop")
    }



    @Test
    fun `retrieves null if no admin password is set`() {
        val job = Fixtures.manifestJob.copy(properties = null)
        val instanceGroup = Fixtures.manifestInstanceGroup.copy(jobs = arrayOf(job))
        val result = subject.getAdminPassword(Fixtures.manifest.copy(instance_groups = arrayOf(instanceGroup)))

        assertThat(result).isNull()
    }
}