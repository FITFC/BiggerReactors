package net.roguelogix.biggerreactors;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLLoader;
import net.roguelogix.biggerreactors.machine.client.CyaniteReprocessorScreen;
import net.roguelogix.biggerreactors.machine.containers.CyaniteReprocessorContainer;
import net.roguelogix.biggerreactors.multiblocks.heatexchanger.containers.HeatExchangerFluidPortContainer;
import net.roguelogix.biggerreactors.multiblocks.heatexchanger.containers.HeatExchangerTerminalContainer;
import net.roguelogix.biggerreactors.multiblocks.heatexchanger.client.HeatExchangerFluidPortScreen;
import net.roguelogix.biggerreactors.multiblocks.heatexchanger.client.HeatExchangerTerminalScreen;
import net.roguelogix.biggerreactors.multiblocks.reactor.client.*;
import net.roguelogix.biggerreactors.multiblocks.reactor.containers.*;
import net.roguelogix.biggerreactors.multiblocks.turbine.client.BladeRenderer;
import net.roguelogix.biggerreactors.multiblocks.turbine.client.TurbineFluidPortScreen;
import net.roguelogix.biggerreactors.multiblocks.turbine.client.TurbineTerminalScreen;
import net.roguelogix.biggerreactors.multiblocks.turbine.containers.TurbineFluidPortContainer;
import net.roguelogix.biggerreactors.multiblocks.turbine.containers.TurbineTerminalContainer;
import net.roguelogix.biggerreactors.multiblocks.turbine.tiles.TurbineRotorBearingTile;
import net.roguelogix.biggerreactors.registries.FluidTransitionRegistry;
import net.roguelogix.biggerreactors.registries.ReactorModeratorRegistry;
import net.roguelogix.biggerreactors.registries.TurbineCoilRegistry;
import net.roguelogix.phosphophyllite.event.ReloadDataEvent;
import net.roguelogix.phosphophyllite.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("unused")
@Mod(BiggerReactors.modid)
public class BiggerReactors {

    public static final String modid = "biggerreactors";

    public static final Logger LOGGER = LogManager.getLogger();
    
    public BiggerReactors() {
        new Registry();
//        SimBench.main(null);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
        MinecraftForge.EVENT_BUS.addListener(this::onReloadData);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            MinecraftForge.EVENT_BUS.addListener(this::onRenderWorldLast);
        }
        version = FMLLoader.getLoadingModList().getModFileById(modid).versionString();
    }
    
    public void onReloadData(final ReloadDataEvent reloadDataEvent) {
        ReactorModeratorRegistry.loadRegistry();
        TurbineCoilRegistry.loadRegistry();
        FluidTransitionRegistry.loadRegistry();
    }

    public void onClientSetup(final FMLClientSetupEvent e) {
        // TODO: 6/28/20 Registry.
        //  Since I already have the comment here, also need to do a capability registry. I have a somewhat dumb capability to register.
        MenuScreens.register(CyaniteReprocessorContainer.INSTANCE,
                CyaniteReprocessorScreen::new);
        MenuScreens.register(ReactorTerminalContainer.INSTANCE,
                CommonReactorTerminalScreen::new);
        MenuScreens.register(ReactorCoolantPortContainer.INSTANCE,
                ReactorCoolantPortScreen::new);
        MenuScreens.register(ReactorAccessPortContainer.INSTANCE,
                ReactorAccessPortScreen::new);
        MenuScreens.register(ReactorControlRodContainer.INSTANCE,
                ReactorControlRodScreen::new);
        MenuScreens.register(ReactorRedstonePortContainer.INSTANCE,
                ReactorRedstonePortScreen::new);
        MenuScreens.register(TurbineTerminalContainer.INSTANCE,
                TurbineTerminalScreen::new);
        MenuScreens.register(TurbineFluidPortContainer.INSTANCE,
                TurbineFluidPortScreen::new);
        MenuScreens.register(HeatExchangerTerminalContainer.INSTANCE,
                HeatExchangerTerminalScreen::new);
        MenuScreens.register(HeatExchangerFluidPortContainer.INSTANCE,
                HeatExchangerFluidPortScreen::new);
    
    
        BlockEntityRenderers.register(TurbineRotorBearingTile.SUPPLIER.TYPE, BladeRenderer::new);
    }

    public static long lastRenderTime = 0;

    public void onRenderWorldLast(RenderLevelLastEvent event) {
        lastRenderTime = System.nanoTime();
    }

    private static String version;
    public static String modVersion(){
        return version;
    }
}
