package org.grails.plugins.databasemigration.command

import liquibase.Liquibase


class DbmUpdateTenantsCommand implements AllTenantsMigrationCommand {
    final String description = 'Updates all data sources to the current version'

    @Override
    void handle() {
        withLiquibaseForAllTenants { Liquibase liquibase ->
            withTransaction {
                liquibase.update(contexts as String)
            }
        }
    }
}
