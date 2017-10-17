package org.neo4j.cloudfoundry.odb.adapter

import com.google.gson.Gson
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.ExpectedSystemExit
import org.junit.contrib.java.lang.system.SystemErrRule
import org.junit.contrib.java.lang.system.SystemOutRule
import org.neo4j.cloudfoundry.odb.adapter.command.CreateBindingCommand
import org.neo4j.cloudfoundry.odb.adapter.command.DashboardUrlCommand
import org.neo4j.cloudfoundry.odb.adapter.command.DeleteBindingCommand
import org.neo4j.cloudfoundry.odb.adapter.command.Fixtures
import org.neo4j.cloudfoundry.odb.adapter.command.GenerateManifestCommand
import org.neo4j.cloudfoundry.odb.adapter.command.error.CommandOutput
import org.neo4j.cloudfoundry.odb.adapter.serializer.YamlSerializer


class AppTest {
    @get:Rule
    val exitStatus = ExpectedSystemExit.none()

    @get:Rule
    val systemOut = SystemOutRule().enableLog().muteForSuccessfulTests()

    @get:Rule
    val systemErr = SystemErrRule().enableLog().muteForSuccessfulTests()

    private val generateManifestCommand = mock<GenerateManifestCommand>()
    private val dashboardUrlCommand = mock<DashboardUrlCommand>()
    private val createBindingCommand = mock<CreateBindingCommand>()
    private val deleteBindingCommand = mock<DeleteBindingCommand>()
    private val subject = App(generateManifestCommand,
                              dashboardUrlCommand,
                              createBindingCommand,
                              deleteBindingCommand,
                              Gson(),
                              YamlSerializer())

    @Test
    fun `generates a manifest`() {
        exitStatus.expectSystemExitWithStatus(0)
        whenever(generateManifestCommand.execute()).thenReturn(CommandOutput.Standard(Fixtures.manifestYaml))

        subject.execute(arrayOf("generate-manifest", Fixtures.serviceDeploymentJson, Fixtures.planJson, "{}"))

        assertThat(systemOut.log)
                .isEqualTo(Fixtures.manifestYaml)
        assertThat(systemErr.log).isEmpty()
    }

    @Test
    fun `prints an error when the command outputs an error`() {
        exitStatus.expectSystemExitWithStatus(42)
        val errorMessage = "Something wrong happened.\nSorry."
        whenever(generateManifestCommand.execute()).thenReturn(CommandOutput.Error(42, errorMessage))

        subject.execute(arrayOf("generate-manifest", Fixtures.serviceDeploymentJson, Fixtures.planJson, "{}"))

        assertThat(systemErr.log).isEqualTo(errorMessage)
        assertThat(systemOut.log).isEmpty()
    }

    @Test
    fun `prints an error when the command is not supported`() {
        exitStatus.expectSystemExitWithStatus(10)
        val warningMessage = "Not supported\nSorry."

        whenever(dashboardUrlCommand.execute()).thenReturn(CommandOutput.Unsupported(warningMessage))

        subject.execute(arrayOf("dashboard-url", "some-uid", Fixtures.planJson, Fixtures.manifestYaml))

        assertThat(systemErr.log).isEqualTo(warningMessage)
        assertThat(systemOut.log).isEmpty()
    }

    @Test
    fun `creates a binding`() {
        exitStatus.expectSystemExitWithStatus(0)
        whenever(createBindingCommand.execute())
                .thenReturn(CommandOutput.Standard(Fixtures.bindingJson))

        subject.execute(arrayOf("create-binding", "awesome-binding-id", Fixtures.boshVmJson, Fixtures.manifestYaml, "{}"))

        assertThat(systemOut.log).isEqualTo(Fixtures.bindingJson)
        assertThat(systemErr.log).isEmpty()
    }

    @Test
    fun `deletes an existing binding`() {
        exitStatus.expectSystemExitWithStatus(0)
        whenever(deleteBindingCommand.execute())
                .thenReturn(CommandOutput.Standard(""))

        subject.execute(arrayOf("remove-binding", "awesome-binding-id", Fixtures.boshVmJson, Fixtures.manifestYaml, "{}"))

        assertThat(systemOut.log).isEmpty()
        assertThat(systemErr.log).isEmpty()
    }
}

