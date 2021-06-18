package com.zdt.module.utils;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Strings;
import com.zdt.module.enums.QueryRuleEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class QueryGenerator {

    private static final String BEGIN = "_begin";
    private static final String END = "_end";
    /**
     * 数字类型字段，拼接此后缀 接受多值参数
     */
    private static final String MULTI = "_MultiString";
    private static final String STAR = "*";
    private static final String COMMA = ",";

    /**
     * 页面带有规则值查询，空格作为分隔符
     */
    private static final String QUERY_SEPARATE_KEYWORD = " ";

    private static final String NOT_EQUAL = "!";

    public static final String QUERY_COMMA_ESCAPE = "++";

    /**
     * mysql 模糊查询之特殊字符下划线 （_、\）
     */
    public static final String LIKE_MYSQL_SPECIAL_STRS = "_,%";

    /**
     * 排序方式
     */
    private static final String ORDER_TYPE = "order";
    private static final String ORDER_TYPE_ASC = "ASC";
    /**
     * 排序列
     */
    private static final String ORDER_COLUMN = "column";

    public static <T> QueryWrapper<T> initQueryWrapper(T requestVO, Map<String, String[]> parameterMap) {
        long start = System.currentTimeMillis();
        QueryWrapper<T> queryWrapper = new QueryWrapper<T>();
        installMplus(queryWrapper, requestVO, parameterMap);
        log.debug("---查询条件构造器初始化完成,耗时:" + (System.currentTimeMillis() - start) + "毫秒----");
        return queryWrapper;
    }

    public static void installMplus(QueryWrapper<?> queryWrapper, Object searchObj, Map<String, String[]> parameterMap) {
        try {
            PropertyDescriptor origDescriptors[] = PropertyUtils.getPropertyDescriptors(searchObj);
            String name, type, column;
            for (int i = 0; i < origDescriptors.length; i++) {
                name = origDescriptors[i].getName();
                type = origDescriptors[i].getPropertyType().toString();
                if (judgedIsUselessField(name) || !PropertyUtils.isReadable(searchObj, name)) {
                    continue;
                }
                Object value = PropertyUtils.getSimpleProperty(searchObj, name);
                column = getTableFieldName(searchObj.getClass(), name);
                if (column == null) {
                    //column为null只有一种情况 那就是 添加了注解@TableField(exist = false) 后续都不用处理了
                    continue;
                }
                //区间查询
                doIntervalQuery(queryWrapper, parameterMap, type, name, column);
                //判断单值  参数带不同标识字符串 走不同的查询
                //TODO 这种前后带逗号的支持分割后模糊查询需要否 使多选字段的查询生效
                if (null != value && value.toString().startsWith(COMMA) && value.toString().endsWith(COMMA)) {
                    String multiLikeval = value.toString().replace(",,", COMMA);
                    String[] vals = multiLikeval.substring(1).split(COMMA);
                    final String field = oConvertUtils.camelToUnderline(column);
                    if (vals.length > 1) {
                        queryWrapper.and(j -> {
                            j = j.like(field, vals[0]);
                            for (int k = 1; k < vals.length; k++) {
                                j = j.or().like(field, vals[k]);
                            }
                            //return j;
                        });
                    } else {
                        queryWrapper.and(j -> j.like(field, vals[0]));
                    }
                } else {
                    //根据参数值带什么关键字符串判断走什么类型的查询
                    QueryRuleEnum rule = convert2Rule(value);
                    value = replaceValue(rule, value);
                    // add -begin 添加判断为字符串时设为全模糊查询
                    //if( (rule==null || QueryRuleEnum.EQ.equals(rule)) && "class java.lang.String".equals(type)) {
                    // 可以设置左右模糊或全模糊，因人而异
                    //rule = QueryRuleEnum.LIKE;
                    //}
                    // add -end 添加判断为字符串时设为全模糊查询
                    addEasyQuery(queryWrapper, column, rule, value);
                }
            }
            // 排序逻辑 处理
            doMultiFieldsOrder(queryWrapper, parameterMap);
        } catch (IllegalAccessException e) {
            log.error("非法参数异常", e);
        } catch (InvocationTargetException e) {
            log.error("代理对象异常", e);
        } catch (NoSuchMethodException e) {
            log.error("方法找不到", e);
        } catch (Exception e) {
            log.error("系统异常", e);
        }
    }

    //多字段排序 TODO 需要修改前端
    public static void doMultiFieldsOrder(QueryWrapper<?> queryWrapper, Map<String, String[]> parameterMap) {
        String column = null, order = null;
        if (parameterMap != null && parameterMap.containsKey(ORDER_COLUMN)) {
            column = parameterMap.get(ORDER_COLUMN)[0];
        }
        if (parameterMap != null && parameterMap.containsKey(ORDER_TYPE)) {
            order = parameterMap.get(ORDER_TYPE)[0];
        }
        log.debug("排序规则>>列:" + column + ",排序方式:" + order);
        if (!Strings.isNullOrEmpty(column) && !Strings.isNullOrEmpty(order)) {

            if (order.toUpperCase().indexOf(ORDER_TYPE_ASC) >= 0) {
                queryWrapper.orderByAsc(oConvertUtils.camelToUnderline(column));
            } else {
                queryWrapper.orderByDesc(oConvertUtils.camelToUnderline(column));
            }
        }
    }

    /**
     * 替换掉关键字字符
     *
     * @param rule
     * @param value
     * @return
     */
    private static Object replaceValue(QueryRuleEnum rule, Object value) {
        if (rule == null) {
            return null;
        }
        if (!(value instanceof String)) {
            return value;
        }
        String val = (value + "").trim();
        if (rule == QueryRuleEnum.LIKE) {
            value = val.substring(1, val.length() - 1);
            //mysql 模糊查询之特殊字符下划线 （_、\）
            value = specialStrConvert(value.toString());
        } else if (rule == QueryRuleEnum.LEFT_LIKE || rule == QueryRuleEnum.NE) {
            value = val.substring(1);
            //mysql 模糊查询之特殊字符下划线 （_、\）
            value = specialStrConvert(value.toString());
        } else if (rule == QueryRuleEnum.RIGHT_LIKE) {
            value = val.substring(0, val.length() - 1);
            //mysql 模糊查询之特殊字符下划线 （_、\）
            value = specialStrConvert(value.toString());
        } else if (rule == QueryRuleEnum.IN) {
            value = val.split(",");
        } else if (rule == QueryRuleEnum.EQ_WITH_ADD) {
            value = val.replaceAll("\\+\\+", COMMA);
        } else {
            //update-begin--Author:scott  Date:20190724 for：initQueryWrapper组装sql查询条件错误 #284-------------------
            if (val.startsWith(rule.getValue())) {
                //TODO 此处逻辑应该注释掉-> 如果查询内容中带有查询匹配规则符号，就会被截取的（比如：>=您好）
                value = val.replaceFirst(rule.getValue(), "");
            } else if (val.startsWith(rule.getCondition() + QUERY_SEPARATE_KEYWORD)) {
                value = val.replaceFirst(rule.getCondition() + QUERY_SEPARATE_KEYWORD, "").trim();
            }
            //update-end--Author:scott  Date:20190724 for：initQueryWrapper组装sql查询条件错误 #284-------------------
        }
        return value;
    }

    /**
     * mysql 模糊查询之特殊字符下划线 （_、\）
     *
     * @param value:
     * @Return: java.lang.String
     */
    private static String specialStrConvert(String value) {
        String[] specialStr = QueryGenerator.LIKE_MYSQL_SPECIAL_STRS.split(",");
        for (String str : specialStr) {
            if (value.indexOf(str) != -1) {
                value = value.replace(str, "\\" + str);
            }
        }
        return value;
    }

    /**
     * 根据所传的值 转化成对应的比较方式
     * 支持><= like in !
     *
     * @param value
     * @return
     */
    private static QueryRuleEnum convert2Rule(Object value) {
        // 避免空数据
        if (value == null) {
            return null;
        }
        String val = (value + "").toString().trim();
        if (val.length() == 0) {
            return null;
        }
        QueryRuleEnum rule = null;

        //update-begin--Author:scott  Date:20190724 for：initQueryWrapper组装sql查询条件错误 #284-------------------
        //TODO 此处规则，只适用于 le lt ge gt
        // step 2 .>= =<
        if (rule == null && val.length() >= 3) {
            if (QUERY_SEPARATE_KEYWORD.equals(val.substring(2, 3))) {
                rule = QueryRuleEnum.getByValue(val.substring(0, 2));
            }
        }
        // step 1 .> <
        if (rule == null && val.length() >= 2) {
            if (QUERY_SEPARATE_KEYWORD.equals(val.substring(1, 2))) {
                rule = QueryRuleEnum.getByValue(val.substring(0, 1));
            }
        }
        //update-end--Author:scott  Date:20190724 for：initQueryWrapper组装sql查询条件错误 #284---------------------

        // step 3 like
        if (rule == null && val.contains(STAR)) {
            if (val.startsWith(STAR) && val.endsWith(STAR)) {
                rule = QueryRuleEnum.LIKE;
            } else if (val.startsWith(STAR)) {
                rule = QueryRuleEnum.LEFT_LIKE;
            } else if (val.endsWith(STAR)) {
                rule = QueryRuleEnum.RIGHT_LIKE;
            }
        }

        // step 4 in
        if (rule == null && val.contains(COMMA)) {
            //TODO in 查询这里应该有个bug  如果一字段本身就是多选 此时用in查询 未必能查询出来
            rule = QueryRuleEnum.IN;
        }
        // step 5 !=
        if (rule == null && val.startsWith(NOT_EQUAL)) {
            rule = QueryRuleEnum.NE;
        }
        // step 6 xx+xx+xx 这种情况适用于如果想要用逗号作精确查询 但是系统默认逗号走in 所以可以用++替换【此逻辑作废】
        if (rule == null && val.indexOf(QUERY_COMMA_ESCAPE) > 0) {
            rule = QueryRuleEnum.EQ_WITH_ADD;
        }

        //update-begin--Author:taoyan  Date:20201229 for：initQueryWrapper组装sql查询条件错误 #284---------------------
        //特殊处理：Oracle的表达式to_date('xxx','yyyy-MM-dd')含有逗号，会被识别为in查询，转为等于查询
        if (rule == QueryRuleEnum.IN && val.indexOf("yyyy-MM-dd") >= 0 && val.indexOf("to_date") >= 0) {
            rule = QueryRuleEnum.EQ;
        }
        //update-end--Author:taoyan  Date:20201229 for：initQueryWrapper组装sql查询条件错误 #284---------------------

        return rule != null ? rule : QueryRuleEnum.EQ;
    }

    /**
     * 获取表字段名
     *
     * @param clazz
     * @param name
     * @return
     */
    private static String getTableFieldName(Class<?> clazz, String name) {
        try {
            //如果字段加注解了@TableField(exist = false),不走DB查询
            Field field = null;
            try {
                field = clazz.getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                //e.printStackTrace();
            }

            //如果为空，则去父类查找字段
            if (field == null) {
                List<Field> allFields = getClassFields(clazz);
                List<Field> searchFields = allFields.stream().filter(a -> a.getName().equals(name)).collect(Collectors.toList());
                if (searchFields != null && searchFields.size() > 0) {
                    field = searchFields.get(0);
                }
            }

            if (field != null) {
                TableField tableField = field.getAnnotation(TableField.class);
                if (tableField != null) {
                    if (tableField.exist() == false) {
                        //如果设置了TableField false 这个字段不需要处理
                        return null;
                    } else {
                        String column = tableField.value();
                        //如果设置了TableField value 这个字段是实体字段
                        if (!"".equals(column)) {
                            return column;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }

    /**
     * 区间查询
     *
     * @param queryWrapper query对象
     * @param parameterMap 参数map
     * @param type         字段类型
     * @param filedName    字段名称
     * @param columnName   列名称
     */
    private static void doIntervalQuery(QueryWrapper<?> queryWrapper, Map<String, String[]> parameterMap, String type, String filedName, String columnName) throws ParseException {
        // 添加 判断是否有区间值
        String endValue = null, beginValue = null;
        if (parameterMap != null && parameterMap.containsKey(filedName + BEGIN)) {
            beginValue = parameterMap.get(filedName + BEGIN)[0].trim();
            addQueryByRule(queryWrapper, columnName, type, beginValue, QueryRuleEnum.GE);

        }
        if (parameterMap != null && parameterMap.containsKey(filedName + END)) {
            endValue = parameterMap.get(filedName + END)[0].trim();
            addQueryByRule(queryWrapper, columnName, type, endValue, QueryRuleEnum.LE);
        }
        //多值查询
        if (parameterMap != null && parameterMap.containsKey(filedName + MULTI)) {
            endValue = parameterMap.get(filedName + MULTI)[0].trim();
            addQueryByRule(queryWrapper, columnName.replace(MULTI, ""), type, endValue, QueryRuleEnum.IN);
        }
    }

    private static void addQueryByRule(QueryWrapper<?> queryWrapper, String name, String type, String value, QueryRuleEnum rule) throws ParseException {
        if (!Strings.isNullOrEmpty(value)) {
            Object temp;
            // 针对数字类型字段，多值查询
            if (value.indexOf(COMMA) != -1) {
                temp = value;
                addEasyQuery(queryWrapper, name, rule, temp);
                return;
            }

            switch (type) {
                case "class java.lang.Integer":
                    temp = Integer.parseInt(value);
                    break;
                case "class java.math.BigDecimal":
                    temp = new BigDecimal(value);
                    break;
                case "class java.lang.Short":
                    temp = Short.parseShort(value);
                    break;
                case "class java.lang.Long":
                    temp = Long.parseLong(value);
                    break;
                case "class java.lang.Float":
                    temp = Float.parseFloat(value);
                    break;
                case "class java.lang.Double":
                    temp = Double.parseDouble(value);
                    break;
                case "class java.time.LocalDateTime":
                    temp = getDateQueryByRule(value, rule);
                    break;
                default:
                    temp = value;
                    break;
            }
            addEasyQuery(queryWrapper, name, rule, temp);
        }
    }

    /**
     * 获取日期类型的值
     *
     * @param value
     * @param rule
     * @return
     * @throws ParseException
     */
    private static LocalDateTime getDateQueryByRule(String value, QueryRuleEnum rule) throws ParseException {
        LocalDateTime date = null;
        if (value.length() == 10) {
            if (rule == QueryRuleEnum.GE) {
                //比较大于
                date = DateUtils.stringToLocalDateTime(value + " 00:00:00", DateUtils.YYYYMMDDHHMMSS);
            } else if (rule == QueryRuleEnum.LE) {
                //比较小于
                date = DateUtils.stringToLocalDateTime(value + " 23:59:59", DateUtils.YYYYMMDDHHMMSS);
            }
            //TODO 日期类型比较特殊 可能oracle下不一定好使
        }
        if (date == null) {
            date = DateUtils.stringToLocalDateTime(value, DateUtils.YYYYMMDDHHMMSS);
        }
        return date;
    }

    /**
     * 根据规则走不同的查询
     *
     * @param queryWrapper QueryWrapper
     * @param name         字段名字
     * @param rule         查询规则
     * @param value        查询条件值
     */
    private static void addEasyQuery(QueryWrapper<?> queryWrapper, String name, QueryRuleEnum rule, Object value) {
        if (value == null || rule == null || value != null) {
            return;
        }
        name = oConvertUtils.camelToUnderline(name);
        log.info("--查询规则-->" + name + " " + rule.getValue() + " " + value);
        switch (rule) {
            case GT:
                queryWrapper.gt(name, value);
                break;
            case GE:
                queryWrapper.ge(name, value);
                break;
            case LT:
                queryWrapper.lt(name, value);
                break;
            case LE:
                queryWrapper.le(name, value);
                break;
            case EQ:
            case EQ_WITH_ADD:
                queryWrapper.eq(name, value);
                break;
            case NE:
                queryWrapper.ne(name, value);
                break;
            case IN:
                if (value instanceof String) {
                    queryWrapper.in(name, (Object[]) value.toString().split(","));
                } else if (value instanceof String[]) {
                    queryWrapper.in(name, (Object[]) value);
                }
                //update-begin-author:taoyan date:20200909 for:【bug】in 类型多值查询 不适配postgresql #1671
                else if (value.getClass().isArray()) {
                    queryWrapper.in(name, (Object[]) value);
                } else {
                    queryWrapper.in(name, value);
                }
                //update-end-author:taoyan date:20200909 for:【bug】in 类型多值查询 不适配postgresql #1671
                break;
            case LIKE:
                queryWrapper.like(name, value);
                break;
            case LEFT_LIKE:
                queryWrapper.likeLeft(name, value);
                break;
            case RIGHT_LIKE:
                queryWrapper.likeRight(name, value);
                break;
            default:
                log.info("--查询规则未匹配到---");
                break;
        }
    }

    /**
     * 获取class的 包括父类的
     *
     * @param clazz
     * @return
     */
    private static List<Field> getClassFields(Class<?> clazz) {
        List<Field> list = new ArrayList<Field>();
        Field[] fields;
        do {
            fields = clazz.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                list.add(fields[i]);
            }
            clazz = clazz.getSuperclass();
        } while (clazz != Object.class && clazz != null);
        return list;
    }

    /**
     * @param name
     * @return
     */
    private static boolean judgedIsUselessField(String name) {
        return "class".equals(name) || "ids".equals(name)
                || "page".equals(name) || "rows".equals(name)
                || "sort".equals(name) || "order".equals(name);
    }
}
