package mods.thecomputerizer.sleepless.world;

import mods.thecomputerizer.sleepless.network.PacketRenderTests;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class TestRendersCommand extends CommandBase {

    @Override
    public @Nonnull String getName() {
        return "testrenders";
    }

    @Override
    public @Nonnull String getUsage(@Nonnull ICommandSender sender) {
        return "Test Renders Command initialized";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(sender instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) sender;
            if(args.length==0) server.getCommandManager().executeCommand(sender,"/testrenders ~2 ~ ~ 1 0 0 200");
            else if(args.length >= 7) {
                try {
                    BlockPos pos = parseBlockPos(sender, args, 0, true);
                    double xRot = Double.parseDouble(args[3]);
                    double yRot = Double.parseDouble(args[4]);
                    double zRot = Double.parseDouble(args[5]);
                    int ticks = Integer.parseInt(args[6]);
                    new PacketRenderTests(pos.getX(), pos.getY(), pos.getZ(), xRot, yRot, zRot, ticks).addPlayers(player).send();
                    player.sendStatusMessage(new TextComponentTranslation("commands.sleepless.testrenders.success"), true);
                } catch (NumberFormatException ex) {
                    throw new CommandException("commands.sleepless.testrenders.fail");
                }
            } else throw new CommandException("commands.sleepless.testrenders.fail");
        } else throw new CommandException("commands.sleepless.testrenders.player");
    }
}
