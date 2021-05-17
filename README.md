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

## 空闲检测
- 服务端加上 read idle check – 服务器 10s 接受不到 channel 的请求就断掉连接
  - 保护自己、瘦身(及时清理空闲的连接)
- 客户端加上 write idle check + keepalive – 客户端 5s 不发送数据就发一个 keepalive
  - 避免连接被断
  - 启用不频繁的 keepalive
                                                        