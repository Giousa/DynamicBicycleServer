package com.km1930.dynamicbicycleserver;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.km1930.dynamicbicycleserver.code.MsgPackDecode;
import com.km1930.dynamicbicycleserver.code.MsgPackEncode;
import com.km1930.dynamicbicycleserver.handler.ServerHandler;

import java.net.InetAddress;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();


        //将服务运行在子线程,这样不会和主线程UI逻辑冲突
        new Thread(new Runnable() {
            @Override
            public void run() {
                serverStart();

            }
        }).start();

    }

    private void initView() {

        Button start = (Button) findViewById(R.id.btn_server);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("我被点击了");
            }
        });
    }

    private void serverStart() {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workGroup = new NioEventLoopGroup(4);
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap
                    .group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline p = socketChannel.pipeline();
                            p.addLast(new IdleStateHandler(10, 0, 0));
                            p.addLast(new MsgPackDecode());
                            p.addLast(new MsgPackEncode());
                            p.addLast(new ServerHandler());
                        }
                    });

            Channel ch = bootstrap.bind(12345).sync().channel();

            System.out.println("------Server Start------");

            ch.closeFuture().sync();//一定要加上,否则服务会直接close,打印下面的日志

            System.out.println("------Server Close------");
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
