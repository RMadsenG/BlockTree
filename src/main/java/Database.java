import java.sql.*;
import java.util.ArrayList;

public class Database
{
    private static Connection connection;

    private static Connection connect()
    {
        if (connection == null)
        {
            try
            {
                Class.forName("org.mariadb.jdbc.Driver");

                connection = DriverManager.getConnection(
                        "jdbc:mariadb://192.168.1.40/BlockChain",
                        "blocker", "aaaaaaaa");
            }
            catch (SQLException | ClassNotFoundException e)
            {
                e.printStackTrace();
            }
        }
        return connection;
    }

    /**
     * Get the blocks with block number {@code block_number}
     *
     * @return List of blocks
     */
    public static ArrayList<Block> getBlocks()
    {
        Connection connection = Database.connect();
        ResultSet r;

        ArrayList<Block> blocks = new ArrayList<>(1);

        try
        {
            Statement statement = connection.createStatement();
            statement.executeQuery(SQL.GET_HASHES);
            r = statement.getResultSet();

            while (r.next())
            {
                int block_number = r.getInt("block_number");
                String hash = r.getString("this_hash");
                Block b = new Block(block_number, null, hash, null, null);
                blocks.add(b);
            }
        }
        catch (SQLException throwable)
        {
            throwable.printStackTrace();
        }

        return blocks;
    }

    /**
     * Get the blocks with block number {@code block_number}
     *
     * @param block_number block number
     * @return List of blocks
     */
    public static ArrayList<Block> getBlocks(int block_number)
    {
        Connection connection = Database.connect();
        ResultSet r;

        ArrayList<Block> blocks = new ArrayList<>(1);

        try
        {
            PreparedStatement statement = connection.prepareStatement(SQL.GET_HASH_WITH_BLOCK_NUMBER);
            statement.setInt(1, block_number);
            statement.execute();
            r = statement.getResultSet();

            while (r.next())
            {
                String hash = r.getString("this_hash");
                Block b = new Block(block_number, null, hash, null, null);
                blocks.add(b);
            }
        }
        catch (SQLException throwable)
        {
            throwable.printStackTrace();
        }

        return blocks;
    }

    /**
     * Gets the all the leaf blocks
     *
     * @return A list of leaf blocks
     */
    public static ArrayList<Block> getLeaves()
    {
        Connection connection = Database.connect();
        ResultSet r;

        ArrayList<Block> blocks = new ArrayList<>(1);

        try
        {
            Statement statement = connection.createStatement();
            statement.executeQuery(SQL.GET_LEAF_HASHES);
            r = statement.getResultSet();

            while (r.next())
            {
                int number = r.getInt("block_number");
                String hash = r.getString("this_hash");
                Block b = new Block(number, null, hash, null, null);
                blocks.add(b);
            }
        }
        catch (SQLException throwable)
        {
            throwable.printStackTrace();
        }
        return blocks;
    }

    /**
     * Don't be stupid
     *
     * @param b block
     * @return if it succeed
     */
    public static boolean insertBlock(Block b)
    {
        boolean success = false;
        Connection connection = Database.connect();
        if (b == null)
        {
            return false;
        }

        try
        {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO Block" +
                    " (block_number, this_hash, prev_hash, nonce, data) " +
                    "VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

            statement.setInt(1, b.getNumber());
            statement.setString(2, b.getThisHash());
            statement.setString(3, b.getPrevHash());
            statement.setLong(4, b.getNonce());
            statement.setString(5, b.getData());
            int result = statement.executeUpdate();

            if (result > 0)
            {
                success = true;
            }
        }
        catch (SQLException throwable)
        {
            throwable.printStackTrace();
        }
        return success;
    }
}
