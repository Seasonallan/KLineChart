package com.season.example.net;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.season.klinechart.DepthDataBean;
import com.season.klinechart.KLineEntity;
import com.season.example.fragment.DealRecord;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WebSocketService {

    private static WebSocketService instance;

    public static WebSocketService getInstance() {
        if (null == instance) {
            synchronized (WebSocketService.class) {
                if (null == instance) {
                    instance = new WebSocketService();
                }
            }
        }
        return instance;
    }
    WebSocketService(){
        subscribeHandler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                unSubTag = false;
                send("sub", currentSubscribe);
            }
        };
    }

    Handler subscribeHandler;
    int subscribeId = 20210807;
    WebSocketClient clientChart;
    WebSocketClient clientTrade;
    String coinCode;
    String coinCodeUSDT = "USDT";
    String currentSubscribe = null;
    long timeInterval = 0;
    long countSub = 1;
    volatile boolean unSubTag = false;
    public void subscribe(String interval){
        if (currentSubscribe != null && !currentSubscribe.equals(interval)){
            unSubTag = true;
            //取消订阅
            send("unsub", currentSubscribe);
        }
        currentSubscribe = interval;
        switch (interval){
            case "1D":
                timeInterval = 24 * 60 * 60 * 1000;
                countSub = 100000000;
                break;
            case "1W":
                timeInterval = 7 * 24 * 60 * 60 * 1000;
                countSub = 2000000000;
                break;
            case "1M":
                timeInterval = 8 * 24 * 60 * 60 * 1000;
                countSub = 100000000;
                break;
            case "-1":
                timeInterval = 1 * 1000;
                countSub = 100000;
                break;
            default:
                timeInterval = Integer.parseInt(interval) * 60 * 1000;
                countSub =  Integer.parseInt(interval) * 100000;
                break;
        }
        startSubscribe();
    }
    private void startSubscribe(){
        subscribeHandler.removeMessages(subscribeId);
        subscribeHandler.sendEmptyMessageDelayed(subscribeId, unSubTag?3000:300);
    }

    public void req(String interval){
        send("req", interval);
    }

    private void send(String cmd, String interval){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("cmd", cmd);
            jsonObject.put("coinCode", coinCode);
            jsonObject.put("interval", interval);
            jsonObject.put("timeout", "1440");
            jsonObject.put("mobile", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String sendStr = jsonObject.toString();
        Log.e("JWebSClientService", sendStr);
        if (clientChart != null)
            clientChart.send(jsonObject.toString());
    }

    public void subscribeTrade(String language){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("cmd", "trading");
            jsonObject.put("coinCode", coinCode);
            jsonObject.put("language", language);
            jsonObject.put("type", "0");
            jsonObject.put("timeout", "1440");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (clientTrade != null)
            clientTrade.send(jsonObject.toString());
    }


    long currentTime;
    private abstract class WebClient extends WebSocketClient{
        public WebClient(URI serverUri) {
            super(serverUri);
        }

        @Override
        public void onMessage(String message) {
            //Log.e("JWebSClientService", message);
            try {
                JSONObject jsonObject = new JSONObject(message);
                if (jsonObject.has("type")) {

                    String type = jsonObject.getString("type");

                    if (type.equals("node")) {
                        //加载增量数据
                        JSONObject object = jsonObject.getJSONObject("data");
                        KLineEntity kLineEntity = new KLineEntity();
                        kLineEntity.Id = object.getString("id");
                        kLineEntity.Close = Float.parseFloat(object.getString("close"));
                        kLineEntity.High = Float.parseFloat(object.getString("high"));
                        kLineEntity.Low = Float.parseFloat(object.getString("low"));
                        kLineEntity.Open = Float.parseFloat(object.getString("open"));
                        kLineEntity.Time = object.getLong("seq") * 1000L;
                        kLineEntity.Date = new SimpleDateFormat("MM-dd HH:mm").format(new Date(kLineEntity.Time));
                        kLineEntity.Volume = Float.parseFloat(object.getString("count"))/countSub;

                        if (unSubTag){ //取消订阅后会有一段多余的数据留存，跳过他们
                            startSubscribe();
                        }else{
                            if (kLineEntity.Time - currentTime >= timeInterval){
                                currentTime = kLineEntity.Time;
                                notifySubscribe(false, kLineEntity);
                            }else{
                                notifySubscribe(true, kLineEntity);
                            }
                        }
                        //Log.e("JWebSClientService",  timeInterval + "-->>  "+kLineEntity.Date + ">>"+ kLineEntity.Volume);
                    } else if (type.equals("one")) {
                        //第一次加载所有数据
                        List<KLineEntity> data = new ArrayList<>();
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            KLineEntity kLineEntity = new KLineEntity();
                            kLineEntity.Id = object.getString("id");
                            kLineEntity.Close = Float.parseFloat(object.getString("close"));
                            kLineEntity.High = Float.parseFloat(object.getString("high"));
                            kLineEntity.Low = Float.parseFloat(object.getString("low"));
                            kLineEntity.Open = Float.parseFloat(object.getString("open"));
                            kLineEntity.Time = object.getLong("seq");
                            kLineEntity.Date = new SimpleDateFormat("MM-dd HH:mm").format(new Date(kLineEntity.Time));
                            kLineEntity.Volume = Float.parseFloat(object.getString("count"));
                            if (kLineEntity.Volume != 0){
                                //Log.e("JWebSClientService", "append>>"+ kLineEntity.Date + "-->>"+kLineEntity.Volume);
                                data.add(kLineEntity);
                            }else{
                                //Log.e("JWebSClientService", kLineEntity.Date + "-->>"+ kLineEntity.Time);
                            }
                        }
                        currentTime = data.get(data.size() - 1).Time;
                        //TODO：数据流有问题，待修正调整
                        notifyCurrentChart(data);
                    }
                } else {
                    //更新最新数据
                    Log.e("JWebSClientService", jsonObject.toString());
                    JSONArray areaArray = jsonObject.getJSONArray("area");
                    for (int i = 0; i < areaArray.length(); i++) {
                        JSONObject itemObject = areaArray.getJSONObject(i);
                        if (coinCodeUSDT.equals(itemObject.getString("areaname"))) {
                            JSONArray dataArray = itemObject.getJSONArray("data");
                            for (int j = 0; j < dataArray.length(); j++) {
                                JSONObject dataItemObject = dataArray.getJSONObject(i);
                                if (coinCode.equals(dataItemObject.getString("coinCode"))) {
                                    BeanPrice beanPrice = new BeanPrice();
                                    beanPrice.currentExchangPrice = dataItemObject.getString("currentExchangPrice");
                                    beanPrice.maxPrice = dataItemObject.getString("maxPrice");
                                    beanPrice.minPrice = dataItemObject.getString("minPrice");
                                    beanPrice.openPrice = dataItemObject.getString("openPrice");
                                    beanPrice.usdttormb = dataItemObject.getDouble("usdttormb");
                                    beanPrice.transactionSum = dataItemObject.getString("transactionSum");
                                    beanPrice.coinSymbol = dataItemObject.getString("coinSymbol");
                                    beanPrice.currencyToRmb = dataItemObject.getString("currencyToRmb");
                                    notifyCurrentPrice(beanPrice);
                                    break;
                                }
                            }
                            break;
                        }
                    }
                    JSONArray buySellDataArray = jsonObject.getJSONArray("buySellData");
                    for (int i = 0; i < areaArray.length(); i++) {
                        JSONObject itemObject = buySellDataArray.getJSONObject(i);
                        if (coinCode.equals(itemObject.getString("symbolId"))) {
                            JSONObject payload = itemObject.getJSONObject("payload");
                            JSONObject trades = payload.getJSONObject("trades");
                            if (trades.has("amount")){
                                JSONArray amountArray = trades.getJSONArray("amount");
                                JSONArray priceArray = trades.getJSONArray("price");
                                JSONArray timeArray = trades.getJSONArray("time");
                                JSONArray directionArray = trades.getJSONArray("direction");

                                List<DealRecord> dealRecordList = new ArrayList<>();
                                for (int j = 0; j < amountArray.length(); j++) {
                                    DealRecord dealRecord = new DealRecord();
                                    dealRecord.setAmount(amountArray.getDouble(j));
                                    dealRecord.setDirection(directionArray.getInt(i));
                                    dealRecord.setPrice(priceArray.getString(j));
                                    dealRecord.setTime(timeArray.getInt(j));
                                    dealRecord.setId(dealRecord.getTime());
                                    dealRecordList.add(dealRecord);
                                }
                                notifyTradeRecord(dealRecordList);
                            }
                            break;
                        }
                    }
                    if (System.currentTimeMillis() - recordTime > 3000){
                        recordTime = System.currentTimeMillis();
                        JSONObject deepObject = jsonObject.getJSONObject("deep");
                        JSONArray asksArray = deepObject.getJSONArray("asks");
                        JSONArray bidsArray = deepObject.getJSONArray("bids");
                        ArrayList<DepthDataBean> buyList = new ArrayList<>();
                        ArrayList<DepthDataBean> sellList = new ArrayList<>();
                        String price;
                        String volume;
                        try {
                            if (asksArray != null) {
                                for (int i = 0; i < asksArray.length(); i++) {
                                    DepthDataBean depthDataBean = new DepthDataBean();
                                    price = String.valueOf(asksArray.getJSONArray(i).get(0));
                                    volume = String.valueOf(asksArray.getJSONArray(i).get(1));
                                    depthDataBean.setVolume(Float.valueOf(volume));
                                    depthDataBean.setPrice(Float.valueOf(price));

                                    sellList.add(depthDataBean);
                                }
                            } else {
                                DepthDataBean depthDataBean = new DepthDataBean();
                                depthDataBean.setVolume(0);
                                depthDataBean.setPrice(0);
                                sellList.add(depthDataBean);
                            }
                            if (bidsArray != null) {
                                for (int i = 0; i < bidsArray.length(); i++) {
                                    DepthDataBean depthDataBean = new DepthDataBean();
                                    price = String.valueOf(bidsArray.getJSONArray(i).get(0));
                                    volume = String.valueOf(bidsArray.getJSONArray(i).get(1));
                                    depthDataBean.setVolume(Float.valueOf(volume));
                                    depthDataBean.setPrice(Float.valueOf(price));

                                    buyList.add(0, depthDataBean);
                                }
                            } else {
                                DepthDataBean depthDataBean = new DepthDataBean();
                                depthDataBean.setVolume(0);
                                depthDataBean.setPrice(0);
                                buyList.add(depthDataBean);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (buyList.size() > 0 || sellList.size() > 0)
                            notifyDepthValue(buyList, sellList);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onClose(int i, String s, boolean b) {

        }
    }
    private long recordTime = -1;

    public void connect(String url, String coinCode) {
        Log.e("JWebSClientService", "connect:" + url);
        this.coinCode = coinCode;
        String[] coinCodeArr = coinCode.split("_");
        if ((coinCodeArr.length > 1)) {
            coinCodeUSDT = coinCodeArr[1];
        }

        URI uri = URI.create(url);
        this.clientChart = new WebClient(uri){
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.e("JWebSClientService", "onOpen");
                notifyConnected(0);
            }

            @Override
            public void onError(Exception e) {
                if (clientChart != null)
                    reconnect();
            }
        };
        this.clientTrade = new WebClient(uri){
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.e("JWebSClientService", "onOpen");
                notifyConnected(1);
            }

            @Override
            public void onError(Exception e) {
                if (clientTrade != null)
                    reconnect();
            }
        };
        try {
            clientChart.connectBlocking();
            clientTrade.connectBlocking();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void notifyDepthValue(List<DepthDataBean> buyList, List<DepthDataBean> sellList) {
        if (messageResponseListener != null) {
            messageResponseListener.onDepthChange(buyList, sellList);
        }
    }

    private void notifyConnected(int index) {
        if (messageResponseListener != null) {
            messageResponseListener.onSocketConnected(index);
        }
    }

    private void notifySubscribe(boolean update, KLineEntity kLineEntity) {
        if (messageResponseListener != null) {
            messageResponseListener.onSubscribe(update, kLineEntity);
        }
    }

    private void notifyCurrentChart(List<KLineEntity> data) {
        if (messageResponseListener != null) {
            messageResponseListener.onChartChange(data);
        }
    }

    private IMessageResponseListener messageResponseListener;
    public interface IMessageResponseListener {
        void onPriceChange(BeanPrice price);
        void onChartChange(List<KLineEntity> data);
        void onSubscribe(boolean update, KLineEntity data);
        void onSocketConnected(int index);
        void onTradeChange(List<DealRecord> list);
        void onDepthChange(List<DepthDataBean> buyList, List<DepthDataBean> sellList);
    }

    /**
     * 注册用户状态监听器
     *
     * @param action
     */
    public void register(IMessageResponseListener action) {
        messageResponseListener = action;
    }

    /**
     * 注销用户状态监听器，回收资源
     *
     * @param action
     */
    public void unRegister(IMessageResponseListener action) {
        messageResponseListener = null;
    }


    private void notifyTradeRecord(List<DealRecord> list) {
        if (messageResponseListener != null) {
            messageResponseListener.onTradeChange(list);
        }
    }

    /**
     *
     * @param price
     */
    public void notifyCurrentPrice(BeanPrice price) {
        if (messageResponseListener != null) {
            messageResponseListener.onPriceChange(price);
        }
    }


    public void close() {
        try {
            if (null != clientChart) {
                clientChart.close();
            }
            if (null != clientTrade) {
                clientTrade.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            clientTrade = null;
            clientChart = null;
        }
    }

}
