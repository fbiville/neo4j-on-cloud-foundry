package org.neo4j.cloudfoundry.odb.adapter.command

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.neo4j.cloudfoundry.odb.adapter.command.error.CommandOutput

class DashboardUrlCommandTest {
    @Test
    fun `command is not supported`() {
        val expectedMessage = "Command is not supported"
        assertThat(DashboardUrlCommand().execute()).isEqualTo(CommandOutput.Unsupported(expectedMessage))
    }
}