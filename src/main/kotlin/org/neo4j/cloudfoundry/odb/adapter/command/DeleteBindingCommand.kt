package org.neo4j.cloudfoundry.odb.adapter.command

import org.neo4j.cloudfoundry.odb.adapter.CommandErrors
import org.neo4j.cloudfoundry.odb.adapter.command.error.CommandOutput
import org.neo4j.cloudfoundry.odb.adapter.command.persistence.CredentialsRepository
import org.neo4j.cloudfoundry.odb.adapter.command.persistence.PersistenceError
import org.neo4j.cloudfoundry.odb.adapter.command.supplier.AdminPasswordSupplier
import org.neo4j.cloudfoundry.odb.adapter.command.supplier.BoltUriSupplier
import org.neo4j.cloudfoundry.odb.adapter.command.supplier.DriverSupplier
import org.neo4j.cloudfoundry.odb.adapter.domain.BoshVms
import org.neo4j.cloudfoundry.odb.adapter.domain.Either
import org.neo4j.cloudfoundry.odb.adapter.domain.RequestParameters
import org.neo4j.cloudfoundry.odb.adapter.domain.manifest.Manifest
import org.neo4j.driver.v1.Driver
import org.neo4j.driver.v1.exceptions.Neo4jException
import org.neo4j.driver.v1.exceptions.ServiceUnavailableException
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters

@Command(name = "remove-binding")
class DeleteBindingCommand(private val credentialsRepository: CredentialsRepository,
                           private val driverSupplier: DriverSupplier,
                           private val boltUriSupplier: BoltUriSupplier,
                           private val adminPasswordSupplier: AdminPasswordSupplier) : ServiceAdapterCommand {

    @Parameters(index = "0", arity = "1")
    lateinit var bindingId: String

    @Parameters(index = "1", arity = "1")
    lateinit var boshVms: BoshVms

    @Parameters(index = "2", arity = "1")
    lateinit var manifest: Manifest

    @Parameters(index = "3", arity = "1")
    lateinit var requestParams: RequestParameters

    override fun execute(): CommandOutput {
        val maybeDriver = instantiateDriver()
        return when (maybeDriver) {
            is Either.Left<CommandOutput.Error> -> maybeDriver.value
            is Either.Right<Driver> -> {
                try {
                    val driver = maybeDriver.value
                    if (!credentialsRepository.exists(driver, bindingId)) {
                        val error = CommandErrors.bindingDoesNotExistError
                        return CommandOutput.Error(error.first, error.second(bindingId))
                    }
                    credentialsRepository.remove(driver, bindingId)
                    CommandOutput.Standard("")
                } catch (e: PersistenceError) {
                    val userDeletionError = CommandErrors.userDeletionError
                    return CommandOutput.Error(userDeletionError.first, userDeletionError.second(bindingId, e))
                } catch (e: Neo4jException) {
                    val unreachableVmError = CommandErrors.unreachableVmError
                    return CommandOutput.Error(unreachableVmError.first, unreachableVmError.second(bindingId, e))
                }
            }
        }

    }

    private fun instantiateDriver(): Either<CommandOutput.Error, Driver> {
        val boltUri = boltUriSupplier.getBoltUri(boshVms)
        if (boltUri == null) {
            val error = CommandErrors.noVmIpError
            return Either.Left(CommandOutput.Error(error.first, error.second(bindingId)))
        }
        val adminPassword = adminPasswordSupplier.getAdminPassword(manifest)
        if (adminPassword == null) {
            val error = CommandErrors.noPasswordError
            return Either.Left(CommandOutput.Error(error.first, error.second(bindingId)))
        }
        return Either.Right(driverSupplier.getDriver(
                boltUri,
                adminPassword
        ))
    }

}
