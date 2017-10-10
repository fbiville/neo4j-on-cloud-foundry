package org.neo4j.cloudfoundry.odb.adapter.command.converter

import com.google.gson.JsonSyntaxException
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions
import org.junit.Test
import org.neo4j.cloudfoundry.odb.adapter.command.Fixtures
import org.neo4j.cloudfoundry.odb.adapter.domain.servicedeployment.ServiceDeployment
import picocli.CommandLine

class ServiceDeploymentConverterTest {

    @Test
    fun `fails when the Service Deployment payload is not valid`() {
        val subject = ServiceDeploymentConverter()

        Assertions.assertThatExceptionOfType(CommandLine.ParameterException::class.java)
                .isThrownBy { subject.convert("""\salut\""") }
                .withMessage("Parameter 'service-deployment' cannot be deserialized")
                .withCauseInstanceOf(JsonSyntaxException::class.java)
    }

    @Test
    fun `fails when the Service Deployment payload is incomplete`() {
        val mandatoryFieldsValidator = mock<MandatoryFieldsValidator>()
        val subject = ServiceDeploymentConverter(mandatoryFieldsValidator)
        whenever(mandatoryFieldsValidator.validate(any<ServiceDeployment>(), eq(""))).thenReturn(listOf("truc", "machin"))

        Assertions.assertThatExceptionOfType(CommandLine.ParameterException::class.java)
                .isThrownBy { subject.convert("{}") }
                .withMessage("Parameter 'service-deployment' is missing mandatory parameters: truc, machin")
    }

    @Test
    fun `converts a valid Service Deployment payload`() {
        val subject = ServiceDeploymentConverter(MandatoryFieldsValidator())

        Assertions.assertThat(subject.convert(Fixtures.serviceDeploymentJson)).isEqualTo(Fixtures.serviceDeployment)
    }
}

