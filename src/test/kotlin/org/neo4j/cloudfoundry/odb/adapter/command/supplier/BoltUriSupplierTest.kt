package org.neo4j.cloudfoundry.odb.adapter.command.supplier

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.neo4j.cloudfoundry.odb.adapter.command.Fixtures
import org.neo4j.cloudfoundry.odb.adapter.domain.BoshVms

class BoltUriSupplierTest {

    val subject = BoltUriSupplier()

    @Test
    fun `creates Bolt URI`() {
        val result = subject.getBoltUri(Fixtures.boshVms)

        assertThat(result).isEqualTo("bolt://192.0.2.1:7687")
    }

    @Test
    fun `returns null Bolt URI if no VMs are provided`() {
        val result = subject.getBoltUri(BoshVms(arrayOf()))

        assertThat(result).isNull()
    }
}