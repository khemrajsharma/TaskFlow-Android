pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "TaskFlow"
include(":app")
include(":core:ui")
include(":core:utils")
include(":core:navigation")
include(":core:permission")
include(":data:local")
include(":data:repository")
include(":domain:model")
include(":domain:repository")
include(":domain:usecase")
include(":features:tasks")
include(":features:reminders")
include(":features:settings")
