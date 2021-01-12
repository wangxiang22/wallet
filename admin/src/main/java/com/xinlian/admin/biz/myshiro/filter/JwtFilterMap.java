package com.xinlian.admin.biz.myshiro.filter;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

public class JwtFilterMap extends LinkedHashMap<String, List<String>> {

    public void addRoleUrl(String url, List<String> roles){
        this.put(url, roles);
    }

    public boolean matchRole(String url, String role){
        List<String> roles = matchRole(url);
        if(roles == null){
            return false;
        }
        if(roles.contains("none") || roles.contains(role)){
            return true;
        }
        return false;
    }

    public List<String> matchRole(String url){
        Iterator<String> it = this.keySet().iterator();
        String key = null;
        while(it.hasNext()){
            key = it.next();
            if(url.startsWith(key)){
                return this.get(key);
            }
        }
        return null;
    }

}
