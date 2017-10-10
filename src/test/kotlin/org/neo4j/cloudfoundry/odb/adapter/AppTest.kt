package org.neo4j.cloudfoundry.odb.adapter

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.ExpectedSystemExit
import org.junit.contrib.java.lang.system.SystemErrRule
import org.junit.contrib.java.lang.system.SystemOutRule
import org.neo4j.cloudfoundry.odb.adapter.command.Fixtures
import org.neo4j.cloudfoundry.odb.adapter.command.GenerateManifestCommand
import org.neo4j.cloudfoundry.odb.adapter.command.error.CommandOutput


class AppTest {
    @get:Rule
    val exitStatus = ExpectedSystemExit.none()

    @get:Rule
    val systemOut = SystemOutRule().enableLog().muteForSuccessfulTests()

    @get:Rule
    val systemErr = SystemErrRule().enableLog().muteForSuccessfulTests()

    private val generateManifestCommand = mock<GenerateManifestCommand>()
    private val subject = App(generateManifestCommand)

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
    fun `prints an error when it couldn't generate a manifest`() {
        exitStatus.expectSystemExitWithStatus(42)
        val errorMessage = "Something wrong happened.\nSorry."
        whenever(generateManifestCommand.execute()).thenReturn(CommandOutput.Error(errorMessage))

        subject.execute(arrayOf("generate-manifest", Fixtures.serviceDeploymentJson, Fixtures.planJson, "{}"))

        assertThat(systemErr.log).isEqualTo(errorMessage)
        assertThat(systemOut.log).isEmpty()
    }
}