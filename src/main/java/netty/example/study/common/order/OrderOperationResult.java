package netty.example.study.common.order;

import lombok.Data;
import netty.example.study.common.OperationResult;

@Data
public class OrderOperationResult extends OperationResult {

    private final int tableId;
    private final String dish;
    private final boolean complete;

}
