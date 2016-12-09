package org.grails.plugins.databasemigration.command

import liquibase.Liquibase


class DbmChangelogSyncTenantsCommand implements AllTenantsMigrationCommand {
    final String description = 'Mark all changes as executed for all tenants (for all dataSources)'

    @Override
    void handle() {
        withLiquibaseForAllTenants { Liquibase liquibase ->
            liquibase.changeLogSync(contexts as String)
        }
    }
}
