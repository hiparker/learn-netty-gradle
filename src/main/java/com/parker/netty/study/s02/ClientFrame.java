package com.parker.netty.study.s02;

import io.netty.channel.ChannelFuture;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.ExecutionException;

/**
 * @BelongsProject: learn-netty-gradle
 * @BelongsPackage: com.parker.netty.study02
 * @Author: Parker
 * @CreateTime: 2020-08-20 01:58
 * @Description: 聊天室
 */
public class ClientFrame extends Frame {

    private static  String TITLE = "聊天室 v1.0.0";

    private String name = "二狗子";
    private ChannelFuture channelFuture;
    private TextArea ta = new TextArea();
    private TextField tf = new TextField();

    /** 画布宽高 */
    public static int WIDTH = 800, HEIGHT = 600;

    public ClientFrame() throws ExecutionException, InterruptedException {

        ClientFrame that = this;

        // 可见
        this.setVisible(true);
        // 设置窗口大小
        this.setSize(WIDTH,HEIGHT);
        // 禁止改变大小
        this.setResizable(false);
        // 设置标题
        this.setTitle(TITLE);

        this.add(ta,BorderLayout.CENTER);
        this.add(tf,BorderLayout.SOUTH);

        // window 监听器
        this.addWindowListener(new WindowAdapter() {

            // 监听 关闭事件 关闭当前java程序
            @Override
            public void windowClosing(WindowEvent e)  {
                System.exit(0);
            }
        });

        // window 按键监听
        tf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 发送消息
                Client.INSTANCE.send(that.name,tf.getText());
                tf.setText("");
            }
        });

        // 创建客户端信道
        new Thread(()->{
            Client.INSTANCE.connect(that);
        }).start();
    }


    public TextArea getTa() {
        return ta;
    }

    public TextField getTf() {
        return tf;
    }

    public void print(String msg){
        if(msg == null){
            return;
        }
        ta.setText(ta.getText()+System.getProperty("line.separator")+msg);
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        ClientFrame clientFrame = new ClientFrame();

    }

}
