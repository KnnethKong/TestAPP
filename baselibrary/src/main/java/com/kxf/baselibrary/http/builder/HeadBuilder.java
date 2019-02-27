package com.kxf.baselibrary.http.builder;


import com.kxf.baselibrary.http.OkHttpUtils;
import com.kxf.baselibrary.http.request.OtherRequest;
import com.kxf.baselibrary.http.request.RequestCall;

/**
 * Created by zhy on 16/3/2.
 */
public class HeadBuilder extends GetBuilder
{
    @Override
    public RequestCall build()
    {
        return new OtherRequest(null, null, OkHttpUtils.METHOD.HEAD, url, tag, params, headers,id).build();
    }
}
