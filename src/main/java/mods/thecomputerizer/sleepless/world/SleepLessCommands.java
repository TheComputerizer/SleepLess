package mods.thecomputerizer.sleepless.world;

import mcp.MethodsReturnNonnullByDefault;
import mods.thecomputerizer.sleepless.capability.CapabilityHandler;
import mods.thecomputerizer.sleepless.core.Constants;
import mods.thecomputerizer.sleepless.network.PacketRenderTests;
import mods.thecomputerizer.sleepless.world.nightterror.NightTerror;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SleepLessCommands extends CommandBase {

    private final String[] testRenderTab = new String[]{"@s","~","~","~","0","0","0","200"};
    private String curSubName;
    private EntityPlayerMP curPlayerSelector;

    @Override
    public String getName() {
        return Constants.MODID;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        String baseLang = "commands."+Constants.MODID+".";
        return Objects.nonNull(this.curSubName) && !this.curSubName.isEmpty() ?
                baseLang+this.curSubName +".usage" : baseLang+"help";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String ... args) throws CommandException {
        try {
            if (args.length == 0) sendMessage(sender, true, false, "help");
            this.curSubName = getOrNull(0, args);
            if (Objects.isNull(this.curSubName) || this.curSubName.isEmpty())
                sendMessage(sender, true, false, "help");
            else {
                switch (this.curSubName) {
                    case "setsleepdebt": {
                        setSleepDebt(server, sender, getOrNull(1, args), getOrNull(2, args));
                        resetParameters();
                        return;
                    }
                    case "testrenders": {
                        if (args.length < 5) sendMessage(sender, true, false, "usage");
                        testRenders(server, sender, getOrNull(1, args), getOrNull(2, args), getOrNull(3, args),
                                getOrNull(4, args), getOrNull(5, args), getOrNull(6, args),
                                getOrNull(7, args), getOrNull(8, args));
                        resetParameters();
                        return;
                    }
                    case "nightterror": {
                        nightTerror(server, sender, getOrNull(1, args));
                        resetParameters();
                        return;
                    }
                    default: {
                        resetParameters();
                        sendMessage(sender, true, false, "usage");
                    }
                }
            }
        } catch (CommandException ex) {
            resetParameters();
            throw ex;
        }
    }

    protected @Nullable String getOrNull(int index, String ... args) {
        return args.length>=index+1 ? args[index] : null;
    }

    private void setSleepDebt(MinecraftServer server, ICommandSender sender, @Nullable String unparsedPlayer,
                              @Nullable String unparsedDebt) throws CommandException {
        parsePlayer(server,sender,unparsedPlayer);
        if(Objects.isNull(unparsedDebt)) sendMessage(sender,true,false,"usage");
        else {
            float debt = Math.max(0f,(float)parseDouble(sender,Float.NaN,unparsedDebt));
            CapabilityHandler.setSleepDebt(this.curPlayerSelector, debt);
            sendMessage(sender,false,true,"success",debt);
        }
    }

    private void testRenders(MinecraftServer server, ICommandSender sender, @Nullable String unparsedPlayer,
                             @Nullable String unparsedX, @Nullable String unparsedY, @Nullable String unparsedZ,
                             @Nullable String unparsedRotX, @Nullable String unparsedRotY, @Nullable String unparsedRotZ,
                             @Nullable String unparsedTicks) throws CommandException {
        parsePlayer(server,sender,unparsedPlayer);
        Vec3d pos = parseExternalCoords(sender,unparsedX,unparsedY,unparsedZ);
        if(Objects.isNull(pos)) sendMessage(sender,true,false,"pos");
        else {
            Vec3d rotVec = new Vec3d(parseDouble(sender,0d,unparsedRotX),
                    parseDouble(sender,0d,unparsedRotY),parseDouble(sender,0d,unparsedRotZ));
            int ticks = (int)parseDouble(sender,0d,unparsedTicks);
            new PacketRenderTests(pos,rotVec,ticks).addPlayers(this.curPlayerSelector).send();
            sendMessage(sender,false,true,"success",pos.x,pos.y,pos.z);
        }
    }

    private void nightTerror(MinecraftServer server, ICommandSender sender, @Nullable String subType) throws CommandException {
        if(Objects.isNull(subType) || subType.isEmpty())
            sendMessage(sender, true, false, "usage");
        else {
            switch(subType) {
                case "begin": {
                    WorldServer overworld = server.getWorld(0);
                    if(Objects.nonNull(NightTerror.INSTANCE))
                        sendMessage(sender,false,false,"fail.begin.active");
                    else if(overworld.isDaytime())
                        sendMessage(sender,false,false,"fail.begin.time");
                    else {
                        NightTerror.createInstance(overworld);
                        sendMessage(sender, false, false, "success.begin");
                    }
                    return;
                }
                case "stop": {
                    if(Objects.nonNull(NightTerror.INSTANCE)) {
                        NightTerror.INSTANCE.finish();
                        sendMessage(sender, false, false, "success.stop");
                    }
                    else sendMessage(sender, false, false, "fail.stop");
                    return;
                }
                default: sendMessage(sender, true, false, "usage");
            }
        }
    }

    private void parsePlayer(MinecraftServer server, ICommandSender sender, @Nullable String unparsedPlayer)
            throws CommandException {
        if(Objects.isNull(unparsedPlayer)) return;
        try {
            this.curPlayerSelector = getPlayer(server,sender,unparsedPlayer);
        } catch (CommandException ignored) {}
        if(Objects.isNull(this.curPlayerSelector))
            sendMessage(sender,true,false,"player.fail");
    }

    private double parseDouble(ICommandSender sender, double defVal, @Nullable String unparsed) throws CommandException {
        if(Objects.isNull(unparsed) || unparsed.isEmpty())
            sendMessage(sender,true,false,"number.missing");
        else {
            try {
                return Double.parseDouble(unparsed);
            } catch (NumberFormatException ex) {
                if (Double.isNaN(defVal)) sendMessage(sender,true,false,"number",unparsed);
            }
        }
        return defVal; //This should be unreachable
    }

    private @Nullable Vec3d parseExternalCoords(ICommandSender sender, @Nullable String unparsedX, @Nullable String unparsedY,
                                                @Nullable String unparsedZ) throws CommandException {
        if(Objects.isNull(unparsedX) || Objects.isNull(unparsedY) || Objects.isNull(unparsedZ) ||
                unparsedX.isEmpty() || unparsedY.isEmpty() || unparsedZ.isEmpty())
            sendMessage(sender,true,false,"pos.missing");
        else return new Vec3d(parseExternalCoord(sender,this.curPlayerSelector.posX,unparsedX),
                parseExternalCoord(sender,this.curPlayerSelector.posY,unparsedY),
                parseExternalCoord(sender,this.curPlayerSelector.posZ,unparsedZ));
        return null; //This should be unreachable
    }

    /**
     * Assumes the unparsed value can never be null or empty
     */
    private double parseExternalCoord(ICommandSender sender, double original, String unparsed) throws CommandException {
        if(!unparsed.contains("~")) return parseDouble(sender,Float.NaN,unparsed);
        return (float)original+parseDouble(sender,Float.NaN,unparsed.replaceAll("~",""));
    }

    private void sendMessage(ICommandSender sender, boolean isException, boolean isStatusMsg,
                               @Nullable String extraLang, Object ... args) throws CommandException {
        String lang = buildLangKey(extraLang);
        checkStatusMessage(isStatusMsg,lang,args);
        if(isException) throw new CommandException(lang,args);
        else notifyCommandListener(sender,this,lang,args);
    }

    private String buildLangKey(@Nullable String extraLang) {
        String lang = "commands."+Constants.MODID;
        if(Objects.nonNull(this.curSubName) && !this.curSubName.isEmpty()) lang+=("."+this.curSubName);
        if(Objects.nonNull(extraLang) && !extraLang.isEmpty()) lang+=("."+extraLang);
        return lang;
    }

    private void checkStatusMessage(boolean isStatusMsg, String lang, Object ... args) {
        if(Objects.nonNull(this.curPlayerSelector) && isStatusMsg)
            this.curPlayerSelector.sendStatusMessage(new TextComponentTranslation(lang,args),true);
    }

    private String buildCommandString(boolean withSlash, Object ... args) {
        StringBuilder builder = withSlash ? new StringBuilder("/") : new StringBuilder();
        for(Object arg : args) builder.append(arg).append(" ");
        return builder.toString().trim();
    }

    private void resetParameters() {
        this.curSubName = null;
        this.curPlayerSelector = null;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
                                          @Nullable BlockPos targetPos) {
        if(args.length==0) return Arrays.asList("setsleepdebt","testrenders","nightterror");
        if(Objects.nonNull(this.curSubName)) {
            if(this.curSubName.matches("testrenders")) return testRendersTabCompletions(args.length-1);
            if(this.curSubName.matches("nightterror")) return Arrays.asList("start","stop");
        }
        return Collections.emptyList();
    }

    private List<String> testRendersTabCompletions(int length) {
        StringBuilder builder = new StringBuilder();
        for(int i=length; i<this.testRenderTab.length; i++)
            builder.append(this.testRenderTab[i]).append(" ");
        return Collections.singletonList(builder.toString().trim());
    }
}
