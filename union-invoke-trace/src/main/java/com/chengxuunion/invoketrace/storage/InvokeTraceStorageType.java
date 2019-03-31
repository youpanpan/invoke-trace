package com.chengxuunion.invoketrace.storage;

/**
 * 存储类型
 *
 * @author youpanpan
 * @date: 2019-03-13 17:19
 * @since v1.0
 */
public enum InvokeTraceStorageType {

    /**
     * 内存存储
     */
    MEMORY(1),

    /**
     * 数据库存储
     */
    DATABASE(2);

    private int type;

    private InvokeTraceStorageType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }


}
