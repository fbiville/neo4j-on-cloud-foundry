package org.neo4j.cloudfoundry.odb.adapter.command.error

interface ManifestCommandError {
    fun getDescription(): String
}

data class ReleaseNotFound(val jobName: String) : ManifestCommandError {
    override fun getDescription(): String {
        return """
            Could not find release for job name $jobName
        """.trimIndent()
    }
}

data class JobNotFound(val instanceGroupName: String) : ManifestCommandError {
    override fun getDescription(): String {
        return """
            Could not find job for instance group name $instanceGroupName
        """.trimIndent()
    }
}

object MigrationNotSupported : ManifestCommandError {
    override fun getDescription(): String {
        return "ServiceMigration is not supported yet"
    }
}
