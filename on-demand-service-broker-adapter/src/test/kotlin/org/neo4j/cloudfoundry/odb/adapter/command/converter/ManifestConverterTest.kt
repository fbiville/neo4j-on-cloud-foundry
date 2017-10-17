package org.neo4j.cloudfoundry.odb.adapter.command.converter

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.Test
import org.neo4j.cloudfoundry.odb.adapter.command.Fixtures
import org.neo4j.cloudfoundry.odb.adapter.domain.manifest.Manifest
import org.neo4j.cloudfoundry.odb.adapter.serializer.YamlSerializer
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.ConstructorException
import picocli.CommandLine.ParameterException

class ManifestConverterTest {

    private val subject = ManifestConverter(YamlSerializer(), MandatoryFieldsValidator())

    @Test
    fun `fails when the Manifest payload has unknown properties`() {
        assertThatExceptionOfType(ParameterException::class.java)
                .isThrownBy { subject.convert("""nope: haha""") }
                .withMessage("Parameter 'manifest' cannot be deserialized")
                .withCauseInstanceOf(ConstructorException::class.java)
    }

    @Test
    fun `fails when the Manifest is incomplete`() {
        val manifest = """
            |---
            |name: neo4j
            """.trimMargin()
        val mandatoryFieldsValidator = mock<MandatoryFieldsValidator>()
        whenever(mandatoryFieldsValidator.validate(any<Manifest>(), eq(""))).thenReturn(listOf("param1", "param2", "param3.nested"))

        val subject = ManifestConverter(YamlSerializer(), mandatoryFieldsValidator)

        assertThatExceptionOfType(ParameterException::class.java)
                .isThrownBy { subject.convert(manifest) }
                .withMessage("Parameter 'manifest' is missing mandatory parameters: param1, param2, param3.nested")
    }

    @Test
    fun `converts a valid Manifest payload`() {
        val result = subject.convert(Fixtures.manifestYaml)
        assertThat(result).isEqualTo(Fixtures.manifest)
    }

}

