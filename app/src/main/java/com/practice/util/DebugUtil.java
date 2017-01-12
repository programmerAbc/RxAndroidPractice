package com.practice.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gaofeng on 2017-01-11.
 */

public class DebugUtil {
    public static List<String> generateDummyData(int count){
        List<String> data=new ArrayList<>();
        for(int i=0;i<count;++i){
            data.add("item "+i);
        }
        return data;
    }
}
