package org.grails.plugins.databasemigration.command

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import liquibase.Liquibase
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@CompileStatic
class DbmUpdateTenantsCommand implements AllTenantsMigrationCommand {
    private static final Logger log = LoggerFactory.getLogger(this);

    final String description = 'Updates all data sources to the current version'

    @Override
    void handle() {
        log.debug("Running updating all tenants in dataSources")
        withLiquibaseForAllTenants { Liquibase liquibase ->
            withTransaction {
                log.debug("Start update command for a single tenant (context: $contexts)...")
                liquibase.update(contexts as String)
                log.debug("Finish update command for a single tenant")
            }
        }
        log.debug("Finish updating all tenants")
    }
}
