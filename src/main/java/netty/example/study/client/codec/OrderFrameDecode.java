package netty.example.study.client.codec;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @description: 【一次解码】解决tcp半包、粘包问题
 * @author: lizhenzhen
 * @date: 2021-05-08 14:33
 **/
public class OrderFrameDecode extends LengthFieldBasedFrameDecoder {

    /**
     * 解码：解出了一个没有粘包和半包问题的byteBuf
     */
    public OrderFrameDecode() {
        /**
         * int maxFrameLength：最大长度
         * int lengthFieldOffset：长度字段的位移
         * int lengthFieldLength, 长度字段：2
         * int lengthAdjustment,：长度调整
         * int initialBytesToStrip：去掉头字段：2
         */
        super(Integer.MAX_VALUE, 0, 2, 0, 2);
    }
}
