package com.alibaba.rocketmq.action;

import com.alibaba.rocketmq.common.Table;
import org.apache.commons.cli.Option;
import org.apache.commons.lang.StringUtils;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Enumeration;


/**
 * 
 * @author yankai913@gmail.com
 * @date 2014-2-17
 */
public abstract class AbstractAction {

    protected abstract String getFlag();


    protected abstract String getName();

    public static final String TITLE = "title";

    public static final String BODY_PAGE = "bodyPage";

    public static final String FORM_ACTION = "action";

    public static final String KEY_TABLE = "table";

    public static final String OPTIONS = "options";


    protected void putTable(ModelMap map, Table table) {
        map.put(KEY_TABLE, table);
    }


    protected void putPublicAttribute(ModelMap map, String title, Collection<Option> options,
            HttpServletRequest request) {
        putPublicAttribute(map, title, options);
        @SuppressWarnings("unchecked")
        Enumeration<String> enumer = request.getParameterNames();
        while (enumer.hasMoreElements()) {
            String key = enumer.nextElement();
            String value = request.getParameter(key);
            addOptionValue(options, key, value);
        }
    }


    protected void putPublicAttribute(ModelMap map, String title, Collection<Option> options) {
        putPublicAttribute(map, title);
        putOptions(map, options);
    }


    protected void putOptions(ModelMap map, Collection<Option> options) {
        map.put(OPTIONS, options);
    }


    /**
     * 在控制器中，一定要有这个方法
     * @param map
     * @param title
     */
    protected void putPublicAttribute(ModelMap map, String title) {
        map.put(getFlag(), "active");
        map.put(TITLE, getName() + ":" + title);
        map.put(BODY_PAGE, getName().toLowerCase() + "/" + title + ".vm"); // 这个表示 BodyPage 的组成路径

        map.put(FORM_ACTION, title + ".do"); // 这个表示请求 action
    }


    @SuppressWarnings("unchecked")
    protected void addOptionValue(Collection<Option> options, String key, Object value) {
        if (value == null) {
            return;
        }
        if (value instanceof String) {
            String tempVal = (String) value;
            if (StringUtils.isBlank(tempVal)) {
                return;
            }
        }
        for (Option opt : options) {
            if (opt.getLongOpt().equals(key)) {
                opt.getValuesList().add(value);
            }
        }
    }


    protected void checkOptions(Collection<Option> options) {
        for (Option option : options) {
            if (option.isRequired()) {
                String value = option.getValue();
                if (StringUtils.isBlank(value)) {
                    throw new IllegalStateException("option: key =[" + option.getLongOpt() + "], required=["
                            + option.isRequired() + "] is blank!");
                }
            }
        }
    }

    public static final String ALERT_MSG = "alertMsg";


    protected void putAlertMsg(Throwable t, ModelMap map) {
        map.put(ALERT_MSG, t.getMessage());
    }

    public static final String ALERT_TRUE = "alertTrue";


    protected void putAlertTrue(ModelMap map) {
        map.put(ALERT_TRUE, true);
    }

    public static final String TEMPLATE = "template";

    public static final String POST = "POST";

    public static final String GET = "GET";


    protected void throwUnknowRequestMethodException(HttpServletRequest request) {
        throw new IllegalStateException("unknown request method: " + request.getMethod());
    }
}
