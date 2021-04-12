package dev.lazurite.rayon.core.impl.physics.space.body.type;

import dev.lazurite.rayon.core.impl.physics.space.util.Clump;

public interface TerrainLoading {
    void setDoTerrainLoading(boolean terrainLoading);
    boolean shouldDoTerrainLoading();

    void setEnvironmentLoadDistance(int environmentLoadDistance);
    int getEnvironmentLoadDistance();

    void setClump(Clump clump);
    Clump getClump();
}
