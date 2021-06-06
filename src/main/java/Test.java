import org.apache.commons.cli.*;

import java.util.ArrayList;

public class Test
{
    /**
     * Gets block number, {@code number} and attaches it to the first previous block it can
     *
     * @param number The block number to create
     * @param name   The metadata
     * @return The block
     */
    private static Block getNextBlock(int number, String name)
    {
        Block block;
        if (number == 0)
        {
            block = Miner.createBlock(0, name, (String) null);
        }
        else
        {
            ArrayList<Block> blocks = Database.getBlocks(number - 1);
            block = Miner.findNewBlockFast(name, blocks);
        }
        return block;
    }

    /**
     * Gets a block to attach to the end of any of the chains. (as a leaf to the block tree)
     *
     * @param name The metadata
     * @return The block
     */
    private static Block attachToRandomLeaf(String name)
    {
        ArrayList<Block> blocks = Database.getLeaves();
        return Miner.findNewBlockFast(name, blocks);
    }

    /**
     * Gets a block attached to a random existing block
     *
     * @param name The metadata
     * @return The block
     */
    private static Block getRandomBlock(String name)
    {

        ArrayList<Block> blocks = Database.getBlocks();
        return Miner.findNewBlockFast(name, blocks);
    }

    public static void main(String[] args)
    {
        CommandLine cmd = getOptions(args);
        if (cmd == null)
        {
            System.out.println("exiting...");
            System.exit(0);
        }
        boolean insert = !cmd.hasOption("d");

        String[] strings = cmd.getArgs();
        if (strings.length > 1)
        {
            System.err.println("To Many arguments");
            System.exit(-1);
        }
        if (strings.length < 1)
        {
            System.err.println("No data to include in the block");
            System.exit(-1);
        }

        String name = strings[0];

        Block b;
        if (cmd.hasOption('n'))
        {
            int number = Integer.parseInt(cmd.getOptionValue('n'));
            b = getNextBlock(number, name);
        }
        else if (cmd.hasOption('l'))
        {
            b = attachToRandomLeaf(name);
        }
        else
        {
            b = getRandomBlock(name);
        }
        if (insert)
        {
            Database.insertBlock(b);
        }
    }

    private static CommandLine getOptions(String[] args)
    {
        CommandLine cmd;
        Options options = new Options();

        options.addOption("h", "help", false, "Shows this message");
        options.addOption("d", "dry", false, "Creates and shows the block but doesn't add it to the database");

        OptionGroup methodSelector = new OptionGroup();
        Option number = Option.builder("n").
                longOpt("number").
                hasArg(true).
                argName("number").
                desc("Creates a block with the number Specified").
                numberOfArgs(1).
                optionalArg(false).
                build();
        Option leaf = Option.builder("l").
                longOpt("leaf").
                hasArg(false).
                desc("Creates a block at a leaf of the block tree").
                numberOfArgs(0).
                build();
        Option random = Option.builder("r").
                longOpt("random").
                hasArg(false).
                desc("Creates a block linked to a random other block (default)").
                numberOfArgs(0).
                build();

        methodSelector.addOption(leaf);
        methodSelector.addOption(number);
        methodSelector.addOption(random);

        options.addOptionGroup(methodSelector);

        CommandLineParser parser = new DefaultParser();
        try
        {
            cmd = parser.parse(options, args);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
            cmd = null;
        }

        if (cmd != null && cmd.hasOption("h"))
        {
            HelpFormatter d = new HelpFormatter();
            d.printHelp("[OPTIONS ...] <BLOCK DATA>", options);
            cmd = null;
        }
        return cmd;
    }
}
