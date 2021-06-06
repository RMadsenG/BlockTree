public class SQL
{
    public static final String GET_HASHES = "SELECT block_number,this_hash FROM Block";
    public static final String GET_HASH_WITH_BLOCK_NUMBER = "SELECT this_hash FROM Block WHERE block_number=?";
    public static final String GET_LEAF_HASHES = "SELECT block_number, this_hash FROM Block WHERE id NOT IN (" +
            "SELECT this.id from Block as this " +
            "JOIN Block as next " +
            "ON this.block_number+1=next.block_number " +
            "WHERE this.this_hash=next.prev_hash)";
}
