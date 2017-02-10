package com.km1930.dynamicbicycleserver.handler;

import com.km1930.dynamicbicycleserver.common.CustomHeartbeatHandler;
import com.km1930.dynamicbicycleserver.model.DeviceValue;
import com.km1930.dynamicbicycleserver.model.TypeData;

import java.util.List;

import io.netty.channel.ChannelHandlerContext;

/**
 * Description:
 * Author:Giousa
 * Date:2017/2/9
 * Email:65489469@qq.com
 */
public class ServerHandler extends CustomHeartbeatHandler {

    public ServerHandler() {
        super("server");
    }


    @Override
    protected void handleData(ChannelHandlerContext channelHandlerContext, Object msg) {
        System.out.println(name+"  handleData:"+msg);

        List<DeviceValue> deviceValues = (List<DeviceValue>) msg;
        String dev = String.valueOf(deviceValues.get(1));
        System.out.println("dev = "+dev);
        int angle = Integer.parseInt(String.valueOf(deviceValues.get(3)));
        System.out.println("angle = "+angle);


        DeviceValue s = new DeviceValue();
        s.setType(TypeData.CUSTOME);
        s.setSpeed(0);
        s.setAngle(15);
        s.setDeviceName("server response");
        channelHandlerContext.writeAndFlush(s);
    }


    @Override
    protected void handleReaderIdle(ChannelHandlerContext ctx) {
        super.handleReaderIdle(ctx);
        System.err.println("---client " + ctx.channel().remoteAddress().toString() + " reader timeout, close it---");
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(name+" exception"+cause.toString());
    }
}