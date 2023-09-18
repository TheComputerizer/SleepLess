package mods.thecomputerizer.sleepless.world;

import mods.thecomputerizer.sleepless.capability.CapabilityHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nonnull;

public class SetSleepDebtCommand extends CommandBase {
    @Override
    public @Nonnull String getName() {
        return "setsleepdebt";
    }

    @Override
    public @Nonnull String getUsage(@Nonnull ICommandSender sender) {
        return "Set Sleep Debt Command initialized";
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {
        if(sender instanceof EntityPlayerMP && args.length>=1) {
            EntityPlayerMP player = (EntityPlayerMP)sender;
            try {
                float val = Math.max(0f,Float.parseFloat(args[0]));
                CapabilityHandler.getSleepDebtCapability(player).setDebt(player,val);
                player.sendStatusMessage(new TextComponentTranslation("commands.sleepless.setsleepdebt.success",
                        val), true);
            } catch (NumberFormatException ex) {
                throw new CommandException("commands.sleepless.setsleepdebt.fail");
            }
        } else throw new CommandException("commands.sleepless.setsleepdebt.fail");
    }
}
