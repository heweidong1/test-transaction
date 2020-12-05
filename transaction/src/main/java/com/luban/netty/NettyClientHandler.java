package com.luban.netty;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.luban.transaction.Transaction;
import com.luban.transaction.TransactionMangage;
import com.luban.transaction.TransactionType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.List;
import java.util.Map;

public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    private ChannelHandlerContext context;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        context = ctx;
    }

    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("接受数据:" + msg.toString());
        JSONObject jsonObject = JSON.parseObject((String) msg);

        String groupId = jsonObject.getString("groupId");
        String command = jsonObject.getString("command");
        JSONArray transactionId = jsonObject.getJSONArray("transactionId");
        List<String> transactionIds = transactionId.toJavaList(String.class);
        for (String id : transactionIds) {
            //拿到通知的事物对象
            Transaction transaction = TransactionMangage.getTransactionById(groupId, id);
            Map<String, Transaction> transactions = TransactionMangage.getTransactions(groupId);
            if (transaction!=null){
                System.out.println("接收command:" + command+",找到事务ID:"+id);
                if (command.equals("rollback")){
                    transaction.setTransactionType(TransactionType.ROLLBACK);
                }else{
                    transaction.setTransactionType(TransactionType.COMMIT);
                }
                transaction.getTask().signalTask();
            }
            // 对事务进行操作
            //给指定事物放行
        }


    }

    public synchronized Object call(JSONObject data) throws Exception {
        context.writeAndFlush(data.toJSONString()).channel().newPromise();
        return null;
    }
}
