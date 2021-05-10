package netty.example.study.client.handler.dispatcher;

import netty.example.study.common.OperationResult;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description: 请求等待中心
 * @author: lizhenzhen
 * @date: 2021-05-10 10:11
 **/
public class RequestPendingCenter {
    private Map<Long, OperationResultFuture> map = new ConcurrentHashMap<>();

    // 添加
    public void add(Long streamId, OperationResultFuture future){
        this.map.put(streamId, future);
    }

    /**
     * 设置响应结果
     */
    public void set(Long streamId, OperationResult operationResult){
         OperationResultFuture operationResultFuture = this.map.get(streamId);
         if (operationResultFuture != null){
             operationResultFuture.setSuccess(operationResult);
             // 设置成功后移除
             this.map.remove(streamId);
         }
    }
}
