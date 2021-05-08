package netty.example.study.common.auth;

import lombok.Data;
import netty.example.study.common.OperationResult;

@Data
public class AuthOperationResult extends OperationResult {

    private final boolean passAuth;

}
