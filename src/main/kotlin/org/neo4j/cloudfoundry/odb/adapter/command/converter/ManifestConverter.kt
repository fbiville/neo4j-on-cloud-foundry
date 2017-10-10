package org.neo4j.cloudfoundry.odb.adapter.command.converter

import org.neo4j.cloudfoundry.odb.adapter.App
import org.neo4j.cloudfoundry.odb.adapter.domain.manifest.Manifest
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import org.yaml.snakeyaml.error.YAMLException
import picocli.CommandLine

class ManifestConverter : CommandLine.ITypeConverter<Manifest> {
    private val yamlDeserializer = Yaml(Constructor(Manifest::class.java))

    private val mandatoryFieldValidator: MandatoryFieldsValidator
    constructor() : this(MandatoryFieldsValidator())

    internal constructor(mandatoryFieldsValidator: MandatoryFieldsValidator) {
        this.mandatoryFieldValidator = mandatoryFieldsValidator
    }

    override fun convert(value: String): Manifest {
        try {
            val manifest = yamlDeserializer.load(value)
            checkMandatoryFields("manifest", manifest)
            return manifest as Manifest
        } catch (e: YAMLException) {
            throw CommandLine.ParameterException(App.deserializationError("manifest"), e)
        }
    }

    private fun checkMandatoryFields(name: String, manifest: Any) {
        val missingFields = mandatoryFieldValidator.validate(manifest)
        if (!missingFields.isEmpty()) {
            throw CommandLine.ParameterException(App.missingMandatoryFieldsError(name, missingFields))
        }
    }

}

