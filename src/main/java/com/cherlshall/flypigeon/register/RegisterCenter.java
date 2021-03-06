package com.cherlshall.flypigeon.register;

import com.cherlshall.flypigeon.config.RegisterCenterConfiguration;
import com.cherlshall.flypigeon.exception.CommandExecuteException;
import com.cherlshall.flypigeon.exception.CommandRegisterException;
import com.cherlshall.flypigeon.register.command.CommandLeaf;
import com.cherlshall.flypigeon.register.command.CommandTree;
import com.cherlshall.flypigeon.register.command.CommandTrunk;
import com.cherlshall.flypigeon.register.response.ResponseWithCommand;
import com.cherlshall.flypigeon.register.response.ResponseWithCommandAdapter;
import com.cherlshall.flypigeon.register.response.ResponseWithoutCommand;

import java.util.Set;

/**
 * 注册中心
 * 负责命令的注册、存储、响应
 *
 * @author hu.tengfei
 * @since 2019/7/30
 */
public class RegisterCenter {

    private final RegisterCenterConfiguration configuration;
    private final CommandTree commandTree = new CommandTree();

    public RegisterCenter() {
        this.configuration = new RegisterCenterConfiguration();
    }

    public RegisterCenter(RegisterCenterConfiguration configuration) {
        this.configuration = configuration;
    }

    public void register(String command, ResponseWithCommand executor) {
        String[] commandItems = splitRegisterCommand(command);
        CommandTree currentNode = this.commandTree;
        int lastIndex = commandItems.length - 1;
        for (int i = 0; i < lastIndex; i++) {
            currentNode = currentNode.addTrunk(commandItems[i]);
        }
        currentNode.addLeaf(commandItems[lastIndex], executor);
    }

    public void register(String command, ResponseWithoutCommand executor) {
        register(command, new ResponseWithCommandAdapter(executor));
    }

    public String execute(String command) {
        try {
            String[] commandItems = splitExecuteCommand(command);
            if (commandItems[0].equals(configuration.getHelpCmdName())) {
                return configuration.getHelpResponseHandler().handle(command, commandTree);
            }
            CommandTree currentNode = this.commandTree;
            int lastIndex = commandItems.length - 1;
            for (int i = 0; i < lastIndex; i++) {
                currentNode = currentNode.getTrunk(commandItems[i]);
                if (currentNode == null) {
                    throw new CommandExecuteException("command not register, command = " + command);
                }
            }
            CommandLeaf leaf = currentNode.getLeaf(commandItems[lastIndex]);
            if (leaf == null) {
                CommandTrunk trunk = currentNode.getTrunk(commandItems[lastIndex]);
                if (trunk == null) {
                    throw new CommandExecuteException("command not register, command = " + command);
                }
                Set<String> trunkNames = trunk.getTrunkNames();
                Set<String> leafNames = trunk.getLeafNames();
                return configuration.getCommandResponseHandler().handle(trunkNames, leafNames);
            }
            return configuration.getUserResponseHandler().handle(leaf.getResponseWithCommand().getResponse(command));
        } catch (Exception e) {
            return configuration.getExceptionResponseHandler().handle(e);
        }

    }

    private String[] splitRegisterCommand(String command) {
        if (command == null) {
            throw new CommandRegisterException("command cannot null");
        }
        String commandTrim = command.trim();
        if (commandTrim.isEmpty()) {
            throw new CommandRegisterException("command cannot empty, command = " + command);
        }
        String[] commandItems = commandTrim.split("\\s+");
        String helpCmdName = configuration.getHelpCmdName();
        if (commandItems[0].equals(helpCmdName)) {
            throw new CommandRegisterException("command cannot start with help command, help command = " + helpCmdName);
        }
        return commandItems;
    }

    private String[] splitExecuteCommand(String command) {
        if (command == null) {
            throw new CommandExecuteException("command cannot null");
        }
        String commandTrim = command.trim();
        if (commandTrim.isEmpty()) {
            throw new CommandExecuteException("command cannot empty, command = " + command);
        }
        return commandTrim.split("\\s+");
    }

}
