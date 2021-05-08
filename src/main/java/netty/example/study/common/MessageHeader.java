package netty.example.study.common;

import lombok.Data;

@Data
public class MessageHeader {
    /**
     * 版本号
     */
    private int version = 1;
    /**
     * 操作code
     */
    private int opCode;
    private long streamId;

}
