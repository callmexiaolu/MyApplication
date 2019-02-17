package com.example.myapplication.dbutil;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.myapplication.db.DBManager;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Create by LuKaiqi on 2019/1/22.
 * function:数据库表操作类
 * 该类使用反射进行获取属性进而添加，因此数据库表字段名必须与类的变量名一致，否则添加出错。
 */
public class TableOperate {

    private DBManager mDBManager;
    private SQLiteDatabase db;
    private static TableOperate sOperate = null;

    private TableOperate() {
        mDBManager = DBManager.newInstance();
        db = mDBManager.getDataBase();
    }

    public static synchronized TableOperate getInstance() {
        if (sOperate == null) {
            sOperate = new TableOperate();
        }
        return sOperate;
    }

    /**
     * 查询数据库的名，数据库的添加
     *
     * @param tableName  查询的数据库表的名字
     * @param entityType 查询表对应的实体类名
     * @param fieldName  查询的字段名，即表字段名
     * @param value      查询的字段值，即筛选条件值
     * @param <T>        泛型代表需要查询的表对应的实体类，即table_user表对应User类
     * @return 返回查询结果，结果为查询类的对象，即User对象
     *
     * 例：ArrayList list = TableOperate.query(TABLE_CUSTOMER, Customer.class, TableConfig.Customer.CUSTOMER_NAME, "京客隆");
     *  Customer cus = (Customer) list.get(0);
     */
    public <T> ArrayList<T> query(String tableName, Class<T> entityType, String fieldName, String value) {

        ArrayList<T> list = new ArrayList<>();
        Cursor cursor = db.query(tableName, null, fieldName + " like ?", new String[]{value}, null, null, " id desc", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            try {
                T t = entityType.newInstance();
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    String content = cursor.getString(i);//获得获取的数据记录第i条字段的内容
                    String columnName = cursor.getColumnName(i);// 获取数据记录第i条字段名的
                    if (!columnName.equals("id")) {//表中id属性不添加到实体类中，因为实体类中没有id这个字段
                        Field field = entityType.getDeclaredField(columnName);//获取该字段名的Field对象。
                        field.setAccessible(true);//取消对属性的修饰符的检查访问，以便为属性赋值
                        field.set(t, content);
                        field.setAccessible(false);//恢复对属性的修饰符的检查访问
                    }
                }
                list.add(t);
                cursor.moveToNext();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return list;
    }

    /**
     * 向数据库插入数据
     *
     * @param tableName 数据库插入数据的数据表
     * @param object    数据库插入的对象
     */
    public void insert(String tableName, Object object) {

        Class clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();//获取该类所有的属性
        ContentValues value = new ContentValues();

        for (Field field : fields) {
            try {
                field.setAccessible(true); //取消对age属性的修饰符的检查访问，以便为属性赋值
                //类字段为String类型的,或者为Integer，int的
                if (field.getType().getName().equals(java.lang.String.class.getName())) {
                    String content = (String) field.get(object);//获取该属性的内容
                    value.put(field.getName(), content);
                } else if (field.getType().getName().equals(java.lang.Integer.class.getName()) ||
                        field.getType().getName().equals("int")) {
                    Integer content = field.getInt(object);//获取该属性的内容
                    value.put(field.getName(), content);
                } else if (field.getType().getName().equals(java.lang.Double.class.getName())) {
                    Double content = field.getDouble(object);
                    value.put(field.getName(), content);
                } else if (field.getType().getName().equals(java.lang.Float.class.getName())) {
                    Float content = field.getFloat(object);
                    value.put(field.getName(), content);
                }
                field.setAccessible(false);//恢复对age属性的修饰符的检查访问
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        db.insert(tableName, null, value);
    }

    /**
     * 删除数据
     *
     * @param tableName 删除数据库的表名
     * @param fieldName 删除的字段名
     * @param value     删除的字段的值
     */
    public void delete(String tableName, String fieldName, String value) {
        db.delete(tableName, fieldName + "=?", new String[]{value});
    }


    /**
     * 更改数据库内容
     *
     * @param tableName   更改数据的数据表
     * @param columnName  更改的数据的字段名
     * @param columnValue 更改的数据的字段值
     * @param object      更改的数据
     */
    public void uptate(String tableName, String columnName, String columnValue, Object object) {
        try {
            Class clazz = object.getClass();
            Field[] fields = clazz.getDeclaredFields();//获取该类所有的属性
            ContentValues value = new ContentValues();
            for (Field field : fields) {
                field.setAccessible(true); //取消对属性的修饰符的检查访问，以便为属性赋值
                String content = (String) field.get(object);//获取该属性的内容
                value.put(field.getName(), content);
                field.setAccessible(false);//恢复对属性的修饰符的检查访问
            }
            db.update(tableName, value, columnName+ "=?", new String[]{columnValue});
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        }
    }

}
