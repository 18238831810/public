package com.cf.util.http;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * <p>Description: </p>
 * <p>Company: yingchuang</p>
 *
 * @author lantern
 * @date 2019/2/28
 */
@Slf4j
public class HttpWebResult {
    public static ResultJson getMonoResult(int code, String code_desc, Object ch_msg)
    {
        return new ResultJson( code, code_desc, ch_msg);
    }


    public static ResultJson getMonoSucResult(String msg, Object ch_msg) {
        return getMonoResult(200, msg, ch_msg);
    }

    public static ResultJson getMonoSucResult(Object ch_msg) {
        return getMonoResult(200, "success", ch_msg);
    }
    public static ResultJson getMonoError(String errorMsg)
    {
        return getMonoResult(500, errorMsg, null);
    }
    public static ResultJson getMonoSucStr()
    {
        return getMonoResult(200, "success", null);
    }

    /****
     * 输出字符串
     *
     * @param msg
     */
    public static void print(HttpServletResponse response, String msg)
    {
        PrintWriter out = null;
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        msg = StringUtils.isBlank(msg) ? "" : msg;
        try
        {
            out = response.getWriter();
            out.println(msg);
        }
        catch (IOException e)
        {
            log.error(e.getMessage());
        }
        finally
        {
            if (out != null)
            {
                out.flush();
                out.close();
            }
        }
    }

}
