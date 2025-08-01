import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    java
    alias(libs.plugins.pluginYmlPaper)
}

group = "uk.co.notnull"
version = "1.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
	maven {
		url = uri("https://repo.papermc.io/repository/maven-public/")
	}

    maven {
        url = uri("https://repo.not-null.co.uk/snapshots/")
    }

    maven {
        url = uri("https://maven.enginehub.org/repo/")
    }

    maven {
        url = uri("https://jitpack.io")
    }

	mavenLocal()
}

dependencies {
	compileOnly(libs.paperApi)

    compileOnly(libs.worldguard)
    compileOnly(libs.plotsquaredCore)
    compileOnly(libs.plotsquaredBukkit)
    implementation(platform(libs.intellectualsitesBom))
    compileOnly(libs.griefPrevention)
    compileOnly(libs.gsit)
    compileOnly(libs.creativeItemFilter)
    compileOnly(libs.customItemsApi)
    paperLibrary(libs.messagesHelper)
}

paper {
    main = "com.mira.furnitureengine.FurnitureEngine"
    loader = "com.mira.furnitureengine.FurnitureEngineLoader"
    apiVersion = libs.versions.paperApi.get().replace(Regex("\\-R\\d.\\d-SNAPSHOT"), "")
    authors = listOf("Jim (AnEnragedPigeon)", "Mira")
    generateLibrariesJson = true
    description = "Custom usable furniture items"
    load = BukkitPluginDescription.PluginLoadOrder.STARTUP

    permissions {
        register("furnitureengine.command") {
            default = BukkitPluginDescription.Permission.Default.TRUE
        }
        register("furnitureengine.give") {
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("furnitureengine.get") {
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("furnitureengine.blockbreak") {
            default = BukkitPluginDescription.Permission.Default.TRUE
        }
        register("furnitureengine.blockplace") {
            default = BukkitPluginDescription.Permission.Default.TRUE
        }
        register("furnitureengine.admin") {
            default = BukkitPluginDescription.Permission.Default.OP
            children = listOf("furnitureengine.give", "furnitureengine.get", "furnitureengine.blockbreak", "furnitureengine.blockplace")
        }
        register("furnitureengine.*") {
            default = BukkitPluginDescription.Permission.Default.OP
            children = listOf("furnitureengine.admin")
        }
    }

    serverDependencies {
        register("WorldGuard") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.AFTER
        }
        register("PlotSquared") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.AFTER
        }
        register("GriefPrevention") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.AFTER
        }
        register("GSit") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.AFTER
        }
        register("CreativeItemFilter") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.AFTER
        }
        register("CustomItems") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.AFTER
        }
    }
}

tasks {
    compileJava {
        options.compilerArgs.addAll(listOf("-Xlint:all", "-Xlint:-processing"))
        options.encoding = "UTF-8"
    }
}
