package org.neo4j.cloudfoundry.odb.adapter.command

import org.neo4j.cloudfoundry.odb.adapter.domain.manifest.Manifest
import org.neo4j.cloudfoundry.odb.adapter.domain.manifest.ManifestInstanceGroup
import org.neo4j.cloudfoundry.odb.adapter.domain.manifest.ManifestJob
import org.neo4j.cloudfoundry.odb.adapter.domain.manifest.ManifestNetwork
import org.neo4j.cloudfoundry.odb.adapter.domain.manifest.ManifestProperties
import org.neo4j.cloudfoundry.odb.adapter.domain.manifest.ManifestRelease
import org.neo4j.cloudfoundry.odb.adapter.domain.manifest.ManifestStemcell
import org.neo4j.cloudfoundry.odb.adapter.domain.plan.Plan
import org.neo4j.cloudfoundry.odb.adapter.domain.plan.PlanInstanceGroup
import org.neo4j.cloudfoundry.odb.adapter.domain.servicedeployment.ServiceDeployment
import org.neo4j.cloudfoundry.odb.adapter.domain.servicedeployment.ServiceRelease
import org.neo4j.cloudfoundry.odb.adapter.domain.servicedeployment.ServiceStemcell
import org.neo4j.cloudfoundry.odb.adapter.domain.update.Update

class Fixtures {
    companion object {
        val manifestInstanceGroup = ManifestInstanceGroup(
                name = "neo4j",
                azs = arrayOf("z1", "z2", "z3"),
                instances = 1,
                jobs = arrayOf(ManifestJob("neo4j", "neo4j", properties = mapOf())),
                vm_type = "default",
                stemcell = "default",
                persistent_disk_type = "10240",
                networks = arrayOf(ManifestNetwork("default")))
        val manifestStemcell = ManifestStemcell("default", "ubuntu-trusty", "latest")
        val manifestRelease = ManifestRelease("neo4j", "latest")
        val update = Update(
                canaries = 2,
                max_in_flight = 1,
                canary_watch_time = "5000-60000",
                update_watch_time = "5000-60000"
        )
        val manifest = Manifest(
                name = "neo4j",
                releases = arrayOf(manifestRelease),
                stemcells = arrayOf(manifestStemcell),
                update = update,
                instance_groups = arrayOf(manifestInstanceGroup),
                properties = ManifestProperties("pff"))
        val release = ServiceRelease(
                name = "neo4j",
                version = "latest",
                jobs = arrayOf("neo4j")
        )
        val stemcell = ServiceStemcell("ubuntu-trusty", "3445.11")
        val serviceDeployment = ServiceDeployment(
                deployment_name = "service-instance_1b42a2ea-cb9b-4dd0-8859-267048723a42",
                releases = arrayOf(release),
                stemcell = stemcell)

        val instanceGroup = PlanInstanceGroup(
                name = "neo4j",
                vm_type = "default",
                persistent_disk_type = "10240",
                networks = arrayOf("default"),
                instances = 1,
                azs = arrayOf("z1", "z2", "z3")
        )
        val plan = Plan(
                instance_groups = arrayOf(instanceGroup),
                properties = mapOf(),
                update = update
        )

        val manifestYaml = """
            |---
            |name: neo4j
            |
            |releases:
            |- name: neo4j
            |  version: latest
            |
            |stemcells:
            |- alias: default
            |  os: ubuntu-trusty
            |  version: latest
            |
            |update:
            |  canaries: 2
            |  max_in_flight: 1
            |  canary_watch_time: 5000-60000
            |  update_watch_time: 5000-60000
            |
            |instance_groups:
            |- name: neo4j
            |  azs: [z1, z2, z3]
            |  instances: 1
            |  jobs:
            |  - name: neo4j
            |    release: neo4j
            |    properties: {}
            |  vm_type: default
            |  stemcell: default
            |  persistent_disk_type: 10240
            |  networks:
            |  - name: default
            |
            |properties:
            |  admin_password: pff
            """.trimMargin()

        val serviceDeploymentJson = """{
  "deployment_name": "service-instance_1b42a2ea-cb9b-4dd0-8859-267048723a42",
  "releases": [
    {
      "name": "neo4j",
      "version": "latest",
      "jobs": ["neo4j"]
    }
  ],
  "stemcell": {
    "stemcell_os": "ubuntu-trusty",
    "stemcell_version": "3445.11"
  }
}""".trimIndent()

        val planJson = """{
            "instance_groups": [
                {
                    "name": "neo4j",
                    "vm_type": "default",
                    "persistent_disk_type": "10240",
                    "networks": ["default"],
                    "instances": 1,
                    "azs": ["z1", "z2", "z3"]
                }
            ],
            "properties": {},
            "update": {
                "canaries": 2,
                "max_in_flight": 1,
                "canary_watch_time": "5000-60000",
                "update_watch_time": "5000-60000"
            }
        }""".trimIndent()
    }
}