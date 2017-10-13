package org.neo4j.cloudfoundry.odb.adapter.domain

data class Credentials(val credentials: User)
data class User(val username: String, val password: String)