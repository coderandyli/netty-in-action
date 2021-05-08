package netty.example.study.common.keepalive;

import lombok.Data;
import netty.example.study.common.OperationResult;

@Data
public class KeepaliveOperationResult extends OperationResult {

    private final long time;

}
