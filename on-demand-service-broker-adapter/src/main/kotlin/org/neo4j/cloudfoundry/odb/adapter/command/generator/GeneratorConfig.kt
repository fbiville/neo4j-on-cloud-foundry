package org.neo4j.cloudfoundry.odb.adapter.command.generator

class GeneratorConfig {
    companion object {
        val jobsByInstanceGroupName: Map<String, Array<String>> = mapOf(
                "neo4j" to arrayOf("neo4j")
        )
        val singleStemcellName: String = "default"
    }
}
