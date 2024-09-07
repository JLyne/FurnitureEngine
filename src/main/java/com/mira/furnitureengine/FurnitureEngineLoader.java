package com.mira.furnitureengine;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"UnstableApiUsage", "unused"})
public class FurnitureEngineLoader implements PluginLoader {
	@Override
	public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
		MavenLibraryResolver resolver = new MavenLibraryResolver();
        resolver.addDependency(new Dependency(new DefaultArtifact("@messagesHelperDependency@"), null));
        resolver.addRepository(new RemoteRepository.Builder("notnull", "default", "https://repo.not-null.co.uk/snapshots/").build());

        classpathBuilder.addLibrary(resolver);
	}
}
