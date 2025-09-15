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
        mavenCentral() // Essential for finding many libraries, including club.cred:neopop
        maven(url = "https://libs.dev.cred.club/") // For other specific CRED libraries
    }
}

rootProject.name = "Phonepe"
include(":app")
