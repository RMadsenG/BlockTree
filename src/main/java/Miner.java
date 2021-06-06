import org.json.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.*;

public class Miner
{
    static Random r = new Random();

    public static Block createBlock(int number, String name, String prevHash)
    {
        JSONObject a = new JSONObject();
        a.put("block", number);
        a.put("data", name);
        if (number == 0)
        {
            a.put("prev_hash", "0000000000000000000000000000000000000000000000000000000000000000");
        }
        else
        {
            a.put("prev_hash", prevHash);
        }
        Miner.mine(a);

        return new Block(a.getInt("block"), a.getString("prev_hash"), a.getString("this_hash"), a.getLong("nonce"), a.getString("data"));
    }

    private static void mine(JSONObject json)
    {
        MessageDigest messageDigest;
        try
        {
            messageDigest = MessageDigest.getInstance("sha-256");
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
            return;
        }

        for (long i = r.nextLong(); true; i++)
        {
            json.put("nonce", i);
            byte[] bytes = messageDigest.digest(json.toString().getBytes());
            if ((bytes[0] | bytes[1]) == 0)
            {
                json.put("this_hash", toHex(bytes));
                break;
            }
        }
    }

    /**
     * Creates a block that is linked to a random previous block<br><br>
     * <p>
     * This function will attempt to mine the next block for each of the blocks in {@code pastBlocks}.<br>
     * When one is successfully mined, the other mining operations are terminated.<br>
     * This makes the previous block effectively random.<br><br>
     * If strings.size()==1 It does not do this and just mines the block normally.
     * If strings.size()<1 It yells at you and returns null.
     *
     * @param name       The data to be added to the block
     * @param pastBlocks The list of blocks that the new block can be attached to
     * @return The Block object that gets created
     */
    public static Block findNewBlockFast(String name, ArrayList<Block> pastBlocks)
    {
        Block block;

        if (pastBlocks.size() < 1)
        {
            System.err.println("Theres no hashes");
            block = null;
        }
        else if (pastBlocks.size() == 1)
        {
            Block b = pastBlocks.get(0);
            block = createBlock(b.getNumber() + 1, name, b.getThisHash());
        }
        else
        {
            int threads = Math.min(Runtime.getRuntime().availableProcessors() - 1, pastBlocks.size());
            ExecutorService executorService = Executors.newFixedThreadPool(threads);
            CompletionService<Block> completionService = new ExecutorCompletionService<>(executorService);
            for (Block b : pastBlocks)
            {
                completionService.submit(new MineCallable(b.getNumber() + 1, name, b.getThisHash()));
            }
            try
            {
                Future<Block> future = completionService.take();
                block = future.get();
                executorService.shutdown();
            }
            catch (InterruptedException | ExecutionException e)
            {
                e.printStackTrace();
                block = null;
            }
        }

        return block;
    }

    private static class MineCallable implements Callable<Block>
    {
        private final String prev_hash;
        private final String name;
        private final int number;

        /**
         * The Callable class to run
         *
         * @param number    The number of the block this will create
         * @param name      The metadata of the block this will create
         * @param prev_hash The hash of the previous block
         */
        MineCallable(int number, String name, String prev_hash)
        {
            this.name = name;
            this.prev_hash = prev_hash;
            this.number = number;
        }

        @Override
        public Block call() throws Exception
        {
            return createBlock(number, name, prev_hash);
        }
    }


    private static String toHex(byte[] bytes)
    {
        StringBuilder sb = new StringBuilder(64);
        for (byte b : bytes)
        {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}
