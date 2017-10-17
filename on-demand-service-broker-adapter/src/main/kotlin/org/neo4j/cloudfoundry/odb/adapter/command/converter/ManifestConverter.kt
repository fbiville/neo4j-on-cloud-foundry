package org.neo4j.cloudfoundry.odb.adapter.command.converter

import org.neo4j.cloudfoundry.odb.adapter.ErrorMessages
import org.neo4j.cloudfoundry.odb.adapter.domain.manifest.Manifest
import org.neo4j.cloudfoundry.odb.adapter.serializer.YamlSerializer
import org.yaml.snakeyaml.error.YAMLException
import picocli.CommandLine

class ManifestConverter(private val yamlSerializer: YamlSerializer,
                        private val mandatoryFieldValidator: MandatoryFieldsValidator) : CommandLine.ITypeConverter<Manifest> {

    private val parameterName = "manifest"

    override fun convert(value: String): Manifest {
        try {
            val manifest = yamlSerializer.deserialize(Manifest::class.java, value)
            checkMandatoryFields(parameterName, manifest)
            return manifest
        } catch (e: YAMLException) {
            throw CommandLine.ParameterException(ErrorMessages.deserializationFailed(parameterName), e)
        }
    }

    private fun checkMandatoryFields(name: String, manifest: Any) {
        val missingFields = mandatoryFieldValidator.validate(manifest)
        if (!missingFields.isEmpty()) {
            throw CommandLine.ParameterException(ErrorMessages.missingMandatoryFields(name, missingFields))
        }
    }

}

