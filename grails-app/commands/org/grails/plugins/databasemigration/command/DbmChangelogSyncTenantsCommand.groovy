package org.grails.plugins.databasemigration.command

import groovy.transform.CompileStatic
import liquibase.Liquibase
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@CompileStatic
class DbmChangelogSyncTenantsCommand implements AllTenantsMigrationCommand {
    private static final Logger log = LoggerFactory.getLogger(this);

    final String description = 'Mark all changes as executed for all tenants (for all dataSources)'

    @Override
    void handle() {
        log.debug("Start changelog sync for all tenants")
        withLiquibaseForAllTenants { Liquibase liquibase ->
            log.debug("Start changelog sync for a specific tenant (contexts: $contexts)...")
            liquibase.changeLogSync(contexts as String)
            log.debug("Finish changelog sync for a specific tenant")
        }
        log.debug("Finish changelog sync for all tenants")
    }
}
