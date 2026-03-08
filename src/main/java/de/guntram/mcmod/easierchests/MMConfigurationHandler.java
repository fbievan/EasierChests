package de.guntram.mcmod.easierchests;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class MMConfigurationHandler implements ModMenuApi
{
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        // No in-game config screen for now; edit easierchests.properties manually
        return parent -> null;
    }
}

