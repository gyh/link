package com.example.market.ljw.service;

import android.os.Handler;

import com.example.market.ljw.entity.bean.output.Member;
import com.example.market.ljw.core.common.frame.BaseActivity;

/**
 * Created by GYH on 2014/10/27.
 */
public class ScoreThread extends Thread{

    private boolean isRunning = false;
    private long intervalTime;
    private BaseActivity baseActivity;
    private Handler handler;
    private Member member;

    public ScoreThread(BaseActivity baseActivity,long intervalTime,Handler handler,Member member){
        this.baseActivity = baseActivity;
        this.intervalTime = intervalTime;
        this.handler = handler;
        this.member = member;
    }

    @Override
    public void run() {
        super.run();
        //设置该线程的优先等级为最高
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
        while (isRunning){
//            try {
//                sleep(intervalTime);
//                Map<String, Object> param = new LinkedHashMap<String, Object>();
//                param.put(Constant.RequestKeys.SERVICENAME, "submit_score");
//                param.put(Constant.RequestKeys.DATA, baseActivity.gson.toJson(
//                        InputDataUtils.submitScore(baseActivity.getDataForShPre(Constant.SaveKeys.TOKENKEY, ""),
//                                curDuration,member.getID()+"")));
//                baseActivity.execute(Constant.SERVER_URL, true, param, null, new HttpGroup.OnEndListener() {
//                    @Override
//                    public void onEnd(HttpResponse httpresponse) {
//                        JsonParser jsonParser = new JsonParser();
//                        JsonElement jsonElement = jsonParser.parse(httpresponse.getResultObject().getContent());
//                        JsonObject jsonObject = jsonElement.getAsJsonObject();
//                        boolean success = jsonObject.get("success").getAsBoolean();
//                        if (success) {
//                            member = baseActivity.gson.fromJson(jsonObject.get("data").getAsJsonObject(), Member.class);
//                            handler.sendEmptyMessage(1);
//                        } else {
//                            String error_message = jsonObject.get("error_message").getAsString();
//                            PopUtils.showToast(error_message);
//                        }
//                    }
//                }, new HttpGroup.OnParseListener() {
//                    @Override
//                    public Entity onParse(String result) {
//                        Entity entity = new Entity();
//                        entity.setContent(result);
//                        return entity;
//                    }
//                });
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
    }
}
