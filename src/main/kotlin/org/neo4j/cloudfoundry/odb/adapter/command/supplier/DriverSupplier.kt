package org.neo4j.cloudfoundry.odb.adapter.command.supplier

import org.neo4j.driver.v1.AuthTokens
import org.neo4j.driver.v1.Driver
import org.neo4j.driver.v1.GraphDatabase

class DriverSupplier {
    fun getDriver(boltUri: String, adminPassword: String): Driver {
        return GraphDatabase.driver(
                boltUri,
                AuthTokens.basic("neo4j", adminPassword)
        )
    }
}