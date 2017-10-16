package org.neo4j.cloudfoundry.odb.adapter.command.supplier

import org.assertj.core.api.Assertions.assertThat
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.neo4j.harness.junit.Neo4jRule
import org.slf4j.bridge.SLF4JBridgeHandler

class DriverSupplierTest {

    @get:Rule
    val neo4jRule = Neo4jRule()

    companion object {
        @JvmStatic
        @BeforeClass
        fun prepareAll() {
            SLF4JBridgeHandler.removeHandlersForRootLogger()
        }
    }

    @Test
    fun `retrieves a driver`() {
        assertThat(DriverSupplier().getDriver(neo4jRule.boltURI().toString(), "wow"))
                .overridingErrorMessage("Expected a non null driver")
                .isNotNull()
    }
}