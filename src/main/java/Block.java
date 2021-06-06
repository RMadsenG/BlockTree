import java.sql.Connection;

public class Block
{
    private final Integer number;
    private final String prev_hash;
    private final String this_hash;
    private final Long nonce;
    private final String data;

    public Block(Integer number, String prev_hash, String this_hash, Long nonce, String data)
    {
        this.number = number;
        this.prev_hash = prev_hash;
        this.this_hash = this_hash;
        this.nonce = nonce;
        this.data = data;
    }

    public Integer getNumber()
    {
        return number;
    }

    public String getPrevHash()
    {
        return prev_hash;
    }

    public String getThisHash()
    {
        return this_hash;
    }

    public Long getNonce()
    {
        return nonce;
    }

    public String getData()
    {
        return data;
    }

    @Override
    public String toString()
    {
        return "Block{" +
                "number=" + number +
                ", prev_hash='" + prev_hash + '\'' +
                ", this_hash='" + this_hash + '\'' +
                ", nonce=" + nonce +
                ", data='" + data + '\'' +
                '}';
    }
}
