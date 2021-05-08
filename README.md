# 请求-响应整体流程
- 典型的4+1结构
    - 4个一二级编解码
    - 1个业务处理器
```
    OrderFrameDecode --> OrderProtocolDecode --> 
                                                    OrderServerProcessHandler
    OrderProtocolEncode <--- OrderFrameEncode <---                                           
```   
                                                        