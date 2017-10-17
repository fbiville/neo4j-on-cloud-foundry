package org.neo4j.cloudfoundry.odb.adapter.command.converter

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.Test
import org.neo4j.cloudfoundry.odb.adapter.command.Fixtures
import org.neo4j.cloudfoundry.odb.adapter.domain.servicedeployment.ServiceDeployment
import picocli.CommandLine

class ServiceDeploymentConverterTest {

    private val subject = ServiceDeploymentConverter(Gson(), MandatoryFieldsValidator())

    @Test
    fun `fails when the Service Deployment payload is not valid`() {
        assertThatExceptionOfType(CommandLine.ParameterException::class.java)
                .isThrownBy { subject.convert("""\salut\""") }
                .withMessage("Parameter 'service-deployment' cannot be deserialized")
                .withCauseInstanceOf(JsonSyntaxException::class.java)
    }

    @Test
    fun `fails when the Service Deployment payload is incomplete`() {
        val mandatoryFieldsValidator = mock<MandatoryFieldsValidator>()
        val subject = ServiceDeploymentConverter(Gson(), mandatoryFieldsValidator)
        whenever(mandatoryFieldsValidator.validate(any<ServiceDeployment>(), eq(""))).thenReturn(listOf("truc", "machin"))

        assertThatExceptionOfType(CommandLine.ParameterException::class.java)
                .isThrownBy { subject.convert("{}") }
                .withMessage("Parameter 'service-deployment' is missing mandatory parameters: truc, machin")
    }

    @Test
    fun `converts a valid Service Deployment payload`() {
        assertThat(subject.convert(Fixtures.serviceDeploymentJson)).isEqualTo(Fixtures.serviceDeployment)
    }
}

