# 整体流程
## 服务端
- 典型的4+1结构
    - 4个一二级编解码
    - 1个业务处理器
```
    OrderFrameDecode(处理tcp半包粘包问题) --> OrderProtocolDecode(解码成RequestMessage) --> 
                                                                                         OrderServerProcessHandler（业务逻辑处理） 
    OrderFrameEncode(处理tcp半包粘包问题) <--- OrderProtocolEncode(编码成ByteBuf) <---                                           
```

## 客户端
```
  OrderProtocolEncode(编码成ByteBuf) ---> OrderFrameEncode(处理tcp半包粘包问题)
  OrderProtocolDecode(解码成ResponseMessage)  <--- OrderFrameDecode(处理tcp半包粘包问题) <---
```
                                                        