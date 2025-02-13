package net.roguelogix.biggerreactors.multiblocks.reactor.tiles;

import mekanism.api.chemical.gas.IGasHandler;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.roguelogix.biggerreactors.multiblocks.reactor.util.ReactorTransitionTank;
import net.roguelogix.biggerreactors.multiblocks.reactor.blocks.ReactorAccessPort;
import net.roguelogix.biggerreactors.multiblocks.reactor.blocks.ReactorCoolantPort;
import net.roguelogix.biggerreactors.multiblocks.reactor.containers.ReactorCoolantPortContainer;
import net.roguelogix.biggerreactors.multiblocks.reactor.state.ReactorCoolantPortState;
import net.roguelogix.phosphophyllite.fluids.FluidHandlerWrapper;
import net.roguelogix.phosphophyllite.fluids.IPhosphophylliteFluidHandler;
import net.roguelogix.phosphophyllite.client.gui.api.IHasUpdatableState;
import net.roguelogix.phosphophyllite.fluids.MekanismGasWrappers;
import net.roguelogix.phosphophyllite.multiblock.IAssemblyAttemptedTile;
import net.roguelogix.phosphophyllite.multiblock.IOnAssemblyTile;
import net.roguelogix.phosphophyllite.multiblock.IOnDisassemblyTile;
import net.roguelogix.phosphophyllite.registry.RegisterTile;
import net.roguelogix.phosphophyllite.util.BlockStates;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static net.roguelogix.biggerreactors.multiblocks.reactor.blocks.ReactorAccessPort.PortDirection.*;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ReactorCoolantPortTile extends ReactorBaseTile implements IPhosphophylliteFluidHandler, MenuProvider, IHasUpdatableState<ReactorCoolantPortState>, IAssemblyAttemptedTile, IOnAssemblyTile, IOnDisassemblyTile {
    
    @RegisterTile("reactor_coolant_port")
    public static final BlockEntityType.BlockEntitySupplier<ReactorCoolantPortTile> SUPPLIER = new RegisterTile.Producer<>(ReactorCoolantPortTile::new);
    
    public ReactorCoolantPortTile(BlockEntityType<?> TYPE, BlockPos pos, BlockState state) {
        super(TYPE, pos, state);
    }
    
    private static final Capability<IGasHandler> GAS_HANDLER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
    
    @Override
    public <T> LazyOptional<T> capability(Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return LazyOptional.of(() -> this).cast();
        }
        if (cap == GAS_HANDLER_CAPABILITY) {
            return LazyOptional.of(() -> MekanismGasWrappers.wrap(this)).cast();
        }
        return super.capability(cap, side);
    }
    
    @Nullable
    private ReactorTransitionTank transitionTank;
    
    @Override
    public int tankCount() {
        if (transitionTank == null) {
            return 0;
        }
        return transitionTank.tankCount();
    }
    
    @Override
    public long tankCapacity(int tank) {
        if (transitionTank == null) {
            return 0;
        }
        return transitionTank.tankCapacity(tank);
    }
    
    @Override
    public Fluid fluidTypeInTank(int tank) {
        if (transitionTank == null) {
            return Fluids.EMPTY;
        }
        return transitionTank.fluidTypeInTank(tank);
    }
    
    @Nullable
    @Override
    public CompoundTag fluidTagInTank(int tank) {
        if (transitionTank == null) {
            return null;
        }
        return transitionTank.fluidTagInTank(tank);
    }
    
    @Override
    public long fluidAmountInTank(int tank) {
        if (transitionTank == null) {
            return 0;
        }
        return transitionTank.fluidAmountInTank(tank);
    }
    
    @Override
    public boolean fluidValidForTank(int tank, Fluid fluid) {
        if (transitionTank == null) {
            return false;
        }
        return transitionTank.fluidValidForTank(tank, fluid);
    }
    
    @Override
    public long fill(Fluid fluid, @Nullable CompoundTag tag, long amount, boolean simulate) {
        if (transitionTank == null || direction != INLET) {
            return 0;
        }
        return transitionTank.fill(fluid, tag, amount, simulate);
    }
    
    @Override
    public long drain(Fluid fluid, @Nullable CompoundTag tag, long amount, boolean simulate) {
        if (transitionTank == null || direction == INLET) {
            return 0;
        }
        return transitionTank.drain(fluid, tag, amount, simulate);
    }
    
    public long pushFluid() {
        if (!connected || direction == INLET) {
            return 0;
        }
        if (handlerOptional.isPresent() && transitionTank != null) {
            Fluid fluid = transitionTank.vaporType();
            long amount = transitionTank.vaporAmount();
            amount = transitionTank.drain(fluid, null, amount, true);
            amount = handler.fill(fluid, null, amount, false);
            amount = transitionTank.drain(fluid, null, amount, false);
            return amount;
        } else {
            handlerOptional = LazyOptional.empty();
            handler = null;
            connected = false;
        }
        return 0;
    }
    
    private boolean connected = false;
    Direction steamOutputDirection = null;
    
    LazyOptional<?> handlerOptional = LazyOptional.empty();
    IPhosphophylliteFluidHandler handler = null;
    FluidTank EMPTY_TANK = new FluidTank(0);
    private ReactorAccessPort.PortDirection direction = INLET;
    public final ReactorCoolantPortState reactorCoolantPortState = new ReactorCoolantPortState(this);
    
    @SuppressWarnings("DuplicatedCode")
    public void neighborChanged() {
        handlerOptional = LazyOptional.empty();
        handler = null;
        if (steamOutputDirection == null) {
            connected = false;
            return;
        }
        assert level != null;
        BlockEntity te = level.getBlockEntity(worldPosition.relative(steamOutputDirection));
        if (te == null) {
            connected = false;
            return;
        }
        connected = false;
        LazyOptional<IFluidHandler> fluidOptional = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, steamOutputDirection.getOpposite());
        if (fluidOptional.isPresent()) {
            connected = true;
            handlerOptional = fluidOptional;
            handler = FluidHandlerWrapper.wrap(fluidOptional.orElse(EMPTY_TANK));
        } else if (GAS_HANDLER_CAPABILITY != null) {
            LazyOptional<IGasHandler> gasOptional = te.getCapability(GAS_HANDLER_CAPABILITY, steamOutputDirection.getOpposite());
            if (gasOptional.isPresent()) {
                IGasHandler gasHandler = gasOptional.orElse(MekanismGasWrappers.EMPTY_TANK);
                connected = true;
                handlerOptional = gasOptional;
                handler = MekanismGasWrappers.wrap(gasHandler);
            }
        }
    }
    
    public void setDirection(ReactorAccessPort.PortDirection direction) {
        this.direction = direction;
        this.setChanged();
    }
    
    @Override
    protected void readNBT(CompoundTag compound) {
        if (compound.contains("direction")) {
            direction = ReactorAccessPort.PortDirection.valueOf(compound.getString("direction"));
        }
    }
    
    @Override
    
    protected CompoundTag writeNBT() {
        CompoundTag NBT = new CompoundTag();
        NBT.putString("direction", String.valueOf(direction));
        return NBT;
    }
    
    @Override
    public void onAssemblyAttempted() {
        assert level != null;
        level.setBlock(worldPosition, level.getBlockState(worldPosition).setValue(PORT_DIRECTION_ENUM_PROPERTY, direction), 3);
    }
    
    @Override
    public void runRequest(String requestName, Object requestData) {
        // Change IO direction.
        if (requestName.equals("setDirection")) {
            this.setDirection(((Integer) requestData != 0) ? OUTLET : INLET);
            assert level != null;
            level.setBlock(this.worldPosition, this.getBlockState().setValue(PORT_DIRECTION_ENUM_PROPERTY, direction), 3);
        }
        
        super.runRequest(requestName, requestData);
    }
    
    @Override
    public Component getDisplayName() {
        return Component.translatable(ReactorCoolantPort.INSTANCE.getDescriptionId());
    }
    
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player player) {
        return new ReactorCoolantPortContainer(windowId, this.worldPosition, player);
    }
    
    @Override
    
    public ReactorCoolantPortState getState() {
        this.updateState();
        return this.reactorCoolantPortState;
    }
    
    @Override
    public void updateState() {
        reactorCoolantPortState.direction = (this.direction == INLET);
    }
    
    @Override
    public void onAssembly() {
        this.transitionTank = controller().coolantTank();
        steamOutputDirection = getBlockState().getValue(BlockStates.FACING);
        neighborChanged();
    }
    
    @Override
    public void onDisassembly() {
        steamOutputDirection = null;
        transitionTank = null;
        neighborChanged();
    }
}
